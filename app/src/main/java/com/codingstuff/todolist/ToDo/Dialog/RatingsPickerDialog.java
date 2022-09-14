package com.codingstuff.todolist.ToDo.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.codingstuff.todolist.ToDo.Model.enums.Priority;
import com.codingstuff.todolist.R;

public class RatingsPickerDialog extends DialogFragment {

    public static final String TAG = "RatingsPickerDialog";

    private int[] tvBoxIds = new int[]{R.id.tv11, R.id.tv12, R.id.tv21, R.id.tv22};



    private OnPrioritySelectedListener onPrioritySelectedListener;

    public RatingsPickerDialog() {

    }

    public static RatingsPickerDialog newInstance(OnPrioritySelectedListener onPrioritySelectedListener) {
        RatingsPickerDialog ratingsPickerDialog = new RatingsPickerDialog();
        ratingsPickerDialog.setOnPrioritySelectedListener(onPrioritySelectedListener);
        return ratingsPickerDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String title = getArguments().getString("title");
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_priority_picker_layout, null);

        for (int i = 0; i <tvBoxIds.length ; i++) {
            final int finalI = i;
            rootView.findViewById(tvBoxIds[i]).setOnClickListener(v -> {
                Priority priority = null;
                switch (finalI){
                    case 0:
                        priority = Priority.LEVEL_01;
                        break;
                    case 1:
                        priority = Priority.LEVEL_02;
                        break;
                    case 2:
                        priority = Priority.LEVEL_03;
                        break;
                    case 3:
                        priority = Priority.LEVEL_04;
                        break;
                }
                if (onPrioritySelectedListener != null) {
                    onPrioritySelectedListener.onPrioritySelected(priority);
                }
                dismiss();
            });
        }

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.pick_one_from_four_priorities)
                .setView(rootView)
                .setNegativeButton(R.string.text_cancel, null)
                .create();

        /*return new AlertDialog.Builder(getContext())
                .setTitle("title")
                .setView(rootView)
                .setNegativeButton(R.string.text_cancel, null)
                .create();*/
    }


    public void setOnPrioritySelectedListener(OnPrioritySelectedListener onPrioritySelectedListener) {
        this.onPrioritySelectedListener = onPrioritySelectedListener;
    }

    public interface OnPrioritySelectedListener{
        void onPrioritySelected(Priority priority);
    }

}
