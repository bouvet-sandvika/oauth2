package no.bouvet.sandvika.oauth2.authorization.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11JsseProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.apache.tomcat.util.net.SSLHostConfig.CertificateVerification.REQUIRED;

@Configuration
@ConditionalOnProperty(name = "bouvet.authorization.client-auth-type", havingValue = "x509")
public class TomcatSslClientAuthConnectorConfiguration {

    @Bean
    public ServletWebServerFactory servletContainer(@Value("${bouvet.authorization.client-auth-port}") int port,
                                                    @Value("${server.ssl.key-store:}") String keyStore,
                                                    @Value("${server.ssl.key-store-password:}") String keyStorePassword,
                                                    @Value("${server.ssl.key-alias:}") String keyAlias,
                                                    @Value("${server.ssl.trust-store:}") String trustStore,
                                                    @Value("${server.ssl.trust-store-password:}") String trustStorePassword) {

        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(port);
        connector.setScheme("https");
        connector.setSecure(true);

        AbstractHttp11JsseProtocol<?> protocolHandler = (AbstractHttp11JsseProtocol<?>) connector.getProtocolHandler();
        protocolHandler.setSSLEnabled(true);
        protocolHandler.setSslProtocol("TLS");
        protocolHandler.setClientAuth(REQUIRED.name());
        protocolHandler.setKeystoreFile(keyStore);
        protocolHandler.setKeystorePass(keyStorePassword);
        protocolHandler.setKeyAlias(keyAlias);
        protocolHandler.setTruststoreFile(trustStore);
        protocolHandler.setTruststorePass(trustStorePassword);

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }
}
