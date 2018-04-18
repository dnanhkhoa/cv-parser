package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Resume {

    private final Map<String, String> Personal;
    private final List<String>        Qualifications;
    private final List<Work>          Works;
    private final List<Education>     Educations;
    private final List<String>        Skills;
    private final List<String>        ExpectedWorkingConditions;
    private final List<String>        Objective;
    private final List<String>        Interests;

    public Resume() {
        this.Personal = new HashMap<>();
        this.Qualifications = new ArrayList<>();
        this.Works = new ArrayList<>();
        this.Educations = new ArrayList<>();
        this.Skills = new ArrayList<>();
        this.ExpectedWorkingConditions = new ArrayList<>();
        this.Objective = new ArrayList<>();
        this.Interests = new ArrayList<>();
    }

    public Map<String, String> getPersonal() {
        return this.Personal;
    }

    public List<String> getQualifications() {
        return this.Qualifications;
    }

    public List<Work> getWorks() {
        return this.Works;
    }

    public List<Education> getEducations() {
        return this.Educations;
    }

    public List<String> getSkills() {
        return this.Skills;
    }

    public List<String> getExpectedWorkingConditions() {
        return this.ExpectedWorkingConditions;
    }

    public List<String> getObjective() {
        return this.Objective;
    }

    public List<String> getInterests() {
        return this.Interests;
    }

}
