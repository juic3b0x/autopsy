/*
 * Autopsy Forensic Browser
 *
 * Copyright 2013-15 Basis Technology Corp.
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
import java.util.logging.Level;
import javafx.application.Platform;
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
import org.sleuthkit.autopsy.coreutils.MessageNotifyUtil;
import org.sleuthkit.autopsy.events.AutopsyEvent;
import org.sleuthkit.autopsy.imagegallery.datamodel.DrawableDB;
import org.sleuthkit.autopsy.ingest.IngestManager;
import org.sleuthkit.autopsy.modules.filetypeid.FileTypeDetector;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.Content;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;

/** static definitions and utilities for the ImageGallery module */
@NbBundle.Messages({"ImageGalleryModule.moduleName=Image Gallery"})
public class ImageGalleryModule {

    private static final Logger logger = Logger.getLogger(ImageGalleryModule.class.getName());

    private static final String MODULE_NAME = Bundle.ImageGalleryModule_moduleName();

    private static final Object controllerLock = new Object();
    private static ImageGalleryController controller;

    public static ImageGalleryController getController() throws NoCurrentCaseException {
        synchronized (controllerLock) {
            if (controller == null) {
                try {
                    controller = new ImageGalleryController(Case.getCurrentCaseThrows());
                } catch (Exception ex) {
                    throw new NoCurrentCaseException("Error getting ImageGalleryController for the current case.", ex);
                }
            }
            return controller;
        }
    }

    /**
     *
     *
     * This method is invoked by virtue of the OnStart annotation on the OnStart
     * class class
     */
    static void onStart() {
        Platform.setImplicitExit(false);
        logger.info("Setting up ImageGallery listeners"); //NON-NLS

        IngestManager.getInstance().addIngestJobEventListener(new IngestJobEventListener());
        IngestManager.getInstance().addIngestModuleEventListener(new IngestModuleEventListener());
        Case.addPropertyChangeListener(new CaseEventListener());
    }

    static String getModuleName() {
        return MODULE_NAME;
    }

    /**
     * get the Path to the Case's ImageGallery ModuleOutput subfolder; ie
     * ".../[CaseName]/ModuleOutput/Image Gallery/"
     *
     * @param theCase the case to get the ImageGallery ModuleOutput subfolder
     *                for
     *
     * @return the Path to the ModuleOuput subfolder for Image Gallery
     */
    static Path getModuleOutputDir(Case theCase) {
        return Paths.get(theCase.getModuleDirectory(), getModuleName());
    }

    /** provides static utilities, can not be instantiated */
    private ImageGalleryModule() {
    }

    /** is listening enabled for the given case
     *
     * @param c
     *
     * @return true if listening is enabled for the given case, false otherwise
     */
    static boolean isEnabledforCase(Case c) {
        if (c != null) {
            String enabledforCaseProp = new PerCaseProperties(c).getConfigSetting(ImageGalleryModule.MODULE_NAME, PerCaseProperties.ENABLED);
            return isNotBlank(enabledforCaseProp) ? Boolean.valueOf(enabledforCaseProp) : ImageGalleryPreferences.isEnabledByDefault();
        } else {
            return false;
        }
    }

    /** is the drawable db out of date for the given case
     *
     * @param c
     *
     * @return true if the drawable db is out of date for the given case, false
     *         otherwise
     */
    public static boolean isDrawableDBStale(Case c) {
        synchronized (controllerLock) {
            if (controller != null) {
                return controller.isDataSourcesTableStale();
            } else {
                return false;
            }
        }
    }

    /**
     * Is the given file 'supported' and not 'known'(nsrl hash hit). If so we
     * should include it in {@link DrawableDB} and UI
     *
     * @param abstractFile
     *
     * @return true if the given {@link AbstractFile} is "drawable" and not
     *         'known', else false
     */
    public static boolean isDrawableAndNotKnown(AbstractFile abstractFile) throws TskCoreException, FileTypeDetector.FileTypeDetectorInitException {
        return (abstractFile.getKnown() != TskData.FileKnown.KNOWN) && FileTypeUtils.isDrawable(abstractFile);
    }

    static private class IngestModuleEventListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (RuntimeProperties.runningWithGUI() == false) {
                /*
                 * Running in "headless" mode, no need to process any events.
                 * This cannot be done earlier because the switch to core
                 * components inactive may not have been made at start up.
                 */
                IngestManager.getInstance().removeIngestModuleEventListener(this);
                return;
            }
            switch (IngestManager.IngestModuleEvent.valueOf(evt.getPropertyName())) {
                case CONTENT_CHANGED:
                //TODO: do we need to do anything here?  -jm
                case DATA_ADDED:
                    /*
                     * we could listen to DATA events and progressivly update
                     * files, and get data from DataSource ingest modules, but
                     * given that most modules don't post new artifacts in the
                     * events and we would have to query for them, without
                     * knowing which are the new ones, we just ignore these
                     * events for now. The relevant data should all be captured
                     * by file done event, anyways -jm
                     */
                    break;
                case FILE_DONE:
                    /**
                     * getOldValue has fileID getNewValue has
                     * {@link Abstractfile}
                     */

                    AbstractFile file = (AbstractFile) evt.getNewValue();

                    // only process individual files in realtime on the node that is running the ingest
                    // on a remote node, image files are processed enblock when ingest is complete
                    if (((AutopsyEvent) evt).getSourceType() == AutopsyEvent.SourceType.LOCAL) {
                        synchronized (controllerLock) {
                            if (controller != null) {
                                if (controller.isListeningEnabled()) {
                                    if (file.isFile()) {
                                        try {

                                            if (ImageGalleryModule.isDrawableAndNotKnown(file)) {
                                                //this file should be included and we don't already know about it from hash sets (NSRL)
                                                controller.queueDBTask(new ImageGalleryController.UpdateFileTask(file, controller.getDatabase()));
                                            } else if (FileTypeUtils.getAllSupportedExtensions().contains(file.getNameExtension())) {
                                                //doing this check results in fewer tasks queued up, and faster completion of db update
                                                //this file would have gotten scooped up in initial grab, but actually we don't need it
                                                controller.queueDBTask(new ImageGalleryController.RemoveFileTask(file, controller.getDatabase()));
                                            }

                                        } catch (TskCoreException | FileTypeDetector.FileTypeDetectorInitException ex) {
                                            logger.log(Level.SEVERE, "Unable to determine if file is drawable and not known.  Not making any changes to DB", ex); //NON-NLS
                                            MessageNotifyUtil.Notify.error("Image Gallery Error",
                                                    "Unable to determine if file is drawable and not known.  Not making any changes to DB.  See the logs for details.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }

    static private class CaseEventListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (RuntimeProperties.runningWithGUI() == false) {
                /*
                 * Running in "headless" mode, no need to process any events.
                 * This cannot be done earlier because the switch to core
                 * components inactive may not have been made at start up.
                 */
                Case.removePropertyChangeListener(this);
                return;
            }
            synchronized (controllerLock) {
                switch (Case.Events.valueOf(evt.getPropertyName())) {
                    case CURRENT_CASE:

                        // case has changes: close window, reset everything 
                        SwingUtilities.invokeLater(ImageGalleryTopComponent::closeTopComponent);
                        if (controller != null) {
                            controller.shutDown();
                            controller = null;
                        }

                        Case newCase = (Case) evt.getNewValue();
                        if (newCase != null) {
                            // a new case has been opened: connect db, groupmanager, start worker thread
                            try {
                                controller = new ImageGalleryController(newCase);
                            } catch (TskCoreException ex) {
                                logger.log(Level.SEVERE, "Error changing case in ImageGallery.", ex);
                            }
                        }
                        break;

                    case DATA_SOURCE_ADDED:
                        //For a data source added on the local node, prepopulate all file data to drawable database
                        if (((AutopsyEvent) evt).getSourceType() == AutopsyEvent.SourceType.LOCAL) {
                            Content newDataSource = (Content) evt.getNewValue();
                            if (controller.isListeningEnabled()) {
                                controller.queueDBTask(new ImageGalleryController.PrePopulateDataSourceFiles(newDataSource.getId(), controller, controller.getDatabase(), controller.getSleuthKitCase()));
                            }
                        }

                        break;

                    case CONTENT_TAG_ADDED:
                        final ContentTagAddedEvent tagAddedEvent = (ContentTagAddedEvent) evt;
                        if (controller.getDatabase().isInDB(tagAddedEvent.getAddedTag().getContent().getId())) {
                            controller.getTagsManager().fireTagAddedEvent(tagAddedEvent);
                        }

                        break;
                    case CONTENT_TAG_DELETED:

                        final ContentTagDeletedEvent tagDeletedEvent = (ContentTagDeletedEvent) evt;
                        if (controller.getDatabase().isInDB(tagDeletedEvent.getDeletedTagInfo().getContentID())) {
                            controller.getTagsManager().fireTagDeletedEvent(tagDeletedEvent);
                        }

                        break;
                }
            }
        }
    }

    /**
     * Listener for Ingest Job events.
     */
    static private class IngestJobEventListener implements PropertyChangeListener {

        @NbBundle.Messages({
            "ImageGalleryController.dataSourceAnalyzed.confDlg.msg= A new data source was added and finished ingest.\n"
            + "The image / video database may be out of date. "
            + "Do you want to update the database with ingest results?\n",
            "ImageGalleryController.dataSourceAnalyzed.confDlg.title=Image Gallery"
        })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String eventName = evt.getPropertyName();
            if (eventName.equals(IngestManager.IngestJobEvent.DATA_SOURCE_ANALYSIS_COMPLETED.toString())) {
                if (((AutopsyEvent) evt).getSourceType() == AutopsyEvent.SourceType.REMOTE) {
                    // A remote node added a new data source and just finished ingest on it. 
                    //drawable db is stale, and if ImageGallery is open, ask user what to do
                    synchronized (controllerLock) {
                        if (controller != null) {
                            controller.setStale(true);

                            SwingUtilities.invokeLater(() -> {
                                synchronized (controllerLock) {
                                    if (controller.isListeningEnabled() && ImageGalleryTopComponent.isImageGalleryOpen()) {

                                        int answer = JOptionPane.showConfirmDialog(ImageGalleryTopComponent.getTopComponent(),
                                                Bundle.ImageGalleryController_dataSourceAnalyzed_confDlg_msg(),
                                                Bundle.ImageGalleryController_dataSourceAnalyzed_confDlg_title(),
                                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                                        switch (answer) {
                                            case JOptionPane.YES_OPTION:
                                                controller.rebuildDB();
                                                break;
                                            case JOptionPane.NO_OPTION:
                                            case JOptionPane.CANCEL_OPTION:
                                            default:
                                                break; //do nothing
                                            }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }
}
