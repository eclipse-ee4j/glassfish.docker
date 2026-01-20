/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation.
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

package org.glassfish.main.distributions.docker.embedded;

import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.glassfish.main.distributions.docker.testutils.HttpUtilities.getApplication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class EmbeddedDeploymentIT {

    @SuppressWarnings({"rawtypes", "resource"})
    @Container
    private final GenericContainer server = new GenericContainer<>(System.getProperty("embedded.docker.glassfish.image"))
        .withExposedPorts(8080)
        .withFileSystemBind("target/test-classes/application-test.war", "/deploy/application.war", BindMode.READ_ONLY)
        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)))
        .withLogConsumer(o -> System.err.print("GF-Embedded: " + o.getUtf8String()));

    @Test
    void deployedApplicationIsAccessible() throws Exception {
        // Verify the application is deployed by checking if it's accessible
        final HttpResponse<String> appResponse = getApplication(server, "/index.html");
        assertEquals(200, appResponse.statusCode(), "Application response status code");
        assertTrue(appResponse.body().contains("Hello from test app"), "Application should return Hello message");
    }
}
