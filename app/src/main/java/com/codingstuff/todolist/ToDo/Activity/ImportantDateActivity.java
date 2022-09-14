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
import com.codingstuff.todolist.ToDo.Adapter.ImportantDateAdapter;
import com.codingstuff.todolist.ToDo.Functions.AddNewImportantDate;
import com.codingstuff.todolist.ToDo.Dialog.OnDialogCloseListner;
import com.codingstuff.todolist.ToDo.Helper.ImportantDateTouchHelper;
import com.codingstuff.todolist.ToDo.Model.ImportantDateModel;
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

public class ImportantDateActivity extends AppCompatActivity implements OnDialogCloseListner {

    private FloatingActionButton navigation;
    private RecyclerView recyclerView;
    private FloatingActionButton Fab_date;
    private FirebaseFirestore firestore;
    private ImportantDateAdapter adapter;
    private List<ImportantDateModel> imlist;
    private Query query;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_date);

        navigation = findViewById(R.id.floatingActionButton_navdate);
        recyclerView = findViewById(R.id.recycerlview_importantdate);
        Fab_date = findViewById(R.id.floatingActionButton_importantdate);
        firestore = FirebaseFirestore.getInstance();

        imlist = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImportantDateActivity.this));


        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showButtomSheetDialog();
            }
        });

        Fab_date.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                AddNewImportantDate.newInstance().show(getSupportFragmentManager(),AddNewImportantDate.TAG);
            }
        });


        adapter = new ImportantDateAdapter(ImportantDateActivity.this, imlist);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ImportantDateTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);
    }

    private void showData() {
        query = firestore.collection("ImportantDateList").orderBy("Countdown", Query.Direction.ASCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentSnapshot d : value.getDocuments()){
                        String id = d.getId();
                        ImportantDateModel model = d.toObject(ImportantDateModel.class).withId(id);
                        model.ImDateId = id;
                        imlist.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(ImportantDateActivity.this, "No records found in database.", Toast.LENGTH_SHORT).show();
                }

                listenerRegistration.remove();
            }
        });
    }

    @Override
    public void onDialogClose_Task(DialogInterface dialogInterface) {

    }

    @Override
    public void onDialogClose_Date(DialogInterface dialogInterface) {
        imlist.clear();
        showData();
        adapter.notifyDataSetChanged();
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
                startActivity(new Intent(ImportantDateActivity.this , MainActivity.class));
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImportantDateActivity.this , ToDoActivity.class));
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ImportantDateActivity.this , ImportantDateActivity.class));
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
                startActivity(new Intent(ImportantDateActivity.this , GPA_CalculatorMain.class));
            }
        });

        sheetDialog.show();
        sheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sheetDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        sheetDialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}