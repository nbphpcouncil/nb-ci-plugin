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
package org.nbphpcouncil.modules.php.ci.ui.customizer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author Junji Takakura
 */
public class CICustomizerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 6765682363554629423L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private final DefaultListModel customLibraryPathsModel;
    private final FileObject sourceDirectory;

    /**
     * Creates new form CICustomizerPanel
     */
    public CICustomizerPanel(FileObject sourceDirectory) {
        initComponents();

        this.sourceDirectory = sourceDirectory;
        this.enabledCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                fireChange();
                setFieldsEnabled(event.getStateChange() == ItemEvent.SELECTED);
            }
        });
        this.customLibraryPathsModel = new DefaultListModel();
        this.customLibraryPathsModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent event) {
                processChange();
            }

            @Override
            public void intervalRemoved(ListDataEvent event) {
                processChange();
            }

            @Override
            public void contentsChanged(ListDataEvent event) {
                processChange();
            }

            private void processChange() {
                fireChange();
            }
        });
        this.customLibraryPathsList.setModel(customLibraryPathsModel);
        setFieldsEnabled(enabledCheckBox.isSelected());
    }

    public FileObject getSourceDirectory() {
        return sourceDirectory;
    }

    public boolean isSupportEnabled() {
        return enabledCheckBox.isSelected();
    }

    public void setSupportEnabled(boolean enabled) {
        enabledCheckBox.setSelected(enabled);
    }

    public String[] getCustomLibraryPaths() {
        Object[] elements = customLibraryPathsModel.toArray();
        String[] paths = new String[elements.length];

        for (int i = 0; i < elements.length; i++) {
            paths[i] = (String) elements[i];
        }

        return paths;
    }

    public void setCustomLibraryPaths(String[] paths) {
        customLibraryPathsModel.clear();

        for (int i = 0; i < paths.length; i++) {
            addCustomLibraryPath(paths[i]);
        }
    }

    public void addCustomLibraryPath(String path) {
        customLibraryPathsModel.addElement(path);
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    final void setFieldsEnabled(boolean enabled) {
        customLibraryPathsList.setEnabled(enabled);
        addFolderButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enabledCheckBox = new javax.swing.JCheckBox();
        enabledInfoLabel = new javax.swing.JLabel();
        customLibraryPathsLabel = new javax.swing.JLabel();
        customLibraryPathsScrollPane = new javax.swing.JScrollPane();
        customLibraryPathsList = new javax.swing.JList();
        addFolderButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        customLibraryPathsInfoLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(enabledCheckBox, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.enabledCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(enabledInfoLabel, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.enabledInfoLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(customLibraryPathsLabel, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.customLibraryPathsLabel.text")); // NOI18N

        customLibraryPathsScrollPane.setViewportView(customLibraryPathsList);

        org.openide.awt.Mnemonics.setLocalizedText(addFolderButton, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.addFolderButton.text")); // NOI18N
        addFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFolderButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.removeButton.text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(customLibraryPathsInfoLabel, org.openide.util.NbBundle.getMessage(CICustomizerPanel.class, "CICustomizerPanel.customLibraryPathsInfoLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(customLibraryPathsScrollPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(addFolderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(enabledCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(enabledInfoLabel))
                            .addComponent(customLibraryPathsLabel)
                            .addComponent(customLibraryPathsInfoLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(enabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enabledInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customLibraryPathsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(customLibraryPathsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addFolderButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customLibraryPathsInfoLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("CICustomizerPanel.browseFolder.title=Select Folder(s)")
    private void addFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFolderButtonActionPerformed
        File[] folders = new FileChooserBuilder(CICustomizerPanel.class)
                .setTitle(Bundle.CICustomizerPanel_browseFolder_title())
                .setDirectoriesOnly(true)
                .setDefaultWorkingDirectory(FileUtil.toFile(sourceDirectory))
                .forceUseOfDefaultWorkingDirectory(true)
                .showMultiOpenDialog();

        if (folders != null) {
            for (File folder : folders) {
                folder = FileUtil.normalizeFile(folder);
                FileObject fo = FileUtil.toFileObject(folder);

                if (FileUtil.isParentOf(sourceDirectory, fo)) {
                    String relativePath = FileUtil.getRelativePath(sourceDirectory, fo);
                    assert relativePath != null : sourceDirectory + " not parent of " + fo;
                    customLibraryPathsModel.addElement(relativePath);
                } else {
                    customLibraryPathsModel.addElement(folder.getAbsolutePath());
                }
            }
        }
    }//GEN-LAST:event_addFolderButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int[] indexes = customLibraryPathsList.getSelectedIndices();

        for (int index : indexes) {
            customLibraryPathsModel.removeElementAt(index);
        }
    }//GEN-LAST:event_removeButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFolderButton;
    private javax.swing.JLabel customLibraryPathsInfoLabel;
    private javax.swing.JLabel customLibraryPathsLabel;
    private javax.swing.JList customLibraryPathsList;
    private javax.swing.JScrollPane customLibraryPathsScrollPane;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JLabel enabledInfoLabel;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
}
