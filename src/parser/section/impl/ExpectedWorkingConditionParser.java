package parser.section.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import helpers.Helper;
import model.Resume;
import model.Section;
import parser.SectionParser;
import processor.Regex;

public final class ExpectedWorkingConditionParser extends SectionParser {

	private final Regex regex;

	public ExpectedWorkingConditionParser() {
		this.regex = new Regex(new File("models/expected_working_condition_patterns.txt"));
	}

	private void handleRegex(Section section, Resume resume) {
		String content = StringUtils.join(section.getContent(), '\n');
		Map<String, List<List<String>>> matches = this.regex.recognise(content);
		for (String entity : matches.keySet()) {
			if (entity.equalsIgnoreCase("ExpectedSalary")) {
				if (!resume.getPersonal().containsKey("ExpectedSalary")) {
					resume.getPersonal().put(entity, matches.get(entity).get(0).get(1));
				}
			}
		}
	}

	@Override
	public void parse(Section section, Resume resume) {
		this.handleRegex(section, resume);
		for (String line : section.getContent()) {
			resume.getExpectedWorkingConditions().add(Helper.removeNonCharacterBegin(line));
		}
	}

}
