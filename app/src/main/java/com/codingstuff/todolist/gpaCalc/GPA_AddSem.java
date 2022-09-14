package com.codingstuff.todolist.gpaCalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codingstuff.todolist.ToDo.Activity.ImportantDateActivity;
import com.codingstuff.todolist.ToDo.Activity.ToDoActivity;
import com.codingstuff.todolist.LoginPost.MainActivity;
import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class GPA_AddSem extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_add_sem);
        getSupportActionBar().hide();

        Button submitBtn = findViewById(R.id.addSemSubmitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("semesters")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                EditText year = findViewById(R.id.year);
                                EditText semester = findViewById(R.id.semester);
                                EditText goal = findViewById(R.id.goal);

                                String semstr = "Y" + Integer.valueOf(year.getText().toString()) +
                                        "S" + Integer.valueOf(semester.getText().toString());

                                boolean flag = true;

                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    if (doc.getData().get("sem") != null){
                                        if (doc.getData().get("sem").toString().equals(semstr)){
                                            flag = false;
                                        }
                                    }
                                }

                                if (flag){
                                    Map<String, Object> sem = new HashMap<>();
                                    sem.put("sem", semstr);
                                    if (goal.getText().toString().equals("")) {
                                        sem.put("goal", 2f);
                                    }
                                    else {
                                        sem.put("goal", String.format("%.4f", Float.valueOf(goal.getText().toString())));
                                    }
                                    sem.put("fixedCourses", null);

                                    db.collection("semesters").add(sem);

                                    finish();
                                    Intent intent = new Intent(GPA_AddSem.this, GPA_CalculatorActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(GPA_AddSem.this,
                                            "Semester already exists!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void showButtomSheetDialog() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        sheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sheetDialog.setContentView(R.layout.bottomsheet_dialog_layout);

        LinearLayout task = sheetDialog.findViewById(R.id.taskll);
        LinearLayout date = sheetDialog.findViewById(R.id.dateLinearLayout);
        LinearLayout home = sheetDialog.findViewById(R.id.homeLinearLayout);
        LinearLayout tomato = sheetDialog.findViewById(R.id.tomatoLinearLayout);
        LinearLayout cgpacal = sheetDialog.findViewById(R.id.cgpacalLinearLayout);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddSem.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddSem.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddSem.this , ImportantDateActivity.class));
            }
        });

        tomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        cgpacal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddSem.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}
