package com.indax.taskmanager.adapter;

import java.util.ArrayList;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.ExecuteLog;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExecuteLogListAdapter extends BaseAdapter {

	private ArrayList<ExecuteLog> execute_logs;

	public ExecuteLogListAdapter() {
		this.execute_logs = new ArrayList<ExecuteLog>();
	}		
	
	@Override
	public int getCount() {
		return execute_logs.size();
	}

	@Override
	public Object getItem(int position) {
		return execute_logs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.item_execute_log, null);
		}
		
		ExecuteLog log = execute_logs.get(position);
		TextView txt_log_time = (TextView) convertView.findViewById(R.id.txt_log_time);
		TextView txt_remark = (TextView) convertView.findViewById(R.id.txt_remark);
		txt_log_time.setText(log.getLogTime());
		txt_remark.setText(Html.fromHtml(log.getRemark()));
		
		return convertView;
	}

	public void addExecuteLog(ExecuteLog log) {
		execute_logs.add(log);
	}

}
