package com.codingstuff.todolist.ToDo.Model.enums;


import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.codingstuff.todolist.R;


/**
 * Created by wangshouheng on 2017/3/13. */
public enum Priority {
    LEVEL_01(1, R.string.priority_level_01, R.drawable.ic_level_01),
    LEVEL_02(2, R.string.priority_level_02, R.drawable.ic_level_02),
    LEVEL_03(3, R.string.priority_level_03, R.drawable.ic_level_03),
    LEVEL_04(4, R.string.priority_level_04, R.drawable.ic_level_04);

    public final int id;

    @StringRes
    public final int nameRes;

    @DrawableRes
    public final int iconRes;

    Priority(int id, @StringRes int nameRes, @DrawableRes int iconRes){
        this.id = id;
        this.nameRes = nameRes;
        this.iconRes = iconRes;
    }

    public static Priority getTypeById(int id){
        for (Priority type : values()){
            if (type.id == id){
                return type;
            }
        }
        throw new IllegalArgumentException("illegal priority id");
    }

    public static int getIdByType(Priority prio){
        for (Priority type : values()){
            if (type == prio){
                return type.id;
            }
        }
        throw new IllegalArgumentException("illegal priority");
    }
}
