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
@ToString(exclude = "password")
public class UserProperties {

    private String username;
    private String password;
    @Builder.Default
    private Set<String> authorities = emptySet();
}
