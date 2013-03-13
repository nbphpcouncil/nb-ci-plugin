/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.ci.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.openide.util.Exceptions;

/**
 *
 * @author Junji Takakura
 */
public class Utils {

    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }

    public static int indexOf(Object[] array, Object objectToFind) {
        int index = -1;

        if (array != null) {
            if (objectToFind == null) {
                for (int i = 0; i < array.length; i++) {
                    if (array[i] == null) {
                        index = i;
                        break;
                    }
                }
            } else {
                for (int i = 0; i < array.length; i++) {
                    if (objectToFind.equals(array[i])) {
                        index = i;
                        break;
                    }
                }
            }
        }

        return index;
    }
    
    public static String join(Object[] array, CharSequence delimiter) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }

            sb.append(array[i]);
        }

        return sb.toString();
    }

    public static String getMD5Sum(File file) {
        return bytesToHexString(getDigestBytes(file, "MD5")); // NOI18N
    }

    public static String getSHA1Sum(File file) {
        return bytesToHexString(getDigestBytes(file, "SHA1")); // NOI18N
    }

    private static byte[] getDigestBytes(final File file, final String algorithm) {
        assert file != null && file.isFile();

        byte[] digestBytes = null;
        MessageDigest md = null;
        FileInputStream is = null;
        ByteBuffer buffer = null;

        try {
            md = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            // TODO
            Exceptions.printStackTrace(ex);
        }

        try {
            is = new FileInputStream(file);
            buffer = is.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            md.update(buffer);
            digestBytes = md.digest();
        } catch (IOException ex) {
            // TODO
            Exceptions.printStackTrace(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    // TODO
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        return digestBytes;
    }

    private static String bytesToHexString(final byte[] bytes) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];

            String hex = Integer.toHexString(b);

            if (hex.length() == 1) {
                hex = "0" + hex;
            }

            if (hex.length() > 2) {
                hex = hex.substring(hex.length() - 2);
            }

            sb.append(hex);
        }

        return sb.toString();
    }
}
