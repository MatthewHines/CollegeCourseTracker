package hines.matthew.wgucoursetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import hines.matthew.wgucoursetracker.model.Assessment;

public class AssessmentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText assessmentTitle;
    EditText assessmentDescription;
    EditText assessmentNotes;
    Assessment selectedAssessment;
    TextView assessmentCourse;
    TextView status;
    ImageButton saveEdit;
    ImageButton cancelEdit;
    ImageButton toggleEdit;
    EditText assessmentDueDate;

    private DataSource mDataSource;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assessmentTitle = (EditText) findViewById(R.id.assessment_title);
        assessmentNotes = (EditText) findViewById(R.id.assessment_notes);
        assessmentDescription = (EditText) findViewById(R.id.assessment_description);
        assessmentCourse = (TextView) findViewById(R.id.assessment_course);
        status = (TextView) findViewById(R.id.assessment_passed);
        toggleEdit = (ImageButton) findViewById(R.id.edit_assessment);
        cancelEdit = (ImageButton) findViewById(R.id.cancel_edit_assessment);
        saveEdit = (ImageButton) findViewById(R.id.save_assessment);
        assessmentDueDate = (EditText) findViewById(R.id.assessment_due_date);
        setSupportActionBar(toolbar);

        mDataSource = new DataSource(this);
        mDataSource.open();

        dataSetup();

        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!assessmentTitle.getText().toString().matches("")) {
                    selectedAssessment.setName(assessmentTitle.getText().toString());
                    selectedAssessment.setNotes(assessmentNotes.getText().toString());
                    selectedAssessment.setDescription(assessmentDescription.getText().toString());
                    try {
                        selectedAssessment.setDueDate(df.parse(assessmentDueDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDataSource.updateAssesssment(selectedAssessment);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (selectedAssessment.getId() < 0)
            newAssessmentSetup();
    }

    private void newAssessmentSetup(){
        enableEdits();

        assessmentDueDate.setText(df.format(Calendar.getInstance().getTime()));

        cancelEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getBaseContext(),CourseActivity.class);
                intent.putExtra("courseId",getIntent().getIntExtra("courseId",0));
                finish();
                startActivity(intent);
            }
        });

        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isAssessmentValid()) {
                    selectedAssessment.setName(assessmentTitle.getText().toString());
                    selectedAssessment.setCourseId(getIntent().getIntExtra("courseId",0));
                    selectedAssessment.setNotes(assessmentNotes.getText().toString());
                    selectedAssessment.setDescription(assessmentDescription.getText().toString());
                    try {
                        selectedAssessment.setDueDate(df.parse(assessmentDueDate.getText().toString()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mDataSource.createAssesssment(selectedAssessment);
                    Toast.makeText(saveEdit.getContext(), "New assessment saved successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(),CourseActivity.class);
                    intent.putExtra("courseId",getIntent().getIntExtra("courseId",0));
                    finish();
                    startActivity(intent);
                }

            }
        });

    }

    public void enableDateEdits(){
        assessmentDueDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH) + 1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(assessmentDueDate.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                        assessmentDueDate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

    }

    private boolean isAssessmentValid(){

        if (assessmentTitle.getText().toString().matches("")){
            Toast.makeText(saveEdit.getContext(), "Update failed\nAssessment title cannot be empty.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    private void dataSetup(){

        int id = getIntent().getIntExtra("assessmentId",-1);

        if (id >= 0)
            selectedAssessment = mDataSource.getAssessment(getIntent().getIntExtra("assessmentId",0));
        else {
            selectedAssessment = mDataSource.getEmptyAssessment(getIntent().getIntExtra("courseId", 0));
            selectedAssessment.setDueDate(Calendar.getInstance().getTime());
        }

        assessmentCourse.setText(mDataSource.getCourse(selectedAssessment.getCourseId()).getName());
        assessmentTitle.setText(selectedAssessment.getName());
        assessmentDescription.setText(selectedAssessment.getDescription());
        assessmentNotes.setText(selectedAssessment.getNotes());
        if(selectedAssessment.getDueDate() != null) {
            try {
                selectedAssessment.setDueDate(df.parse(assessmentDueDate.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else
            selectedAssessment.setDueDate(Calendar.getInstance().getTime());
        if (selectedAssessment.isPassed())
            status.setText(String.format(getResources().getString(R.string.assessment_passed),"Passed"));
        else
            status.setText(String.format(getResources().getString(R.string.assessment_passed),"Not Passed"));

    }

    private void enableEdits(){
        assessmentTitle.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        assessmentTitle.setFocusableInTouchMode(true);
        assessmentTitle.setFocusable(true);
        assessmentNotes.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        assessmentNotes.setFocusableInTouchMode(true);
        assessmentNotes.setFocusable(true);
        assessmentDescription.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        assessmentDescription.setFocusableInTouchMode(true);
        assessmentDescription.setFocusable(true);
        assessmentDueDate.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        toggleEdit.setVisibility(View.GONE);
        cancelEdit.setVisibility(View.VISIBLE);
        saveEdit.setVisibility(View.VISIBLE);
        enableDateEdits();
    }

    private void disableEdits(){
        assessmentTitle.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        assessmentTitle.setFocusableInTouchMode(false);
        assessmentTitle.setFocusable(false);
        assessmentNotes.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        assessmentNotes.setFocusableInTouchMode(false);
        assessmentNotes.setFocusable(false);
        assessmentDescription.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        assessmentDescription.setFocusableInTouchMode(false);
        assessmentDescription.setFocusable(false);
        assessmentDueDate.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        toggleEdit.setVisibility(View.VISIBLE);
        cancelEdit.setVisibility(View.GONE);
        saveEdit.setVisibility(View.GONE);
        disableDateEdits();
    }

    public void disableDateEdits(){
        assessmentDueDate.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Do nothing
            }
        });
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
        getMenuInflater().inflate(R.menu.assessment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            //Delete assessment
            return true;
        }else if (id == R.id.action_reminder) {
            if(selectedAssessment.getDueDate() != null) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", selectedAssessment.getDueDate().getTime());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", selectedAssessment.getDueDate().getTime() + 60 * 60 * 1000);
                intent.putExtra("title", selectedAssessment.getName() + " due date");
                startActivity(intent);
            } else
                Toast.makeText(getBaseContext(), "Please enter a due date to set a reminder.", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(this,AssessmentListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Here are my notes for "+selectedAssessment.getName()+": "+selectedAssessment.getNotes());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"Sent to:"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
