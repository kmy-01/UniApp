package com.codingstuff.todolist.LoginPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.codingstuff.todolist.ToDo.Activity.ImportantDateActivity;
import com.codingstuff.todolist.ToDo.Activity.ToDoActivity;
import com.codingstuff.todolist.R;
import com.codingstuff.todolist.gpaCalc.GPA_CalculatorMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    User user;
    private FloatingActionButton navigation;
    private static final String TAG = "DocSnippets";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences( "CurrentUser" , Context.MODE_PRIVATE);
        String firestore_userid = sharedPref.getString( "firestore_userid", "Invalid firestore_userid" );
        SharedPreferences.Editor editor = sharedPref.edit();

        Log.d(TAG, "User ID: " + firestore_userid);

        tabLayout = findViewById(R.id.login_signup_tab_layout);
        viewPager = findViewById(R.id.login_signup_view_pager);

        tabLayout.addTab(tabLayout.newTab().setText("Feed"));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        navigation = findViewById(R.id.floatingActionButton_navdate);

        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showButtomSheetDialog();
            }
        });


        // Get document by passing in firestore_userid
        DocumentReference docRef = db.collection("userProfile").document(firestore_userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {  // Fires even if failed
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Get user data
                        Map userData_map;
                        userData_map = document.getData();
                        Map<String, String> userData_strmap = (Map<String, String>) userData_map;

                        user = new User(firestore_userid, userData_strmap.get("displayName"), userData_strmap.get("contactNumber"),userData_strmap.get("community"),userData_strmap.get("privilege"));
                        Context context = getApplicationContext();
                        editor.putString("displayname", user.getDisplayName());
                        editor.putString("privilege", user.getPrivilege());
                        editor.commit();

                        // Staff has an extra tab
                        if (user.getPrivilege().equalsIgnoreCase("staff")){
                            tabLayout.addTab(tabLayout.newTab().setText("Post"));
                            MainActivityAdapter mainActivityAdapter = new MainActivityAdapter( getSupportFragmentManager(), getApplicationContext(), tabLayout.getTabCount() );
                            viewPager.setAdapter( mainActivityAdapter );
                            viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Link Fragments to View Pager
        MainActivityAdapter mainActivityAdapter = new MainActivityAdapter( getSupportFragmentManager(), this, tabLayout.getTabCount() );
        viewPager.setAdapter( mainActivityAdapter );
        viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );
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
                startActivity(new Intent(MainActivity.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this , ImportantDateActivity.class));
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
                startActivity(new Intent(MainActivity.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}