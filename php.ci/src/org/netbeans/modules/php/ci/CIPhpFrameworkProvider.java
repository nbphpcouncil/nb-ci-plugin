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
package org.netbeans.modules.php.ci;

import org.netbeans.modules.php.ci.ui.actions.CIPhpModuleActionsExtender;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.ci.editor.CIEditorExtender;
import org.netbeans.modules.php.ci.preferences.CIPreferences;
import org.netbeans.modules.php.ci.ui.customizer.CIPhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Junji Takakura
 */
public class CIPhpFrameworkProvider extends PhpFrameworkProvider {

    private static final String ICON_PATH = "org/netbeans/modules/php/ci/ui/resources/ci_badge_8.png"; // NOI18N
    private static final CIPhpFrameworkProvider INSTANCE = new CIPhpFrameworkProvider();
    private final BadgeIcon badgeIcon;

    @PhpFrameworkProvider.Registration(position = 400)
    public static CIPhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    public static FileObject locate(PhpModule pm, String relativePath, boolean subdirs) {
        FileObject sourceDirectory = pm.getSourceDirectory();

        FileObject fileObject = sourceDirectory.getFileObject(relativePath);

        if (fileObject != null || !subdirs) {
            return fileObject;
        }

        for (FileObject child : sourceDirectory.getChildren()) {
            fileObject = child.getFileObject(relativePath);

            if (fileObject != null) {
                return fileObject;
            }
        }

        return null;
    }

    @NbBundle.Messages({
        "LBL_FrameworkName=CodeIgniter PHP Web Framework",
        "LBL_FrameworkDescription=CodeIgniter PHP Web Framework"
    })
    public CIPhpFrameworkProvider() {
        super("CodeIgniter PHP Web Framework", Bundle.LBL_FrameworkName(), Bundle.LBL_FrameworkDescription()); // NOI18N
        badgeIcon = new BadgeIcon(ImageUtilities.loadImage(ICON_PATH), CIPhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @Override
    public boolean isInPhpModule(PhpModule pm) {
        return CIPreferences.isEnabled(pm);
    }

    @Override
    public File[] getConfigurationFiles(PhpModule pm) {
        List<File> files = new LinkedList<File>();

        FileObject appConfig = pm.getSourceDirectory().getFileObject(CIPhpFramework.getApplicationConfigDirectoryPath()); // NOI18N

        if (appConfig != null) {
            for (FileObject child : appConfig.getChildren()) {
                if (child.isData()) {
                    files.add(FileUtil.toFile(child));
                }
            }

            Collections.sort(files);
        }

        return files.toArray(new File[files.size()]);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule pm) {
        return new CIPhpModuleExtender();
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule pm) {
        FileObject sourceDirectory = pm.getSourceDirectory();
        PhpModuleProperties properties = new PhpModuleProperties();

        FileObject application = sourceDirectory.getFileObject(CIPhpFramework.DIRECTORY_APPLICATION); // NOI18N

        if (application != null) {
            properties = properties.setWebRoot(application);
        }

        // TODO

        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule pm) {
        return new CIPhpModuleActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule pm) {
        return new CIPhpModuleIgnoredFilesExtender(pm);
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule pm) {
        return null;
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule pm) {
        return new CIEditorExtender();
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule pm) {
        return new CIPhpModuleCustomizerExtender(pm);
    }
}
