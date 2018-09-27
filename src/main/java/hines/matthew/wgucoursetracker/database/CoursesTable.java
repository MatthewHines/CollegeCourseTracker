package hines.matthew.wgucoursetracker.database;

public class CoursesTable {

    public static final String TABLE_COURSES = "courses";
    public static final String COLUMN_ID = "courseId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREDITVALUE = "creditValue";
    public static final String COLUMN_NOTES = "notes";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID,COLUMN_NAME,COLUMN_DESCRIPTION,COLUMN_CREDITVALUE,COLUMN_NOTES};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_CREDITVALUE + " INTEGER NOT NULL," +
                    COLUMN_NOTES + " TEXT" +
                    ");";
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_COURSES;
}
