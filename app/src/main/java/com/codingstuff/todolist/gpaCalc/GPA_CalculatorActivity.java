package com.codingstuff.todolist.gpaCalc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class GPA_CalculatorActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String[] semesters;
    String selectedSem;

    private static float semGoal, totalHours = 0;
    private static int courseCount = 0;
    private static ArrayList<String> fixedCourse;
    private static float[] fixedGoal, fixedHours;
    private static ArrayList<GoalMap> courseGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpacalculator);
        getSupportActionBar().hide();

        LinearLayout mainLayout = findViewById(R.id.gpaMainLayout);

        db.collection("semesters")
                .orderBy("sem")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            int numrow = task.getResult().size();
                            if (numrow > 0){
                                int i = 0;
                                semesters = new String[numrow];
                                for (QueryDocumentSnapshot doc : task.getResult()){
                                    for (Map.Entry<String, Object> e : doc.getData().entrySet()){
                                        if (e.getKey().equals("sem"))
                                            semesters[i] = e.getValue().toString();
                                    }
                                    i++;
                                }

                                addSpinner(mainLayout);
                            }
                            else {
                                TextView tv = new TextView(GPA_CalculatorActivity.this);
                                tv.setText("No semesters found");
                                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                Button addSemBtn = new Button(GPA_CalculatorActivity.this);
                                addSemBtn.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                addSemBtn.setText("ADD SEMESTER");

                                mainLayout.setGravity(Gravity.CENTER);
                                mainLayout.addView(tv);
                                mainLayout.addView(addSemBtn);

                                addSemBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(GPA_CalculatorActivity.this, GPA_AddSem.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                        else {
                            Log.e("UNSUCCESS", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addSpinner(ViewGroup mainLayout){
        LinearLayout ll = new LinearLayout(GPA_CalculatorActivity.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(GPA_CalculatorActivity.this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (GPA_CalculatorActivity.this, android.R.layout.simple_spinner_item, semesters);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        spinner.setPadding(10, 10, 10, 10);

        Button addCourseBtn = new Button(GPA_CalculatorActivity.this);
        addCourseBtn.setText("ADD COURSE");

        Button addSemBtn = new Button(GPA_CalculatorActivity.this);
        addSemBtn.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        addSemBtn.setText("ADD SEMESTER");

        ll.addView(spinner);
        ll.addView(addCourseBtn);
        ll.addView(addSemBtn);

        LinearLayout secondaryLayout = new LinearLayout(GPA_CalculatorActivity.this);
        secondaryLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.addView(ll);

        SharedPreferences pref = getSharedPreferences("GPACalculaterPref", 0);
        String prevselectedsem = pref.getString("selectedSem", null);
        if (prevselectedsem != null)
            spinner.setSelection(spinnerArrayAdapter.getPosition(prevselectedsem));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedSem = parent.getItemAtPosition(pos).toString();
                showCourses(secondaryLayout, (String)parent.getItemAtPosition(pos));

                mainLayout.removeAllViews();
                mainLayout.addView(ll);
                addSeekBar(mainLayout);
                mainLayout.addView(secondaryLayout);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("selectedSem", parent.getItemAtPosition(pos).toString());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPA_CalculatorActivity.this, GPA_AddCourse.class);
                intent.putExtra("sem", selectedSem);
                startActivity(intent);
            }
        });

        addSemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPA_CalculatorActivity.this, GPA_AddSem.class);
                startActivity(intent);
            }
        });
    }

    public void showCourses(ViewGroup layout,String sem){
        String[] attrs = {"courseCode", "courseName", "hours", "courseWork", "totalCW", "goal"};
        db.collection("courses")
                .whereEqualTo("sem", sem)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        layout.removeAllViews();
                        int numrow = task.getResult().size();
                        if (numrow > 0) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                MaterialCardView card = GPA_Calculator.createCard(GPA_CalculatorActivity.this, doc.getData());
                                layout.addView(card);

                                card.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ArrayList<String> course = new ArrayList<>();
                                        course.add(doc.getData().get("courseCode").toString());
                                        course.add(doc.getData().get("sem").toString());
                                        Intent intent = new Intent(GPA_CalculatorActivity.this, GPA_Course.class);
                                        intent.putStringArrayListExtra("course", course);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }
                        else {
                            TextView tv = new TextView(GPA_CalculatorActivity.this);
                            tv.setText(R.string.GPA_course_no_record);
                            layout.addView(tv);
                        }
                    }
                });

    }

    public void addSeekBar(ViewGroup layout){
        LinearLayout goalLayout = new LinearLayout(GPA_CalculatorActivity.this);
        goalLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,80,0,100);
        goalLayout.setLayoutParams(params);

        TextView semGoal = new TextView(GPA_CalculatorActivity.this);
        semGoal.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        semGoal.setTextSize(28);
        semGoal.setTextColor(Color.BLACK);
        semGoal.setTypeface(Typeface.DEFAULT_BOLD);
        semGoal.setPadding(0,20,0,0);

        SeekBar semBar = new SeekBar(GPA_CalculatorActivity.this);
        semBar.setPadding(30,0,30,5);
        semBar.setMin(0);
        semBar.setMax(200);

        TextView semBarVal = new TextView(GPA_CalculatorActivity.this);
        Button changeGoalBtn = new Button(GPA_CalculatorActivity.this);
        changeGoalBtn.setText("CHANGE GOAL");
        changeGoalBtn.setLayoutParams(new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        db.collection("semesters")
                .whereEqualTo("sem", selectedSem)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
                            if (doc.getData().get("goal") != null){
                                semGoal.setText("GOAL: " + doc.getData().get("goal").toString());
                                semBar.setProgress((int)(Float.valueOf(doc.getData().get("goal").toString())*100-200));
                                semBarVal.setText(String.format("%.2f", Float.valueOf(doc.getData().get("goal").toString())));
                                changeGoalBtn.setEnabled(false);

                                DisplayMetrics metrics = new DisplayMetrics();
                                GPA_CalculatorActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                                int width = metrics.widthPixels - 60;
                                semBarVal.setX(semBar.getProgress() / (float) semBar.getMax() * width);
                                goalLayout.addView(semGoal);
                                goalLayout.addView(semBar);
                                goalLayout.addView(semBarVal);
                                goalLayout.addView(changeGoalBtn);
                            }
                        }
                    }
                });

        semBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = (progress * (seekBar.getWidth() - 6 * seekBar.getThumbOffset())) / seekBar.getMax();
                semBarVal.setText(String.format("%.2f", progress / 100.00 + 2));
                semBarVal.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
                changeGoalBtn.setEnabled(true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        changeGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    db.collection("semesters")
                            .whereEqualTo("sem", selectedSem)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot doc : task.getResult()){
                                        db.collection("semesters")
                                                .document(doc.getId())
                                                .update("goal", semBarVal.getText().toString());
                                    }
                                    CalculateGPAGoal(selectedSem);
                                }
                            });
            }
        });

        layout.addView(goalLayout);
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
                                            courseCount = 0;
                                            totalHours = 0;
                                            fixedGoal = new float[fixedCourse.size()];
                                            fixedHours = new float[fixedCourse.size()];
                                            courseGoals = new ArrayList<>();
                                            for (QueryDocumentSnapshot doc : task.getResult()){
                                                ++courseCount;
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

        for (int i = 0; i < courseCount; i++){
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
                            startActivity(getIntent());
                        }
                    });
        }
    }
}

class GoalMap{
    String courseCode;
    float goal, hours;

    public GoalMap(String courseCode, float goal, float hours){
        GoalMap.this.courseCode = courseCode;
        GoalMap.this.goal = goal;
        GoalMap.this.hours = hours;
    }

    public void setGoal(float goal) {
        this.goal = goal;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public float getGoal() {
        return goal;
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoalMap)) return false;
        GoalMap goalMap = (GoalMap) o;
        return Float.compare(goalMap.goal, goal) == 0 && Objects.equals(courseCode, goalMap.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseCode, goal);
    }
}