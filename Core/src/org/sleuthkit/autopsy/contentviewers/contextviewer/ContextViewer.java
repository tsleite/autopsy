/*
 * Autopsy Forensic Browser
 *
 * Copyright 2019 Basis Technology Corp.
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
package org.sleuthkit.autopsy.contentviewers.contextviewer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataContentViewer;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.directorytree.DirectoryTreeTopComponent;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import static org.sleuthkit.datamodel.BlackboardArtifact.ARTIFACT_TYPE.TSK_ASSOCIATED_OBJECT;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Displays additional context for the selected file, such as its source, and
 * usage, if known.
 *
 */
@ServiceProvider(service = DataContentViewer.class, position = 7)
public final class ContextViewer extends javax.swing.JPanel implements DataContentViewer {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContextViewer.class.getName());
    private static final int ARTIFACT_STR_MAX_LEN = 1024;
    private static final int ATTRIBUTE_STR_MAX_LEN = 200;

    // defines a list of artifacts that provide context for a file
    private static final List<BlackboardArtifact.ARTIFACT_TYPE> SOURCE_CONTEXT_ARTIFACTS = new ArrayList<>();

    static {
        SOURCE_CONTEXT_ARTIFACTS.add(TSK_ASSOCIATED_OBJECT);
    }

    private BlackboardArtifact sourceContextArtifact;

    /**
     * Creates new form ContextViewer
     */
    public ContextViewer() {

        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSourceGoToResultButton = new javax.swing.JButton();
        jSourceLabel = new javax.swing.JLabel();
        jSourceNameLabel = new javax.swing.JLabel();
        jSourceTextLabel = new javax.swing.JLabel();
        jUsageGoToResultButton = new javax.swing.JButton();
        jUsageLabel = new javax.swing.JLabel();
        jUsageNameLabel = new javax.swing.JLabel();
        jUsageTextLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jSourceGoToResultButton, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jSourceGoToResultButton.text")); // NOI18N
        jSourceGoToResultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSourceGoToResultButtonActionPerformed(evt);
            }
        });

        jSourceLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jSourceLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jSourceLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jSourceNameLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jSourceNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jSourceTextLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jSourceTextLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jUsageGoToResultButton, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jUsageGoToResultButton.text")); // NOI18N
        jUsageGoToResultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUsageGoToResultButtonActionPerformed(evt);
            }
        });

        jUsageLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jUsageLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jUsageLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jUsageNameLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jUsageNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jUsageTextLabel, org.openide.util.NbBundle.getMessage(ContextViewer.class, "ContextViewer.jUsageTextLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSourceLabel)
                    .addComponent(jUsageLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSourceNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSourceTextLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jUsageNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jUsageTextLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)))))
                .addGap(36, 36, 36))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jUsageGoToResultButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jSourceGoToResultButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSourceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSourceNameLabel)
                    .addComponent(jSourceTextLabel))
                .addGap(18, 18, 18)
                .addComponent(jSourceGoToResultButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUsageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jUsageNameLabel)
                    .addComponent(jUsageTextLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jUsageGoToResultButton)
                .addGap(0, 62, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSourceGoToResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSourceGoToResultButtonActionPerformed

        final DirectoryTreeTopComponent dtc = DirectoryTreeTopComponent.findInstance();

        // Navigate to the source context artifact.
        if (sourceContextArtifact != null) {
            dtc.viewArtifact(sourceContextArtifact);
        }

    }//GEN-LAST:event_jSourceGoToResultButtonActionPerformed

    private void jUsageGoToResultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUsageGoToResultButtonActionPerformed
        final DirectoryTreeTopComponent dtc = DirectoryTreeTopComponent.findInstance();

        // Navigate to the source context artifact.
        if (sourceContextArtifact != null) {
            dtc.viewArtifact(sourceContextArtifact);
        }
    }//GEN-LAST:event_jUsageGoToResultButtonActionPerformed

    @Override
    public void setNode(Node selectedNode) {
        if ((selectedNode == null) || (!isSupported(selectedNode))) {
            resetComponent();
            return;
        }

        AbstractFile file = selectedNode.getLookup().lookup(AbstractFile.class);
        try {
            populateSourceContextData(file);
        } catch (NoCurrentCaseException | TskCoreException ex) {
            logger.log(Level.SEVERE, String.format("Exception displaying context for file %s", file.getName()), ex); //NON-NLS
        }
    }

    @NbBundle.Messages({
        "ContextViewer.title=Context",
        "ContextViewer.toolTip=Displays context for selected file."
    })

    @Override
    public String getTitle() {
        return Bundle.ContextViewer_title();
    }

    @Override
    public String getToolTip() {
        return Bundle.ContextViewer_toolTip();
    }

    @Override
    public DataContentViewer createInstance() {
        return new ContextViewer();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void resetComponent() {
        jSourceGoToResultButton.setVisible(false);
        setSourceName("");
        setSourceText("");
    }

    @Override
    public boolean isSupported(Node node) {

        // check if the node has an abstract file and the file has any context defining artifacts.
        if (node.getLookup().lookup(AbstractFile.class) != null) {
            AbstractFile abstractFile = node.getLookup().lookup(AbstractFile.class);
            for (BlackboardArtifact.ARTIFACT_TYPE artifactType : SOURCE_CONTEXT_ARTIFACTS) {
                List<BlackboardArtifact> artifactsList;
                try {
                    artifactsList = abstractFile.getArtifacts(artifactType);
                    if (!artifactsList.isEmpty()) {
                        return true;
                    }
                } catch (TskCoreException ex) {
                    logger.log(Level.SEVERE, String.format("Exception while looking up context artifacts for file %s", abstractFile), ex); //NON-NLS
                }
            }

        }

        return false;
    }

    @Override
    public int isPreferred(Node node) {
        // this is a low preference viewer.
        return 1;
    }

    /**
     * Looks for context providing artifacts for the given file and populates
     * the source context.
     *
     * @param sourceFile File for which to show the context.
     *
     * @throws NoCurrentCaseException
     * @throws TskCoreException
     */
    private void populateSourceContextData(AbstractFile sourceFile) throws NoCurrentCaseException, TskCoreException {

        SleuthkitCase tskCase = Case.getCurrentCaseThrows().getSleuthkitCase();

        // Check for all context artifacts
        boolean foundASource = false;
        for (BlackboardArtifact.ARTIFACT_TYPE artifactType : SOURCE_CONTEXT_ARTIFACTS) {
            List<BlackboardArtifact> artifactsList = tskCase.getBlackboardArtifacts(artifactType, sourceFile.getId());

            foundASource = !artifactsList.isEmpty();
            for (BlackboardArtifact contextArtifact : artifactsList) {
                addSourceEntry(contextArtifact);
            }
        }
        jSourceGoToResultButton.setVisible(true);
        jUsageGoToResultButton.setVisible(true);
        if (foundASource == false) {
            setSourceName("Unknown");
            showSourceText(false);
            setUsageName("Unknown");
            showUsageText(false);
        }

    }

    /**
     * Adds a source context entry for the selected file based on the given
     * context providing artifact.
     *
     * @param artifact Artifact that may provide context.
     *
     * @throws NoCurrentCaseException
     * @throws TskCoreException
     */
    private void addSourceEntry(BlackboardArtifact artifact) throws TskCoreException {
        setSourceName("Unknown");
        showSourceText(false);
        setUsageName("Unknown");
        showUsageText(false);

        if (BlackboardArtifact.ARTIFACT_TYPE.TSK_ASSOCIATED_OBJECT.getTypeID() == artifact.getArtifactTypeID()) {
            BlackboardAttribute associatedArtifactAttribute = artifact.getAttribute(new BlackboardAttribute.Type(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_ASSOCIATED_ARTIFACT));
            if (associatedArtifactAttribute != null) {
                long artifactId = associatedArtifactAttribute.getValueLong();
                BlackboardArtifact associatedArtifact = artifact.getSleuthkitCase().getBlackboardArtifact(artifactId);

                //save the artifact for "Go to Result" button
                sourceContextArtifact = associatedArtifact;

                setSourceFields(associatedArtifact);
            }
        }
    }

    /**
     * Sets the source label and text fields based on the given associated
     * artifact.
     *
     * @param associatedArtifact - associated artifact
     *
     * @throws TskCoreException
     */
    @NbBundle.Messages({
        "ContextViewer.attachmentSource=Attached to: ",
        "ContextViewer.downloadSource=Downloaded from: ",
        "ContextViewer.recentDocs=Recent Documents: "
    })
    private void setSourceFields(BlackboardArtifact associatedArtifact) throws TskCoreException {
        if (BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE.getTypeID() == associatedArtifact.getArtifactTypeID()
                || BlackboardArtifact.ARTIFACT_TYPE.TSK_EMAIL_MSG.getTypeID() == associatedArtifact.getArtifactTypeID()) {

            setSourceName(Bundle.ContextViewer_attachmentSource());
            setSourceText(msgArtifactToAbbreviatedString(associatedArtifact));

        } else if (BlackboardArtifact.ARTIFACT_TYPE.TSK_WEB_DOWNLOAD.getTypeID() == associatedArtifact.getArtifactTypeID()
                || BlackboardArtifact.ARTIFACT_TYPE.TSK_WEB_CACHE.getTypeID() == associatedArtifact.getArtifactTypeID()) {

            setSourceName(Bundle.ContextViewer_downloadSource());
            setSourceText(webDownloadArtifactToString(associatedArtifact));
        } else if (BlackboardArtifact.ARTIFACT_TYPE.TSK_RECENT_OBJECT.getTypeID() == associatedArtifact.getArtifactTypeID()) {
            setUsageName(Bundle.ContextViewer_recentDocs());
            setUsageText(recentDocArtifactToString(associatedArtifact));
            
        }
    }

    /**
     * Sets the source label string.
     *
     * @param nameLabel String value for source label.
     */
    private void setSourceName(String nameLabel) {
        jSourceNameLabel.setText(nameLabel);
    }

    /**
     * Sets the usage label string.
     *
     * @param nameLabel String value for usage label.
     */
    private void setUsageName(String nameLabel) {
        jUsageNameLabel.setText(nameLabel);
    }

    /**
     * Sets the source text string.
     *
     * @param text String value for source text.
     */
    private void setSourceText(String text) {
        jSourceTextLabel.setText(text);
        showSourceText(!text.isEmpty());
    }

    private void showSourceText(boolean show) {
        jSourceTextLabel.setVisible(show);
        jSourceGoToResultButton.setEnabled(show);
        jSourceLabel.setVisible(show);
        jUsageLabel.setVisible(show);
    }

    /**
     * Sets the Usage text string.
     *
     * @param text String value for Usage text.
     */
    private void setUsageText(String text) {
        jUsageTextLabel.setText(text);
        showUsageText(!text.isEmpty());
    }

    private void showUsageText(boolean show) {
        jUsageTextLabel.setVisible(show);
        jUsageGoToResultButton.setEnabled(show);
        jUsageLabel.setVisible(show);
        jSourceLabel.setVisible(show);
    }

    /**
     * Returns a display string with download source URL from the given
     * artifact.
     *
     * @param artifact artifact to get download source URL from.
     *
     * @return Display string with download URL and date/time.
     *
     * @throws TskCoreException
     */
    @NbBundle.Messages({
        "ContextViewer.downloadURL=URL",
        "ContextViewer.downloadedOn=On"
    })
    private String webDownloadArtifactToString(BlackboardArtifact artifact) throws TskCoreException {
        StringBuilder sb = new StringBuilder(ARTIFACT_STR_MAX_LEN);
        Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> attributesMap = getAttributesMap(artifact);

        if (BlackboardArtifact.ARTIFACT_TYPE.TSK_WEB_DOWNLOAD.getTypeID() == artifact.getArtifactTypeID()
                || BlackboardArtifact.ARTIFACT_TYPE.TSK_WEB_CACHE.getTypeID() == artifact.getArtifactTypeID()) {
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_URL, attributesMap, Bundle.ContextViewer_downloadURL());
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME_CREATED, attributesMap, Bundle.ContextViewer_downloadedOn());
        }
        return sb.toString();
    }

    /**
     * Returns a display string with recent Doc
     * artifact.
     *
     * @param artifact artifact to get doc from.
     *
     * @return Display string with download URL and date/time.
     *
     * @throws TskCoreException
     */
    @NbBundle.Messages({
        "ContextViewer.on=Opened at",
        "ContextViewer.unknown=Opened at unknown time"
    })
    private String recentDocArtifactToString(BlackboardArtifact artifact) throws TskCoreException {
        StringBuilder sb = new StringBuilder(ARTIFACT_STR_MAX_LEN);
        Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> attributesMap = getAttributesMap(artifact);
        
        BlackboardAttribute attribute = attributesMap.get(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME);
        
        if (BlackboardArtifact.ARTIFACT_TYPE.TSK_RECENT_OBJECT.getTypeID() == artifact.getArtifactTypeID()) {
            if (attribute.getValueLong() > 0) {
                appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME, attributesMap, Bundle.ContextViewer_on());
            } else {
                sb.append(Bundle.ContextViewer_unknown());
            }
        }
        return sb.toString();
    }

    /**
     * Returns a abbreviated display string for a message artifact.
     *
     * @param artifact artifact to get download source URL from.
     *
     * @return Display string for message artifact.
     *
     * @throws TskCoreException
     */
    @NbBundle.Messages({
        "ContextViewer.message=Message",
        "ContextViewer.email=Email",
        "ContextViewer.messageFrom=From",
        "ContextViewer.messageTo=To",
        "ContextViewer.messageOn=On",})
    private String msgArtifactToAbbreviatedString(BlackboardArtifact artifact) throws TskCoreException {

        StringBuilder sb = new StringBuilder(ARTIFACT_STR_MAX_LEN);
        Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> attributesMap = getAttributesMap(artifact);

        if (BlackboardArtifact.ARTIFACT_TYPE.TSK_MESSAGE.getTypeID() == artifact.getArtifactTypeID()) {
            sb.append(Bundle.ContextViewer_message()).append(' ');
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_PHONE_NUMBER_FROM, attributesMap, Bundle.ContextViewer_messageFrom());
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_PHONE_NUMBER_TO, attributesMap, Bundle.ContextViewer_messageTo());
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME, attributesMap, Bundle.ContextViewer_messageOn());
        } else if (BlackboardArtifact.ARTIFACT_TYPE.TSK_EMAIL_MSG.getTypeID() == artifact.getArtifactTypeID()) {
            sb.append(Bundle.ContextViewer_email()).append(' ');
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_EMAIL_FROM, attributesMap, Bundle.ContextViewer_messageFrom());
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_EMAIL_TO, attributesMap, Bundle.ContextViewer_messageTo());
            appendAttributeString(sb, BlackboardAttribute.ATTRIBUTE_TYPE.TSK_DATETIME_SENT, attributesMap, Bundle.ContextViewer_messageOn());
        }
        return sb.toString();
    }

    /**
     * Looks up specified attribute in the given map and, if found, appends its
     * value to the given string builder.
     *
     * @param sb            String builder to append to.
     * @param attribType    Attribute type to look for.
     * @param attributesMap Attributes map.
     * @param prependStr    Optional string that is prepended before the
     *                      attribute value.
     */
    private void appendAttributeString(StringBuilder sb, BlackboardAttribute.ATTRIBUTE_TYPE attribType,
            Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> attributesMap, String prependStr) {

        BlackboardAttribute attribute = attributesMap.get(attribType);
        if (attribute != null) {
            String attrVal = attribute.getDisplayString();
            if (!StringUtils.isEmpty(attrVal)) {
                if (!StringUtils.isEmpty(prependStr)) {
                    sb.append(prependStr).append(' ');
                }
                sb.append(StringUtils.abbreviate(attrVal, ATTRIBUTE_STR_MAX_LEN)).append(' ');
            }
        }
    }

    /**
     * Gets all attributes for the given artifact, and returns a map of
     * attributes keyed by attribute type.
     *
     * @param artifact Artifact for which to get the attributes.
     *
     * @return Map of attribute type and value.
     *
     * @throws TskCoreException
     */
    private Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> getAttributesMap(BlackboardArtifact artifact) throws TskCoreException {
        Map<BlackboardAttribute.ATTRIBUTE_TYPE, BlackboardAttribute> attributeMap = new HashMap<>();

        List<BlackboardAttribute> attributeList = artifact.getAttributes();
        for (BlackboardAttribute attribute : attributeList) {
            BlackboardAttribute.ATTRIBUTE_TYPE type = BlackboardAttribute.ATTRIBUTE_TYPE.fromID(attribute.getAttributeType().getTypeID());
            attributeMap.put(type, attribute);
        }

        return attributeMap;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jSourceGoToResultButton;
    private javax.swing.JLabel jSourceLabel;
    private javax.swing.JLabel jSourceNameLabel;
    private javax.swing.JLabel jSourceTextLabel;
    private javax.swing.JButton jUsageGoToResultButton;
    private javax.swing.JLabel jUsageLabel;
    private javax.swing.JLabel jUsageNameLabel;
    private javax.swing.JLabel jUsageTextLabel;
    // End of variables declaration//GEN-END:variables
}
