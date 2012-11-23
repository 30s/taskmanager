package com.indax.taskmanager.models;

import com.indax.taskmanager.providers.TaskContentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Task {
	private String name;
	private TaskType type;
	private Boolean finish;
	private String remark;

	public Task(String name, char type, Boolean finish, String remark) {
		this.name = name;
		switch (type) {
		case 'D':
			this.type = TaskType.DAILY;
			break;
		case 'W':
			this.type = TaskType.WEEKLY;
			break;
		case 'M':
			this.type = TaskType.MONTHLY;
			break;
		case 'Y':
			this.type = TaskType.YEARLY;
			break;
		default:
			this.type = TaskType.ETC;
			break;
		}
		this.finish = finish;
		this.remark = remark;
	}

	public static final class Tasks implements BaseColumns {
		private Tasks() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ TaskContentProvider.AUTHORITY + "/tasks");
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.taskmanager.tasks";
		public static final String TASK_ID = "_id";
		public static final String TASK_NAME = "name";
		public static final String TASK_TYPE = "type";
		public static final String TASK_FINISH = "finish";
		public static final String TASK_REMARK = "remark";
	}

	public String getName() {
		return name;
	}

	public Boolean getFinish() {
		return finish;
	}

	public TaskType getType() {
		return type;
	}

	public String getRemark() {
		return remark;
	}
}
