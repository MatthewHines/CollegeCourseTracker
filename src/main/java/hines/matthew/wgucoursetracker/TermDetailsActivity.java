package hines.matthew.wgucoursetracker;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Course;
import hines.matthew.wgucoursetracker.model.Term;

public class TermDetailsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView termTitle;
    TextView termDateStart;
    TextView termDateEnd;
    EditText termNotes;
    Term selectedTerm;
    TextView termProgress;
    ProgressBar termProgressBar;
    ImageButton saveEdit;
    ImageButton cancelEdit;
    ImageButton toggleEdit;
    FloatingActionButton fab;

    RecyclerView recyclerView;
    CourseRecyclerViewAdapter mAdapter;

    int termCredits;
    int termEarnedCredits;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    DataSource mDataSource;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        termTitle = (TextView) findViewById(R.id.term_title);
        termDateStart = (TextView) findViewById(R.id.term_dates_start);
        termDateEnd = (TextView) findViewById(R.id.term_dates_end);
        termNotes = (EditText) findViewById(R.id.term_notes);
        termProgress = (TextView) findViewById(R.id.term_credit_progress);
        termProgressBar = (ProgressBar) findViewById(R.id.term_progress_bar);
        saveEdit = (ImageButton) findViewById(R.id.save_term);
        cancelEdit = (ImageButton) findViewById(R.id.cancel_edit_term);
        toggleEdit = (ImageButton) findViewById(R.id.edit_term);
        setSupportActionBar(toolbar);

        mDataSource = new DataSource(this);
        mDataSource.open();
        Toast.makeText(this,"Database acquired.",Toast.LENGTH_SHORT).show();

        dataSetup();


        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isTermValid()) {
                    selectedTerm.setName(termTitle.getText().toString());
                    try {
                        selectedTerm.setStartDate(df.parse(termDateStart.getText().toString()));
                        selectedTerm.setEndDate(df.parse(termDateEnd.getText().toString()));
                    } catch (ParseException e) {
                        Log.d("SQL_EXCEPTION", e.toString());
                        Toast.makeText(saveEdit.getContext(), "Update failed - SQL Exception", Toast.LENGTH_LONG).show();
                    }
                    selectedTerm.setNotes(termNotes.getText().toString());
                    mDataSource.updateTerm(selectedTerm);
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
                Intent intent = new Intent(getBaseContext(),CourseListActivity.class);
                intent.putExtra("termId",selectedTerm.getId());
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

        setupRecyclerView();

        if (selectedTerm.getId()<0)
            newTermSetup();

    }

    private void newTermSetup(){
        enableEdits();

        cancelEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //dataSetup();
                //disableEdits();
                Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                finish();
                startActivity(intent);
            }
        });

        saveEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (isTermValid()) {
                    selectedTerm.setName(termTitle.getText().toString());
                    try {
                        selectedTerm.setStartDate(df.parse(termDateStart.getText().toString()));
                        selectedTerm.setEndDate(df.parse(termDateEnd.getText().toString()));
                    } catch (ParseException e) {
                        Log.d("SQL_EXCEPTION", e.toString());
                        Toast.makeText(saveEdit.getContext(), "Update failed - SQL Exception", Toast.LENGTH_LONG).show();
                    }
                    selectedTerm.setNotes(termNotes.getText().toString());
                    mDataSource.createTerm(selectedTerm);
                    Toast.makeText(saveEdit.getContext(), "New term saved successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(),HomeActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        });

        fab.setVisibility(View.GONE);

    }

    private void deleteThisTerm(){
        if(selectedTerm.getCourses() == null || selectedTerm.getCourses().isEmpty()) {
            mDataSource.deleteTerm(selectedTerm.getId());
            Toast.makeText(this, "Term deleted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Term cannot be deleted with enrolled courses.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTermValid(){

        try {
            if(df.parse(termDateStart.getText().toString()).after(df.parse(termDateEnd.getText().toString()))){
                Toast.makeText(saveEdit.getContext(), "Update failed\nTerm end date must be on or after term start date.", Toast.LENGTH_LONG).show();
                return false;
            } else if (termTitle.getText().toString().matches("")){
                Toast.makeText(saveEdit.getContext(), "Update failed\nTerm title cannot be empty.", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(saveEdit.getContext(), "Update failed - Unable to parse dates. Please enter a valid date.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setupRecyclerView(){
        recyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CourseRecyclerViewAdapter(selectedTerm.getCourses(), selectedTerm.getId());
        recyclerView.setAdapter(mAdapter);
    }

    private void dataSetup(){
        selectedTerm = mDataSource.getTerm(getIntent().getIntExtra("termId",0));

        termTitle.setText((CharSequence) selectedTerm.getName());
        termDateStart.setText(df.format(selectedTerm.getStartDate()));
        termDateEnd.setText(df.format(selectedTerm.getEndDate()));
        termNotes.setText((CharSequence) selectedTerm.getNotes());

        termCredits = 0;
        termEarnedCredits = 0;

        for (Course course : selectedTerm.getCourses()){
            termCredits += course.getCreditValue();
            if (mDataSource.isCoursePassed(selectedTerm.getId(),course.getId()))
                termEarnedCredits += course.getCreditValue();
        }

        termProgress.setText(getString(R.string.term_progress,termEarnedCredits,termCredits));
        termProgressBar.setMax(termCredits);
        termProgressBar.setProgress(termEarnedCredits,true);

    }

    private void enableEdits(){
        termTitle.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        termTitle.setFocusableInTouchMode(true);
        termTitle.setFocusable(true);
        termNotes.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        termNotes.setFocusableInTouchMode(true);
        termNotes.setFocusable(true);
        termDateStart.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        termDateEnd.setBackgroundColor(getResources().getColor(R.color.white,getTheme()));
        toggleEdit.setVisibility(View.GONE);
        cancelEdit.setVisibility(View.VISIBLE);
        saveEdit.setVisibility(View.VISIBLE);
        if (selectedTerm.getCourses() != null) {
            mAdapter.editMode();
        }
        enableDateEdits();
    }

    private void disableEdits(){
        termTitle.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        termTitle.setFocusableInTouchMode(false);
        termTitle.setFocusable(false);
        termNotes.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        termNotes.setFocusableInTouchMode(false);
        termNotes.setFocusable(false);
        termDateStart.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        termDateEnd.setBackgroundColor(getResources().getColor(R.color.transparent,getTheme()));
        toggleEdit.setVisibility(View.VISIBLE);
        cancelEdit.setVisibility(View.GONE);
        saveEdit.setVisibility(View.GONE);
        if (selectedTerm.getCourses() != null) {
            mAdapter.endEdit();
        }
        disableDateEdits();
    }

    public void enableDateEdits(){
        termDateStart.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH) + 1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(termDateStart.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                        termDateStart.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        termDateEnd.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH) + 1;
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(termDateEnd.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                        termDateEnd.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

    }

    public void disableDateEdits(){
        termDateStart.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Do nothing
            }
        });

        termDateEnd.setOnClickListener( new View.OnClickListener(){
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
        getMenuInflater().inflate(R.menu.term_details, menu);
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
            deleteThisTerm();
            return true;
        } else if (id == R.id.action_reminder_start) {
            if(selectedTerm.getStartDate() != null) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", selectedTerm.getStartDate().getTime());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", selectedTerm.getEndDate().getTime() + 60 * 60 * 1000);
                intent.putExtra("title", selectedTerm.getName() + " start date");
                startActivity(intent);
            } else
                Toast.makeText(getBaseContext(), "Please enter a start date to set a reminder.", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.action_reminder_end) {
            if(selectedTerm.getEndDate() != null) {
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", selectedTerm.getEndDate().getTime());
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=DAILY");
                intent.putExtra("endTime", selectedTerm.getEndDate().getTime() + 60 * 60 * 1000);
                intent.putExtra("title", selectedTerm.getName() + " end date");
                startActivity(intent);
            } else
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
            // view all assessments
            // indicate if enrolled in parent course
            Intent intent = new Intent(this,AssessmentListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Here are my notes for "+selectedTerm.getName()+": "+selectedTerm.getNotes());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,"Sent to:"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
