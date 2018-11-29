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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import javafx.application.Platform;
import javax.annotation.concurrent.GuardedBy;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.casemodule.NoCurrentCaseException;
import org.sleuthkit.autopsy.casemodule.events.ContentTagAddedEvent;
import org.sleuthkit.autopsy.casemodule.events.ContentTagDeletedEvent;
import org.sleuthkit.autopsy.core.RuntimeProperties;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.events.AutopsyEvent;
import org.sleuthkit.autopsy.imagegallery.datamodel.DrawableDB;
import org.sleuthkit.autopsy.ingest.IngestManager;
import static org.sleuthkit.autopsy.ingest.IngestManager.IngestModuleEvent.DATA_ADDED;
import static org.sleuthkit.autopsy.ingest.IngestManager.IngestModuleEvent.FILE_DONE;
import org.sleuthkit.autopsy.ingest.ModuleDataEvent;
import org.sleuthkit.autopsy.ingest.events.DataSourceAnalysisCompletedEvent;
import org.sleuthkit.autopsy.ingest.events.DataSourceAnalysisStartedEvent;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardArtifact.ARTIFACT_TYPE;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;

/**
 * This class is reponsible for handling selected application events for the
 * image gallery module, managing the image gallery module's per case MVC
 * controller and keeping track of the following state: the module name, the
 * module output directory and whether or not the ingest gallery module is
 * enabled for the current case.
 */
@NbBundle.Messages({"ImageGalleryModule.moduleName=Image Gallery"})
public class ImageGalleryModule {

    private static final Logger logger = Logger.getLogger(ImageGalleryModule.class.getName());
    private static final String MODULE_NAME = Bundle.ImageGalleryModule_moduleName();
    private static final Set<Case.Events> CASE_EVENTS_OF_INTEREST = EnumSet.of(
            Case.Events.CURRENT_CASE,
            Case.Events.DATA_SOURCE_ADDED,
            Case.Events.CONTENT_TAG_ADDED,
            Case.Events.CONTENT_TAG_DELETED
    );
    private static final Object controllerLock = new Object();
    @GuardedBy("controllerLock")
    private static ImageGalleryController controller;

    /**
     * Gets the per case image gallery controller for the current case. The
     * controller is changed in the case event listener.
     *
     * @return The image gallery controller for the current case.
     *
     * @throws TskCoreException If there is a problem creating the controller.
     */
    public static ImageGalleryController getController() throws TskCoreException {
        synchronized (controllerLock) {
            if (controller == null) {
                try {
                    Case currentCase = Case.getCurrentCaseThrows();
                    controller = new ImageGalleryController(currentCase);
                } catch (NoCurrentCaseException ex) {
                    throw new TskCoreException("no current case", ex);
                }
            }
            return controller;
        }
    }

    /**
     * Sets the implicit exit property attribute of the JavaFX Runtime to false
     * and sets up listeners for application events. It is invoked at
     * application start up by virtue of the OnStart annotation on the OnStart
     * class in this package.
     */
    static void onStart() {
        Platform.setImplicitExit(false);
        IngestManager.getInstance().addIngestJobEventListener(new IngestJobEventListener());
        IngestManager.getInstance().addIngestModuleEventListener(new IngestModuleEventListener());
        Case.addEventTypeSubscriber(CASE_EVENTS_OF_INTEREST, new CaseEventListener());
    }

    /**
     * Gets the image gallery module name.
     *
     * @return The module name,
     */
    static String getModuleName() {
        return MODULE_NAME;
    }

    /**
     * Gets the path to the image gallery module output folder for a given case.
     *
     * @param theCase The case.
     *
     * @return The path to the image gallery module output folder for the case.
     */
    public static Path getModuleOutputDir(Case theCase) {
        return Paths.get(theCase.getModuleDirectory(), getModuleName());
    }

    /**
     * Prevents instantiation.
     */
    private ImageGalleryModule() {
    }

    /**
     * Indicates whether or not the image gallery module is enabled for a given
     * case.
     *
     * @param theCase The case.
     *
     * @return True or false.
     */
    static boolean isEnabledforCase(Case theCase) {
        if (theCase != null) {
            String enabledforCaseProp = new PerCaseProperties(theCase).getConfigSetting(ImageGalleryModule.MODULE_NAME, PerCaseProperties.ENABLED);
            return isNotBlank(enabledforCaseProp) ? Boolean.valueOf(enabledforCaseProp) : ImageGalleryPreferences.isEnabledByDefault();
        } else {
            return false;
        }
    }

    /**
     * Indicates whether or not a given file is of interest to the image gallery
     * module (is "drawable") and is not marked as a "known" file (e.g., is not
     * a file in the NSRL hash set).
     *
     * @param file The file.
     *
     * @return True if the file is "drawable" and not "known", false otherwise.
     *
     * @throws FileTypeDetectorInitException If there is an error determining
     *                                       the type of the file.
     */
    private static boolean isDrawableAndNotKnown(AbstractFile abstractFile) throws FileTypeDetector.FileTypeDetectorInitException {
        return (abstractFile.getKnown() != TskData.FileKnown.KNOWN) && FileTypeUtils.isDrawable(abstractFile);
    }

    /**
     * A listener for ingest module application events.
     */
    static private class IngestModuleEventListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            /*
             * If running in "headless" mode, there is no need to process any
             * ingest module events during the current session.
             *
             * Note that this check cannot be done earlier on start up because
             * the "headless" property may not have been set yet.
             */
            if (RuntimeProperties.runningWithGUI() == false) {
                IngestManager.getInstance().removeIngestModuleEventListener(this);
                return;
            }

            /*
             * Only process individual files and artifacts in "real time" on the
             * node that is running the ingest job. On a remote node, image
             * files are processed as a group when the ingest job is complete.
             */
            if (((AutopsyEvent) event).getSourceType() != AutopsyEvent.SourceType.LOCAL) {
                return;
            }

            ImageGalleryController currentController;
            try {
                currentController = getController();
            } catch (TskCoreException ex) {
                logger.log(Level.SEVERE, String.format("Failed to handle %s event", event.getPropertyName()), ex); //NON-NLS
                return;
            }

            String eventType = event.getPropertyName();
            switch (IngestManager.IngestModuleEvent.valueOf(eventType)) {
                case FILE_DONE:
                    AbstractFile file = (AbstractFile) event.getNewValue();
                    if (!file.isFile()) {
                        return;
                    }
                    if (currentController.isListeningEnabled()) {
                        try {
                            if (isDrawableAndNotKnown(file)) {
                                currentController.queueDBTask(new ImageGalleryController.UpdateFileTask(file, currentController.getDatabase()));
                            }
                        } catch (FileTypeDetector.FileTypeDetectorInitException ex) {
                            logger.log(Level.SEVERE, String.format("Failed to determine if file is of interest to the image gallery module, ignoring file (obj_id=%d)", file.getId()), ex); //NON-NLS
                        }
                    }
                    break;
                case DATA_ADDED:
                    ModuleDataEvent artifactAddedEvent = (ModuleDataEvent) event.getOldValue();
                    if (artifactAddedEvent.getBlackboardArtifactType().getTypeID() == ARTIFACT_TYPE.TSK_METADATA_EXIF.getTypeID()) {
                        DrawableDB drawableDB = currentController.getDatabase();
                        if (artifactAddedEvent.getArtifacts() != null) {
                            for (BlackboardArtifact art : artifactAddedEvent.getArtifacts()) {
                                drawableDB.addExifCache(art.getObjectID());
                            }
                        }
                    } else if (artifactAddedEvent.getBlackboardArtifactType().getTypeID() == ARTIFACT_TYPE.TSK_HASHSET_HIT.getTypeID()) {
                        DrawableDB drawableDB = currentController.getDatabase();
                        if (artifactAddedEvent.getArtifacts() != null) {
                            for (BlackboardArtifact art : artifactAddedEvent.getArtifacts()) {
                                drawableDB.addHashSetCache(art.getObjectID());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * A listener for case application events.
     */
    static private class CaseEventListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            /*
             * If running in "headless" mode, there is no need to process any
             * case events during the current session. Note that this check
             * cannot be done earlier in onStart because the "headless" property
             * may not have been set yet.
             */
            if (RuntimeProperties.runningWithGUI() == false) {
                Case.removePropertyChangeListener(this);
                return;
            }

            Case.Events eventType = Case.Events.valueOf(event.getPropertyName());
            if (eventType == Case.Events.CURRENT_CASE) {
                synchronized (controllerLock) {
                    if (event.getNewValue() != null && event.getNewValue() instanceof Case) {
                        /*
                         * CURRENT_CASE(_OPENED) event. Construct a new Image
                         * Gallery controller.
                         */
                        Case newCase = (Case) event.getNewValue();
                        try {
                            controller = new ImageGalleryController(newCase);
                        } catch (TskCoreException ex) {
                            logger.log(Level.SEVERE, "Failed to construct controller for new case", ex);
                        }
                    } else if (event.getOldValue() != null && event.getOldValue() instanceof Case) {
                        /*
                         * CURRENT_CASE(_CLOSED) event. Shut down the controller
                         * for the case and close the top component, if it is
                         * open.
                         */
                        SwingUtilities.invokeLater(ImageGalleryTopComponent::closeTopComponent);
                        controller.shutDown();
                        controller = null;
                    }
                }
            } else {
                ImageGalleryController currentController;
                try {
                    currentController = getController();
                } catch (TskCoreException ex) {
                    logger.log(Level.SEVERE, String.format("Failed to handle %s event", event.getPropertyName()), ex); //NON-NLS
                    return;
                }

                switch (eventType) {
                    case DATA_SOURCE_ADDED:
                        if (((AutopsyEvent) event).getSourceType() == AutopsyEvent.SourceType.LOCAL) {
                            Content newDataSource = (Content) event.getNewValue();
                            if (currentController.isListeningEnabled()) {
                                currentController.getDatabase().insertOrUpdateDataSource(newDataSource.getId(), DrawableDB.DrawableDbBuildStatusEnum.DEFAULT);
                            }
                        }
                        break;
                    case CONTENT_TAG_ADDED:
                        final ContentTagAddedEvent tagAddedEvent = (ContentTagAddedEvent) event;
                        long objId = tagAddedEvent.getAddedTag().getContent().getId();
                        DrawableDB drawableDB = currentController.getDatabase();
                        drawableDB.addTagCache(objId);
                        if (drawableDB.isInDB(objId)) { // RJCTODO: Put in cache before in DB check?
                            currentController.getTagsManager().fireTagAddedEvent(tagAddedEvent);
                        }
                        break;
                    case CONTENT_TAG_DELETED:
                        final ContentTagDeletedEvent tagDeletedEvent = (ContentTagDeletedEvent) event;
                        if (currentController.getDatabase().isInDB(tagDeletedEvent.getDeletedTagInfo().getContentID())) {
                            currentController.getTagsManager().fireTagDeletedEvent(tagDeletedEvent);
                        }
                        break;
                    default:
                        logger.log(Level.SEVERE, String.format("Received %s event with no subscription", event.getPropertyName())); //NON-NLS
                        break;
                }
            }
        }
    }

    /**
     * A listener for ingest job application events.
     */
    static private class IngestJobEventListener implements PropertyChangeListener {

        @NbBundle.Messages({
            "ImageGalleryController.dataSourceAnalyzed.confDlg.msg= A new data source was added and finished ingest.\n"
            + "The image / video database may be out of date. "
            + "Do you want to update the database with ingest results?\n",
            "ImageGalleryController.dataSourceAnalyzed.confDlg.title=Image Gallery"
        })
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            ImageGalleryController controller;
            try {
                controller = getController();
            } catch (TskCoreException ex) {
                logger.log(Level.SEVERE, String.format("Failed to handle %s event", event.getPropertyName()), ex); //NON-NLS
                return;
            }

            String eventType = event.getPropertyName();
            switch (IngestManager.IngestJobEvent.valueOf(eventType)) {
                case DATA_SOURCE_ANALYSIS_STARTED:
                    if (((AutopsyEvent) event).getSourceType() == AutopsyEvent.SourceType.LOCAL) {
                        if (controller.isListeningEnabled()) {
                            DataSourceAnalysisStartedEvent dataSourceAnalysisStartedEvent = (DataSourceAnalysisStartedEvent) event;
                            Content dataSource = dataSourceAnalysisStartedEvent.getDataSource();

                            controller.getDatabase().insertOrUpdateDataSource(dataSource.getId(), DrawableDB.DrawableDbBuildStatusEnum.IN_PROGRESS);
                        }
                    }
                    break;
                case DATA_SOURCE_ANALYSIS_COMPLETED:
                    if (((AutopsyEvent) event).getSourceType() == AutopsyEvent.SourceType.LOCAL) {
                        /*
                         * This node just completed analysis of a data source.
                         * Set the state of the local drawables database.
                         */
                        if (controller.isListeningEnabled()) {
                            DataSourceAnalysisCompletedEvent dataSourceAnalysisCompletedEvent = (DataSourceAnalysisCompletedEvent) event;
                            Content dataSource = dataSourceAnalysisCompletedEvent.getDataSource();
                            try {
                            DrawableDB.DrawableDbBuildStatusEnum datasourceDrawableDBStatus
                                    = controller.hasFilesWithNoMimetype(dataSource)
                                    ? DrawableDB.DrawableDbBuildStatusEnum.DEFAULT
                                    : DrawableDB.DrawableDbBuildStatusEnum.COMPLETE;
                            controller.getDatabase().insertOrUpdateDataSource(dataSource.getId(), datasourceDrawableDBStatus);
                            } catch (TskCoreException ex) {
                                logger.log(Level.SEVERE, "Failed to query case database to determine drawables database state", ex);
                            }
                        }
                    } else {
                        /*
                         * A remote node just completed analysis of a data
                         * source. The local drawables database is therefore
                         * stale. If the image gallery top component is open,
                         * give the user an opportunity to update the drawables
                         * database now.
                         */
                        controller.setStale(true);
                        if (controller.isListeningEnabled()) {
                            SwingUtilities.invokeLater(() -> {
                                if (ImageGalleryTopComponent.isImageGalleryOpen()) {
                                    int showAnswer = JOptionPane.showConfirmDialog(ImageGalleryTopComponent.getTopComponent(),
                                            Bundle.ImageGalleryController_dataSourceAnalyzed_confDlg_msg(),
                                            Bundle.ImageGalleryController_dataSourceAnalyzed_confDlg_title(),
                                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                                    switch (showAnswer) {
                                        case JOptionPane.YES_OPTION:
                                            controller.rebuildDB();
                                            break;
                                        case JOptionPane.NO_OPTION:
                                        case JOptionPane.CANCEL_OPTION:
                                        default:
                                            break; //do nothing
                                    }
                                }
                            });
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
