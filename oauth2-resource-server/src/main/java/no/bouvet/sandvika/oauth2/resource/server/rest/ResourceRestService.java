package no.bouvet.sandvika.oauth2.resource.server.rest;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

import static java.lang.String.format;

@RestController
public class ResourceRestService {

    @RolesAllowed({"ROLE_USER", "ROLE_BRUKER", "ROLE_SERVER_BRUKER"})
    @RequestMapping(value = "/hentBackendData")
    @ResponseBody
    public String hentData(OAuth2Authentication auth) {
        if (auth.getUserAuthentication() != null) {
            return format("Hei på deg bruker '%s'. Jeg ser du har følgende authorities: %s. Klienten '%s' har følgende scopes: %s",
                    auth.getPrincipal(),
                    auth.getAuthorities(),
                    auth.getOAuth2Request().getClientId(),
                    auth.getOAuth2Request().getScope());
        } else {
            return format("Hei på deg klient '%s'. Jeg ser du har følgende authorities: %s, og følgende scopes: %s",
                    auth.getOAuth2Request().getClientId(),
                    auth.getAuthorities(),
                    auth.getOAuth2Request().getScope());
        }
    }

    @RolesAllowed("INGEN_TILGANG")
    @RequestMapping(value = "/ingenTilgang")
    @ResponseBody
    public String ingenTilgang() {
        throw new IllegalStateException("FEIL: Skal ikke kunne bli kalt!!");
    }
}
