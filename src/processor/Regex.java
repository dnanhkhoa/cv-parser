package processor;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public final class Regex {

    private final Set<String>          entityTypes;
    private final Map<String, Pattern> patterns;
    private final Map<String, String>  variablesMap;

    public Regex(File file) {
        this.entityTypes = new HashSet<>();
        this.patterns = new HashMap<>();
        this.variablesMap = new HashMap<>();
        this.loadFile(file);
    }

    private void loadFile(File file) {
        try (InputStream inputStream = FileUtils.openInputStream(file)) {

            List<String> lines = IOUtils.readLines(inputStream, UTF_8);

            Pattern variableLoadPattern = Pattern.compile("\\{\\{@([^\\}]+)\\}\\}",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);
            Pattern functionLoadPattern = Pattern.compile("\\{\\{@@file\\(([^\\)]+)\\)\\}\\}",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);
            Pattern functionIncludePattern = Pattern.compile("\\{\\{@@include\\(([^\\)]+)\\)\\}\\}",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);

            for (String line : lines) {
                line = StringUtils.trim(line);
                if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                    continue;
                }

                Boolean breakLine = false;

                // {{@@include()}}
                Matcher matcher = functionIncludePattern.matcher(line);
                while (matcher.find()) {
                    this.loadFile(new File(matcher.group(1)));
                    breakLine = true;
                }

                if (breakLine) {
                    continue;
                }

                String[] splittedLine = line.split("=");
                if (splittedLine.length <= 1) {
                    continue;
                }
                String type = StringUtils.trim(splittedLine[0]);
                String regexPattern = StringUtils.trim(splittedLine[1]);
                if (StringUtils.isBlank(type)) {
                    continue;
                }

                // {{@@file()}}
                matcher = functionLoadPattern.matcher(regexPattern);
                while (matcher.find()) {
                    try (InputStream loadFileStream = FileUtils.openInputStream(new File(matcher.group(1)))) {
                        List<String> inList = IOUtils.readLines(loadFileStream, UTF_8);
                        regexPattern = regexPattern.replace(matcher.group(0), StringUtils.join(inList, '|'));
                    } catch (IOException e) {
                        regexPattern = regexPattern.replace(matcher.group(0), "");
                        System.err.println(String.format("%s is missing!", matcher.group(1)));
                    }
                }

                Boolean flagError = false;

                // {{@variable}}
                matcher = variableLoadPattern.matcher(regexPattern);
                while (matcher.find()) {
                    String variableData = variablesMap.getOrDefault(matcher.group(1), null);
                    if (variableData != null) {
                        regexPattern = regexPattern.replace(matcher.group(0), variableData);
                    } else {
                        flagError = true;
                        System.err.println(String.format("Variable %s is not found!", matcher.group(1)));
                    }
                }

                if (flagError) {
                    continue;
                }

                if (!type.startsWith("@")) {
                    this.patterns.put(type,
                            Pattern.compile(regexPattern, Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL));
                    this.entityTypes.add(type);
                }
                variablesMap.put(type.replaceAll("^@", ""), regexPattern);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getEntityTypes() {
        return this.entityTypes;
    }

    public List<List<String>> findMatches(String text, Pattern pattern) {
        List<List<String>> results = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            List<String> allGroups = new ArrayList<>();
            for (int i = 0; i <= matcher.groupCount(); ++i) {
                allGroups.add(matcher.group(i));
            }
            results.add(allGroups);
        }
        return results;
    }

    public Map<String, List<List<String>>> recognise(String text) {
        Map<String, List<List<String>>> result = new HashMap<>();
        for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            List<List<String>> names = findMatches(text, entry.getValue());
            if (!names.isEmpty()) {
                result.put(entry.getKey(), names);
            }
        }
        return result;
    }
}