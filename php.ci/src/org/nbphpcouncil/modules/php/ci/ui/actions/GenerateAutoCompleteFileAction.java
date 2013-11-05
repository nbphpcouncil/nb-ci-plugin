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
package org.nbphpcouncil.modules.php.ci.ui.actions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.nbphpcouncil.modules.php.ci.support.CISupport;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.LifecycleManager;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Junji Takakura
 */
public class GenerateAutoCompleteFileAction extends BaseAction {

    private static final long serialVersionUID = -4701999225431103495L;
    private static final GenerateAutoCompleteFileAction INSTANCE = new GenerateAutoCompleteFileAction();
    private static final RequestProcessor RP = new RequestProcessor("Generate CodeIgniter auto complete file", 1); // NOI18N
    static final Queue<Runnable> RUNNABLES = new ConcurrentLinkedQueue<Runnable>();
    private static final RequestProcessor.Task TASK = RP.create(new Runnable() {
        @Override
        public void run() {
            Runnable toRun = RUNNABLES.poll();
            while (toRun != null) {
                toRun.run();
                toRun = RUNNABLES.poll();
            }
        }
    }, true);

    public static GenerateAutoCompleteFileAction getInstance() {
        return INSTANCE;
    }

    private GenerateAutoCompleteFileAction() {
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @NbBundle.Messages("LBL_CreateAutoCompleteFile=Generate Auto Complete File")
    @Override
    protected String getPureName() {
        return Bundle.LBL_CreateAutoCompleteFile();
    }

    @Override
    @NbBundle.Messages("LBL_CreateAutoCompleteFile_ProgressBar=Generating Auto Complete File")
    protected void actionPerformed(final PhpModule pm) {
        RUNNABLES.add(new Runnable() {
            @Override
            public void run() {
                ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.LBL_CreateAutoCompleteFile_ProgressBar());
                handle.start();
                try {
                    LifecycleManager.getDefault().saveAll();
                    CISupport.generateAutoCompletionFile(pm);
                } finally {
                    handle.finish();
                }
            }
        });
        TASK.schedule(0);
    }
}
