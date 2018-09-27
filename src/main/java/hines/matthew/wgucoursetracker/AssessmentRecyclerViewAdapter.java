package hines.matthew.wgucoursetracker;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Assessment;

public class AssessmentRecyclerViewAdapter extends RecyclerView.Adapter<AssessmentRecyclerViewAdapter.ViewHolder> {


    private ArrayList<Assessment> values;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    private DataSource mDataSource;

    boolean edit = false;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtNotes;
        public TextView txtPassed;
        public TextView txtCourse;
        public TextView txtDueDate;
        public ImageView imgIcon;
        public Button btnDelete;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.assessment_title);
            txtDescription = (TextView) v.findViewById(R.id.assessment_description);
            txtNotes = (TextView) v.findViewById(R.id.assessment_notes);
            txtPassed = (TextView) v.findViewById(R.id.assessment_passed);
            txtCourse = (TextView) v.findViewById(R.id.assessment_course);
            imgIcon = (ImageView) v.findViewById(R.id.assessment_icon);
            btnDelete = (Button) v.findViewById(R.id.delete_button);
            txtDueDate = (TextView) v.findViewById(R.id.assessment_due_date);
        }
    }

    public AssessmentRecyclerViewAdapter(ArrayList<Assessment> myDataset) {
        values = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.course_assessment, parent, false);
        return new ViewHolder(v);

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
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Assessment currentAssessment = values.get(position);

        mDataSource = new DataSource(holder.itemView.getContext());
        mDataSource.open();

        holder.txtTitle.setText(String.format(holder.itemView.getResources().getString(R.string.course_title),currentAssessment.getName()));
        holder.txtDescription.setText(currentAssessment.getDescription());
        holder.txtNotes.setText(currentAssessment.getNotes());
        if (currentAssessment.getDueDate() != null) {
            holder.txtDueDate.setText(df.format(currentAssessment.getDueDate()));
        }
        holder.txtCourse.setText(String.format(holder.itemView.getResources().getString(R.string.assessment_course),mDataSource.getCourse(currentAssessment.getCourseId()).getName()));

        if (currentAssessment.isPassed())
            holder.txtPassed.setText(String.format(holder.itemView.getResources().getString(R.string.assessment_passed)," Passed"));
        else
            holder.txtPassed.setText(String.format(holder.itemView.getResources().getString(R.string.assessment_passed)," Not Passed"));

        if (currentAssessment.isPassed()) {
            holder.imgIcon.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.passed,
                            holder.itemView.getContext().getTheme()));
        } else{
            holder.imgIcon.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.transparent,
                            holder.itemView.getContext().getTheme()));
        }

        if(edit){
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(currentAssessment.isPassed()) {
                currentAssessment.setPassed(false);
                mDataSource.updateAssesssment(currentAssessment);
            }
            else {
                currentAssessment.setPassed(true);
                mDataSource.updateAssesssment(currentAssessment);
            }

            notifyItemChanged(position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mDataSource.deleteAssessment(currentAssessment.getId());
                Toast.makeText(v.getContext(), "Assessment deleted", Toast.LENGTH_SHORT).show();
                values.remove(position);
                notifyDataSetChanged();

            }
        });

        /** Change to "mark assessment as passed" action
         v.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        //Toast.makeText(v.getContext(), "inside viewholder position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
        expandCourses(v);
        }
        });
         **/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Long Clicked", Toast.LENGTH_SHORT).show();
                //Show  term editor activity
                Intent intent = new Intent(v.getContext(),AssessmentActivity.class);
                intent.putExtra("assessmentId",currentAssessment.getId());
                v.getContext().startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
