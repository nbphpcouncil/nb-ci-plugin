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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.netbeans.modules.php.ci.CIPhpFramework;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * @author Junji Takakura
 */
public class CIZipInflater {
    
    private List<UnZipFilter> filters = new ArrayList<UnZipFilter>();
    
    public void unzip(final String path, final FileObject dest) throws IOException {
        String rootDirectory = "";
        ZipFile zip = new ZipFile(path);
        Enumeration<? extends ZipEntry> entries = zip.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            int index = entryName.lastIndexOf(CIPhpFramework.FILE_INDEX_PHP);

            if (index > -1) {
                rootDirectory = entryName.substring(0, index);
                break;
            }
        }

        ZipInputStream inputStream = null;
        ZipEntry entry = null;

        try {
            inputStream = new ZipInputStream(new FileInputStream(path));
            int index = rootDirectory.length();

            while ((entry = inputStream.getNextEntry()) != null) {
                if(!allow(entry)) {
                    continue;
                }
                
                String entryName = entry.getName();

                if (entryName.startsWith(rootDirectory)) {
                    entryName = entryName.substring(index);
                }

                if (entryName.length() > 0) {
                    if (entry.isDirectory()) {
                        FileUtil.createFolder(dest, entryName);
                    } else {
                        FileObject fo = FileUtil.createData(dest, entryName);
                        OutputStream outputStream = fo.getOutputStream();

                        try {
                            FileUtil.copy(inputStream, outputStream);
                        } finally {
                            outputStream.close();
                        }
                    }
                }
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public  List<UnZipFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<UnZipFilter> filters) {
        this.filters = filters;
    }
    
    public void clearFilters() {
        filters.clear();
    }
    
    public void addFilter(UnZipFilter filter) {
        filters.add(filter);
    }
    
    public void removeFilter(UnZipFilter filter) {
        filters.remove(filter);
    }
    
    private boolean allow(ZipEntry entry) {
        for(UnZipFilter filter : filters) {
            if(!filter.allow(entry)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static interface UnZipFilter {
        boolean allow(ZipEntry entry);
    }    
}
