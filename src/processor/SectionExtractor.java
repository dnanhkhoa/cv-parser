package processor;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import helpers.Helper;
import model.Section;
import model.SectionTypes;

public final class SectionExtractor {

    private final Map<SectionTypes, Pattern> sectionMap;

    /**
     * Initialize class and load section file.
     * 
     * @param file
     */
    public SectionExtractor(File file) {
        this.sectionMap = new HashMap<>();

        try (InputStream inputStream = FileUtils.openInputStream(file)) {
            List<String> lines = IOUtils.readLines(inputStream, UTF_8);
            for (String line : lines) {
                line = StringUtils.trim(line);
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                String[] splittedLine = line.split("\\s*=\\s*");
                String[] config = splittedLine[0].split("\\s*\\|\\s*");
                String[] tokens = splittedLine[1].split("\\s*,\\s*");

                List<String> regexTokens = new ArrayList<>();

                if (config.length > 1) {
                    String[] stringCases = config[1].split("\\s*,\\s*");
                    for (String stringCase : stringCases) {
                        if (stringCase.equalsIgnoreCase("UPPER")) {
                            for (String token : tokens) {
                                regexTokens.add(StringUtils.upperCase(token));
                            }
                        } else if (stringCase.equalsIgnoreCase("PROPER")) {
                            for (String token : tokens) {
                                regexTokens.add(WordUtils.capitalizeFully(token));
                            }
                        } else if (stringCase.equalsIgnoreCase("LOWER")) {
                            for (String token : tokens) {
                                regexTokens.add(StringUtils.lowerCase(token));
                            }
                        } else if (stringCase.equalsIgnoreCase("DEFAULT")) {
                            for (String token : tokens) {
                                regexTokens.add(token);
                            }
                        }
                    }
                } else {
                    for (String token : tokens) {
                        regexTokens.add(token);
                    }
                }
                this.sectionMap.put(SectionTypes.valueOf(config[0]),
                        Pattern.compile(StringUtils.join(regexTokens, '|'), Pattern.UNICODE_CHARACTER_CLASS));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Classify line in resume into SectionTypes
     * 
     * @param line
     * @return SectionTypes
     */
    private SectionTypes findSectionType(String line) {
        line = Helper.removeNonCharacterBegin(line);
        if (StringUtils.isBlank(line)) {
            return SectionTypes.Unknown;
        }
        for (Map.Entry<SectionTypes, Pattern> entry : this.sectionMap.entrySet()) {
            if (entry.getValue().matcher(line).find()) {
                return entry.getKey();
            }
        }
        return SectionTypes.Unknown;
    }

    /**
     * Clustering resume into Sections
     * 
     * @param lines
     * @return List of Sections
     */
    public List<Section> extractSection(List<String> lines) {
        List<Section> sections = new ArrayList<>();

        int lineIndex = 0;

        Section assumedPersonalSection = new Section(SectionTypes.Personal);
        while (lineIndex < lines.size() - 1 && this.findSectionType(lines.get(lineIndex)) == SectionTypes.Unknown) {
            // Remove blank lines
            if (StringUtils.isNotBlank(lines.get(lineIndex))) {
                assumedPersonalSection.getContent().add(lines.get(lineIndex));
            }
            lineIndex++;
        }
        sections.add(assumedPersonalSection);

        while (lineIndex < lines.size()) {
            SectionTypes sectionType = this.findSectionType(lines.get(lineIndex));
            // This is a new section
            if (sectionType != SectionTypes.Unknown) {
                Section section = new Section(sectionType);
                // Assume the next line is content of current section
                while (lineIndex < lines.size() - 1
                        && this.findSectionType(lines.get(lineIndex + 1)) == SectionTypes.Unknown) {
                    lineIndex++;
                    if (StringUtils.isNotBlank(lines.get(lineIndex))) {
                        section.getContent().add(lines.get(lineIndex));
                    }
                }
                sections.add(section);
            }
            lineIndex++;
        }

        return sections;
    }

}
