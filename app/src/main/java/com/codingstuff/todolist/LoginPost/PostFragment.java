package com.codingstuff.todolist.LoginPost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PostFragment extends Fragment {

    private static final String TAG = "DocSnippets";
    TextInputLayout postLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.post_tab_fragment, container, false);
        MaterialButton submit_btn = root.findViewById(R.id.post_btn);

        final String[] DEPARTMENTS = new String[]{"DSA","DSSC", "DEAS", "DSS", "Division of Finance"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                root.getContext(),
                android.R.layout.simple_dropdown_item_1line,
                DEPARTMENTS
        );

        AutoCompleteTextView dept_choice = (AutoCompleteTextView) root.findViewById(R.id.dept_choice);
        dept_choice.setAdapter(adapter);
        TextInputEditText post_content = root.findViewById(R.id.post_content);

        // Clear text button
        postLabel = root.findViewById(R.id.postLabel);
        postLabel.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_content.setText("");
            }
        });

        submit_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> postInfo = new HashMap<>();
                        SharedPreferences sharedPref = getContext().getSharedPreferences( "CurrentUser" , Context.MODE_PRIVATE);
                        postInfo.put("author", sharedPref.getString( "displayname", "Invalid Author" ));
                        postInfo.put("content",post_content.getText().toString());
                        postInfo.put("type",dept_choice.getText().toString());

                        db.collection("post")
                                .add(postInfo)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Intent intent = new Intent(root.getContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                }
        );


        return root;
    }
}