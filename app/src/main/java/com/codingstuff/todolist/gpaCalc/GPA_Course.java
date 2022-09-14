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
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GPA_Course extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int courseCount = 0;

    private static float semGoal, totalHours = 0;
    private static int scourseCount = 0;
    private static ArrayList<String> fixedCourse;
    private static float[] fixedGoal, fixedHours;
    private static ArrayList<GoalMap> courseGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_course);
        getSupportActionBar().hide();

        ArrayList<String> attrs = getIntent().getStringArrayListExtra("course");

        EditText courseNameUpdate = findViewById(R.id.courseNameUpdate);
        EditText hoursUpdate = findViewById(R.id.hoursUpdate);
        EditText courseWorkUpdate = findViewById(R.id.courseWorkUpdate);
        EditText totalCWUpdate = findViewById(R.id.totalCWUpdate);

        db.collection("courses")
                .whereEqualTo("courseCode", attrs.get(0))
                .whereEqualTo("sem", attrs.get(1))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            courseNameUpdate.setText(doc.getData().get("courseName").toString());
                            hoursUpdate.setText(doc.getData().get("hours").toString());
                            courseWorkUpdate.setText(doc.getData().get("courseWork").toString());
                            totalCWUpdate.setText(doc.getData().get("totalCW").toString());
                        }
                    }
                });

        Button deleteCourseBtn = findViewById(R.id.deleteCourseBtn);
        Button editCourseBtn = findViewById(R.id.editCourseBtn);
        Button setGoalBtn = findViewById(R.id.setGoalBtn);

        db.collection("semesters")
                .whereEqualTo("sem", attrs.get(1))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            if (doc.getData().get("fixedCourses") != null){

                                db.collection("courses")
                                        .whereEqualTo("sem", attrs.get(1))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot doc : task.getResult()){
                                                    courseCount+=1;
                                                }
                                                ArrayList<String> fixedCourses = new ArrayList<>();
                                                fixedCourses = (ArrayList<String>) doc.getData().get("fixedCourses");

                                                boolean flag = true;

                                                for (String course : fixedCourses){
                                                    if (course.equals(attrs.get(0)))
                                                        flag = false;
                                                }

                                                if (fixedCourses.size() >= courseCount - 1 && flag){
                                                    setGoalBtn.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                            else {
                                db.collection("courses")
                                        .whereEqualTo("sem", attrs.get(1))
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot doc : task.getResult()){
                                                    ++courseCount;
                                                }
                                                if (courseCount <= 1){
                                                    setGoalBtn.setEnabled(false);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("courses")
                        .whereEqualTo("courseCode", attrs.get(0))
                        .whereEqualTo("sem", attrs.get(1))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    db.collection("courses")
                                            .document(doc.getId())
                                            .delete();
                                }
                            }
                        });
                finish();
                startActivity(new Intent(GPA_Course.this, GPA_CalculatorActivity.class));
            }
        });

        editCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("courses")
                        .whereEqualTo("courseCode", attrs.get(0))
                        .whereEqualTo("sem", attrs.get(1))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    Map<String, Object> course = new HashMap<>();
                                    course.put("courseName", courseNameUpdate.getText().toString());
                                    course.put("courseWork", courseWorkUpdate.getText().toString());
                                    course.put("hours", hoursUpdate.getText().toString());
                                    course.put("totalCW", totalCWUpdate.getText().toString());

                                    db.collection("courses")
                                            .document(doc.getId())
                                            .update(course);
                                    finish();
                                    startActivity(new Intent(GPA_Course.this, GPA_CalculatorActivity.class));
                                }
                            }
                        });
            }
        });

        setGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoalBtn.setEnabled(false);
                LinearLayout layout = findViewById(R.id.setGoalLayout);
                String[] grades = {"C", "C+", "B-", "B", "B+", "A-", "A", "A+"};
                Float[] gradePts = {2.00f, 2.33f, 2.67f, 3.00f, 3.33f, 3.67f, 4.00f, 4.00f};

                TextView goalTV = new TextView(GPA_Course.this);
                goalTV.setTextSize(20);
                goalTV.setPadding(0,50,0,0);
                goalTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                goalTV.setText(grades[0]);
                goalTV.setTextColor(Color.BLACK);

                SeekBar goalBar = new SeekBar(GPA_Course.this);
                goalBar.setMax(7);

                Button setConfirm = new Button(GPA_Course.this);
                setConfirm.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                setConfirm.setText("CONFIRM SET GOAL");

                layout.addView(goalTV);
                layout.addView(goalBar);
                layout.addView(setConfirm);

                goalBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        goalTV.setText(grades[progress]);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                setConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("courses")
                                .whereEqualTo("courseCode", attrs.get(0))
                                .whereEqualTo("sem", attrs.get(1))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            db.collection("courses")
                                                    .document(doc.getId())
                                                    .update("goal", gradePts[goalBar.getProgress()]);
                                        }
                                    }
                                });

                        db.collection("semesters")
                                .whereEqualTo("sem", attrs.get(1))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                            ArrayList<String> fixedCourses = new ArrayList<>();
                                            boolean flag = true;
                                            if (doc.getData().get("fixedCourses") != null){
                                                fixedCourses = (ArrayList<String>) doc.getData().get("fixedCourses");
                                                for (String course : fixedCourses){
                                                    if (attrs.get(0).equals(course)){
                                                        flag = false;
                                                    }
                                                }
                                            }
                                            if (flag){
                                                fixedCourses.add(attrs.get(0));
                                            }
                                            db.collection("semesters")
                                                    .document(doc.getId())
                                                    .update("fixedCourses", fixedCourses);
                                        }
                                        CalculateGPAGoal(attrs.get(1));
                                    }
                                });
                    }
                });
            }
        });

    }

    public void CalculateGPAGoal(String sem){
        db.collection("semesters")
                .whereEqualTo("sem", sem)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean flag = false;
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            semGoal = Float.valueOf(doc.getData().get("goal").toString());
                            if (doc.getData().get("fixedCourses") != null) {
                                fixedCourse = (ArrayList<String>) doc.getData().get("fixedCourses");
                                flag = true;
                            }
                        }

                        if (flag) {
                            db.collection("courses")
                                    .whereEqualTo("sem", sem)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            scourseCount = 0;
                                            totalHours = 0;
                                            fixedGoal = new float[fixedCourse.size()];
                                            fixedHours = new float[fixedCourse.size()];
                                            courseGoals = new ArrayList<>();
                                            for (QueryDocumentSnapshot doc : task.getResult()){
                                                ++scourseCount;
                                                totalHours += Float.valueOf(doc.getData().get("hours").toString());
                                                if (fixedCourse.indexOf(doc.getData().get("courseCode").toString()) != -1){
                                                    fixedGoal[fixedCourse.indexOf(doc.getData().get("courseCode").toString())] =
                                                            Float.valueOf(doc.getData().get("goal").toString());
                                                    fixedHours[fixedCourse.indexOf(doc.getData().get("courseCode").toString())] =
                                                            Float.valueOf(doc.getData().get("hours").toString());
                                                    GoalMap courseGoal = new GoalMap(doc.getData().get("courseCode").toString(),
                                                            Float.valueOf(doc.getData().get("goal").toString()),
                                                            Float.valueOf(doc.getData().get("hours").toString()));
                                                    courseGoals.add(courseGoal);
                                                }
                                                else {
                                                    GoalMap courseGoal = new GoalMap(doc.getData().get("courseCode").toString(),
                                                            semGoal, Float.valueOf(doc.getData().get("hours").toString()));
                                                    courseGoals.add(0, courseGoal);
                                                }

                                            }
                                            calculateGPA(sem);
                                        }
                                    });
                        }
                        else {
                            db.collection("courses")
                                    .whereEqualTo("sem", sem)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot doc : task.getResult()){
                                                db.collection("courses").document(doc.getId())
                                                        .update("goal", semGoal);
                                            }
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    });
                        }
                    }
                });
    }

    public void calculateGPA(String sem){
        float G = semGoal * totalHours;
        float[] rand = fixedGoal;
        float specialGrade = 0;

        for (int i = 0; i < scourseCount; i++){
            if (i == 0){
                specialGrade = G / courseGoals.get(0).getHours();
            }
            else {
                specialGrade -= courseGoals.get(i).getHours() / courseGoals.get(0).getHours() * rand[i-1];
            }
        }

        if (specialGrade < 2)
            specialGrade = 2;

        courseGoals.get(0).setGoal(specialGrade);

        for (GoalMap courseGoal : courseGoals){
            db.collection("courses")
                    .whereEqualTo("sem", sem)
                    .whereEqualTo("courseCode", courseGoal.getCourseCode())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                db.collection("courses")
                                        .document(doc.getId())
                                        .update("goal", courseGoal.getGoal());
                            }
                            finish();
                            startActivity(new Intent(GPA_Course.this, GPA_CalculatorActivity.class));
                        }
                    });
        }
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
                startActivity(new Intent(GPA_Course.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_Course.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GPA_Course.this , ImportantDateActivity.class));
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
                startActivity(new Intent(GPA_Course.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}