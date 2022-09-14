package com.codingstuff.todolist.ToDo.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ImportantDateId {

    @Exclude
    public String ImDateId;

    public  <T extends  ImportantDateId> T withId(@NonNull final String id){
        this.ImDateId = id;
        return  (T) this;
    }
}
