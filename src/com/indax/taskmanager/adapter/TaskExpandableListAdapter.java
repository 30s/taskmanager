package com.indax.taskmanager.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.Task;

public class TaskExpandableListAdapter extends BaseExpandableListAdapter {

	private ArrayList<Task> tasks_daily;
	private ArrayList<Task> tasks_weekly;
	private ArrayList<Task> tasks_monthly;
	private ArrayList<Task> tasks_yearly;
	private ArrayList<Task> tasks_etc;

	public TaskExpandableListAdapter() {
		this.tasks_daily = new ArrayList<Task>(8);
		this.tasks_weekly = new ArrayList<Task>(8);
		this.tasks_monthly = new ArrayList<Task>(8);
		this.tasks_yearly = new ArrayList<Task>(8);
		this.tasks_etc = new ArrayList<Task>(8);
	}

	@Override
	public int getGroupCount() {
		return 5;
	}

	@Override
	public int getChildrenCount(int groupPosition) {		
		return getGroup(groupPosition).size();
	}

	@Override
	public ArrayList<Task> getGroup(int groupPosition) {
		switch ( groupPosition ) {
		case 0:
			return this.tasks_daily;
		case 1:
			return this.tasks_weekly;
		case 2:
			return this.tasks_monthly;
		case 3:
			return this.tasks_yearly;
		case 4:
			return this.tasks_etc;
		default:
			return null;
		}
	}

	@Override
	public Task getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 1000 * groupPosition + childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView view;
		
        if (convertView instanceof TextView) {
            view = (TextView)convertView;
        } else {
            final Context context = parent.getContext();
            final LayoutInflater inflater = LayoutInflater.from(context);
            view = (TextView)inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
        }
        
        switch (groupPosition) {
        case 0:
        	view.setText("Daily Tasks");
        	break;
        case 1:
        	view.setText("Weekly Tasks");
        	break;
        case 2:
        	view.setText("Monthly Tasks");
        	break;
        case 3:
        	view.setText("Yearly Tasks");
        	break;
        case 4:
        	view.setText("Non-Repeat Tasks");
        	break;
        default:
        	break;
        }
		
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewGroup view = (convertView instanceof ViewGroup) ? (ViewGroup) convertView
				: (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(
						R.layout.task_item, null);
		
		Task task = getChild(groupPosition, childPosition);
		CheckBox chk_item = (CheckBox) view.findViewById(R.id.chk_item);				
		chk_item.setChecked(task.getFinish());
		chk_item.setText(task.getName());
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
