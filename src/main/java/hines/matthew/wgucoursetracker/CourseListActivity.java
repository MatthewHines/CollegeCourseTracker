package hines.matthew.wgucoursetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Course;
import hines.matthew.wgucoursetracker.model.Enrollment;
import hines.matthew.wgucoursetracker.model.Term;

public class CourseListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataSource mDataSource;
    CourseRecyclerViewAdapter mAdapter;

    FloatingActionButton fab;

    int termId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDataSource = new DataSource(this);
        mDataSource.open();
        Toast.makeText(this,"Database acquired.",Toast.LENGTH_SHORT).show();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),CourseActivity.class);
                intent.putExtra("courseId",-1);
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

        termId = getIntent().getIntExtra("termId",-1);

        if (termId >= 0)
            selectorMode();
        else
            setupRecyclerView();
    }

    private void selectorMode(){
        setupRecyclerEditView(termId);
        mAdapter.addMode();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Course> newCourses = mAdapter.getNewCourses();
                Term term = mDataSource.getTerm(termId);
                for(Course course : newCourses){
                    Enrollment enrollment = new Enrollment(term,course);
                    mDataSource.createEnrollment(enrollment);
                }
                Intent intent = new Intent(getBaseContext(),TermDetailsActivity.class);
                intent.putExtra("termId",termId);
                startActivity(intent);
            }
        });

        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = (Gravity.BOTTOM | Gravity.CENTER);
        fab.setImageResource(R.drawable.ic_done);

    }

    private void setupRecyclerView(){

        ArrayList<Course> courseList = new ArrayList<>(mDataSource.getAllCourses());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CourseRecyclerViewAdapter(courseList);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupRecyclerEditView(int term){

        ArrayList<Course> courseList = new ArrayList<>(mDataSource.getUnenrolledCourses());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.course_recycler_view);
        recyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CourseRecyclerViewAdapter(courseList);
        recyclerView.setAdapter(mAdapter);
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
        getMenuInflater().inflate(R.menu.course_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete){
            if(!mAdapter.addMode) {
                if (mAdapter.edit) {
                    mAdapter.endEdit();
                    fab.setVisibility(View.VISIBLE);
                } else {
                    mAdapter.editMode();
                    fab.setVisibility(View.GONE);
                }
            } else
                Toast.makeText(getBaseContext(), "Delete disabled during add course action.", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_courses) {
            Intent intent = new Intent(this,CourseListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_assessments) {
            Intent intent = new Intent(this,AssessmentListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Toast.makeText(getBaseContext(), "Select a course to share notes.", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
