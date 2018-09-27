package hines.matthew.wgucoursetracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hines.matthew.wgucoursetracker.model.Course;

public class InnerRecyclerViewAdapter extends RecyclerView.Adapter<InnerRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Course> values;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View layout;
        public TextView txtTitle;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.course_title);
        }
    }

    public InnerRecyclerViewAdapter(ArrayList<Course> myDataset) {
        values = myDataset;
    }

    @Override
    public InnerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.term_course_list_item, parent, false);
        /** Change to "view course" action
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "inside viewholder position = " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                expandCourses(v);
            }
        });
        **/
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Course currentCourse = values.get(position);

        holder.txtTitle.setText(currentCourse.getName());

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}
