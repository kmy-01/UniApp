package com.codingstuff.todolist.LoginPost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codingstuff.todolist.R;

import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    EditText username, password, displayName, mobileNumber;
    Spinner community, privilege;
    Button submit_btn;
    float v=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        username = root.findViewById(R.id.signup_email);
        password = root.findViewById(R.id.signup_password);
        displayName = root.findViewById(R.id.display_name);
        mobileNumber = root.findViewById(R.id.mobile_number);
        community  = root.findViewById(R.id.community);
        privilege  = root.findViewById(R.id.privilege);
        submit_btn = root.findViewById(R.id.signup_btn);

        submit_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username_text = username.getText().toString();
                        String password_text = password.getText().toString();
                        String displayName_text = displayName.getText().toString();
                        String mobileNumber_text = mobileNumber.getText().toString();
                        String community_text = community.getSelectedItem().toString().toLowerCase();
                        String privilege_text = privilege.getSelectedItem().toString().toLowerCase();

                        if (validate_email_pw(username, password, displayName, mobileNumber, username_text, password_text, displayName_text, mobileNumber_text)==false)
                            return;

                        // Pass data back to LoginSignupActivity.java
                        Intent intent = new Intent( getActivity(), LoginSignupActivity.class );
                        intent.putExtra("action", "signup");

                        intent.putExtra("username", username_text);
                        intent.putExtra("password", password_text);
                        intent.putExtra("mobileNumber", mobileNumber_text);
                        intent.putExtra("community", community_text);

                        Context context = getContext();
                        SharedPreferences sharedPref  = context.getSharedPreferences("CurrentUser" , Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("displayname", displayName_text);
                        editor.putString("privilege", privilege_text);
                        editor.commit();

                        startActivity( intent );
                    }
                }
        );

        // Animation
        username.setTranslationX(300);
        password.setTranslationX(300);
        username.setAlpha(v);
        password.setAlpha(v);
        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();

        return root;
    }

    public boolean validate_email_pw(EditText username, EditText password, EditText displayName, EditText mobileNumber, String username_text, String password_text, String displayName_text, String mobileNumber_text){
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
        if ( !Pattern.compile(".{6,}").matcher(password_text).matches() ){
            password.setError("Please key in at least 6 characters");
            password.requestFocus();
            return false;
        }
        // Validate Display Name
        if (displayName_text.isEmpty()){
            displayName.setError("Display Name is required");
            displayName.requestFocus();
            return false;
        }
        // Validate Mobile Number
        if (mobileNumber_text.isEmpty()){
            mobileNumber.setError("Mobile number is required");
            mobileNumber.requestFocus();
            return false;
        }
        if (!Pattern.compile("[0-9]*").matcher(password_text).matches()){
            mobileNumber.setError("eg. 0123456789");
            mobileNumber.requestFocus();
            return false;
        }


        return true;
    }
}