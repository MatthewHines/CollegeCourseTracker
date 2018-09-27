package hines.matthew.wgucoursetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Course;

public class CourseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText courseTitle;
    EditText courseStartDate;
    TextView dateHyphen;
    EditText courseEndDate;
    EditText courseNotes;
    Course selectedCourse;
    TextView courseTerm;
    EditText courseDescription;
    EditText courseCredits;
    ImageButton saveEdit;
    ImageButton cancelEdit;
    ImageButton toggleEdit;
    FloatingActionButton fab;

    RecyclerView recyclerView;
    AssessmentRecyclerViewAdapter mAdapter;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        mDataSource = new DataSource(this);
        mDataSource.open();
        Toast.makeText(this,"Database acquired.",Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        courseTitle = (EditText) findViewById(R.id.course_title);
        courseNotes = (EditText) findViewById(R.id.course_notes);
        courseDescription = (EditText) findViewById(R.id.course_description);
        courseStartDate = (EditText) findViewById(R.id.course_start);
        dateHyphen = (TextView) findViewById(R.id.dates_hyphen);
        courseEndDate = (EditText) findViewById(R.id.course_end);
        courseTerm = (TextView) findViewById(R.id.course_term);
        toggleEdit = (ImageButton) findViewById(R.id.edit_course);
        saveEdit = (ImageButton) findViewById(R.id.save_course);
        cancelEdit = (ImageButton) findViewById(R.id.cancel_edit_course);
        toggleEdit = (ImageButton) findViewById(R.id.edit_course);
        courseCredits = (EditText) findViewById(R.id.course_credits);

        setSupportActionBar(toolbar);
        dataSetup();

        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isCourseValid()) {
                    selectedCourse.setName(courseTitle.getText().toString());
                    try {
                        selectedCourse.setStartDate(df.parse(courseStartDate.getText().toString()));
                        selectedCourse.setEndDate(df.parse(courseEndDate.getText().toString()));
                    } catch (ParseException e) {
                        Log.d("SQL_EXCEPTION", e.toString());
                        Toast.makeText(saveEdit.getContext(), "Update failed - SQL Exception", Toast.LENGTH_LONG).show();
                    }
                    selectedCourse.setCreditValue(Integer.valueOf(courseCredits.getText().toString()));
                    selectedCourse.setNotes(courseNotes.getText().toString());
                    mDataSource.updateCourse(selectedCourse);
                    dataSetup();
                    disableEdits();
                    Toast.makeText(saveEdit.getContext(), "Changes saved successfully.", Toast.LENGTH_LONG).show();
                }

            }
        });

        cancelEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dataSetup();
                disableEdits();
            }
        });

        toggleEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                enableEdits();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),AssessmentActivity.class);
                intent.putExtra("courseId",selectedCourse.getId());
                finish();
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(selectedCourse.getAssessments() != null)
            setupRecyclerView();

        disableEdits();

        if (selectedCourse.getId() < 0)
            newCourseSetup();
    }

    private void newCourseSetup(){
        enableEdits();

        cancelEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),CourseListActivity.class);
                finish();
                startActivity(intent);
            }
        });

        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isCourseValid()) {
                    selectedCourse.setName(courseTitle.getText().toString());
                    if (courseStartDate.getVisibility() == View.VISIBLE) {
                        try {
                            selectedCourse.setStartDate(df.parse(courseStartDate.getText().toString()));
                            selectedCourse.setEndDate(df.parse(courseEndDate.getText().toString()));
                        } catch (ParseException e) {
                            Log.d("SQL_EXCEPTION", e.toString());
                            Toast.makeText(saveEdit.getContext(), "Update failed - SQL Exception", Toast.LENGTH_LONG).show();
                        }
                    }
                    selectedCourse.setNotes(courseNotes.getText().toString());
                    selectedCourse.setDescription(courseDescription.getText().toString());
                    selectedCourse.setCreditValue(Integer.valueOf(courseCredits.getText().toString()));
                    mDataSource.createCourse(selectedCourse);
                    Toast.makeText(saveEdit.getContext(), "New course saved successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(),CourseListActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });

        fab.setVisibility(View.GONE);

    }

    private void setupRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.assessment_recycler_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new AssessmentRecyclerViewAdapter(selectedCourse.getAssessments());
        recyclerView.setAdapter(mAdapter);
    }

    private boolean isCourseValid(){

        try {
            if(courseStartDate.getVisibility() == View.VISIBLE) {
                if (df.parse(courseStartDate.getText().toString()).after(df.parse(courseEndDate.getText().toString()))) {
                    Toast.makeText(saveEdit.getContext(), "Update failed\nCourse end date must be on or after term start date.", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            if (courseTitle.getText().toString().matches("")){
                Toast.makeText(saveEdit.getContext(), "Update failed\nCourse title cannot be empty.", Toast.LENGTH_LONG).show();
                return false;
            }
            if (courseCredits.getText().toString().matches("") || Integer.valueOf(courseCredits.getText().toString()) < 0 || Integer.valueOf(courseCredits.getText().toString()) > 12){
                Toast.makeText(saveEdit.getContext(), "Update failed\nCourse credits cannot be empty.\nMust be a value between 0 and 12.", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(saveEdit.getContext(), "Update failed - Unable to parse dates", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void disableEdits(){
        courseTitle.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        courseTitle.setFocusableInTouchMode(false);
        courseTitle.setFocusable(false);
        courseDescription.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        courseDescription.setFocusableInTouchMode(false);
        courseDescription.setFocusable(false);
        courseNotes.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        courseNotes.setFocusableInTouchMode(false);
        courseNotes.setFocusable(false);
        courseCredits.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        courseCredits.setFocusableInTouchMode(false);
        courseCredits.setFocusable(false);
        courseCredits.setText(String.format(getResources().getString(R.string.course_credits),selectedCourse.getCreditValue()));
        courseStartDate.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        courseEndDate.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        toggleEdit.setVisibility(View.VISIBLE);
        cancelEdit.setVisibility(View.GONE);
        saveEdit.setVisibility(View.GONE);
        if (selectedCourse.getAssessments() != null) {
            mAdapter.endEdit();
        }
        disableDateEdits();
    }

    private void enableEdits(){
        courseTitle.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        courseTitle.setFocusableInTouchMode(true);
        courseTitle.setFocusable(true);
        courseDescription.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        courseDescription.setFocusableInTouchMode(true);
        courseDescription.setFocusable(true);
        courseNotes.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        courseNotes.setFocusableInTouchMode(true);
        courseNotes.setFocusable(true);
        courseCredits.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        courseCredits.setFocusableInTouchMode(true);
        courseCredits.setFocusable(true);
        courseCredits.setText(Integer.toString(selectedCourse.getCreditValue()));
        courseStartDate.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        courseEndDate.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        toggleEdit.setVisibility(View.GONE);
        cancelEdit.setVisibility(View.VISIBLE);
        saveEdit.setVisibility(View.VISIBLE);
        if (selectedCourse.getAssessments() != null) {
            mAdapter.editMode();
        }
        enableDateEdits();
    }

    private void dataSetup(){
        selectedCourse = mDataSource.getCourse(getIntent().getIntExtra("courseId",0));

        if(getIntent().getIntExtra("termId",-1) >= 0)
            selectedCourse.setTermId(getIntent().getIntExtra("termId",-1));

        if (selectedCourse.getTermId() >= 0){
            courseTerm.setVisibility(View.VISIBLE);
            courseTerm.setText(mDataSource.getTerm(selectedCourse.getTermId()).getName());
            selectedCourse.setStartDate(mDataSource.getEnrollment(selectedCourse.getTermId(),selectedCourse.getId()).getStartDate());
            selectedCourse.setEndDate(mDataSource.getEnrollment(selectedCourse.getTermId(),selectedCourse.getId()).getEndDate());
        }

        if (selectedCourse.getName() != null)
            courseTitle.setText(String.format(getResources().getString(R.string.course_title),selectedCourse.getName()));
        else
            courseTitle.setText("");

        if (selectedCourse.getStartDate() != null) {
            courseStartDate.setVisibility(View.VISIBLE);
            courseStartDate.setText(df.format(selectedCourse.getStartDate()));
            dateHyphen.setVisibility(View.VISIBLE);
            courseEndDate.setVisibility(View.VISIBLE);
            courseEndDate.setText(df.format(selectedCourse.getEndDate()));
        }
        courseCredits.setText(String.format(getResources().getString(R.string.course_credits),selectedCourse.getCreditValue()));
        courseDescription.setText(selectedCourse.getDescription());
        courseNotes.setText(selectedCourse.getNotes());

    }

    public void enableDateEdits(){
        courseStartDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH) + 1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(courseStartDate.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                        courseStartDate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(mDataSource.getTerm(selectedCourse.getTermId()).getStartDate().getTime());
                datePicker.getDatePicker().setMaxDate(mDataSource.getTerm(selectedCourse.getTermId()).getEndDate().getTime());
                datePicker.show();
            }
        });

        courseEndDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH) +1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(courseEndDate.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                        courseEndDate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(mDataSource.getTerm(selectedCourse.getTermId()).getStartDate().getTime());
                datePicker.getDatePicker().setMaxDate(mDataSource.getTerm(selectedCourse.getTermId()).getEndDate().getTime());
                datePicker.show();
            }
        });

    }

    public void disableDateEdits(){
        courseStartDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Do nothing
            }
        });

        courseEndDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Do nothing
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataSource.open();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reminder_start) {
            if(selectedCourse.getStartDate() != null) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", selectedCourse.getStartDate().getTime());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", selectedCourse.getEndDate().getTime() + 60 * 60 * 1000);
                intent.putExtra("title", selectedCourse.getName() + " start date");
                startActivity(intent);
            }else
                Toast.makeText(getBaseContext(), "Please enter a start date to set a reminder.", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_reminder_end) {
            if(selectedCourse.getEndDate() != null) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", selectedCourse.getEndDate().getTime());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", selectedCourse.getEndDate().getTime() + 60 * 60 * 1000);
                intent.putExtra("title", selectedCourse.getName() + " end date");
                startActivity(intent);
            }else
                Toast.makeText(getBaseContext(), "Please enter an end date to set a reminder.", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_terms) {
            // Handle the term list view action (Effectively, this returns you home.)
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_courses) {
            // handle course list view action
            //indicate if ever passed & if enrolled.
            Intent intent = new Intent(this,CourseListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_assessments) {
            // view all assessment for enrolled courses
            Intent intent = new Intent(this,AssessmentListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Here are my notes for "+selectedCourse.getName()+": "+selectedCourse.getNotes());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"Sent to:"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
