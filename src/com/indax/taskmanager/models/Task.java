package com.indax.taskmanager.models;

public class Task {
	private String name;
	private TaskType type;
	private Boolean finish;
	private String remark;
	
	public Task(String name, char type, Boolean finish, String remark) {
		this.name = name;
		switch (type ) {
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
