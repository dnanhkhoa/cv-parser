package parser.section.impl;

import helpers.Helper;
import model.Resume;
import model.Section;
import parser.SectionParser;

public final class SkillsParser extends SectionParser {

	@Override
	public void parse(Section section, Resume resume) {
		for (String line : section.getContent()) {
			resume.getSkills().add(Helper.removeNonCharacterBegin(line));
		}
	}

}
