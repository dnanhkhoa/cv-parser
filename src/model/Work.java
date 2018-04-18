package model;

import java.util.ArrayList;
import java.util.List;

public final class Work {

    private Period             Period;
    private String             Employer;
    private String             Location;
    private String             PositionHeld;
    private final List<String> Description;

    public Work() {
        this(null);
    }

    public Work(Period period) {
        this.Period = period;
        this.Description = new ArrayList<>();
    }

    public Period getPeriod() {
        return Period;
    }

    public void setPeriod(Period period) {
        this.Period = period;
    }

    public String getEmployer() {
        return Employer;
    }

    public void setEmployer(String employer) {
        this.Employer = employer;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public String getPositionHeld() {
        return PositionHeld;
    }

    public void setPositionHeld(String positionHeld) {
        this.PositionHeld = positionHeld;
    }

    public List<String> getDescription() {
        return Description;
    }

}
