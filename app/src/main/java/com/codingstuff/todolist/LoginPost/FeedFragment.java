package com.codingstuff.todolist.LoginPost;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codingstuff.todolist.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedFragment extends Fragment {

    private static final String TAG = "DocSnippets";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.feed_tab_fragment, container, false);

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        List<Post> listOfPosts = new ArrayList<>();

        db.collection("post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Get posts from firestore & store them in POJO object
                            Map postContent;
                            // Store all posts in a list
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                postContent = document.getData();
                                Post post = new Post(document.getId(), postContent);
                                // Store all posts inside listOfPosts
                                listOfPosts.add( post );
                                Log.d(TAG, "postContent.get Author => " + postContent.get("author"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        // Display posts on Feed Fragment
                        LinearLayout scrollview_ll = root.findViewById(R.id.scrollview_ll);

                        for (int i=0; i<listOfPosts.size(); i++){
                            MaterialCardView card = new MaterialCardView(getContext());
                            LinearLayout.LayoutParams card_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            card_params.setMargins(20,20,20,20);
                            card.setLayoutParams( card_params );
                            card.setCardElevation(10);

                            LinearLayout card_ll = new LinearLayout(getContext());
                            card_ll.setOrientation(LinearLayout.VERTICAL);
                            card_ll.setPadding(50,50,50,50);

                            TextView posttitle = new TextView(getContext());
                            LinearLayout.LayoutParams posttitle_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            posttitle_params.setMargins(12,12,12,12);
                            card_ll.setLayoutParams( posttitle_params );

                            TextView postcontent = new TextView(getContext());
                            LinearLayout.LayoutParams body_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            body_params.setMargins(0,16,0,0);
                            postcontent.setLayoutParams( body_params );

                            TextView authorname = new TextView(getContext());
                            authorname.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams author_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            author_params.setMargins(0,8,0,0);
                            authorname.setLayoutParams( body_params );

                            Map<String, String> post = (Map<String, String>)listOfPosts.get(i).getPosts();
                            String postID = listOfPosts.get(i).getPostID();

                            // Get Department & set as header
                            posttitle.setText(post.get("type"));
                            posttitle.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Large);
                            // Get content & set as body
                            postcontent.setText(post.get("content"));
                            postcontent.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Large);
                            authorname.setText( "Posted by: " + post.get("author") );
                            authorname.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Small);

                            card_ll.addView(posttitle);
                            card_ll.addView(postcontent);
                            card_ll.addView(authorname);

                            SharedPreferences sharedPref  = getContext().getSharedPreferences("CurrentUser" , Context.MODE_PRIVATE);
                            String priv = sharedPref.getString("privilege", "Fail to retrive privilege from shared pref" );


                            if ( priv.equals("staff") ){
                                MaterialButton cancelbtn = new MaterialButton(getContext());
                                cancelbtn.setText("Remove Post");
                                cancelbtn.setTextColor( getResources().getColor(R.color.white) );
                                cancelbtn.setBackgroundColor( getResources().getColor(R.color.orange) );
                                card_ll.addView(cancelbtn);

                                cancelbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Delete Post")
                                                .setMessage("Are you sure you want to delete this post?")

                                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                                // The dialog is automatically dismissed when a dialog button is clicked.
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        db.collection("post").document(postID)
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                        Toast.makeText(getContext(), "Deleted a Post", Toast.LENGTH_LONG).show();
                                                                        Log.d(TAG, "Post Deleted");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getContext(), "Fail to delete Post", Toast.LENGTH_LONG).show();
                                                                        Log.w(TAG, "Error deleting post", e);
                                                                    }
                                                                });
                                                        getFragmentManager().beginTransaction().detach(FeedFragment.this).attach(FeedFragment.this).commit();
                                                        Log.i("FragmentIsRefreshed: ", "Yes");
                                                    }
                                                })
                                                // A null listener allows the button to dismiss the dialog and take no further action.
                                                .setNegativeButton(android.R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                });
                            }

                            card.addView(card_ll);
                            scrollview_ll.addView(card);
                        }


                    }
                });

        return root;
    }
}