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
package org.nbphpcouncil.modules.php.ci.repository;

import java.util.HashSet;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Junji Takakura
 */
public class CIRepositories {

    public static final String REPOSITORY_CI = "com-codeigniter"; // NOI18N
    public static final String REPOSITORY_ELLISLAB_CI = "com-ellislab-codeigniter"; // NOI18N
    public static final String REPOSITORY_BITBUCKET = "org-bitbucket-ellislab"; // NOI18N
    public static final String REPOSITORY_GITHUB = "com-github-ellislab"; // NOI18N
    public static final String REPOSITORY_JP_SF_CI = "jp-sourceforge-projects-codeigniter"; // NOI18N
    public static final String REACTOR = "Reactor"; // NOI18N
    public static final String CORE = "Core"; // NOI18N
    private static final String ROOT_PATH = "org-nbphpcouncil-modules-php-ci"; // NOI18N
    private static final String REPOSITORY_CI_REACTOR_URL = "http://downloads.codeigniter.com/reactor/"; // NOI18N
    private static final String REPOSITORY_ELLISLAB_CI_REACTOR_URL = "http://ellislab.com/codeigniter/"; // NOI18N
    private static final String REPOSITORY_BITBUCKET_REACTOR_URL = "https://bitbucket.org/ellislab/"; // NOI18N
    private static final String REPOSITORY_BITBUCKET_CORE_URL = "https://bitbucket.org/ellislab/"; // NOI18N
    private static final String REPOSITORY_GITHUB_REACTOR_URL = "https://github.com/EllisLab/CodeIgniter/"; // NOI18N
    private static final String REPOSITORY_JP_SF_CI_REACTOR_URL = "http://iij.dl.sourceforge.jp/codeigniter/"; // NOI18N
    private static final CIRepositories INSTANCE = new CIRepositories();
    private final CIRepository codeIgniterReactor;
    private final CIRepository ellislabReactor;
    private final CIRepository bitbucketReactor;
    private final CIRepository bitbucketCore;
    private final CIRepository githubReactor;
    private final CIRepository jpSFCIReactor;
    private final CIRepository[] repositories;

    public static CIRepositories getInstance() {
        return INSTANCE;
    }

    private CIRepositories() {
        codeIgniterReactor = new CIRepository(REPOSITORY_CI, REACTOR, REPOSITORY_CI_REACTOR_URL);
        ellislabReactor = new CIRepository(REPOSITORY_ELLISLAB_CI, REACTOR, REPOSITORY_ELLISLAB_CI_REACTOR_URL);
        bitbucketReactor = new CIRepository(REPOSITORY_BITBUCKET, REACTOR, REPOSITORY_BITBUCKET_REACTOR_URL);
        bitbucketCore = new CIRepository(REPOSITORY_BITBUCKET, CORE, REPOSITORY_BITBUCKET_CORE_URL);
        githubReactor = new CIRepository(REPOSITORY_GITHUB, REACTOR, REPOSITORY_GITHUB_REACTOR_URL);
        jpSFCIReactor = new CIRepository(REPOSITORY_JP_SF_CI, REACTOR, REPOSITORY_JP_SF_CI_REACTOR_URL);
        repositories = new CIRepository[]{codeIgniterReactor, ellislabReactor, bitbucketReactor, bitbucketCore, githubReactor, jpSFCIReactor};
    }

    public Set<CIFile> getFiles(String repository, String branch) {
        Set<CIFile> files = null;

        if (REPOSITORY_CI.equals(repository)) {
            if (REACTOR.equals(branch)) {
                files = codeIgniterReactor.getFiles();
            }
        } else if (REPOSITORY_ELLISLAB_CI.equals(repository)) {
            if (REACTOR.equals(branch)) {
                files = ellislabReactor.getFiles();
            }
        } else if (REPOSITORY_BITBUCKET.equals(repository)) {
            if (REACTOR.equals(branch)) {
                files = bitbucketReactor.getFiles();
            } else if (CORE.equals(branch)) {
                files = bitbucketCore.getFiles();
            }
        } else if (REPOSITORY_GITHUB.equals(repository)) {
            if (REACTOR.equals(branch)) {
                files = githubReactor.getFiles();
            }
        } else if (REPOSITORY_JP_SF_CI.equals(repository)) {
            if (REACTOR.equals(branch)) {
                files = jpSFCIReactor.getFiles();
            }
        }

        return files;
    }

    public Set<CIFile> getAllFiles() {
        Set<CIFile> files = new HashSet<CIFile>();

        for (CIRepository repository : repositories) {
            files.addAll(repository.getFiles());
        }

        return files;
    }

    private class CIRepository {

        private static final String ATTRIBUTE_FILE = "file"; // NOI18N
        private static final String ATTRIBUTE_VERSION = "version"; // NOI18N
        private static final String ATTRIBUTE_URL = "url"; // NOI18N
        private static final String ATTRIBUTE_MD5SUM = "md5sum"; // NOI18N
        private static final String ATTRIBUTE_SHA1SUM = "sha1sum"; // NOI18N
        private final String name;
        private final String branch;
        private final String url;

        public CIRepository(String name, String branch, String url) {
            assert name != null && branch != null && url != null;

            this.name = name;
            this.branch = branch;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getBranch() {
            return branch;
        }

        public String getUrl() {
            return url;
        }

        public Set<CIFile> getFiles() {
            Set<CIFile> files = new HashSet<CIFile>();
            StringBuilder sb = new StringBuilder(ROOT_PATH);
            sb.append("/").append(name);
            sb.append("/").append(branch.toLowerCase());

            FileObject parent = FileUtil.getConfigFile(sb.toString());

            for (FileObject child : parent.getChildren()) {
                String file = (String) child.getAttribute(ATTRIBUTE_FILE);
                String version = (String) child.getAttribute(ATTRIBUTE_VERSION);
                String fileUrl = (String) child.getAttribute(ATTRIBUTE_URL);
                String md5Sum = (String) child.getAttribute(ATTRIBUTE_MD5SUM);
                String sha1Sum = (String) child.getAttribute(ATTRIBUTE_SHA1SUM);

                files.add(new CIFileImpl(file, branch, version, fileUrl, md5Sum, sha1Sum));
            }

            return files;
        }
    }

    private class CIFileImpl implements CIFile {

        private final String file;
        private final String branch;
        private final String version;
        private final String url;
        private final String md5sum;
        private final String sha1sum;

        public CIFileImpl(String file, String branch, String version, String url, String md5sum, String sha1sum) {
            this.file = file;
            this.branch = branch;
            this.version = version;
            this.url = url;
            this.md5sum = md5sum;
            this.sha1sum = sha1sum;
        }

        @Override
        public String getFile() {
            return file;
        }

        @Override
        public String getBranch() {
            return branch;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getMd5Sum() {
            return md5sum;
        }

        @Override
        public String getSha1Sum() {
            return sha1sum;
        }
    }
}
