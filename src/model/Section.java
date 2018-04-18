package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of section in resume
 * 
 * @author Anh Khoa
 */
public final class Section {

    private final SectionTypes type;
    private final List<String> content;

    public Section(SectionTypes type) {
        this.type = type;
        this.content = new ArrayList<>();
    }

    public SectionTypes getType() {
        return this.type;
    }

    public List<String> getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
