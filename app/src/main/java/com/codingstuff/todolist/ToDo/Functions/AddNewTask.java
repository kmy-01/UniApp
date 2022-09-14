package com.codingstuff.todolist.ToDo.Functions;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codingstuff.todolist.ToDo.Dialog.OnDialogCloseListner;
import com.codingstuff.todolist.ToDo.Dialog.RatingsPickerDialog;
import com.codingstuff.todolist.ToDo.Model.enums.Priority;
import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private TextView setDueDate, setPriority;
    private EditText TaskEdit;
    private Button btn_save;
    private FirebaseFirestore firestore;
    private Context context;
    private String getTask, getdue, getprio;
    private String id = "";

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_new_task , container , false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_tv);
        setPriority = view.findViewById(R.id.set_prio_tv);
        TaskEdit = view.findViewById(R.id.task_edittext);
        btn_save = view.findViewById(R.id.save_btn);

        firestore = FirebaseFirestore.getInstance();

        //Set the first time store data
        boolean isUpdate = false;

        //Edit Importance Date
        final Bundle bundle = getArguments();
        if (bundle != null){
            isUpdate = true;
            getTask = bundle.getString("Task");
            id = bundle.getString("Id");
            getdue = bundle.getString("DueDate");
            getprio = bundle.getString("Priority");

            TaskEdit.setText(getTask);
            setDueDate.setText(getdue);
            setPriority.setText(getprio);

            if (getTask.length() > 0){
                btn_save.setEnabled(false);
                btn_save.setBackgroundColor(Color.GRAY);
            }
        }
        
        TaskEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               if (s.toString().equals("")){
                   btn_save.setEnabled(false);
                   btn_save.setBackgroundColor(Color.GRAY);
               }else{
                   btn_save.setEnabled(true);
                   btn_save.setBackgroundColor(getResources().getColor(R.color.green_blue));
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        setDueDate.setText(dayOfMonth + "/" + month + "/" + year);
                        getdue = dayOfMonth + "/" + month +"/"+year;
                    }
                } , YEAR , MONTH , DAY);

                datePickerDialog.show();
            }
        });

        setPriority.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                RatingsPickerDialog.newInstance(priority -> {
                    setPriority.setText(priority.name());
                    getprio = priority.name();

                    if (Priority.getIdByType(priority) == 1){
                        setPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_level_01, 0, 0, 0);
                    }
                    else if (Priority.getIdByType(priority) == 2){
                        setPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_level_02, 0, 0, 0);
                    }
                    else if (Priority.getIdByType(priority) == 3){
                        setPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_level_03, 0, 0, 0);
                    }
                    else if (Priority.getIdByType(priority) == 4){
                        setPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_level_04, 0, 0, 0);
                    }
                    if (getActivity() != null) {
                        getActivity().invalidateOptionsMenu();
                    }
                }).show(requireFragmentManager(), "PRIORITIES_PICKER");
            }
        });

        boolean finalIsUpdate = isUpdate;
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTask = TaskEdit.getText().toString();

                if (TextUtils.isEmpty(getTask)){
                    TaskEdit.setError("Please enter task");
                }else if (TextUtils.isEmpty(getdue)){
                    Toast.makeText(context, "Please select due date!",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(getprio)){
                    Toast.makeText(context, "Please select the priority!", Toast.LENGTH_SHORT).show();
                }
                else{

                    if (finalIsUpdate){
                        firestore.collection("TaskList").document(id).update("Task" , getTask ,
                                "DueDate" , getdue, "Priority", getprio,
                                "UpdatedTime", FieldValue.serverTimestamp());
                        Toast.makeText(context, "TaskModel Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (getTask.isEmpty()){
                            Toast.makeText(context, "Empty Matter not Allowed!", Toast.LENGTH_SHORT).show();
                        }else{
                            //create a new task with task and due
                            Map<String, Object> taskMap = new HashMap<>();

                            taskMap.put("Task", getTask);
                            taskMap.put("DueDate", getdue);
                            taskMap.put("Priority", getprio);
                            taskMap.put("Status", 0);
                            taskMap.put("UpdatedTime", FieldValue.serverTimestamp());

                            //add a new document with a generated ID
                            firestore.collection("TaskList")
                                    .add(taskMap)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "TaskModel Saved", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + task.getResult().getId());
                                            } else {
                                                Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                        }
                    }

                }
                dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose_Task(dialog);
        }
    }
}
