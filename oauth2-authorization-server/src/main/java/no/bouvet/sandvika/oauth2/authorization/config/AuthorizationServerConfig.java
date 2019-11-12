package no.bouvet.sandvika.oauth2.authorization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerTokenServicesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
@Import(AuthorizationServerTokenServicesConfiguration.class) // JWT token support
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final int sessionTimeout;
    private final JwtAccessTokenConverter accessTokenConverter;

    public AuthorizationServerConfig(@Value("${server.servlet.session.timeout}") int sessionTimeout,
                                     JwtAccessTokenConverter accessTokenConverter) {
        this.sessionTimeout = sessionTimeout;
        this.accessTokenConverter = accessTokenConverter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Use AccessTokenConverter from AuthorizationServerTokenServicesConfiguration for JWT token support
        endpoints.accessTokenConverter(accessTokenConverter);
    }

    //@formatter:off
    @Override
    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
        clientDetailsServiceConfigurer.inMemory()
            .withClient("oauth2-client")
                .secret("client-password")
                .authorizedGrantTypes("authorization_code")
                .scopes("read")
                .redirectUris("http://localhost:9292/login")
                .accessTokenValiditySeconds(sessionTimeout)
                .autoApprove(true);
    }
    //@formatter:on
}
