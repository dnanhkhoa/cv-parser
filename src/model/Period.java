package model;

public final class Period {

    private String StartDate;
    private String EndDate;

    public Period() {
    }

    public Period(String startDate, String endDate) {
        this.StartDate = startDate;
        this.EndDate = endDate;
    }

    public Period(String startDate) {
        this.StartDate = startDate;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

}
