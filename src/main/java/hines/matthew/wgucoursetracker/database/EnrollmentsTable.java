package hines.matthew.wgucoursetracker.database;

public class EnrollmentsTable {
    public static final String TABLE_ENROLLMENTS = "enrollment";
    public static final String COLUMN_TERMID = "termId";
    public static final String COLUMN_COURSEID = "courseId";
    public static final String COLUMN_STARTDATE = "startDate";
    public static final String COLUMN_ENDDATE = "endDate";
    public static final String COLUMN_PASSED = "passed";

    public static final String[] ALL_COLUMNS =
            {COLUMN_TERMID,COLUMN_COURSEID,COLUMN_STARTDATE,COLUMN_ENDDATE,COLUMN_PASSED};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ENROLLMENTS + " (" +
                    COLUMN_TERMID + " INTEGER, " +
                    COLUMN_COURSEID + " INTEGER, " +
                    COLUMN_STARTDATE + " TEXT NOT NULL, " +
                    COLUMN_ENDDATE + " TEXT NOT NULL, " +
                    COLUMN_PASSED + " INTEGER NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY (" + COLUMN_TERMID + ","+COLUMN_COURSEID+") " +
                    "FOREIGN KEY (" + COLUMN_COURSEID + ") REFERENCES "+CoursesTable.TABLE_COURSES+" (" + CoursesTable.COLUMN_ID + ") ON DELETE CASCADE " +
                    "FOREIGN KEY (" + COLUMN_TERMID + ") REFERENCES "+TermsTable.TABLE_TERMS+" (" + TermsTable.COLUMN_ID + ") ON DELETE CASCADE " +
                    ");";
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_ENROLLMENTS;
}
