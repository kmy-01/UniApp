package com.codingstuff.todolist.LoginPost;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codingstuff.todolist.R;

public class LogInFragment extends Fragment {

    EditText username;
    EditText password;
    Button submit_btn;
    float v=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        username = root.findViewById(R.id.login_email);
        password = root.findViewById(R.id.login_password);
        submit_btn = root.findViewById(R.id.login_btn);

        submit_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String username_text = username.getText().toString();
                        String password_text = password.getText().toString();

                        // Pass data back to LoginSignupActivity.java
                        Intent intent = new Intent( getActivity(), LoginSignupActivity.class );
                        intent.putExtra("action", "login");
                        intent.putExtra("username", username_text);
                        intent.putExtra("password", password_text);
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
