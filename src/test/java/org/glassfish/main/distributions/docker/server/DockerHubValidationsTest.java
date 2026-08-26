/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.glassfish.docker.ShaGenerator;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class DockerHubValidationsTest {

    @Test
    void shortReadmeFile() throws Exception {
        String content = readFile(ShaGenerator.class.getResource("/images/server/docs/README-short.txt"));
        List<String> lines = content.lines().toList();
        assertEquals(1, lines.size(), "Count of lines");
        String line = lines.get(0);
        assertThat("Line should not start or end with whitespaces", content, equalTo(line));
        assertThat("Length of the line", line, hasLength(lessThanOrEqualTo(100)));
    }

    private String readFile(URL file) throws IOException {
        try (InputStream stream = file.openStream()) {
            return new String(stream.readAllBytes(), UTF_8);
        }
    }
}
