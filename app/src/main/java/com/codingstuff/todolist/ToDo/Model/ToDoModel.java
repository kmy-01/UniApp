package com.codingstuff.todolist.ToDo.Model;

import com.codingstuff.todolist.ToDo.Model.enums.Priority;

public class ToDoModel extends TaskId {

    private String Task, DueDate, UpdatedDate;
    private Priority Priority;
    private int Status;

    public ToDoModel(){

    }

    public ToDoModel(String Task, String DueDate, String prio, String UpdatedDate, int Status){
        this.Task = Task;
        this.DueDate = DueDate;
        this.Priority = Enum.valueOf(Priority.class, prio);
        this.UpdatedDate = UpdatedDate;
        this.Status = Status;
    }

    public void setTask(String Task) {
        this.Task = Task;
    }

    public void setDueDate(String DueDate) {
        this.DueDate = DueDate;
    }

    public void setUpdatedDate(String UpdatedDate) {
        this.UpdatedDate = UpdatedDate;
    }

    public void setPriority(String Priority) {
        this.Priority = Enum.valueOf(Priority.class, Priority);
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getTask() {
        return Task;
    }

    public String getDueDate() {
        return DueDate;
    }

    public String getUpdatedDate() { return UpdatedDate; }

    public String getPriority() {
        return Priority.name();
    }

    public int getStatus() {
        return Status;
    }
}
