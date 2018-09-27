package hines.matthew.wgucoursetracker.database;

public class AssessmentsTable {

    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String COLUMN_ID = "assessmentId";
    public static final String COLUMN_COURSEID = "courseId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_PASSED = "passed";
    public static final String COLUMN_DUEDATE = "dueDate";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID,COLUMN_COURSEID,COLUMN_NAME,COLUMN_DESCRIPTION,COLUMN_NOTES,COLUMN_PASSED,COLUMN_DUEDATE};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ASSESSMENTS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_COURSEID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_PASSED + " INTEGER DEFAULT 0, " +
                    COLUMN_DUEDATE + " TEXT ," +
                    "FOREIGN KEY (" + COLUMN_COURSEID + ") REFERENCES "+CoursesTable.TABLE_COURSES+" (" + CoursesTable.COLUMN_ID + ") ON DELETE CASCADE" +
                    ");";
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_ASSESSMENTS;
}
