package no.bouvet.sandvika.oauth2.authorization.properties;

import lombok.*;

import java.util.Set;

import static java.util.Collections.emptySet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString(exclude = "secret")
public class ClientProperties {

    private String clientId;
    private String secret;
    @Builder.Default
    private Set<String> grantTypes = emptySet();
    @Builder.Default
    private Set<String> scopes = emptySet();
    @Builder.Default
    private Set<String> redirectUris = emptySet();
}
