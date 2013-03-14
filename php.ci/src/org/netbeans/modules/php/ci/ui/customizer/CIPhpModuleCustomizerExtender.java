/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.netbeans.modules.php.ci.ui.customizer;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.ci.preferences.CIPreferences;
import org.netbeans.modules.php.ci.support.CISupport;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender.Change;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author Junji Takakura
 */
public class CIPhpModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private CICustomizerPanel component;
    private boolean valid = false;
    private String errorMessage = null;

    public CIPhpModuleCustomizerExtender(PhpModule pm) {
        component = new CICustomizerPanel(pm.getSourceDirectory());
        component.setSupportEnabled(CIPreferences.isEnabled(pm));
        component.setCustomLibraryPaths(CIPreferences.getCustomLibraryPaths(pm));
    }

    @NbBundle.Messages("CIPhpModuleCustomizerExtender.CodeIgniter=CodeIgniter")
    @Override
    public String getDisplayName() {
        return Bundle.CIPhpModuleCustomizerExtender_CodeIgniter();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        getPanel().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getPanel().removeChangeListener(listener);
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
        validate();
        return valid;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    @Override
    public EnumSet<Change> save(PhpModule pm) {
        EnumSet<Change> changes = EnumSet.noneOf(Change.class);
        CICustomizerPanel panel = getPanel();

        boolean currentEnabled = CIPreferences.isEnabled(pm);
        boolean newEnabled = panel.isSupportEnabled();

        if (newEnabled != currentEnabled) {
            CIPreferences.setEnabled(pm, newEnabled);
            changes.add(Change.FRAMEWORK_CHANGE);
        }

        String[] currentPaths = CIPreferences.getCustomLibraryPaths(pm);
        String[] newPaths = panel.getCustomLibraryPaths();

        if (!Arrays.equals(currentPaths, newPaths)) {
            CIPreferences.setCustomLibraryPaths(pm, newPaths);
            changes.add(Change.FRAMEWORK_CHANGE);
        }
        
        if(changes.isEmpty()) {
            return null;
        } else {
            if(newEnabled) {
                CISupport.generateAutoCompletionFile(pm);
            } else {
                CISupport.removeAutoCompletionFile(pm);
            }
            
            return changes;
        }
    }

    private CICustomizerPanel getPanel() {
        return component;
    }

    @NbBundle.Messages("CIPhpModuleCustomizerExtender.validate.invalidCustomLibraryPathAdded=Invalid custom library path is added.")
    private void validate() {
        CICustomizerPanel panel = getPanel();

        if (!panel.isSupportEnabled()) {
            // nothing to validate
            valid = true;
            errorMessage = null;
            return;
        }

        String parentPath = panel.getSourceDirectory().getPath();
        String paths[] = panel.getCustomLibraryPaths();

        for (String path : paths) {
            File f = new File(path);
            
            if(!f.isAbsolute()) {
                f = new File(parentPath, path);
            }

            if (!f.exists() || !f.isDirectory()) {
                valid = false;
                errorMessage = Bundle.CIPhpModuleCustomizerExtender_validate_invalidCustomLibraryPathAdded();
                return;
            }
        }

        // everything ok
        valid = true;
        errorMessage = null;
    }
}
