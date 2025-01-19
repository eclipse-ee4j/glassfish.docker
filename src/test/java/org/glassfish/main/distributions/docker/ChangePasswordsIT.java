/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package org.glassfish.main.distributions.docker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.glassfish.main.distributions.docker.CommonIntegrationTests.assertDefaultServerRoot;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Ondro Mihalyi
 */
@Testcontainers
public class ChangePasswordsIT {

    @SuppressWarnings({"rawtypes", "resource"})
    @Container
    private final GenericContainer server = new GenericContainer<>(System.getProperty("docker.glassfish.image"))
            .withExposedPorts(8080, 4848)
            .withEnv("AS_ADMIN_MASTERPASSWORD", "mymasterpassword")
            .withEnv("AS_ADMIN_PASSWORD", "myadminpassword")
            .withLogConsumer(o -> System.err.print("GF: " + o.getUtf8String()));

    @Test
    void getRoot() throws Exception {
        assertDefaultServerRoot(server);
    }

    @Test
    void customAdminPassword() throws Exception {
        URI uri = URI.create("https://localhost:" + server.getMappedPort(4848) + "/management/domain.json");

        try (HttpClient client = newInsecureHttpClient()) {
            String basicAuthValue = basicAuthValue("admin", "myadminpassword");
            final HttpResponse<Void> response = client.send(HttpRequest.newBuilder(uri)
                    .setHeader("Authorization", basicAuthValue)
                    .build(), HttpResponse.BodyHandlers.discarding());
            assertEquals(200, response.statusCode(), "Response code");
        }
    }

    private static HttpClient newInsecureHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        return client;
    }

    private static String basicAuthValue(String user, String password) {
        String userCredentials = user + ":" + password;
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        return basicAuth;
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }
    };

}
