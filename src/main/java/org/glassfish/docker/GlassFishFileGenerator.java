/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.glassfish.docker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author David Matejcek
 */
public class GlassFishFileGenerator {

    private static final Path FRAGMENT = Path.of("dockerlibfile-fragment.txt");


    public static void main(String[] args) throws Exception {
        Path basedir = Paths.get(args[0]);
        if (!basedir.toAbsolutePath().toFile().isDirectory()) {
            throw new IllegalArgumentException("The basedir is not an existing directory: " + basedir);
        }
        try (FileOutputStream output = new FileOutputStream(basedir.resolve(Path.of("target", "glassfish")).toFile())) {
            Files.copy(basedir.resolve("dockerlibfile-header.txt"), output);
            Files.list(basedir).filter(p -> p.toFile().isDirectory()).sorted()
                .map(p -> p.resolve(FRAGMENT)).filter(p -> p.toFile().isFile())
                .forEach(p -> copy(p, output));
        }
    }


    private static void copy(Path source, FileOutputStream output) {
        try {
            output.write("\n".getBytes());
            Files.copy(source, output);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
