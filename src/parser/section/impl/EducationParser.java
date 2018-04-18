package parser.section.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import helpers.DateHelper;
import helpers.Helper;
import model.Education;
import model.Period;
import model.Resume;
import model.Section;
import parser.SectionParser;
import processor.Regex;

public final class EducationParser extends SectionParser {

	private final Regex regex;
	private final DateHelper dateHelper;

	public EducationParser() {
		this.regex = new Regex(new File("models/education_patterns.txt"));
		this.dateHelper = new DateHelper(new File("models/date_patterns.txt"));
	}

	private String parseSchool(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("School")) {
			return matches.get("School").get(0).get(1);
		}
		return null;
	}

	private String parseDegree(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("Degree")) {
			return matches.get("Degree").get(0).get(1);
		}
		return null;
	}

	private Boolean isUnknown(String line) {
		return StringUtils.isBlank(this.parseSchool(line)) && this.dateHelper.parseStartAndEndDate(line) == null
				&& StringUtils.isBlank(this.parseDegree(line));
	}

	@Override
	public void parse(Section section, Resume resume) {
		int lineIndex = 0;

		while (lineIndex < section.getContent().size()) {
			String line = section.getContent().get(lineIndex);
			if (!this.isUnknown(line)) {

				Education education = new Education();
				while (lineIndex < section.getContent().size()
						&& !this.isUnknown(section.getContent().get(lineIndex))) {

					line = section.getContent().get(lineIndex);

					String school = this.parseSchool(line);
					Period period = this.dateHelper.parseStartAndEndDate(line);
					String degree = this.parseDegree(line);

					if ((school != null && education.getSchool() != null)
							|| (period != null && education.getPeriod() != null)
							|| (degree != null && education.getDegree() != null)) {
						break;
					}

					if (education.getSchool() == null) {
						education.setSchool(school);
					}
					if (education.getPeriod() == null) {
						education.setPeriod(period);
					}
					if (education.getDegree() == null) {
						education.setDegree(degree);
					}

					lineIndex++;
				}

				while (lineIndex < section.getContent().size() && this.isUnknown(section.getContent().get(lineIndex))) {
					line = section.getContent().get(lineIndex);

					education.getDescription().add(Helper.removeNonCharacterBegin(line));

					lineIndex++;
				}

				resume.getEducations().add(education);
			} else {
				lineIndex++;
			}
		}
	}

}
