/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.glassfish.main.distributions.docker.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.testcontainers.containers.GenericContainer;

import static java.net.http.HttpResponse.BodyHandlers.ofString;

/**
 *
 * @author Ondro Mihalyi
 */
public final class HttpUtilities {

    private HttpUtilities() {
    }

    static HttpResponse<String> getServerDefaultRoot(GenericContainer server) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getMappedPort(8080) + "/");
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            return client.send(request, ofString(StandardCharsets.UTF_8));
        }
    }

    static HttpResponse<String> getEmbeddedDefaultRoot(GenericContainer server) throws Exception {
        URI uri = URI.create("http://localhost:" + server.getMappedPort(8080) + "/");
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri).build();
            return client.send(request, ofString(StandardCharsets.UTF_8));
        }
    }

    static HttpResponse<String> getAdminResource(GenericContainer server, String resourcePath, UserPassword userPass) throws Exception {
        URI uri = URI.create("https://localhost:" + server.getMappedPort(4848) + resourcePath);
        try (HttpClient client = newInsecureHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder(uri)
                    .setHeader("Authorization", basicAuthValue(userPass))
                    .build();
            return client.send(request, ofString(StandardCharsets.UTF_8));
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

    private static String basicAuthValue(UserPassword userPass) {
        String userCredentials = userPass.username() + ":" + userPass.password();
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
