package com.codingstuff.todolist.ToDo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.codingstuff.todolist.ToDo.Functions.AddNewTask;
import com.codingstuff.todolist.ToDo.Activity.ToDoActivity;
import com.codingstuff.todolist.ToDo.Model.ToDoModel;
import com.codingstuff.todolist.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private final List<ToDoModel> todoList;
    private final ToDoActivity activity;
    private FirebaseFirestore firestore;

    public ToDoAdapter(ToDoActivity toDoActivity, List<ToDoModel> todoList){
        this.todoList = todoList;
        activity = toDoActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_task , parent , false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ToDoModel toDoModel = todoList.get(position);
        holder.titleTv.setText(toDoModel.getTask());
        holder.mDueDateTv.setText(toDoModel.getDueDate());
        if (toDoModel.getPriority() != null){
            String prio = toDoModel.getPriority();
            if (prio.equals("LEVEL_01")){
                holder.priority_icon.setImageResource(R.drawable.ic_level_01);
            }
            else if (prio.equals("LEVEL_02")){
                holder.priority_icon.setImageResource(R.drawable.ic_level_02);
            }
            else if (prio.equals("LEVEL_03")){
                holder.priority_icon.setImageResource(R.drawable.ic_level_03);
            }
            else if (prio.equals("LEVEL_04")){
                holder.priority_icon.setImageResource(R.drawable.ic_level_04);
            }
        }

        holder.mCheckBox.setChecked(toBoolean(toDoModel.getStatus()));
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    firestore.collection("TaskList").document(toDoModel.TaskId).update("Status" , 1);
                }else{
                    firestore.collection("TaskList").document(toDoModel.TaskId).update("Status" , 0);
                }
            }
        });

    }

    public void deleteTask(int position){
        ToDoModel toDoModel = todoList.get(position);
        firestore.collection("TaskList").document(toDoModel.TaskId).delete();
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContext(){
        return activity;
    }

    public void editTask(int position){
        ToDoModel toDoModel = todoList.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("Task" , toDoModel.getTask());
        bundle.putString("DueDate" , toDoModel.getDueDate());
        bundle.putString("Priority", toDoModel.getPriority());
        bundle.putString("Id" , toDoModel.TaskId);

        AddNewTask addNewTask = new AddNewTask();
        addNewTask.setArguments(bundle);
        addNewTask.show(activity.getSupportFragmentManager() , addNewTask.getTag());
    }

    private boolean toBoolean(int status){
        return status != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleTv;
        TextView mDueDateTv;
        AppCompatImageView priority_icon;
        CheckBox mCheckBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.tv_title);
            mDueDateTv = itemView.findViewById(R.id.tv_due_date);
            mCheckBox = itemView.findViewById(R.id.mcheckbox);
            priority_icon = itemView.findViewById(R.id.ic_priority);

        }
    }
}
