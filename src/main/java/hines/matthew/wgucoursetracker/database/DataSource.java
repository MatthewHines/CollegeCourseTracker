package hines.matthew.wgucoursetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import hines.matthew.wgucoursetracker.model.Assessment;
import hines.matthew.wgucoursetracker.model.Course;
import hines.matthew.wgucoursetracker.model.Enrollment;
import hines.matthew.wgucoursetracker.model.Term;

public class DataSource {

    private Context mContext;
    private SQLiteDatabase mDatabase;
    SQLiteOpenHelper mDBHelper;

    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    public DataSource(Context context) {
        this.mContext = context;
        mDBHelper = new DBHelper(mContext);
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void open() {
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.execSQL("PRAGMA foreign_keys = ON");
    }

    public void close() {
        mDBHelper.close();
    }

    public Term createTerm(Term term) {
        ContentValues values = term.toValues();
        mDatabase.insert(TermsTable.TABLE_TERMS, null, values);
        return term;
    }

    public Course createCourse(Course course) {
        ContentValues values = course.toValues();
        mDatabase.insert(CoursesTable.TABLE_COURSES, null, values);
        return course;
    }

    public Assessment createAssesssment(Assessment assessment) {
        ContentValues values = assessment.toValues();
        mDatabase.insert(AssessmentsTable.TABLE_ASSESSMENTS, null, values);
        return assessment;
    }

    public Enrollment createEnrollment(Enrollment enrollment) {
        ContentValues values = enrollment.toValues();
        mDatabase.insert(EnrollmentsTable.TABLE_ENROLLMENTS, null, values);
        return enrollment;
    }

    public Term updateTerm(Term term) {
        ContentValues values = term.toValues();

        String[] id = {Integer.toString(term.getId())};

        mDatabase.update(TermsTable.TABLE_TERMS,  values,TermsTable.COLUMN_ID + "=?",id);
        return term;
    }

    public Course updateCourse(Course course) {
        ContentValues values = course.toValues();

        String[] id = {Integer.toString(course.getId())};

        mDatabase.update(CoursesTable.TABLE_COURSES, values, CoursesTable.COLUMN_ID + "=?",id);
        return course;
    }

    public Assessment updateAssesssment(Assessment assessment) {
        ContentValues values = assessment.toValues();

        String[] id = {Integer.toString(assessment.getId())};

        mDatabase.update(AssessmentsTable.TABLE_ASSESSMENTS, values,AssessmentsTable.COLUMN_ID + "=?",id);
        return assessment;
    }

    public Enrollment updateEnrollment(Enrollment enrollment) {
        ContentValues values = enrollment.toValues();

        String[] id = {Integer.toString(enrollment.getTermId()),Integer.toString(enrollment.getCourseId())};

        mDatabase.update(EnrollmentsTable.TABLE_ENROLLMENTS, values,EnrollmentsTable.COLUMN_TERMID+ "=?"+EnrollmentsTable.COLUMN_COURSEID+ "=?",id);
        return enrollment;
    }

    public void deleteTerm(int termId){

        String[] args = {String.valueOf(termId)};

        mDatabase.delete(TermsTable.TABLE_TERMS,TermsTable.COLUMN_ID + "=?",args);
    }

    public void deleteCourse(int courseId){

        String[] args = {String.valueOf(courseId)};

        mDatabase.delete(CoursesTable.TABLE_COURSES,CoursesTable.COLUMN_ID + "=?",args);
    }

    public void deleteAssessment(int assessmentId){

        String[] args = {String.valueOf(assessmentId)};

        mDatabase.delete(AssessmentsTable.TABLE_ASSESSMENTS,AssessmentsTable.COLUMN_ID + "=?",args);
    }

    public void deleteEnrollment(int termId, int courseId){
        String[] args = {String.valueOf(termId),String.valueOf(courseId)};

        mDatabase.delete(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.COLUMN_TERMID + "=? AND " +EnrollmentsTable.COLUMN_COURSEID+"=?",args);
    }

    public ArrayList<Term> getAllTerms(){

        Cursor cursor = mDatabase.query(TermsTable.TABLE_TERMS,TermsTable.ALL_COLUMNS,
                null,null,null,null,TermsTable.COLUMN_STARTDATE);

        ArrayList<Term> allTerms = new ArrayList<>();

        while (cursor.moveToNext()) {

            Term nextTerm = null;
            try {
                nextTerm = new Term(
                        cursor.getInt(cursor.getColumnIndex(TermsTable.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NAME)),
                        df.parse((cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_STARTDATE)))),
                        df.parse(cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_ENDDATE))),
                        cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NOTES)),

                        getTermCourses(cursor.getInt(cursor.getColumnIndex(TermsTable.COLUMN_ID)))
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            allTerms.add(nextTerm);
        }

        return allTerms;
    }

    public Term getTerm(int termId){
        Term term = new Term();
        if (termId >= 0) {
            String[] selection = {String.valueOf(termId)};

            Cursor cursor = mDatabase.query(TermsTable.TABLE_TERMS, TermsTable.ALL_COLUMNS,
                    TermsTable.COLUMN_ID + "=?", selection, null, null, null);

            cursor.moveToNext();

            term = null;
            try {
                term = new Term(
                        cursor.getInt(cursor.getColumnIndex(TermsTable.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NAME)),
                        df.parse(cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_STARTDATE))),
                        df.parse(cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_ENDDATE))),
                        cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NOTES)),

                        getTermCourses(termId)
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return term;
    }

    public ArrayList<Course> getAllCourses(){
        ArrayList<Course> allCourses = new ArrayList<>();

        Cursor cursor = mDatabase.query(CoursesTable.TABLE_COURSES,CoursesTable.ALL_COLUMNS,
                null,null,null,null,CoursesTable.COLUMN_NAME);

        while (cursor.moveToNext()) {

            Course nextCourse = new Course(
                    cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(CoursesTable.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(CoursesTable.COLUMN_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_CREDITVALUE)),
                    null,
                    null,
                    cursor.getString(cursor.getColumnIndex(TermsTable.COLUMN_NOTES)),
                    getCourseAssessments(cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_ID))),
                    isCoursePassed(cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_ID)))
            );

            allCourses.add(nextCourse);
        }

        return allCourses;
    }

    public ArrayList<Course> getUnenrolledCourses(){

        ArrayList<Course> enrolledCourses = new ArrayList<>();

        ArrayList<Course> allCourses = new ArrayList<>(getAllCourses());
        String sql = "SELECT DISTINCT "+EnrollmentsTable.COLUMN_COURSEID+" AS _id FROM "+EnrollmentsTable.TABLE_ENROLLMENTS+";";

        Cursor cursor = mDatabase.rawQuery(sql,null);

        while (cursor.moveToNext()){
            Course testCourse = getCourse(cursor.getInt(cursor.getColumnIndex("_id")));
            for (Course course : allCourses){
                if (course.getId() == testCourse.getId())
                    enrolledCourses.add(course);
            }
        }

        allCourses.removeAll(enrolledCourses);

        return allCourses;
    }

    public Course getCourse(int courseId){
        Course course = new Course();
        if(courseId >= 0) {
            String[] selection = {String.valueOf(courseId)};

            Cursor cursor = mDatabase.query(CoursesTable.TABLE_COURSES, CoursesTable.ALL_COLUMNS,
                    CoursesTable.COLUMN_ID + "=?", selection, null, null, null);

            cursor.moveToNext();
            course = new Course(
                    cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(CoursesTable.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(CoursesTable.COLUMN_DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(CoursesTable.COLUMN_CREDITVALUE)),
                    null,
                    null,
                    cursor.getString(cursor.getColumnIndex(CoursesTable.COLUMN_NOTES)),
                    getCourseAssessments(courseId),
                    isCoursePassed(courseId)
            );
        }

        return course;
    }

    public ArrayList<Assessment> getAllAssessments(){

        ArrayList<Assessment> allAssessments = new ArrayList<>();

        Cursor cursor = mDatabase.query(AssessmentsTable.TABLE_ASSESSMENTS,AssessmentsTable.ALL_COLUMNS,
                null,null,null,null,null);

        while (cursor.moveToNext()) {

            boolean passFail = false;

            if (cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_PASSED)) == 1)
                passFail = true;

            Assessment assessment = new Assessment(
                    cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_COURSEID)),
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NOTES)),
                    passFail
            );
            allAssessments.add(assessment);
        }
        return allAssessments;
    }

    public Assessment getAssessment(int assessmentId){

        String[] selection = {String.valueOf(assessmentId)};

        Cursor cursor = mDatabase.query(AssessmentsTable.TABLE_ASSESSMENTS,AssessmentsTable.ALL_COLUMNS,
                AssessmentsTable.COLUMN_ID +"=?",selection,null,null,null);

        cursor.moveToNext();

        boolean passFail = false;

        if (cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_PASSED)) == 1)
            passFail = true;

        Assessment assessment = new Assessment(
            cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_ID)),
            cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_COURSEID)),
            cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NOTES)),
            passFail
        );

        return assessment;
    }

    public Assessment getEmptyAssessment(int courseId){
        return new Assessment(courseId);
    }

    public Enrollment getEnrollment(int termId, int courseId){

        Enrollment enrollment = null;

        String[] selection = {String.valueOf(termId),String.valueOf(courseId)};

        Cursor cursor = mDatabase.query(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.ALL_COLUMNS,
                EnrollmentsTable.COLUMN_TERMID +"=? AND "+EnrollmentsTable.COLUMN_COURSEID+"=?",selection,null,null,null);

        while(cursor.moveToNext()) {

            boolean passed = false;

            if (cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_PASSED)) == 1)
                passed = true;

            try {
                enrollment = new Enrollment(
                        cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_TERMID)),
                        cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_COURSEID)),
                        df.parse(cursor.getString(cursor.getColumnIndex(EnrollmentsTable.COLUMN_STARTDATE))),
                        df.parse(cursor.getString(cursor.getColumnIndex(EnrollmentsTable.COLUMN_ENDDATE))),
                        passed
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return enrollment;
    }

    public boolean isTermPassed(int termId){

        String[] selection = {String.valueOf(termId)};

        Cursor cursor = mDatabase.query(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.ALL_COLUMNS,
                EnrollmentsTable.COLUMN_TERMID +"=?",selection,null,null,null);

        while(cursor.moveToNext()){
            if (cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_PASSED)) == 0)
                return false;
        }
        return true;
    }

    public boolean isCoursePassed(int termId, int courseId){

        String[] selection = {String.valueOf(termId),String.valueOf(courseId)};

        Cursor cursor = mDatabase.query(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.ALL_COLUMNS,
                EnrollmentsTable.COLUMN_TERMID +"=? AND "+EnrollmentsTable.COLUMN_COURSEID+"=?",selection,null,null,null);

        return (cursor.moveToNext() && !(cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_PASSED)) == 0));
    }

    public boolean isCoursePassed(int courseId){

        String[] selection = {String.valueOf(courseId)};

        Cursor cursor = mDatabase.query(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.ALL_COLUMNS,
                EnrollmentsTable.COLUMN_COURSEID+"=?",selection,null,null,null);

        while(cursor.moveToNext()){
            if (cursor.getInt(cursor.getColumnIndex(EnrollmentsTable.COLUMN_PASSED)) == 1)
                return true;
        }
        return false;
    }

    public void setCourseEnrollmentPass(int termId, int courseId, boolean passed){

        String[] selection = {String.valueOf(termId),String.valueOf(courseId)};

        int passedInt = passed ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(EnrollmentsTable.COLUMN_PASSED,passedInt);

        mDatabase.update(EnrollmentsTable.TABLE_ENROLLMENTS,cv,EnrollmentsTable.COLUMN_TERMID +"=? AND "+EnrollmentsTable.COLUMN_COURSEID+"=?",selection);

    }

    public ArrayList<Course> getTermCourses(int termId){

        ArrayList<Course> termCourses = new ArrayList<>();

        String[] term = {String.valueOf(termId)};

        Cursor cursorEnrollment = mDatabase.query(EnrollmentsTable.TABLE_ENROLLMENTS,EnrollmentsTable.ALL_COLUMNS,
                EnrollmentsTable.COLUMN_TERMID +" =?",term,null,null,null);

        while(cursorEnrollment.moveToNext()){

            String[] course = {String.valueOf(cursorEnrollment.getInt(cursorEnrollment.getColumnIndex(EnrollmentsTable.COLUMN_COURSEID)))};

            Cursor cursorCourse = mDatabase.query(CoursesTable.TABLE_COURSES,CoursesTable.ALL_COLUMNS,
                    CoursesTable.COLUMN_ID +" =?",course,null,null,null);

            while(cursorCourse.moveToNext()){
                boolean passVal;
                if (cursorEnrollment.getInt(cursorEnrollment.getColumnIndex(EnrollmentsTable.COLUMN_PASSED)) == 1)
                    passVal=true;
                else
                    passVal=false;

                try {
                    Course newCourse = new Course(
                            cursorCourse.getInt(cursorCourse.getColumnIndex(CoursesTable.COLUMN_ID)),
                            cursorCourse.getString(cursorCourse.getColumnIndex(CoursesTable.COLUMN_NAME)),
                            cursorCourse.getString(cursorCourse.getColumnIndex(CoursesTable.COLUMN_DESCRIPTION)),
                            cursorCourse.getInt(cursorCourse.getColumnIndex(CoursesTable.COLUMN_CREDITVALUE)),

                            df.parse(cursorEnrollment.getString(cursorEnrollment.getColumnIndex(EnrollmentsTable.COLUMN_STARTDATE))),
                            df.parse(cursorEnrollment.getString(cursorEnrollment.getColumnIndex(EnrollmentsTable.COLUMN_ENDDATE))),

                            cursorCourse.getString(cursorCourse.getColumnIndex(CoursesTable.COLUMN_NOTES)),

                            getCourseAssessments(cursorCourse.getInt(cursorCourse.getColumnIndex(CoursesTable.COLUMN_ID))),
                            passVal,
                            termId
                    );
                    termCourses.add(newCourse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return termCourses;
    }

    public ArrayList<Assessment> getCourseAssessments(int courseId){
        ArrayList<Assessment> courseAssessments = new ArrayList<>();

        String[] course = {String.valueOf(courseId)};

        Cursor cursor = mDatabase.query(AssessmentsTable.TABLE_ASSESSMENTS,AssessmentsTable.ALL_COLUMNS,
                AssessmentsTable.COLUMN_COURSEID +" =?",course,null,null,null);

        while(cursor.moveToNext()){
            boolean passVal;
            if (cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_PASSED)) == 1)
                passVal=true;
            else
                passVal=false;

            Date d = null;

            if (cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_DUEDATE)) != null){
                try {
                    d = df.parse(cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_DUEDATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Assessment newAssessment = null;
            newAssessment = new Assessment(
                    cursor.getInt(cursor.getColumnIndex(AssessmentsTable.COLUMN_ID)),
                    courseId,
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(AssessmentsTable.COLUMN_NOTES)),
                    passVal,
                    d
            );
            courseAssessments.add(newAssessment);
        }

        return courseAssessments;
    }

    public long getTermCount() {
        return DatabaseUtils.queryNumEntries(mDatabase, TermsTable.TABLE_TERMS);
    }

    public long getCourseCount() {
        return DatabaseUtils.queryNumEntries(mDatabase, CoursesTable.TABLE_COURSES);
    }

    public long getAssessmentCount() {
        return DatabaseUtils.queryNumEntries(mDatabase, AssessmentsTable.TABLE_ASSESSMENTS);
    }

    public long getEnrollmentCount() {
        return DatabaseUtils.queryNumEntries(mDatabase, EnrollmentsTable.TABLE_ENROLLMENTS);
    }

    public boolean seedDatabase(Context context) {

        ArrayList<Term> studentTerms = new ArrayList<>(sampleTermData());

        if(getTermCount() == 0 && getCourseCount() == 0) {
            for (Term term : studentTerms){
                try {
                    createTerm(term);
                    for (Course course : term.getCourses()) {
                        createCourse(course);
                        createEnrollment(new Enrollment(term, course));
                        for (Assessment assessment : course.getAssessments()) {
                            createAssesssment(assessment);
                        }
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            setCourseEnrollmentPass(0,0,true);
            setCourseEnrollmentPass(0,1,true);
            setCourseEnrollmentPass(1,5,true);
            return true;
        } else {
            Toast.makeText(context,"The database contains data. Clear database before seeding new data.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public ArrayList<Term> sampleTermData(){
        ArrayList<Term> studentTerms = new ArrayList<>();
        /**SAMPLE TERM DATA**/
        /**Courses created**/
        Course course1 = new Course(0,"Math I","Introduction to Algebra",3, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");
        Course course2 = new Course(1,"Math II","Advanced Algebra concepts",3, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");
        Course course3 = new Course(2,"Math III","Actual rocket science",3, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");
        Course course4 = new Course(3,"Geology I","Hank Schrader was here",4, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");
        Course course5 = new Course(4,"Geology II","In-depth study on the physical and molecular structure of the Infinity Stones",6, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");
        Course course6 = new Course(5,"Chicken Wrangling 101","You will learn the ins and outs of chicken wrangling from a seasoned professional",6, null, null,"Mentor: Jerry Seinfeld\nPhone: 555-555-5555\nEmail: jseinfeld@mail.com");

        /**Assessments created**/
        Assessment assessment1 = new Assessment(0,0,"Math I Final", "Objective Assessment","Algebra I test");
        Assessment assessment2 = new Assessment(1,1,"Math II Final","Objective Assessment","Advanced Algebra II concept test");
        Assessment assessment3 = new Assessment(2,2,"Math III Final","Objective Assessment","Good luck. It really IS rocket science!");
        Assessment assessment4 = new Assessment(3,3,"Geology I Final","Objective Assessment","They're not rocks, they're minerals!");
        Assessment assessment5 = new Assessment(4,4,"Geology II Final","Objective Assessment","Explain the infinity stones. Go.");
        Assessment assessment6 = new Assessment(5,5,"Chicken Wrangling 101 Final","Performance Assessment","You must wrangle a live chicken in front of your fellow classmates.");
        Assessment assessment7 = new Assessment(6,5,"Chicken Wrangling 101 Final Pt. 2","Performance Assessment","You must wrangle a live chicken in front of your fellow classmates while juggling.");

        /**Assessments added to courses**/
        course1.addAssessment(assessment1);
        course2.addAssessment(assessment2);
        course3.addAssessment(assessment3);
        course4.addAssessment(assessment4);
        course5.addAssessment(assessment5);
        course6.addAssessment(assessment6);
        course6.addAssessment(assessment7);

        /**Terms created**/
        Term term1 = new Term(0,"Term 1",
                new GregorianCalendar(2017, Calendar.JUNE,1).getTime(),
                new GregorianCalendar(2017,Calendar.DECEMBER,31).getTime(),"This is my first term. These are placeholder notes.");
        Term term2 = new Term(1,"Term 2",
                new GregorianCalendar(2018,Calendar.JANUARY,1).getTime(),
                new GregorianCalendar(2018,Calendar.JUNE,30).getTime(),"Term 2 placeholder notes.");

        /**Courses added to terms**/
        term1.addCourse(course1);
        term1.addCourse(course2);
        term1.addCourse(course3);
        term1.addCourse(course4);

        term2.addCourse(course5);
        term2.addCourse(course6);

        /**Terms added to ArrayList**/
        studentTerms.add(term1);
        studentTerms.add(term2);

        return studentTerms;
        /**SAMPLE TERM DATA**/
    }

}
