package hines.matthew.wgucoursetracker.model;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Date;

import hines.matthew.wgucoursetracker.database.CoursesTable;

public class Course {
    private int id = -1;
    private String name;
    private String description;
    private int termId = -1;
    private int creditValue;
    private Date startDate;
    private Date endDate;
    private String notes;
    private boolean passed = false;
    private ArrayList<Assessment> assessments = new ArrayList<>();

    public Course(){

    }

    public Course(int id, String name, String description, int creditValue, Date start, Date end){
        this.id = id;
        this.name = name;
        this.description = description;
        this.creditValue = creditValue;
        this.startDate = start;
        this.endDate = end;
    }

    public Course(int id, String name, String description, int creditValue, Date start, Date end, String notes){
        this.id = id;
        this.name = name;
        this.description = description;
        this.creditValue = creditValue;
        this.startDate = start;
        this.endDate = end;
        this.notes = notes;
    }

    public Course(int id, String name, String description, int creditValue, Date start, Date end, String notes, ArrayList<Assessment> assessments){
        this.id = id;
        this.name = name;
        this.description = description;
        this.creditValue = creditValue;
        this.startDate = start;
        this.endDate = end;
        this.notes = notes;
        this.assessments = assessments;
    }

    public Course(int id, String name, String description, int creditValue, Date start, Date end, String notes, ArrayList<Assessment> assessments,boolean passed){
        this.id = id;
        this.name = name;
        this.description = description;
        this.creditValue = creditValue;
        this.startDate = start;
        this.endDate = end;
        this.notes = notes;
        this.assessments = assessments;
        this.passed=passed;
    }

    public Course(int id, String name, String description, int creditValue, Date start, Date end, String notes, ArrayList<Assessment> assessments,boolean passed, int term){
        this.id = id;
        this.name = name;
        this.description = description;
        this.creditValue = creditValue;
        this.startDate = start;
        this.endDate = end;
        this.notes = notes;
        this.assessments = assessments;
        this.passed=passed;
        this.termId=term;
    }

    //Getters

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getCreditValue() {
        return creditValue;
    }

    public String getNotes(){
        return notes;
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public boolean isPassed() {
        return passed;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getTermId() {
        return termId;
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreditValue(int creditValue) {
        this.creditValue = creditValue;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setAssessments(ArrayList<Assessment> assessments) {
        this.assessments = assessments;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    //ArrayList editors

    public String addAssessment(Assessment newAssessment){
        if (assessments == null || assessments.contains(newAssessment)){
            return "Assessment already assigned to course.";
        } else {
            assessments.add(newAssessment);
            return "Assessment added to course.";
        }
    }

    public String removeAssessment(Assessment newAssessment){
        if (assessments != null && assessments.contains(newAssessment)){
            assessments.remove(newAssessment);
            if (assessments.isEmpty())
                return "Course removed. No assessments associated with course.";
            return "Assessment removed from course.";
        } else {
            return "Assessment not assigned to course.";
        }

    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();
        if (id >= 0) {
            values.put(CoursesTable.COLUMN_ID, id);
            values.put(CoursesTable.COLUMN_NAME, name);
            values.put(CoursesTable.COLUMN_DESCRIPTION, description);
            values.put(CoursesTable.COLUMN_CREDITVALUE, creditValue);
            values.put(CoursesTable.COLUMN_NOTES, notes);
        } else{
            values.put(CoursesTable.COLUMN_NAME, name);
            values.put(CoursesTable.COLUMN_DESCRIPTION, description);
            values.put(CoursesTable.COLUMN_CREDITVALUE, creditValue);
            values.put(CoursesTable.COLUMN_NOTES, notes);
        }
        return values;
    }
}
