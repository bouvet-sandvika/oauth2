package no.bouvet.sandvika.oauth2.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.bouvet.sandvika.oauth2.GithubUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@Slf4j
@RestController
public class EmployeeRestService
{
    private final ObjectMapper objectMapper;

    public EmployeeRestService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RolesAllowed({"ROLE_USER"})
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public Object userInfo(Authentication authentication)
    {
        return objectMapper.convertValue(((OAuth2Authentication)authentication).getUserAuthentication().getDetails(), GithubUserInfo.class);
    }
}
