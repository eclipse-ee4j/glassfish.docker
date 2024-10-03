/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation.
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

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame.OutputType;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
@Testcontainers
public class RunembeddedIT {

    @SuppressWarnings({"rawtypes", "resource"})
    @Container
    private final GenericContainer server = new GenericContainer<>(System.getProperty("docker.glassfish.image"))
        .withCommand("runembedded").withExposedPorts(8080).withLogConsumer(o -> {
            // FIXME: If we don't use the interactive terminal, spams STDOUT. To be fixed in 7.0.19+.
            if (o.getType() == OutputType.STDERR) {
                System.err.print("GF: " + o.getUtf8String());
            }
        });

    @Test
    void getRoot() throws Exception {
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
