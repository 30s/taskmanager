package com.indax.taskmanager.adapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.indax.taskmanager.R;
import com.indax.taskmanager.models.ExecuteLog;
import com.indax.taskmanager.models.ExecuteLog.ExecuteLogs;
import com.indax.taskmanager.utils.Utils;

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
		TextView txt_log_time = (TextView) convertView
				.findViewById(R.id.txt_log_time);
		TextView txt_remark = (TextView) convertView
				.findViewById(R.id.txt_remark);
		txt_log_time.setText(log.getLogTime());
		txt_remark.setText(Html.fromHtml(log.getRemark()));

		return convertView;
	}

	public void addExecuteLog(ExecuteLog log) {
		execute_logs.add(log);
	}

	public void load_cursor(Cursor data) {
		execute_logs.clear();
		if (data == null) {
			return;
		}

		int idx_id = data.getColumnIndex(ExecuteLogs.ID);
		int idx_log_time = data.getColumnIndex(ExecuteLogs.LOG_TIME);
		int idx_remark = data.getColumnIndex(ExecuteLogs.REMARK);
		while (data.moveToNext()) {
			long log_timestamp = data.getLong(idx_log_time);
			Date log_time = Utils.getDateFromUTCTimeStamp(log_timestamp);
			ExecuteLog log = new ExecuteLog(data.getLong(idx_id),
					log_timestamp, DateFormat.getDateTimeInstance().format(
							log_time), data.getString(idx_remark));
			execute_logs.add(log);
		}
		notifyDataSetChanged();
	}
	
	public Object [] getExecuteLogs() {
		return execute_logs.toArray();
	}
}
