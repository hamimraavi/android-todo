package com.grofers.hamimraavi.todo;

import android.graphics.Movie;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hamim on 01/07/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Task> taskList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description, scheduled_time, pending;

        public MyViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.title);
            scheduled_time = (TextView) view.findViewById(R.id.genre);
            pending = (TextView) view.findViewById(R.id.year);
        }
    }


    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.description.setText(task.getDescription());
        holder.scheduled_time.setText(task.getScheduledTime());
        holder.pending.setText(task.getPending());
        if(task.getPending().equals("Done"))
            holder.description.setPaintFlags(holder.description.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.description.setPaintFlags(holder.description.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}