package no.bouvet.sandvika.oauth2.authorization.config;

import no.bouvet.sandvika.oauth2.authorization.properties.AuthorizationProperties;
import no.bouvet.sandvika.oauth2.authorization.properties.ClientProperties;
import no.bouvet.sandvika.oauth2.authorization.token.FilterAuthoritiesAccessTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerTokenServicesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.List;

@Configuration
@Import(AuthorizationServerTokenServicesConfiguration.class) // JWT token support
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final int sessionTimeout;
    private final List<ClientProperties> clientPropertiesList;
    private final FilterAuthoritiesAccessTokenConverter filterAuthoritiesAccessTokenConverter;
    private final JwtAccessTokenConverter accessTokenConverter;

    public AuthorizationServerConfig(@Value("${server.servlet.session.timeout}") int sessionTimeout,
                                     AuthorizationProperties authorizationProperties,
                                     FilterAuthoritiesAccessTokenConverter filterAuthoritiesAccessTokenConverter,
                                     JwtAccessTokenConverter accessTokenConverter) {
        this.sessionTimeout = sessionTimeout;
        this.clientPropertiesList = authorizationProperties.getClients();
        this.filterAuthoritiesAccessTokenConverter = filterAuthoritiesAccessTokenConverter;
        this.accessTokenConverter = accessTokenConverter;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // Use AccessTokenConverter from AuthorizationServerTokenServicesConfiguration for JWT token support
        accessTokenConverter.setAccessTokenConverter(filterAuthoritiesAccessTokenConverter);
        endpoints.accessTokenConverter(accessTokenConverter);
    }

    //@formatter:off
    @Override
    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
        InMemoryClientDetailsServiceBuilder clientDetailsServiceBuilder = clientDetailsServiceConfigurer.inMemory();

        clientPropertiesList.forEach(clientProperties -> clientDetailsServiceBuilder
            .withClient(clientProperties.getClientId())
            .secret(clientProperties.getSecret())
            .authorizedGrantTypes(clientProperties.getGrantTypes().toArray(new String[0]))
            .scopes(clientProperties.getScopes().toArray(new String[0]))
            .redirectUris(clientProperties.getRedirectUris().toArray(new String[0]))
            .accessTokenValiditySeconds(sessionTimeout)
            .autoApprove(true));
    }
    //@formatter:on
}
