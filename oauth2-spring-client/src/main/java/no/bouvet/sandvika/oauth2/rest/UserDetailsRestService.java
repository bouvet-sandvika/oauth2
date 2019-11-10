package no.bouvet.sandvika.oauth2.rest;

import lombok.extern.slf4j.Slf4j;
import no.bouvet.sandvika.oauth2.dto.UserInfoDto;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@Slf4j
@RestController
public class UserDetailsRestService
{
    @RolesAllowed({"ROLE_USER"})
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public UserInfoDto userInfo(OAuth2Authentication authentication)
    {
        return UserInfoDto.builder()
                .username(authentication.getName())
                .authorities(authentication.getAuthorities().toString())
                .clientId(authentication.getOAuth2Request().getClientId())
                .scope(authentication.getOAuth2Request().getScope().toString())
                .build();
    }
}
