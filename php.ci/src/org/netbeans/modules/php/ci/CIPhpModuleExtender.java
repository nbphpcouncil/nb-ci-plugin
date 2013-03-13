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

import org.netbeans.modules.php.ci.support.CISupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpInterpreter;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpProgram.InvalidPhpProgramException;
import org.netbeans.modules.php.ci.preferences.CIPreferences;
import org.netbeans.modules.php.ci.support.CIZipInflater;
import org.netbeans.modules.php.ci.support.CIZipInflater.UnZipFilter;
import org.netbeans.modules.php.ci.support.PhpProjectSupport;
import org.netbeans.modules.php.ci.ui.wizards.NewProjectConfigurationPanel;
import org.netbeans.modules.php.spi.phpmodule.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Junji Takakura
 */
public class CIPhpModuleExtender extends PhpModuleExtender {

    private static final Logger LOGGER = Logger.getLogger(CIPhpModuleExtender.class.getName());
    private NewProjectConfigurationPanel panel;

    @Override
    public void addChangeListener(ChangeListener cl) {
        getPanel().addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        getPanel().addChangeListener(cl);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return getErrorMessage() == null;
    }

    @Override
    public String getErrorMessage() {
        String error = null;
        
        try {
            PhpInterpreter.getDefault();
        } catch (InvalidPhpProgramException ex) {
            error = ex.getLocalizedMessage();
        }
        
        if(error == null) {
            String message = getPanel().getErrorMessage();
            
            if(message != null && message.trim().length() > 0) {
                error = getMessage("MSG_CannotExtend", message); // NOI18N
            }
        }
        
        return error;
    }

    @Override
    public String getWarningMessage() {
        return getPanel().getWarningMessage();
    }

    @Override
    public Set<FileObject> extend(PhpModule pm) throws ExtendingException {
        Set<FileObject> files = new HashSet<FileObject>();

        try {
            CIZipInflater inflater = new CIZipInflater();
            inflater.setFilters(getFilters());
            inflater.unzip(getBaseFilePath(), pm.getSourceDirectory());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            LOGGER.log(Level.INFO, "CI Archive File not found in newly created project {0}", pm.getDisplayName());
            throw new ExtendingException(getMessage("MSG_NotExtended")); // NOI18N
        }

        FileObject config = CIPhpFrameworkProvider.locate(pm, CIPhpFramework.getApplicationConfigFilePath(), true);

        if (config != null) {
            files.add(config);
        }

        if (files.isEmpty()) {
            FileObject index = CIPhpFrameworkProvider.locate(pm, CIPhpFramework.FILE_INDEX_PHP, true);

            if (index != null) {
                files.add(index);
            }
        }
        
        CISupport.generateAutoCompletionFile(pm);
        CIPreferences.setEnabled(pm, true);
        
        return files;
    }

    private synchronized NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }

        return panel;
    }

    private String getBaseFilePath() {
        return panel.getBaseFileEntry().getPath();
    }

    private List<UnZipFilter> getFilters() {
        List<UnZipFilter> filters = new ArrayList<UnZipFilter>();

        if (getPanel().isExcludingUserGuideFilesSelected()) {
            filters.add(new UserGuideFilter());
        }

        return filters;
    }
    
    private String getMessage(String resourceName, Object... parameters) {
        return NbBundle.getMessage(CIPhpModuleExtender.class, resourceName, parameters);
    }

    private class UserGuideFilter implements UnZipFilter {

        private final String REGEX = "^[^/]+/" + CIPhpFramework.DIRECTORY_USER_GIDE + "/.*$";

        @Override
        public boolean allow(ZipEntry entry) {
            return !entry.getName().matches(REGEX);
        }
    }
}
