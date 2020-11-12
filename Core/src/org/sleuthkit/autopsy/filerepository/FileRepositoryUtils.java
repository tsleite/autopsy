/*
 * Copyright 2020 Basis Technology Corp.
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
package org.sleuthkit.autopsy.filerepository;

import java.io.File;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import org.sleuthkit.autopsy.core.RuntimeProperties;
import org.sleuthkit.autopsy.core.UserPreferences;
import org.sleuthkit.autopsy.coreutils.MessageNotifyUtil;
import org.sleuthkit.datamodel.FileRepository.FileRepositoryErrorHandler;

/**
 * Utility methods for file repository.
 */
class FileRepositoryUtils {

    /**
     * Create a download folder in the Autopsy temp folder
     * 
     * @return The download folder.
     */
    static File createDownloadFolder() {
        File repoTempDir = Paths.get(UserPreferences.getAppTempDirectory(), "File Repo").toFile();
        if (repoTempDir.exists() == false) {
            repoTempDir.mkdirs();
        }
        return repoTempDir;
    }
    
    /**
     * Error handling class for the file repository. 
     * Will handle displaying relevant error messages to the user.
     */
    static class AutopsyFileRepositoryErrorHandler implements FileRepositoryErrorHandler {
        @Override
        public void displayErrorToUser(String title, String error) {
            if (RuntimeProperties.runningWithGUI()) {
                SwingUtilities.invokeLater(() -> {
                    MessageNotifyUtil.Notify.error(title, error);
                });
            }
        }
    }
}