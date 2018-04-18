package parser;

import java.io.File;

public interface Parser {

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
    String parse(File resume);

}
