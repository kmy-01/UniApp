package com.codingstuff.todolist.LoginPost;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LogInFragment extends Fragment {

    public static final String TAG_login = "LogInFragment";
    EditText username, password;
    TextView forgotpw;
    Button submit_btn;
    float v=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        username = root.findViewById(R.id.login_email);
        password = root.findViewById(R.id.login_password);
        submit_btn = root.findViewById(R.id.login_btn);
        forgotpw = root.findViewById(R.id.forgot_pw);



        submit_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username_text = username.getText().toString();
                        String password_text = password.getText().toString();

                        if (validate_email_pw(username, password, username_text, password_text)==false)
                            return;

                        // Pass data back to LoginSignupActivity.java
                        Intent intent = new Intent( getActivity(), LoginSignupActivity.class );
                        intent.putExtra("action", "login");
                        intent.putExtra("username", username_text);
                        intent.putExtra("password", password_text);
                        startActivity( intent );
                    }
                }
        );

        forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG_login, "Clicked Forgot Password TextView");
                ShowResetPwDialog();
            }
        });

        // Animation
        username.setTranslationX(300);
        password.setTranslationX(300);
        username.setAlpha(v);
        password.setAlpha(v);
        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        return root;
    }

    public void ShowResetPwDialog(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE );
        dialog.setCancelable(false);
        dialog.setContentView( R.layout.custom_dialog );

        MaterialButton submitbtn = dialog.findViewById( R.id.reset_pw_button );

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText email_reset_pw = dialog.findViewById( R.id.email_reset_input );
                String emailAddress = email_reset_pw.getText().toString().trim();


                if (emailAddress.isEmpty()){
                    email_reset_pw.setError("Email is required");
                    email_reset_pw.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    email_reset_pw.setError("Please provide a valid email");
                    email_reset_pw.requestFocus();
                    return;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Link to reset password is sent to " + emailAddress, Toast.LENGTH_LONG).show();
                                    Log.d(TAG_login, "Password Reset Email sent.");
                                }
                                else{
                                    Toast.makeText(getContext(), "Fail to reset password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean validate_email_pw(EditText username, EditText password, String username_text, String password_text){
        // Validate email
        if (username_text.isEmpty()){
            username.setError("Email is required");
            username.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username_text).matches()){
            username.setError("Please provide a valid email");
            username.requestFocus();
            return false;
        }

        // Validate password
        if (password_text.isEmpty()){
            password.setError("Email is required");
            password.requestFocus();
            return false;
        }
        String regEx = ".{6,}";
        Pattern pwPattern = Pattern.compile(regEx, Pattern.UNICODE_CASE);
        if (!pwPattern.matcher( password_text ).matches()){
            password.setError("Please key in at least 6 characters");
            password.requestFocus();
            return false;
        }
        return true;
    }
}
