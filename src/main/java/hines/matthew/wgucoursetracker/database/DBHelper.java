package hines.matthew.wgucoursetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    public static final String DB_FILE_NAME = "coursetracker.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TermsTable.SQL_CREATE);
        db.execSQL(CoursesTable.SQL_CREATE);
        db.execSQL(AssessmentsTable.SQL_CREATE);
        db.execSQL(EnrollmentsTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(TermsTable.SQL_DELETE);
        db.execSQL(CoursesTable.SQL_DELETE);
        db.execSQL(AssessmentsTable.SQL_DELETE);
        db.execSQL(EnrollmentsTable.SQL_DELETE);
        onCreate(db);
    }
}
