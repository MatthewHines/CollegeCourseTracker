package hines.matthew.wgucoursetracker.model;

import android.content.ContentValues;

import java.util.Calendar;
import java.util.Date;

import hines.matthew.wgucoursetracker.database.AssessmentsTable;

public class Assessment {
    private int id = -1;
    private int courseId;
    private String name;
    private String description;
    private String notes;
    private Date dueDate;
    private boolean passed = false;

    public Assessment(int courseId){
        this.courseId = courseId;
        dueDate = Calendar.getInstance().getTime();
    }

    public Assessment(int newId, int courseId, String newName, String newDescription, String newNotes){
        this.id = newId;
        this.courseId = courseId;
        this.name = newName;
        this.description = newDescription;
        this.notes = newNotes;
    }

    public Assessment(int newId, int courseId, String newName, String newDescription, String newNotes, boolean isPassed){
        this.id = newId;
        this.courseId = courseId;
        this.name = newName;
        this.description = newDescription;
        this.notes = newNotes;
        this.passed = isPassed;
    }

    public Assessment(int newId, int courseId, String newName, String newDescription, String newNotes, boolean isPassed,Date dueDate){
        this.id = newId;
        this.courseId = courseId;
        this.name = newName;
        this.description = newDescription;
        this.notes = newNotes;
        this.passed = isPassed;
        this.dueDate = dueDate;
    }

    //Getters

    public int getId(){
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isPassed() {
        return passed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    //Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues();

        int passVal = passed? 1 : 0;

        if(id >= 0) {
            values.put(AssessmentsTable.COLUMN_ID, id);
            values.put(AssessmentsTable.COLUMN_COURSEID, courseId);
            values.put(AssessmentsTable.COLUMN_NAME, name);
            values.put(AssessmentsTable.COLUMN_DESCRIPTION, description);
            values.put(AssessmentsTable.COLUMN_NOTES, notes);
            values.put(AssessmentsTable.COLUMN_PASSED, passVal);
            if(this.dueDate != null)
                values.put(AssessmentsTable.COLUMN_DUEDATE, dueDate.toString());
        } else{
            values.put(AssessmentsTable.COLUMN_COURSEID, courseId);
            values.put(AssessmentsTable.COLUMN_NAME, name);
            values.put(AssessmentsTable.COLUMN_DESCRIPTION, description);
            values.put(AssessmentsTable.COLUMN_NOTES, notes);
            values.put(AssessmentsTable.COLUMN_PASSED, passVal);
            if(this.dueDate != null)
                values.put(AssessmentsTable.COLUMN_DUEDATE, dueDate.toString());
        }
        return values;
    }
}
