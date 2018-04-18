package model;

import java.util.ArrayList;
import java.util.List;

public final class Education {

    private Period             Period;
    private String             School;
    private String             Location;
    private String             Degree;
    private final List<String> Description;

    public Education() {
        this(null);
    }

    public Education(Period period) {
        this.Period = period;
        this.Description = new ArrayList<>();
    }

    public Period getPeriod() {
        return Period;
    }

    public void setPeriod(Period period) {
        this.Period = period;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        this.School = school;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        this.Degree = degree;
    }

    public List<String> getDescription() {
        return Description;
    }

}
