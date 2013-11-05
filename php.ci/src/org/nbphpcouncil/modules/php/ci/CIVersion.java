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
package org.nbphpcouncil.modules.php.ci;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Junji Takakura
 */
public class CIVersion {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?\\b.*"); // NOI18N
    private final String branch;
    private final int major;
    private final int minor;
    private final int micro;

    public CIVersion(String branch, String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(version);
        }

        this.branch = branch;
        this.major = Integer.parseInt(matcher.group(1));
        this.minor = Integer.parseInt(matcher.group(2));

        if (matcher.group(4) != null) {
            this.micro = Integer.parseInt(matcher.group(4));
        } else {
            this.micro = -1;
        }
    }

    public String getBranch() {
        return branch;
    }
    
    public String getVersion() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append(".").append(minor); // NOI18N

        if (micro >= 0) {
            sb.append(".").append(micro); // NOI18N
        }

        return sb.toString();
    }

    public int getMajorVersion() {
        return major;
    }

    public int getMinorVersion() {
        return minor;
    }

    public int getMicroVersion() {
        return micro;
    }

    public @Override
    String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(branch).append(" ").append(getVersion());

        return sb.toString();
    }

    public @Override
    boolean equals(Object o) {
        if (!(o instanceof CIVersion)) {
            return false;
        }

        CIVersion oV = (CIVersion) o;

        return branch.equals(oV.branch) && major == oV.major && minor == oV.minor && micro == oV.micro;
    }

    public @Override
    int hashCode() {
        return toString().hashCode();
    }

    public int compareTo(CIVersion o) {
        if (this.equals(o)) {
            return 0;
        }

        return (major < o.major) ? -1
                : (major > o.major) ? 1
                : (minor < o.minor) ? -1
                : (minor > o.minor) ? 1
                : (micro < o.micro) ? -1
                : (micro > o.micro) ? 1 : 0;
    }
}
