/*
 * Autopsy Forensic Browser
 *
 * Copyright 2013-2018 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.imagegallery;

import java.util.logging.Level;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.IngestManager;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * The Image/Video Gallery panel in the NetBeans provided Options Dialogs
 * accessed via Tools -> Options
 *
 * Uses ImageGalleryPreferences and PerCaseProperties to persist
 * settings
 */
@SuppressWarnings("PMD.SingularField") // UI widgets cause lots of false positives
final class ImageGalleryOptionsPanel extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(ImageGalleryOptionsPanel.class.getName());

    ImageGalleryOptionsPanel(ImageGalleryOptionsPanelController controller) {
        initComponents();

        //listen for interactions
        IngestManager.getInstance().addIngestJobEventListener(evt -> {
            //disable during ingest
            enabledForCaseBox.setEnabled(Case.isCaseOpen() && IngestManager.getInstance().isIngestRunning() == false);
        });

        enabledByDefaultBox.addActionListener(actionEvent -> controller.changed());
        enabledForCaseBox.addActionListener(actionEvent -> controller.changed());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        enabledByDefaultBox = new javax.swing.JCheckBox();
        jSeparator1 = new javax.swing.JSeparator();
        infoIconLabel = new javax.swing.JLabel();
        enabledForCaseBox = new javax.swing.JCheckBox();
        unavailableDuringInjestLabel = new javax.swing.JLabel();
        groupCategorizationWarningBox = new javax.swing.JCheckBox();
        descriptionLabel = new javax.swing.JLabel();
        furtherDescriptionArea = new javax.swing.JTextArea();

        setFont(getFont().deriveFont(getFont().getStyle() & ~java.awt.Font.BOLD, 11));

        jScrollPane1.setBorder(null);

        enabledByDefaultBox.setFont(enabledByDefaultBox.getFont().deriveFont(enabledByDefaultBox.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        org.openide.awt.Mnemonics.setLocalizedText(enabledByDefaultBox, org.openide.util.NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.enabledByDefaultBox.text")); // NOI18N
        enabledByDefaultBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledByDefaultBoxActionPerformed(evt);
            }
        });

        infoIconLabel.setFont(infoIconLabel.getFont().deriveFont(infoIconLabel.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        infoIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/imagegallery/images/info-icon-16.png"))); // NOI18N

        enabledForCaseBox.setFont(enabledForCaseBox.getFont().deriveFont(enabledForCaseBox.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        org.openide.awt.Mnemonics.setLocalizedText(enabledForCaseBox, org.openide.util.NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.enabledForCaseBox.text")); // NOI18N
        enabledForCaseBox.setToolTipText(NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.enabledForCaseBox.toolTipText")); // NOI18N
        enabledForCaseBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledForCaseBoxActionPerformed(evt);
            }
        });

        unavailableDuringInjestLabel.setFont(unavailableDuringInjestLabel.getFont().deriveFont(unavailableDuringInjestLabel.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        unavailableDuringInjestLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/sleuthkit/autopsy/imagegallery/images/warning16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(unavailableDuringInjestLabel, NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.unavailableDuringInjestLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(groupCategorizationWarningBox, NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.groupCategorizationWarningBox.text")); // NOI18N

        descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(descriptionLabel.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        org.openide.awt.Mnemonics.setLocalizedText(descriptionLabel, org.openide.util.NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.descriptionLabel.text")); // NOI18N

        furtherDescriptionArea.setBackground(new java.awt.Color(240, 240, 240));
        furtherDescriptionArea.setColumns(20);
        furtherDescriptionArea.setFont(furtherDescriptionArea.getFont().deriveFont(furtherDescriptionArea.getFont().getStyle() & ~java.awt.Font.BOLD, 11));
        furtherDescriptionArea.setLineWrap(true);
        furtherDescriptionArea.setRows(5);
        furtherDescriptionArea.setText(NbBundle.getMessage(ImageGalleryOptionsPanel.class, "ImageGalleryOptionsPanel.furtherDescriptionArea.text")); // NOI18N
        furtherDescriptionArea.setWrapStyleWord(true);
        furtherDescriptionArea.setPreferredSize(new java.awt.Dimension(378, 74));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(unavailableDuringInjestLabel))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(enabledByDefaultBox)
                                .addComponent(enabledForCaseBox)
                                .addComponent(descriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(infoIconLabel)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(furtherDescriptionArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(groupCategorizationWarningBox))
                        .addGap(0, 36, Short.MAX_VALUE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(descriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enabledByDefaultBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(enabledForCaseBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unavailableDuringInjestLabel)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoIconLabel)
                    .addComponent(furtherDescriptionArea, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupCategorizationWarningBox)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void enabledByDefaultBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledByDefaultBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enabledByDefaultBoxActionPerformed

    private void enabledForCaseBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledForCaseBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enabledForCaseBoxActionPerformed

    void load() {
        enabledByDefaultBox.setSelected(ImageGalleryPreferences.isEnabledByDefault());
        try {
            if (IngestManager.getInstance().isIngestRunning() == false) {
                enabledForCaseBox.setEnabled(true);
                enabledForCaseBox.setSelected(ImageGalleryModule.isEnabledforCase(Case.getCurrentCaseThrows()));
            } else {
                enabledForCaseBox.setEnabled(false);
                enabledForCaseBox.setSelected(enabledByDefaultBox.isSelected());
            }
        } catch (NoCurrentCaseException ex) {
            enabledForCaseBox.setEnabled(false);
            enabledForCaseBox.setSelected(enabledByDefaultBox.isSelected());
        }
        groupCategorizationWarningBox.setSelected(ImageGalleryPreferences.isGroupCategorizationWarningDisabled());
    }

    void store() {
        ImageGalleryPreferences.setEnabledByDefault(enabledByDefaultBox.isSelected());

        ImageGalleryPreferences.setGroupCategorizationWarningDisabled(groupCategorizationWarningBox.isSelected());

        // If a case is open, save the per case setting
        try {
            Case openCase = Case.getCurrentCaseThrows();
            ImageGalleryModule.getController().setListeningEnabled(enabledForCaseBox.isSelected());
            new PerCaseProperties(openCase).setConfigSetting(ImageGalleryModule.getModuleName(), PerCaseProperties.ENABLED, Boolean.toString(enabledForCaseBox.isSelected()));
        } catch (NoCurrentCaseException ex) {
            // It's not an error if there's no case open
        } catch (TskCoreException ex) {
            logger.log(Level.SEVERE, "Failed to get image gallery controller", ex); //NON-NLS
        }

    }

    /**
     * @return true, since there is no way for this form to be invalid
     */
    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JCheckBox enabledByDefaultBox;
    private javax.swing.JCheckBox enabledForCaseBox;
    private javax.swing.JTextArea furtherDescriptionArea;
    private javax.swing.JCheckBox groupCategorizationWarningBox;
    private javax.swing.JLabel infoIconLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel unavailableDuringInjestLabel;
    // End of variables declaration//GEN-END:variables
}
