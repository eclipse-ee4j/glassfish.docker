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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.testcontainers.containers.GenericContainer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Ondro Mihalyi
 */
public final class CommonIntegrationTests {

    private CommonIntegrationTests() {
    }

    static void assertDefaultServerRoot(GenericContainer server) throws Exception {
        URL url = URI.create("http://localhost:" + server.getMappedPort(8080) + "/").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String content;
        try {
            connection.setRequestMethod("GET");
            assertEquals(200, connection.getResponseCode(), "Response code");
            try (InputStream in = connection.getInputStream()) {
                content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } finally {
            connection.disconnect();
        }
        assertThat(content, stringContainsInOrder("Eclipse GlassFish", "index.html", "production-quality"));
    }

    static void assertEmbeddedDefaultRoot(GenericContainer server) throws Exception {
        URL url = URI.create("http://localhost:" + server.getMappedPort(8080) + "/").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("GET");
            assertEquals(404, connection.getResponseCode(), "Response code");
        } finally {
            connection.disconnect();
        }
    }
}