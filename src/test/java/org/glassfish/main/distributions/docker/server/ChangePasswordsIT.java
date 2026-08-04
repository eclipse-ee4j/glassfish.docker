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
package org.glassfish.main.distributions.docker.server;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.glassfish.main.distributions.docker.testutils.HttpUtilities.getAdminResource;
import static org.glassfish.main.distributions.docker.testutils.HttpUtilities.getServerDefaultRoot;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.wait.strategy.WaitAllStrategy.Mode.WITH_MAXIMUM_OUTER_TIMEOUT;

/**
 * @author Ondro Mihalyi
 */
@Testcontainers
public class ChangePasswordsIT {

    private static final String ADMIN_PASSWORD = UUID.randomUUID().toString();
    private static final String ADMIN_MASTERPASSWORD = "mymasterpassword";

    @SuppressWarnings({"rawtypes", "resource"})
    @Container
    private final GenericContainer server = new GenericContainer<>(System.getProperty("gf.docker.server.image"))
        .withExposedPorts(8080, 4848)
        .withEnv("AS_ADMIN_MASTERPASSWORD", ADMIN_MASTERPASSWORD)
        .withEnv("AS_ADMIN_PASSWORD", ADMIN_PASSWORD)
        .withEnv("JAVA_OPTIONS",
            "-Djavax.net.ssl.trustStore=/opt/gfinstall/glassfish/domains/domain1/config/cacerts.jks "
          + "-Djavax.net.ssl.trustStorePassword=" + ADMIN_MASTERPASSWORD)
        .waitingFor(new WaitAllStrategy(WITH_MAXIMUM_OUTER_TIMEOUT)
            .withStrategy(Wait.forLogMessage("^STARTING DOMAIN.*", 1))
            .withStrategy(Wait.forListeningPorts(4848, 8080))
            .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)))
        .withLogConsumer(o -> System.err.print("GF: " + LocalTime.now() + ": " + o.getUtf8String()));

    @Test
    void rootResourceGivesOkWithDefaultResponse() throws Exception {
        final HttpResponse<String> defaultRootResponse = getServerDefaultRoot(server);
        assertAll(
            () -> assertEquals(200, defaultRootResponse.statusCode(), "Response status code"),
            () -> assertThat(defaultRootResponse.body(), stringContainsInOrder("Eclipse GlassFish", "index.html", "production-quality"))
        );
    }

    @Test
    void customAdminPassword_HTTP() throws Exception {
        final HttpResponse<String> adminResourceResponse = getAdminResource(server, "/management/domain.json",
                new UserPassword("admin", ADMIN_PASSWORD));
        assertEquals(200, adminResourceResponse.statusCode(), "Response status code");
    }

    @Test
    void customAdminPassword_asadmin() throws Exception {
        ExecResult result = server.execInContainer("asadmin", "--passwordfile", "/opt/gfinstall/password.txt", "get", "*");
        assertAll(
            () -> assertEquals(0, result.getExitCode(), "Exit code"),
            () -> assertThat(result.getStderr(), result.getStdout(), stringContainsInOrder("servers.server.server.name=server"))
        );
    }
}
