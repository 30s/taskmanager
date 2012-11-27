package com.indax.taskmanager.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.Task;
import com.indax.taskmanager.models.Task.Tasks;

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
	
	public void clearTask() {
		this.tasks_daily.clear();
		this.tasks_weekly.clear();
		this.tasks_monthly.clear();
		this.tasks_yearly.clear();
		this.tasks_etc.clear();
	}

	public void load_tasks(Cursor cursor) {
		clearTask();
		if ( cursor == null ) {
			return;
		}
		
		int idx_guid = cursor.getColumnIndex(Tasks.GUID);
		int idx_name = cursor.getColumnIndex(Tasks.NAME);
		int idx_type = cursor.getColumnIndex(Tasks.TYPE);
		int idx_finish = cursor.getColumnIndex(Tasks.FINISH);
		int idx_remark = cursor.getColumnIndex(Tasks.REMARK);
		while (cursor.moveToNext()) {
			int guid = cursor.getInt(idx_guid);
			String name = cursor.getString(idx_name);
			String type = cursor.getString(idx_type);
			int finish = cursor.getInt(idx_finish);
			String remark = cursor.getString(idx_remark);
			Task t = new Task(guid, name, type.charAt(0), finish != 0, remark);
			addChild(t);
		}
		notifyDataSetChanged();		
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

	public void addChild(Task task) {
		switch (task.getType()) {
		case DAILY:
			tasks_daily.add(task);
			break;
		case WEEKLY:
			tasks_weekly.add(task);
			break;
		case MONTHLY:
			tasks_monthly.add(task);
			break;
		case YEARLY:
			tasks_yearly.add(task);
			break;
		default:
			tasks_etc.add(task);
			break;
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
            view = (TextView)inflater.inflate(R.layout.task_header, null);
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
		TextView txt_remark = (TextView) view.findViewById(R.id.txt_remark);
		chk_item.setChecked(task.getFinish());
		chk_item.setText(task.getName());
		txt_remark.setText(task.getRemark());
		
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

}
