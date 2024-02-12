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

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author David Matejcek
 */
public class ShaGenerator {

    public static void main(String[] args) throws Exception {
        System.out.println("Arguments: " + Arrays.toString(args));
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Invalid arguments: " + Arrays.toString(args));
        }
        final Path input = Paths.get(args[0]);
        final File properties = new File(args[1]);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        byte[] bytes = Files.readAllBytes(input);
        BigInteger no = new BigInteger(1, messageDigest.digest(bytes));
        String hashtext = no.toString(16);
        while (hashtext.length() < 128) {
            hashtext = "0" + hashtext;
        }
        Properties props = new Properties();
        props.setProperty("glassfish.zip.sha512", " ".repeat(128 - hashtext.length()) + hashtext);
        try (FileOutputStream output = new FileOutputStream(properties)) {
            props.store(output, null);
        }
    }

}
