package no.bouvet.sandvika.oauth2.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BackendRestService {
    private final RestTemplate restTemplate;

    public BackendRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/data", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public String getData() {
        return restTemplate.getForEntity("http://localhost:9393/hentBackendData", String.class).getBody();
    }
}
