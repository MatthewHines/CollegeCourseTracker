package hines.matthew.wgucoursetracker;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Assessment;
import hines.matthew.wgucoursetracker.model.Course;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Course> values;
    private ArrayList<Assessment> assessments;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private int termId = -1;

    public boolean edit = false;

    public boolean addMode = false;
    public ArrayList<Course> selectedCourses = new ArrayList<>();

    private DataSource mDataSource;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView txtTitle;
        public TextView txtStart;
        public TextView txtEnd;
        public TextView txtCredits;
        public TextView txtDescription;
        public TextView txtPassed;
        public TextView txtAssessment;
        public TextView txtHyphen;
        public Button btnDelete;



        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.course_title);
            txtStart = (TextView) v.findViewById(R.id.course_start);
            txtEnd = (TextView) v.findViewById(R.id.course_end);
            txtCredits = (TextView) v.findViewById(R.id.course_credits);
            txtDescription = (TextView) v.findViewById(R.id.course_description);
            txtAssessment = (TextView) v.findViewById(R.id.course_assessment);
            txtPassed = (TextView) v.findViewById(R.id.course_passed);
            txtHyphen = (TextView) v.findViewById(R.id.course_date_hyphen);
            btnDelete = (Button) v.findViewById(R.id.delete_button);

        }
    }

    public CourseRecyclerViewAdapter(ArrayList<Course> myDataset) {
        values = myDataset;
    }

    public CourseRecyclerViewAdapter(ArrayList<Course> myDataset, int term) {
        values = myDataset;
        termId = term;
    }

    @Override
    public CourseRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.term_details_course, parent, false);
        /** Change to "view course" action
         v.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        //Toast.makeText(v.getContext(), "inside viewholder position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        expandCourses(v);
        }
        });
        **/

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Course currentCourse = values.get(position);

        mDataSource = new DataSource(holder.itemView.getContext());
        mDataSource.open();

        currentCourse.setTermId(termId);

        String passed = "Not Passed";

        if(currentCourse.isPassed())
            passed = "Passed";

        int assessmentCount = currentCourse.getAssessments().size();

        holder.txtAssessment.setText(String.format(holder.itemView.getResources().getString(R.string.course_assessments),Integer.toString(assessmentCount)));
        holder.txtPassed.setText(String.format(holder.itemView.getResources().getString(R.string.assessment_passed),passed));
        holder.txtTitle.setText(currentCourse.getName());
        if (currentCourse.getStartDate() != null) {
            holder.txtStart.setText(df.format(currentCourse.getStartDate()));
            holder.txtEnd.setText(df.format(currentCourse.getEndDate()));
        } else{
            holder.txtStart.setVisibility(View.GONE);
            holder.txtEnd.setVisibility(View.GONE);
            holder.txtHyphen.setVisibility(View.GONE);
        }
        holder.txtCredits.setText(String.valueOf(currentCourse.getCreditValue()));
        holder.txtDescription.setText(currentCourse.getDescription());

        if (currentCourse.isPassed())
            holder.txtCredits.setBackground(holder.itemView.getResources().getDrawable(R.drawable.bg_passed, holder.itemView.getContext().getTheme()));
        else
            holder.txtCredits.setBackground(holder.itemView.getResources().getDrawable(R.drawable.bg, holder.itemView.getContext().getTheme()));


        if(edit){
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnDelete.setVisibility(View.GONE);
        }

        if(!addMode) {
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentCourse.getTermId() < 0) {
                        mDataSource.deleteCourse(currentCourse.getId());
                        Toast.makeText(v.getContext(), "Course deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        mDataSource.deleteEnrollment(termId, currentCourse.getId());
                        Toast.makeText(v.getContext(), "Course unenrolled", Toast.LENGTH_SHORT).show();
                    }
                    values.remove(position);
                    notifyDataSetChanged();
                }
            });

            final int courseId = currentCourse.getId();


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(v.getContext(), CourseActivity.class);
                    intent.putExtra("courseId", courseId);
                    if (termId >= 0) {
                        intent.putExtra("termId", termId);
                    }
                    v.getContext().startActivity(intent);
                    return true;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (termId >= 0) {
                        if(currentCourse.isPassed()) {
                            mDataSource.setCourseEnrollmentPass(termId, courseId, false);
                            currentCourse.setPassed(false);
                        }
                        else {
                            mDataSource.setCourseEnrollmentPass(termId, courseId, true);
                            currentCourse.setPassed(true);
                        }
                        notifyItemChanged(position);
                    }
                }
            });

        } else {

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedCourses.add(values.get(position));
                    values.remove(position);
                    notifyDataSetChanged();
                }
            });

        }


    }

    public ArrayList<Course> getNewCourses(){
        return selectedCourses;
    }

    public void addMode(){
        addMode = true;
    }

    public void editMode(){
        this.edit = true;
        notifyDataSetChanged();
    }

    public void endEdit(){
        this.edit = false;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}
