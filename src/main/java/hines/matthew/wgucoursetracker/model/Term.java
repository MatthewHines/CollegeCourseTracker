package hines.matthew.wgucoursetracker.model;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hines.matthew.wgucoursetracker.database.TermsTable;

public class Term {
    private int id = -1;
    private String name;
    private Date startDate;
    private Date endDate;
    private String notes;
    private ArrayList<Course> courses = new ArrayList<>();

    public Term(){
        startDate = Calendar.getInstance().getTime();
        endDate = Calendar.getInstance().getTime();
    }

    public Term(int newId, String newName, Date startDate, Date endDate, String notes){
        this.id=newId;
        this.name=newName;
        this.startDate=startDate;
        this.endDate=endDate;
        this.notes=notes;
    }

    public Term(int newId, String newName,Date startDate, Date endDate, String notes, ArrayList<Course> courseList){
        this.id=newId;
        this.name=newName;
        this.startDate=startDate;
        this.endDate=endDate;
        this.notes=notes;
        this.courses=courseList;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String addCourse(Course course){
        if (courses == null || courses.contains(course)){
            return "Course already exists in term.";
        } else {
            courses.add(course);
            return "Course added to term.";
        }

    }

    public String removeCourse(Course course){
        if (courses !=null && courses.contains(course)){
            courses.remove(course);
            return "Course removed.";
        } else {
            return course.getName() + " is not in the selected term.";
        }

    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();

        if(this.id >= 0) {
            values.put(TermsTable.COLUMN_ID, id);
            values.put(TermsTable.COLUMN_NAME, name);
            values.put(TermsTable.COLUMN_STARTDATE, startDate.toString());
            values.put(TermsTable.COLUMN_ENDDATE, endDate.toString());
            values.put(TermsTable.COLUMN_NOTES, notes);
        } else{
            values.put(TermsTable.COLUMN_NAME, name);
            values.put(TermsTable.COLUMN_STARTDATE, startDate.toString());
            values.put(TermsTable.COLUMN_ENDDATE, endDate.toString());
            values.put(TermsTable.COLUMN_NOTES, notes);
        }
        return values;
    }
}
