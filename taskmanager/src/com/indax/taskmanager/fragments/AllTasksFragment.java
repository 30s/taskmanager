package com.indax.taskmanager.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.indax.taskmanager.ExecuteLogActivity;
import com.indax.taskmanager.R;
import com.indax.taskmanager.adapter.TaskExpandableListAdapter;
import com.indax.taskmanager.models.Task;

public class AllTasksFragment extends SherlockFragment implements OnChildClickListener {
	
	private TaskExpandableListAdapter task_adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_all_tasks, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ExpandableListView lst_task = (ExpandableListView) getActivity().findViewById(R.id.lst_task);
		lst_task.setAdapter(task_adapter);
		lst_task.setOnChildClickListener(this);
	}

	public void setTaskAdapter(TaskExpandableListAdapter task_adapter) {
		this.task_adapter = task_adapter;
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Task task = task_adapter.getChild(groupPosition, childPosition);
		Intent intent = new Intent(parent.getContext(), ExecuteLogActivity.class);
		intent.putExtra("task_name", task.getName());
		intent.putExtra("task_guid", task.getGuid() + "");
		startActivity(intent);
		
		return false;
	}
}
