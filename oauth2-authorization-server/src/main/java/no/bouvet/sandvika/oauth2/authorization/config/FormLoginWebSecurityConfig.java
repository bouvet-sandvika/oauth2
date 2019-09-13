package no.bouvet.sandvika.oauth2.authorization.config;

import no.bouvet.sandvika.oauth2.authorization.properties.AuthorizationProperties;
import no.bouvet.sandvika.oauth2.authorization.properties.UserProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class FormLoginWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final List<UserProperties> userPropertiesList;

    public FormLoginWebSecurityConfig(AuthorizationProperties authorizationProperties) {
        this.userPropertiesList = authorizationProperties.getUsers();
    }

    //@formatter:off
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable()
            .authorizeRequests()
            .anyRequest()
                .authenticated()
            .and()
            .formLogin()
                .permitAll();
    }
    //@formatter:on

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
        userPropertiesList.forEach(userProperties -> configurer.withUser(createUserDetails(userProperties)));
    }

    private UserDetails createUserDetails(UserProperties userProperties) {
        return new User(
            userProperties.getUsername(),
            userProperties.getPassword(),
            userProperties.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
