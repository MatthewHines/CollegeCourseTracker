package hines.matthew.wgucoursetracker.database;

public class TermsTable {

    public static final String TABLE_TERMS = "terms";
    public static final String COLUMN_ID = "termId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STARTDATE = "startDate";
    public static final String COLUMN_ENDDATE = "endDate";
    public static final String COLUMN_NOTES = "notes";

    public static final String[] ALL_COLUMNS =
            {COLUMN_ID,COLUMN_NAME,COLUMN_STARTDATE,COLUMN_ENDDATE,COLUMN_NOTES};

    public static final String SQL_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_STARTDATE + " TEXT NOT NULL," +
                    COLUMN_ENDDATE + " TEXT NOT NULL," +
                    COLUMN_NOTES + " TEXT" +
                    ");";
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_TERMS;

}
