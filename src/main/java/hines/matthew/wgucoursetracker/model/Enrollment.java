package hines.matthew.wgucoursetracker.model;

import android.content.ContentValues;

import java.util.Date;

import hines.matthew.wgucoursetracker.database.EnrollmentsTable;

public class Enrollment {
    private int termId;
    private int courseId;
    private Date startDate;
    private Date endDate;
    private boolean passed = false;

    public Enrollment(Term term, Course course){
        this.termId = term.getId();
        this.courseId = course.getId();
        this.startDate = term.getStartDate();
        this.endDate = term.getEndDate();
    }

    public Enrollment(int term, int course, Date start, Date end, boolean passed){
        this.termId = term;
        this.courseId = course;
        this.startDate = start;
        this.endDate = end;
        this.passed = passed;
    }

    public Enrollment(int term, int course, Date start, Date end){
        this.termId = term;
        this.courseId = course;
        this.startDate = start;
        this.endDate = end;
    }

    //Getters

    public int getTermId() {
        return termId;
    }

    public int getCourseId() {
        return courseId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public boolean isPassed() {
        return passed;
    }

    //Setters

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();

        int passVal = passed? 1 : 0;

        values.put(EnrollmentsTable.COLUMN_TERMID, termId);
        values.put(EnrollmentsTable.COLUMN_COURSEID, courseId);
        values.put(EnrollmentsTable.COLUMN_STARTDATE, startDate.toString());
        values.put(EnrollmentsTable.COLUMN_ENDDATE, endDate.toString());
        values.put(EnrollmentsTable.COLUMN_PASSED, passVal);

        return values;
    }

}
