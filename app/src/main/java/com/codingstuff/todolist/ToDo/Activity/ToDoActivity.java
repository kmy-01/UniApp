package com.codingstuff.todolist.ToDo.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.codingstuff.todolist.LoginPost.MainActivity;
import com.codingstuff.todolist.ToDo.Adapter.ToDoAdapter;
import com.codingstuff.todolist.ToDo.Functions.AddNewTask;
import com.codingstuff.todolist.ToDo.Dialog.OnDialogCloseListner;
import com.codingstuff.todolist.ToDo.Helper.TaskTouchHelper;
import com.codingstuff.todolist.ToDo.Model.ToDoModel;
import com.codingstuff.todolist.R;
import com.codingstuff.todolist.gpaCalc.GPA_CalculatorMain;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ToDoActivity extends AppCompatActivity implements OnDialogCloseListner{

    private FloatingActionButton navigation;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        navigation = findViewById(R.id.floatingActionButton_navtask);
        recyclerView = findViewById(R.id.recycerlview);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();
        firestore = FirebaseFirestore.getInstance();

        mList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ToDoActivity.this));

        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showButtomSheetDialog();
            }
        });


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            }
        });


        adapter = new ToDoAdapter(ToDoActivity.this , mList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TaskTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);
    }

    private void showData(){

       query = firestore.collection("TaskList").orderBy("Priority" , Query.Direction.DESCENDING);

       listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentSnapshot d : value.getDocuments()){
                        String id = d.getId();
                        ToDoModel toDoModel = d.toObject(ToDoModel.class).withId(id);
                        toDoModel.TaskId = id;
                        mList.add(toDoModel);
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(ToDoActivity.this, "No records found in database.", Toast.LENGTH_SHORT).show();
                }
                listenerRegistration.remove();

            }
        });
    }

    @Override
    public void onDialogClose_Task(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogClose_Date(DialogInterface dialogInterface) {


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
                startActivity(new Intent(ToDoActivity.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToDoActivity.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ToDoActivity.this , ImportantDateActivity.class));
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
                startActivity(new Intent(ToDoActivity.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }


}