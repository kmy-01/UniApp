package com.codingstuff.todolist.ToDo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.codingstuff.todolist.ToDo.Functions.AddNewImportantDate;
import com.codingstuff.todolist.ToDo.Activity.ImportantDateActivity;
import com.codingstuff.todolist.ToDo.Model.ImportantDateModel;
import com.codingstuff.todolist.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ImportantDateAdapter extends RecyclerView.Adapter<ImportantDateAdapter.MyViewHolder>{

    private List<ImportantDateModel> dateList;
    private ImportantDateActivity activity;
    private FirebaseFirestore firestore;

    public ImportantDateAdapter(ImportantDateActivity activity, List<ImportantDateModel> model){
        this.dateList = model;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.each_important_date , parent , false);
        firestore = FirebaseFirestore.getInstance();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ImportantDateModel datemodel = dateList.get(position);
        holder.matterTv.setText(datemodel.getMatter());
        if (datemodel.getCountdown() == 0){
            holder.countdownTv.setBackgroundColor(R.drawable.redcolor);
        }
        else if (datemodel.getCountdown() < 0){
            holder.countdownTv.setBackgroundColor(R.drawable.greycolor);
        }
        holder.countdownTv.setText(String.valueOf(datemodel.getCountdown()));

        if (datemodel.getPriority() != null){
            String prio = datemodel.getPriority();
            if (prio.equals("LEVEL_01")){
                holder.priority_date.setImageResource(R.drawable.ic_level_01);
            }
            else if (prio.equals("LEVEL_02")){
                holder.priority_date.setImageResource(R.drawable.ic_level_02);
            }
            else if (prio.equals("LEVEL_03")){
                holder.priority_date.setImageResource(R.drawable.ic_level_03);
            }
            else if (prio.equals("LEVEL_04")){
                holder.priority_date.setImageResource(R.drawable.ic_level_04);
            }
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public void deleteImportantDate(int position){
        ImportantDateModel datemodel = dateList.get(position);
        firestore.collection("ImportantDateList").document(datemodel.ImDateId).delete();
        dateList.remove(position);
        notifyItemRemoved(position);
    }

    public Context getContextDate(){
        return activity;
    }

    public void editImportantDate(int position){
        ImportantDateModel datemodel = dateList.get(position);

        Bundle matterbundle = new Bundle();
        matterbundle.putString("Matter", datemodel.getMatter());
        matterbundle.putString("DueDate", datemodel.getDueDate());
        /*bundle.putInt("Countdown", datemodel.getCountdown());*/
        matterbundle.putString("Priority", datemodel.getPriority());
        matterbundle.putString("Id", datemodel.ImDateId);
        /*bundle.putString("UpdatedTime", datemodel.getUpdatedDate());*/

        AddNewImportantDate addImDate = new AddNewImportantDate();
        addImDate.setArguments(matterbundle);
        addImDate.show(activity.getSupportFragmentManager() , addImDate.getTag());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView matterTv;
        TextView countdownTv;
        AppCompatImageView priority_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            matterTv = itemView.findViewById(R.id.tv_matter);
            countdownTv = itemView.findViewById(R.id.tv_countdown);
            priority_date = itemView.findViewById(R.id.ic_priority_date);
        }
    }
}
