package no.bouvet.sandvika.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class OAuth2SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private void configureKeyStores(@Value("${server.ssl.key-store}") String keyStore,
                                    @Value("${server.ssl.key-store-password}") String keyStorePassword,
                                    @Value("${server.ssl.trust-store}") String trustStore,
                                    @Value("${server.ssl.trust-store-password}") String trustStorePassword) throws FileNotFoundException
    {
        System.setProperty("javax.net.ssl.keyStore", ResourceUtils.getFile(keyStore).getAbsolutePath());
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        System.setProperty("javax.net.ssl.trustStore", ResourceUtils.getFile(trustStore).getAbsolutePath());
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                    .authenticated()
                .and()
                    .csrf()
                .disable(); // Required for GET on /logout
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .interceptors((request, body, execution) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth.getDetails() instanceof OAuth2AuthenticationDetails) {
                        OAuth2AuthenticationDetails oAuth2AuthDetails = (OAuth2AuthenticationDetails) auth.getDetails();
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2AuthDetails.getTokenValue());
                    }
                    return execution.execute(request, body);

                }).errorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() == HttpStatus.UNAUTHORIZED &&
                        response.getHeaders().containsKey(HttpHeaders.WWW_AUTHENTICATE)) {
                    return false; // Do not throw Exception on token related errors
                }
                return super.hasError(response);
            }
        }).build();
    }
}
