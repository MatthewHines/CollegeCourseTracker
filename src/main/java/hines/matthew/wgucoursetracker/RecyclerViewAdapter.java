package hines.matthew.wgucoursetracker;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hines.matthew.wgucoursetracker.database.DataSource;
import hines.matthew.wgucoursetracker.model.Course;
import hines.matthew.wgucoursetracker.model.Term;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Term> values;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

    DataSource mDataSource;

    private RecyclerView recyclerView;
    private InnerRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    boolean edit = false;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public TextView txtDates;
        public Button btnDelete;
        public ProgressBar progressBar;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.term_title);
            txtDates = (TextView) v.findViewById(R.id.term_dates);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            btnDelete = (Button) v.findViewById(R.id.delete_button);
        }
    }

    public void add(int position, Term item) {
        values.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        values.remove(position);
        notifyItemRemoved(position);
    }

    public RecyclerViewAdapter(ArrayList<Term> myDataset) {
        values = myDataset;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.term_list_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandCourses(v);
            }
        });

        return vh;
    }

    public void expandCourses(View view){
        View selected = view.findViewById(R.id.expandable_course_view);
        ImageView expandIcon = view.findViewById(R.id.expand_toggle);

        if (selected.getVisibility() == View.VISIBLE) {
            selected.setVisibility(View.GONE);
            expandIcon.setImageResource(R.drawable.ic_expand_more);
        } else {
            selected.setVisibility(View.VISIBLE);
            expandIcon.setImageResource(R.drawable.ic_expand_less);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        mDataSource = new DataSource(holder.itemView.getContext());

        final Term currentTerm = values.get(position);

        final int termId = currentTerm.getId();
        ArrayList<Course> termCourses = new ArrayList<>(currentTerm.getCourses());

        holder.txtTitle.setText(currentTerm.getName());
        holder.txtDates.setText((df.format(currentTerm.getStartDate())+" - "+df.format(currentTerm.getEndDate())));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(), "Long Clicked", Toast.LENGTH_SHORT).show();
                //Show  term editor activity
                Intent intent = new Intent(v.getContext(),TermDetailsActivity.class);
                intent.putExtra("termId",termId);
                v.getContext().startActivity(intent);
                return true;
            }
        });

        int termCredits = 0;
        int termEarnedCredits = 0;

        for (Course course : currentTerm.getCourses()){
            termCredits += course.getCreditValue();
            if (mDataSource.isCoursePassed(currentTerm.getId(),course.getId()))
                termEarnedCredits += course.getCreditValue();
        }

        holder.progressBar.setMax(termCredits);
        holder.progressBar.setProgress(termEarnedCredits,true);

        recyclerView = holder.itemView.findViewById(R.id.inner_recycler_view);
        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new InnerRecyclerViewAdapter(termCourses);
        recyclerView.setAdapter(mAdapter);

        if(edit){
            holder.btnDelete.setVisibility(View.VISIBLE);
        }else{
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(currentTerm.getCourses() == null || currentTerm.getCourses().isEmpty()) {
                    mDataSource.deleteTerm(currentTerm.getId());
                    values.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "Term deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Term cannot be deleted with enrolled courses.", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
