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
package org.nbphpcouncil.modules.php.ci.ui.options;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

/**
 *
 * @author Junji Takakura
 */
public class CIOptions {

    private static final String PREFERENCES_PATH = "php.ci"; // NOI18N
    private static final String BASE_FILE_ENTRIES_PATH = "base.file.entries"; // NOI18N
    private static final String LANGUAGE_PACK_FILE_ENTRIES_PATH = "language.pack.file.entries"; // NOI18N
    // Entry Keys
    private static final String KEY_ENTRY_PATH = "entry.path"; // NOI18N
    private static final String KEY_ENTRY_BRANCH = "entry.barnch"; // NOI18N
    private static final String KEY_ENTRY_VERSION = "entry.version"; // NOI18N
    private static final CIOptions INSTANCE = new CIOptions();
    private final ChangeSupport cs = new ChangeSupport(this);

    public static CIOptions getInstance() {
        return INSTANCE;
    }

    private CIOptions() {
        getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {

            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                cs.fireChange();
            }
        });
        getPreferences().addNodeChangeListener(new NodeChangeListener() {

            @Override
            public void childAdded(NodeChangeEvent evt) {
                cs.fireChange();
            }

            @Override
            public void childRemoved(NodeChangeEvent evt) {
                cs.fireChange();
            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    public List<CIEntry> getEntries(final CIEntry.Type type) {
        List<CIEntry> entries = new LinkedList<CIEntry>();

        try {
            String nodePath = getNodePath(type);

            if (getPreferences().nodeExists(nodePath)) {
                Preferences parent = getPreferences().node(nodePath);
                String[] childrenNames = parent.childrenNames();

                for (String childName : childrenNames) {
                    Preferences child = parent.node(childName);

                    String path = child.get(KEY_ENTRY_PATH, null);
                    String branch = child.get(KEY_ENTRY_BRANCH, null);
                    String version = child.get(KEY_ENTRY_VERSION, null);

                    entries.add(new CIEntry(type, childName, path, branch, version));
                }
            }
        } catch (BackingStoreException ex) {
            // TODO needs to implment it.
            Exceptions.printStackTrace(ex);
        }

        return entries;
    }

    public void putEntries(final CIEntry.Type type, List<CIEntry> entries) {
        Preferences parent = getPreferences().node(getNodePath(type));

        for (CIEntry entry : entries) {
            Preferences child = parent.node(entry.getName());
            child.put(KEY_ENTRY_PATH, entry.getPath());
            child.put(KEY_ENTRY_BRANCH, entry.getBranch());
            child.put(KEY_ENTRY_VERSION, entry.getVersion());
        }
    }

    public void clearEntries(final CIEntry.Type type) {
        try {
            getPreferences().node(getNodePath(type)).removeNode();
        } catch (BackingStoreException ex) {
            // TODO needs to implment it.
            Exceptions.printStackTrace(ex);
        }
    }

    private String getNodePath(final CIEntry.Type type) {
        assert type != null;

        String nodePath = null;

        if (type == CIEntry.Type.BASE) {
            nodePath = BASE_FILE_ENTRIES_PATH;
        } else if (type == CIEntry.Type.LANGUAGE_PACK) {
            nodePath = LANGUAGE_PACK_FILE_ENTRIES_PATH;
        }

        return nodePath;
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(CIOptions.class).node(PREFERENCES_PATH);
    }
}
