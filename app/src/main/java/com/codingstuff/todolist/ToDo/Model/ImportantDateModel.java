package com.codingstuff.todolist.ToDo.Model;

import com.codingstuff.todolist.ToDo.Model.enums.Priority;

public class ImportantDateModel extends ImportantDateId{

    private String Matter, DueDate, UpdatedDate;
    private long Countdown;
    private Priority Priority;

    public long getCountdown() {
        return Countdown;
    }

    public void setCountdown(long Countdown) {
        this.Countdown = Countdown;
    }

    public String getMatter() {
        return Matter;
    }

    public void setMatter(String Matter) {
        this.Matter = Matter;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String DueDate) {
        this.DueDate = DueDate;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String UpdatedDate) {
        this.UpdatedDate = UpdatedDate;
    }

    public String getPriority() {
        return Priority.name();
    }

    public void setPriority(String Priority) {
        this.Priority = Enum.valueOf(Priority.class, Priority);
    }
}
