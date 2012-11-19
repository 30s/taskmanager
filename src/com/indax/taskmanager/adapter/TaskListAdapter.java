package com.indax.taskmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.Task;

public class TaskListAdapter extends BaseAdapter {

	private Task[] tasks;

	public TaskListAdapter(Task... tasks) {
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		return tasks.length;
	}

	@Override
	public Object getItem(int position) {
		return tasks[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (convertView instanceof TextView) ? (TextView) convertView
				: (TextView) LayoutInflater.from(parent.getContext()).inflate(
						R.layout.task_item, null);
		view.setText(tasks[position].getName());
		return view;
	}

}
