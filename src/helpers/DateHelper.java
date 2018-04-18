package helpers;

import java.io.File;
import java.util.List;
import java.util.Map;

import model.Period;
import processor.Regex;

public final class DateHelper {

	private final Regex regex;

	public DateHelper(File file) {
		this.regex = new Regex(file);
	}

	public Period parseStartAndEndDate(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("StartEnd")) {
			List<List<String>> data = matches.get("StartEnd");
			return new Period(data.get(0).get(1), data.get(0).get(2));
		}
		return null;

	}
}
