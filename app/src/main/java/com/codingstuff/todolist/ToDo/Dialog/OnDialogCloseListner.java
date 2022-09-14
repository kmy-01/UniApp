package com.codingstuff.todolist.ToDo.Dialog;

import android.content.DialogInterface;

public interface OnDialogCloseListner {

    void onDialogClose_Task(DialogInterface dialogInterface);

    void onDialogClose_Date(DialogInterface dialogInterface);
}
