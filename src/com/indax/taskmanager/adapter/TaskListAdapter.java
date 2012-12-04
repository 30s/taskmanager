package com.indax.taskmanager.adapter;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.Task;

public class TaskListAdapter extends BaseAdapter {

	private ArrayList<Task> tasks;

	public TaskListAdapter(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public TaskListAdapter() {
		this.tasks = new ArrayList<Task>(20);
	}

	public ArrayList<Task> getTaskList() {
		return this.tasks;
	}
	
	@Override
	public int getCount() {
		return tasks.size();
	}

	@Override
	public Object getItem(int position) {
		return tasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup view = (convertView instanceof ViewGroup) ? (ViewGroup) convertView
				: (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
						R.layout.task_item, null);
		
		Task task = tasks.get(position);
		TextView txt_task = (TextView) view.findViewById(R.id.txt_task);				
		txt_task.setText(task.getName());
		
		return view;
	}

}
