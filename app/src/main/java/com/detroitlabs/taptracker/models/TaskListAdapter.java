package com.detroitlabs.taptracker.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.detroitlabs.taptracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTextView;
        private final TextView lastTimeTextView;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.textView);
            lastTimeTextView = itemView.findViewById(R.id.lastTimeView);
        }

        public void bind(final Task item, final OnItemClickListener listener) {
            if (listener == null) return;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task item);
    }

    private final LayoutInflater mInflater;
    private List<Task> mTasks; // Cached copy of Tasks
    private OnItemClickListener onItemClickListener;

    public TaskListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (mTasks != null) {
            Task current = mTasks.get(position);
            holder.taskTextView.setText(current.getTask());
            holder.lastTimeTextView.setText(formatLastTime(current.getLastCompletedTime()));
            holder.bind(mTasks.get(position), onItemClickListener);
        } else {
            // Covers the case of data not being ready yet.
            holder.taskTextView.setText("No Task");
            holder.lastTimeTextView.setText("--");
        }
    }

    private SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd h:mm a", Locale.US);
    private String formatLastTime(Date lastTime) {
        if (lastTime == null) {
            return "Never";
        }

        // TODO format time based on how long ago it was
        // if < 1 min ex. 30 secs ago
        // if < 1 hour ex 6 mins ago
        // if < 24 hour ex. Today 6:30pm
        // else ex. 7/18 6:30pm

        return timeFormat.format(lastTime);
    }

    public void setTasks(List<Task> Tasks) {
        mTasks = Tasks;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mTasks has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mTasks != null)
            return mTasks.size();
        else return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}