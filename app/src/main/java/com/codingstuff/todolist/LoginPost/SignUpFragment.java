package com.codingstuff.todolist.LoginPost;

import android.content.Intent;
import android.os.Bundle;
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

                        // Pass data back to LoginSignupActivity.java
                        Intent intent = new Intent( getActivity(), LoginSignupActivity.class );
                        intent.putExtra("action", "signup");

                        intent.putExtra("username", username_text);
                        intent.putExtra("password", password_text);
                        intent.putExtra("displayName", displayName_text);
                        intent.putExtra("mobileNumber", mobileNumber_text);
                        intent.putExtra("community", community_text);
                        intent.putExtra("privilege", privilege_text);
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
}