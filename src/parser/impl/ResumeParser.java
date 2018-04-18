package parser.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.jsoup.helper.StringUtil;
import org.xml.sax.ContentHandler;

import com.google.gson.Gson;

import helpers.Helper;
import model.Resume;
import model.Section;
import model.SectionTypes;
import parser.Parser;
import parser.SectionParser;
import parser.section.impl.ActivitiesParser;
import parser.section.impl.AwardParser;
import parser.section.impl.EducationParser;
import parser.section.impl.ExpectedWorkingConditionParser;
import parser.section.impl.InterestsParser;
import parser.section.impl.ObjectiveParser;
import parser.section.impl.PersonalParser;
import parser.section.impl.ProjectParser;
import parser.section.impl.QualificationParser;
import parser.section.impl.SkillsParser;
import parser.section.impl.SummaryParser;
import parser.section.impl.WorkParser;
import processor.SectionExtractor;

public class ResumeParser implements Parser {

    private final Tika                             tika;
    private final LanguageDetector                 detector;
    private final SectionExtractor                 sectionExtractor;
    private final Map<SectionTypes, SectionParser> parserMap;

    private final Gson                             gson;

    private final String[]                         supportedTypes     = new String[] {
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain"
    };
    private final String[]                         supportedLanguages = new String[] {
            "en", "vi"
    };

    public ResumeParser() throws IOException {
        this.tika = new Tika();
        this.detector = new OptimaizeLangDetector().loadModels();
        this.sectionExtractor = new SectionExtractor(new File("models/sections.txt"));
        this.gson = new Gson();
        this.parserMap = new HashMap<>();
        // Configs
        this.parserMap.put(SectionTypes.Personal, new PersonalParser());
        this.parserMap.put(SectionTypes.Qualification, new QualificationParser());
        this.parserMap.put(SectionTypes.Education, new EducationParser());
        this.parserMap.put(SectionTypes.WorkExperience, new WorkParser());
        this.parserMap.put(SectionTypes.Skills, new SkillsParser());
        this.parserMap.put(SectionTypes.ExpectedWorkingCondition, new ExpectedWorkingConditionParser());
        this.parserMap.put(SectionTypes.Objective, new ObjectiveParser());
        this.parserMap.put(SectionTypes.Interest, new InterestsParser());
        this.parserMap.put(SectionTypes.Award, new AwardParser());
        this.parserMap.put(SectionTypes.Project, new ProjectParser());
        this.parserMap.put(SectionTypes.Summary, new SummaryParser());
        this.parserMap.put(SectionTypes.Activities, new ActivitiesParser());
    }

    private Resume buildResume(List<String> lines) {
        Resume resume = new Resume();
        List<Section> sections = this.sectionExtractor.extractSection(lines);
        for (Section section : sections) {
            SectionParser sectionParser = this.parserMap.get(section.getType());
            if (sectionParser != null) {
                sectionParser.parse(section, resume);
            }
        }
        return resume;
    }

    private List<String> clean(String text) {
        Pattern bodyPattern = Pattern.compile("<body>(.+?)<\\/body>",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);

        Pattern cleanNewLineInTagPattern = Pattern.compile("(<[^>/]+>[^<]*)\\r?\\n([^<]*<\\/[^>]+>)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);

        Pattern cleanSimpleHTMLTagPattern = Pattern.compile("<[^>/]+>([^\r\n<]+)<\\/[^>]+>",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL);

        text = StringEscapeUtils.unescapeHtml(text);

        Matcher matcher = bodyPattern.matcher(text);
        if (!matcher.find()) {
            return null;
        }

        text = matcher.group(1);

        matcher = cleanSimpleHTMLTagPattern.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceAll("$1");
            matcher = cleanSimpleHTMLTagPattern.matcher(text);
        }

        matcher = cleanNewLineInTagPattern.matcher(text);
        while (matcher.find()) {
            text = matcher.replaceAll("$1 $2");
            matcher = cleanNewLineInTagPattern.matcher(text);
        }

        // Removes HTML tags
        // .replaceAll("[^\\S\\n]*([!,.?:;])", "$1 ")
        text = text.replaceAll("<[^>]+>", "").replaceAll("\\t+", " ").replaceAll("[^\\S\\n]+", " ").replaceAll("[“”]",
                "");

        String[] old_lines = text.split("\\r?\\n");
        List<String> new_lines = new ArrayList<>();
        for (String line : old_lines) {
            line = StringUtils.trimToNull(Helper.removeNonCharacterBegin(line));
            if (line != null) {
                new_lines.add(line);
            }
        }
        return new_lines;
    }

    /**
     * The main purpose of this method is to parse unstructured data (resume
     * file) to structured data (JSON). This method accepts the resume file as
     * java.io.File object, parse them and return a JSON string which represents
     * resume information
     *
     * @param resume
     *            resume file in DOC, DOCX or PDF extension
     * @return JSON string represents resume information. Refer the suggested
     *         structure here
     */
    public String parse(File file) {
        try (InputStream inputStream = FileUtils.openInputStream(file)) {
            Metadata metadata = new Metadata();
            ContentHandler handler = new ToXMLContentHandler();
            AutoDetectParser parser = (AutoDetectParser) this.tika.getParser();

            parser.parse(inputStream, handler, metadata);
            String content = handler.toString();

            if (!StringUtil.in(this.detector.detect(content).getLanguage(), this.supportedLanguages)
                    || !StringUtil.in(metadata.get(Metadata.CONTENT_TYPE), this.supportedTypes)) {
                throw new Exception("This file is not supported!");
            }
            List<String> lines = this.clean(content);
            if (content == null) {
                throw new Exception("This file is not supported!");
            }

            // System.out.println(StringUtils.join(lines, "\n"));

            Resume resume = this.buildResume(lines);
            // Converts character \\u0026 to &
            return this.gson.toJson(resume).replace("\\u0026", "&");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
