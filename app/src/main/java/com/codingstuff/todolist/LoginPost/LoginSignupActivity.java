package com.codingstuff.todolist.LoginPost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class LoginSignupActivity extends AppCompatActivity {

    ImageView logo;
    TabLayout tabLayout;
    ViewPager viewPager;
    float v=0;
    Context context = LoginSignupActivity.this;

    private static final String TAG_ep = "EmailPassword";
    private static final String TAG_rw = "ReadAndWriteSnippets";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth mAuth; // Singleton
        FirebaseFirestore db;

        // AUTHENTICATION
        // Get Firebase Instance
        mAuth = FirebaseAuth.getInstance();

        // FIRESTORE
        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]

        // Get layout element
        setContentView(R.layout.activity_login);
        logo = findViewById(R.id.logo);
        tabLayout = findViewById(R.id.login_signup_tab_layout);
        viewPager = findViewById(R.id.login_signup_view_pager);

        // Tab Layout
        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Link Fragments to View Pager
        final LogInAdapter loginAdapter = new LogInAdapter( getSupportFragmentManager(), this, tabLayout.getTabCount() );
        viewPager.setAdapter( loginAdapter );
        viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );

        // Animation
        logo.setTranslationY(100);
        tabLayout.setTranslationY(300);
        logo.setAlpha(v);
        logo.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(300).start();

        // ---------------- FIREBASE USER AUTHENTICATION ------------------
        String email, password, displayName, mobileNumber, community, privilege;

        String action = this.getIntent().getStringExtra("action");

        if (action!=null){
            switch (action){
                case "signup":
//                    Toast.makeText(getApplicationContext(), "signup", Toast.LENGTH_LONG).show();
                    email = this.getIntent().getStringExtra("username");
                    password = this.getIntent().getStringExtra("password");
                    SharedPreferences sharedPref  = getApplicationContext().getSharedPreferences("CurrentUser" , Context.MODE_PRIVATE);
                    displayName = sharedPref.getString("displayname", "Fail to retrive privilege from shared pref" );
                    mobileNumber = this.getIntent().getStringExtra("mobileNumber");
                    community = this.getIntent().getStringExtra("community");
                    privilege = sharedPref.getString("privilege", "Fail to retrive privilege from shared pref" );

                    this.createAccount(mAuth, db, context, email, password, displayName, mobileNumber, community, privilege);
                    break;
                case "login":
//                    Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_LONG).show();
                    email = this.getIntent().getStringExtra("username");
                    password = this.getIntent().getStringExtra("password");
                    this.signIn(mAuth, context, email, password);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Tab not available", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//         Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Toast.makeText(getApplicationContext(), "onStart currentUserID: "+currentUser.getUid(), Toast.LENGTH_LONG).show();
//
//        if(currentUser != null){
//            Intent mainpage_intent = new Intent(LoginSignupActivity.this , MainActivity.class );
//            startActivity(mainpage_intent);
//            Toast.makeText(LoginSignupActivity.this, "go to MainActivity", Toast.LENGTH_SHORT).show();
//        }
    }

    private void createAccount(FirebaseAuth mAuth, FirebaseFirestore db, Context context, String email, String password, String displayName, String mobileNumber, String community, String privilege) {

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_ep, "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // TODO: Output UserID
                            String currentUserID = currentUser.getUid();
//                            Toast.makeText(LoginSignupActivity.this, "Go to MainActivity; uid: " + currentUserID, Toast.LENGTH_LONG).show();

                            User new_user = new User(currentUserID, displayName, mobileNumber, community, privilege);

                            // [START update_profile]
                            if (currentUser != null) {

                                // Add a new document with a generated ID
                                db.collection("userProfile").document(currentUserID)
                                        .set(new_user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG_rw, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG_rw, "Error adding document", e);
                                            }
                                        });
                            }

                            // Go to main page after successful sign up
                            Intent mainpage_intent = new Intent(context , MainActivity.class );
                            SharedPreferences sharedPref  = context.getSharedPreferences("CurrentUser" , Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("firestore_userid", currentUserID);
                            editor.commit();
                            startActivity(mainpage_intent);
                        } else {
                            // If sign up fails, display a message to the user.
                            Log.w(TAG_ep, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginSignupActivity.this, "Sign Up failed.\nPlease Try again",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(FirebaseAuth mAuth, Context context, String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_ep, "signInWithEmail: success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // TODO: Output UserID
                            String currentUserID = user.getUid();

                            // Go to main page if log in is successful
                            Intent mainpage_intent = new Intent(context , MainActivity.class );
                            SharedPreferences sharedPref  = context.getSharedPreferences("CurrentUser" , Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("firestore_userid", currentUserID);
                            editor.commit();
                            startActivity(mainpage_intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_ep, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginSignupActivity.this, "Authentication failed.\nPlease try again.",
                                    Toast.LENGTH_LONG).show();
                            // TODO
                        }
                    }
                });
        // [END sign_in_with_email]
    }

}