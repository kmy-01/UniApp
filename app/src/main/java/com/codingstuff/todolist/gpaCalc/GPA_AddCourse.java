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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.codingstuff.todolist.R;

import java.util.HashMap;
import java.util.Map;

public class GPA_AddCourse extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private float semGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_add_course);
        getSupportActionBar().hide();

        Button submitBtn = findViewById(R.id.addSemSubmitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("courses")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                EditText courseName = findViewById(R.id.courseNameEditText);
                                EditText courseCode = findViewById(R.id.courseCodeEditText);
                                EditText hours = findViewById(R.id.hoursEditText);
                                EditText courseWork = findViewById(R.id.courseWorkEditText);
                                EditText totalCW = findViewById(R.id.totalCWEditText);
                                String sem = GPA_AddCourse.this.getIntent().getStringExtra("sem");

                                boolean flag = true;

                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    if (doc.getData().get("sem") != null &&
                                            doc.getData().get("courseCode") != null){
                                        if (doc.getData().get("sem").toString().equals(sem) &&
                                                doc.getData().get("courseCode")
                                                        .toString()
                                                        .equalsIgnoreCase(courseCode.getText().toString())){
                                            flag = false;
                                        }
                                    }
                                }

                                if (flag){
                                    Map<String, Object> course = new HashMap<>();
                                    course.put("courseCode", courseCode.getText().toString());
                                    course.put("courseName", courseName.getText().toString());
                                    course.put("courseWork", courseWork.getText().toString());
                                    course.put("sem", sem);
                                    course.put("hours", Float.valueOf(hours.getText().toString()));
                                    course.put("totalCW", Float.valueOf(totalCW.getText().toString()));

                                    db.collection("semesters")
                                            .whereEqualTo("sem", sem)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                                        semGoal = Float.valueOf(doc.getData().get("goal").toString());
                                                        course.put("goal", semGoal);
                                                        db.collection("courses").add(course);
                                                    }
                                                }
                                            });

                                    finish();
                                    Intent intent = new Intent(GPA_AddCourse.this, GPA_CalculatorActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(GPA_AddCourse.this,
                                            "Course already exists!", Toast.LENGTH_LONG).show();
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
                startActivity(new Intent(GPA_AddCourse.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddCourse.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_AddCourse.this , ImportantDateActivity.class));
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
                startActivity(new Intent(GPA_AddCourse.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}