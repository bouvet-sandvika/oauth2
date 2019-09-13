package no.bouvet.sandvika.oauth2.authorization.token;

import lombok.extern.slf4j.Slf4j;
import no.bouvet.sandvika.oauth2.authorization.properties.AuthorizationProperties;
import no.bouvet.sandvika.oauth2.authorization.properties.ClientProperties;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FilterAuthoritiesAccessTokenConverter extends DefaultAccessTokenConverter {

    private Map<String, Set<String>> clientAllowedAuthoritiesMap;

    public FilterAuthoritiesAccessTokenConverter(AuthorizationProperties authorizationProperties) {
        clientAllowedAuthoritiesMap = authorizationProperties.getClients().stream()
            .collect(Collectors.toMap(ClientProperties::getClientId, ClientProperties::getAllowedAuthorities));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

        Map<String, Object> accessTokenMap = new HashMap<>(super.convertAccessToken(token, authentication));

        if (accessTokenMap.get(AUTHORITIES) != null) {
            String clientId = authentication.getOAuth2Request().getClientId();
            Set<String> clientAllowedAuthorities = clientAllowedAuthoritiesMap.get(clientId);

            Set<Object> userAuthoritiesSet = (Set) accessTokenMap.get(AUTHORITIES);
            Set<String> filteredAuthorities = userAuthoritiesSet.stream()
                .map(String::valueOf)
                .filter(clientAllowedAuthorities::contains)
                .collect(Collectors.toSet());

            log.debug("User '{}' on client '{}' approved for authorities {}", authentication.getName(), clientId, filteredAuthorities);
            accessTokenMap.put(AUTHORITIES, filteredAuthorities);
        }
        return accessTokenMap;
    }
}
