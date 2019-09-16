package no.bouvet.sandvika.oauth2.authorization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

// Annotations copied from parent:
@Configuration
@Order(0)
@Import({ClientDetailsServiceConfiguration.class, AuthorizationServerEndpointsConfiguration.class})
public class X509AuthorizationServerSecurityConfiguration extends AuthorizationServerSecurityConfiguration {

    private final String clientAuthType;
    private final ClientDetailsService clientDetailsService;

    public X509AuthorizationServerSecurityConfiguration(@Value("${bouvet.authorization.client-auth-type}") String clientAuthType,
                                                        ClientDetailsService clientDetailsService) {
        this.clientAuthType = clientAuthType;
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        if ("x509".equalsIgnoreCase(clientAuthType)) {
            http.apply(new SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {
                @Override
                public void init(HttpSecurity http) throws Exception {
                    X509AuthenticationFilter x509AuthenticationFilter = new X509AuthenticationFilter();
                    x509AuthenticationFilter.setAuthenticationManager(authenticationManagerBean());

                    http.httpBasic().disable()
                        .x509().x509AuthenticationFilter(x509AuthenticationFilter)
                        .userDetailsService(new ClientDetailsUserDetailsService(clientDetailsService));
                }
            });
        }
    }
}
