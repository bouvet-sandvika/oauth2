package no.bouvet.sandvika.oauth2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoDto {
    private String username;
    private String authorities;
    private String clientId;
    private String scope;
}
