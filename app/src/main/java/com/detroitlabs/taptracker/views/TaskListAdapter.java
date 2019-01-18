/*
 * Copyright 2018 Jason Petterson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.detroitlabs.taptracker.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.detroitlabs.taptracker.R;
import com.detroitlabs.taptracker.models.Task;
import com.detroitlabs.taptracker.utils.DateFormatUtil;

import java.util.Date;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTextView;
        private final TextView lastTimeTextView;
        private final ImageButton trackButton;
        private final ImageButton viewMoreButton;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.task_title);
            lastTimeTextView = itemView.findViewById(R.id.last_completed_date);
            trackButton = itemView.findViewById(R.id.track_button);
            viewMoreButton = itemView.findViewById(R.id.view_more_button);
        }

        void bind(final Task item, final OnItemClickListener listener) {
            if (listener == null) return;

            itemView.setOnClickListener(view -> {
                listener.onItemClick(item);
            });

            itemView.setOnLongClickListener(view -> {
                boolean handled = listener.onItemLongClick(item);
                if (handled) {
                    notifyDataSetChanged();
                }
                return handled;
            });

            trackButton.setOnClickListener(view -> {
                listener.onTrackButtonClicked(item);
                notifyDataSetChanged();
            });

            viewMoreButton.setOnClickListener(v -> {
                listener.onViewMoreButtonClicked(v, item);
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task item);

        boolean onItemLongClick(Task item);

        void onTrackButtonClicked(Task item);

        void onViewMoreButtonClicked(View view, Task item);
    }

    private List<Task> tasks;
    private OnItemClickListener onItemClickListener;
    private DateFormatUtil dateFormatUtil;

    public TaskListAdapter(Context context) {
        dateFormatUtil = new DateFormatUtil(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (tasks != null) {
            Task current = tasks.get(position);
            holder.taskTextView.setText(current.getTask());
            holder.lastTimeTextView.setText(formatLastTime(current.getLastCompletedTime()));
            holder.bind(tasks.get(position), onItemClickListener);
        } else {
            holder.taskTextView.setText("No Task");
            holder.lastTimeTextView.setText("--");
        }
    }

    private String formatLastTime(@Nullable Date lastTime) {
        if (lastTime == null) {
            return "Never";
        }
        return dateFormatUtil.formatDateWithTimeSinceNow(lastTime);
    }

    public void setTasks(List<Task> Tasks) {
        tasks = Tasks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (tasks != null) {
            return tasks.size();
        } else return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}