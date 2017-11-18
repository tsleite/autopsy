/*
 * Autopsy Forensic Browser
 *
 * Copyright 2011-2017 Basis Technology Corp.
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
package org.sleuthkit.autopsy.casemodule.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.concurrent.Immutable;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.coreutils.ModuleSettings;
import org.sleuthkit.datamodel.TagName;
import org.sleuthkit.autopsy.datamodel.DhsImageCategory;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;
import org.sleuthkit.datamodel.TskData;

/**
 * A tag name definition consisting of a display name, description and color.
 */
@Immutable
final class TagNameDefiniton implements Comparable<TagNameDefiniton> {

    private static final Logger LOGGER = Logger.getLogger(TagNameDefiniton.class.getName());
    @NbBundle.Messages({"TagNameDefiniton.predefTagNames.bookmark.text=Bookmark",
        "TagNameDefiniton.predefTagNames.followUp.text=Follow Up",
        "TagNameDefiniton.predefTagNames.notableItem.text=Notable Item"})
    private static final String TAGS_SETTINGS_NAME = "Tags"; //NON-NLS
    private static final String TAG_NAMES_SETTING_KEY = "TagNames"; //NON-NLS    

    private static final List<String> STANDARD_NOTABLE_TAG_DISPLAY_NAMES = Arrays.asList(Bundle.TagNameDefiniton_predefTagNames_notableItem_text(), DhsImageCategory.ONE.getDisplayName(), DhsImageCategory.TWO.getDisplayName(), DhsImageCategory.THREE.getDisplayName());  // NON-NLS
    private static final List<String> STANDARD_TAG_DISPLAY_NAMES = Arrays.asList(Bundle.TagNameDefiniton_predefTagNames_bookmark_text(), Bundle.TagNameDefiniton_predefTagNames_followUp_text(),
            Bundle.TagNameDefiniton_predefTagNames_notableItem_text(), DhsImageCategory.ONE.getDisplayName(),
            DhsImageCategory.TWO.getDisplayName(), DhsImageCategory.THREE.getDisplayName(),
            DhsImageCategory.FOUR.getDisplayName(), DhsImageCategory.FIVE.getDisplayName());
    private final String displayName;
    private final String description;
    private final TagName.HTML_COLOR color;
    private final TskData.FileKnown knownStatusDenoted;

    /**
     * Constructs a tag name definition consisting of a display name,
     * description, color and knownStatus.
     *
     * @param displayName The display name for the tag name.
     * @param description The description for the tag name.
     * @param color       The color for the tag name.
     * @param knownStatus The status denoted by the tag.
     */
    TagNameDefiniton(String displayName, String description, TagName.HTML_COLOR color, TskData.FileKnown status) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.knownStatusDenoted = status;
    }

    static List<String> getStandardTagNames() {
        return STANDARD_TAG_DISPLAY_NAMES;
    }

    /**
     * Gets the display name for the tag name.
     *
     * @return The display name.
     */
    String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description for the tag name.
     *
     * @return The description.
     */
    String getDescription() {
        return description;
    }

    /**
     * Gets the color for the tag name.
     *
     * @return The color.
     */
    TagName.HTML_COLOR getColor() {
        return color;
    }

    /**
     * Whether or not the status that this tag implies is the Notable status
     *
     * @return true if the Notable status is implied by this tag, false
     *         otherwise.
     */
    boolean isNotable() {
        return knownStatusDenoted == TskData.FileKnown.BAD;
    }

    /**
     * Compares this tag name definition with the specified tag name definition
     * for order.
     *
     * @param other The tag name definition to which to compare this tag name
     *              definition.
     *
     * @return Negative integer, zero, or a positive integer to indicate that
     *         this tag name definition is less than, equal to, or greater than
     *         the specified tag name definition.
     */
    @Override
    public int compareTo(TagNameDefiniton other) {
        return this.getDisplayName().toLowerCase().compareTo(other.getDisplayName().toLowerCase());
    }

    /**
     * Returns a hash code value for this tag name definition.
     *
     * @return The has code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.displayName);
        return hash;
    }

    /**
     * Indicates whether some other object is "equal to" this tag name
     * definition.
     *
     * @param obj The object to test for equality.
     *
     * @return True or false.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TagNameDefiniton)) {
            return false;
        }
        TagNameDefiniton thatTagName = (TagNameDefiniton) obj;
        return this.getDisplayName().equals(thatTagName.getDisplayName());
    }

    /**
     * A string representation of this tag name definition.
     *
     * @return The display name of the tag type.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * @return A string representation of the tag name definition in the format
     *         that is used by the tags settings file.
     */
    private String toSettingsFormat() {
        return displayName + "," + description + "," + color.name() + "," + knownStatusDenoted.toString();
    }

    private TagName saveToCase(SleuthkitCase caseDb) {
        TagName tagName = null;
        try {
            tagName = caseDb.addOrUpdateTagName(displayName, description, color, knownStatusDenoted);
        } catch (TskCoreException ex) {
            LOGGER.log(Level.SEVERE, "Error updating non-file object ", ex);
        }
        return tagName;
    }

    /**
     * Gets tag name definitions from the tag settings file as well as the
     * default tag name definitions.
     *
     * @return A set of tag name definition objects.
     */
    static synchronized Set<TagNameDefiniton> getTagNameDefinitions() {
        Set<TagNameDefiniton> tagNames = new HashSet<>();
        //modifiable copy of default tags list for us to keep track of which default tags have already been created
        Set<String> standardTags = new HashSet<>(STANDARD_TAG_DISPLAY_NAMES);
        String setting = ModuleSettings.getConfigSetting(TAGS_SETTINGS_NAME, TAG_NAMES_SETTING_KEY);
        if (null != setting && !setting.isEmpty()) {
            List<String> tagNameTuples = Arrays.asList(setting.split(";"));
            int numberOfAttributes = 0;
            if (tagNameTuples.size() > 0) {
                // Determine if Tags.properties file needs to be upgraded
                numberOfAttributes = tagNameTuples.get(0).split(",").length;
            }
            if (numberOfAttributes == 3) {
                // Upgrade Tags.Properties with the settings in Central Repository Settings if necessary
                tagNames.addAll(upgradeTagPropertiesFile(tagNameTuples, standardTags));
            } else if (numberOfAttributes == 4) {
                // if the Tags.Properties file is up to date parse it
                tagNames.addAll(readCurrentTagPropertiesFile(tagNameTuples, standardTags));
            }
            //create standard tags which should always exist which were not already created for whatever reason, such as upgrade
            for (String standardTagName : standardTags) {
                if (STANDARD_NOTABLE_TAG_DISPLAY_NAMES.contains(standardTagName)) {
                    tagNames.add(new TagNameDefiniton(standardTagName, "", TagName.HTML_COLOR.NONE, TskData.FileKnown.BAD));
                } else {
                    tagNames.add(new TagNameDefiniton(standardTagName, "", TagName.HTML_COLOR.NONE, TskData.FileKnown.UNKNOWN));
                }
            }
        }
        return tagNames;

    }

    /**
     * Read the central repository properties file to get any knownStatus
     * related tag settings that may exist in it.
     *
     * @param tagProperties           the list of comma seperated tags in the
     *                                Tags.properties file
     * @param standardTagsToBeCreated the list of standard tags which have yet
     *                                to be created
     *
     * @return tagNames a list of TagNameDefinitions
     */
    private static Set<TagNameDefiniton> upgradeTagPropertiesFile(List<String> tagProperties, Set<String> standardTagsToBeCreated) {
        Set<TagNameDefiniton> tagNames = new HashSet<>();
        List<String> legacyNotableTags = new ArrayList<>();
        String badTagsStr = ModuleSettings.getConfigSetting("CentralRepository", "db.badTags"); // NON-NLS
        if (badTagsStr == null || badTagsStr.isEmpty()) {  //if there were no bad tags in the central repo properties file use the default list
            legacyNotableTags.addAll(STANDARD_NOTABLE_TAG_DISPLAY_NAMES);
        } else {  //otherwise use the list that was in the central repository properties file
            legacyNotableTags.addAll(Arrays.asList(badTagsStr.split(",")));
        }
        for (String tagNameTuple : tagProperties) {
            String[] tagNameAttributes = tagNameTuple.split(","); //get the attributes
            standardTagsToBeCreated.remove(tagNameAttributes[0]); //remove the tag from the list of standard tags which have not been created
            if (legacyNotableTags.contains(tagNameAttributes[0])) { //if tag should be notable mark create it as such
                tagNames.add(new TagNameDefiniton(tagNameAttributes[0], tagNameAttributes[1],
                        TagName.HTML_COLOR.valueOf(tagNameAttributes[2]), TskData.FileKnown.BAD));
            } else {  //otherwise create it as unknown
                tagNames.add(new TagNameDefiniton(tagNameAttributes[0], tagNameAttributes[1],
                        TagName.HTML_COLOR.valueOf(tagNameAttributes[2]), TskData.FileKnown.UNKNOWN)); //add the default value for that tag 
            }
        }
        return tagNames;
    }

    /**
     * Read the Tags.properties file to get the TagNameDefinitions that are
     * preserved accross cases.
     *
     * @param tagProperties           the list of comma seperated tags in the
     *                                Tags.properties file
     * @param standardTagsToBeCreated the list of standard tags which have yet
     *                                to be created
     *
     * @return tagNames a list of TagNameDefinitions
     */
    private static Set<TagNameDefiniton> readCurrentTagPropertiesFile(List<String> tagProperties, Set<String> standardTagsToBeCreated) {
        Set<TagNameDefiniton> tagNames = new HashSet<>();
        for (String tagNameTuple : tagProperties) {
            String[] tagNameAttributes = tagNameTuple.split(","); //get the attributes
            standardTagsToBeCreated.remove(tagNameAttributes[0]);  //remove the tag from the list of standard tags which have not been created
            tagNames.add(new TagNameDefiniton(tagNameAttributes[0], tagNameAttributes[1],
                    TagName.HTML_COLOR.valueOf(tagNameAttributes[2]), TskData.FileKnown.valueOf(tagNameAttributes[3])));
        }
        return tagNames;
    }

    /**
     * Sets the tag name definitions in the tag settings file.
     *
     * @param tagNames A set of tag name definition objects.
     */
    static synchronized void setTagNameDefinitions(Set<TagNameDefiniton> tagNames) {
        StringBuilder setting = new StringBuilder();
        for (TagNameDefiniton tagName : tagNames) {
            if (setting.length() != 0) {
                setting.append(";");
            }
            setting.append(tagName.toSettingsFormat());
            if (Case.isCaseOpen()) {
                SleuthkitCase caseDb = Case.getCurrentCase().getSleuthkitCase();
                tagName.saveToCase(caseDb);
            }
        }
        ModuleSettings.setConfigSetting(TAGS_SETTINGS_NAME, TAG_NAMES_SETTING_KEY, setting.toString());
    }

}
