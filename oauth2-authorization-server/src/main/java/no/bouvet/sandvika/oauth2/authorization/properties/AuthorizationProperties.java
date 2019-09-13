package no.bouvet.sandvika.oauth2.authorization.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static java.util.Collections.emptyList;

@ConfigurationProperties("bouvet.authorization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class AuthorizationProperties {

    @Builder.Default
    private List<UserProperties> users = emptyList();
    @Builder.Default
    private List<ClientProperties> clients = emptyList();
}
