/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation
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
package org.glassfish.main.distributions.docker.testutils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.glassfish.main.distributions.docker.server.UserPassword;
import org.testcontainers.containers.GenericContainer;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofSeconds;

/**
 *
 * @author Ondro Mihalyi
 */
public final class HttpUtilities {

    private static final TrustManager[] NAIVE_TRUST_MANAGERS = new TrustManager[] {new X509TrustManager() {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    }};


    private HttpUtilities() {
    }

    public static HttpResponse<String> getServerDefaultRoot(GenericContainer server) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getMappedPort(8080) + "/");
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            return client.send(request, ofString(UTF_8));
        }
    }

    public static HttpResponse<String> getEmbeddedDefaultRoot(GenericContainer server) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getMappedPort(8080) + "/");
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            return client.send(request, ofString(UTF_8));
        }
    }

    public static HttpResponse<String> getApplication(GenericContainer server, String appPath) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getMappedPort(8080) + appPath);

        // Wait up to 30 seconds for the application to become available
        long startTime = System.currentTimeMillis();
        HttpResponse<String> response = null;

        while ((System.currentTimeMillis() - startTime) < Duration.ofSeconds(30).toMillis()) {
            try (HttpClient client = newInsecureHttpClient()) {
                final HttpRequest request = HttpRequest.newBuilder(uri).build();
                response = client.send(request, ofString(UTF_8));

                if (response.statusCode() == 200) {
                    return response;
                }

                Thread.sleep(ofSeconds(1)); // Wait 1 second before retrying
            }
        }

        return response; // Return the last response (likely 404)
    }

    public static HttpResponse<String> getAdminResource(GenericContainer server, String resourcePath, UserPassword userPass) throws Exception {
        URI uri = URI.create("https://localhost:" + server.getMappedPort(4848) + resourcePath);
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri)
                    .setHeader("Authorization", basicAuthValue(userPass))
                    .build();
            return client.send(request, ofString(UTF_8));
        }
    }

    private static HttpClient newInsecureHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, NAIVE_TRUST_MANAGERS, new SecureRandom());
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
        return client;
    }

    private static String basicAuthValue(UserPassword userPass) {
        String userCredentials = userPass.username() + ":" + userPass.password();
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        return basicAuth;
    }
}
