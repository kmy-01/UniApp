package com.codingstuff.todolist.ToDo.Functions;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
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
import androidx.annotation.RequiresApi;

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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewImportantDate extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewImportantDate";

    private TextView setDueDate, setPriority;
    private EditText MatterEdit;
    private Button btn_save;
    private FirebaseFirestore firestore;
    private Context context;
    private String getdue, getprio, getmatter;
    private String getid = "";
    private Date due;


    public static AddNewImportantDate newInstance(){
        return new AddNewImportantDate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_new_important_date, container, false);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        setDueDate = view.findViewById(R.id.set_due_tv_date);
        setPriority = view.findViewById(R.id.set_prio_tv_date);
        MatterEdit = view.findViewById(R.id.editdate);
        btn_save = view.findViewById(R.id.save_btn_date);

        Date now = new Date();

        firestore = FirebaseFirestore.getInstance();

        boolean Update = false;

        final Bundle matterbundle = getArguments();
        if (matterbundle != null){
            Update = true;
            getmatter = matterbundle.getString("Matter");
            getid = matterbundle.getString("Id");
            getdue = matterbundle.getString("DueDate");
            getprio = matterbundle.getString("Priority");

            MatterEdit.setText(getmatter);
            setDueDate.setText(getdue);
            setPriority.setText(getprio);

            try {
                due = new SimpleDateFormat("dd/MM/yyyy").parse(getdue);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (getmatter.length() > 0){
                btn_save.setEnabled(false);
                btn_save.setBackgroundColor(Color.GRAY);
            }
        }

        MatterEdit.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equals("")) {
                    btn_save.setEnabled(false);
                    btn_save.setBackgroundColor(Color.GRAY);
                }
                else{
                    btn_save.setEnabled(true);
                    btn_save.setBackgroundColor(getResources().getColor(R.color.dark_yellow));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setDueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                int MONTH = calendar.get(Calendar.MONTH);
                int YEAR = calendar.get(Calendar.YEAR);
                int DAY = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        setDueDate.setText(dayOfMonth + "/" + month + "/" + year);
                        getdue = dayOfMonth + "/" + month + "/" + year;
                        try {
                            due = new SimpleDateFormat("dd/MM/yyyy").parse(getdue);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
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

        boolean finalIsUpdate = Update;
        btn_save.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                getmatter = MatterEdit.getText().toString();

                int countdown = 0;

                countdown = calculateCountdown(now,due);

                if (TextUtils.isEmpty(getmatter)){
                    MatterEdit.setError("Please enter task!");
                }else if (TextUtils.isEmpty(getdue)){
                    Toast.makeText(context, "Please select due date!",Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(getprio)){
                    Toast.makeText(context, "Please select the priority!", Toast.LENGTH_SHORT).show();
                }
                else{

                    if (finalIsUpdate){
                        firestore.collection("ImportantDateList").document(getid).update("Matter", getmatter,
                                "DueDate", getdue, "Priority", getprio, "Countdown", countdown,
                                "UpdatedTime", FieldValue.serverTimestamp());
                        Toast.makeText(context, "ImportantDateModel Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (getmatter.isEmpty()) {
                            Toast.makeText(context, "Empty Matter not Allowed!", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            //create a new task with task and due
                            Map<String, Object> dateMap = new HashMap<>();

                            dateMap.put("Matter", getmatter);
                            dateMap.put("DueDate", getdue);
                            dateMap.put("Priority", getprio);
                            dateMap.put("Countdown", countdown);
                            dateMap.put("UpdatedTime", FieldValue.serverTimestamp());

                            //add a new document with a generated ID
                            firestore.collection("ImportantDateList")
                                    .add(dateMap)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "ImportantDateModel Saved", Toast.LENGTH_SHORT).show();
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
            ((OnDialogCloseListner)activity).onDialogClose_Date(dialog);
        }
    }

    public int calculateCountdown(Date now, Date due){
        int countdown = 0;
        long diff = (due.getTime() - now.getTime())/86400000;
        countdown = (int)Math.abs(diff);
        return countdown;
    }
}