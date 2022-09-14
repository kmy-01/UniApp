package com.codingstuff.todolist.gpaCalc;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codingstuff.todolist.R;
import com.google.android.material.card.MaterialCardView;

import java.util.Map;

public class GPA_Calculator {


    public static MaterialCardView createCard(Context c, Map<String, Object> e){
        LinearLayout outerLayout, innerLayout, secondaryTextLayout;
        MaterialCardView cardView;
        TextView courseCode, hours, goal, courseWork;

        cardView = new MaterialCardView(c);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8,8,8,8);
        cardView.setLayoutParams(params);

        outerLayout = new LinearLayout(c);
        outerLayout.setBackgroundColor(c.getResources().getColor(R.color.lavender));
        outerLayout.setOrientation(LinearLayout.VERTICAL);

        innerLayout = new LinearLayout(c);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(16,16,16,16);

        courseCode = new TextView(c);
        courseCode.setTextColor(Color.BLACK);
        courseCode.setTextSize(20);
        courseCode.setText(e.get("courseCode").toString());

        secondaryTextLayout = new LinearLayout(c);
        params.setMargins(0, 8, 0, 0);
        secondaryTextLayout.setLayoutParams(params);

        hours = new TextView(c);
        hours.setText("Hours: " + e.get("hours").toString());
        hours.setTextSize(16);
        hours.setPadding(0,0,5,0);

        courseWork = new TextView(c);
        courseWork.setText("Coursework: " + e.get("courseWork").toString() + "/" +
                e.get("totalCW").toString());
        courseWork.setTextSize(16);
        courseWork.setPadding(5,0,5,0);

        goal = new TextView(c);
        goal.setText("Goal: " + String.format("%.4f", Float.valueOf(e.get("goal").toString())));
        goal.setTextSize(16);
        goal.setPadding(5,0,5,0);

        secondaryTextLayout.addView(hours);
        secondaryTextLayout.addView(courseWork);
        secondaryTextLayout.addView(goal);
        innerLayout.addView(courseCode);
        innerLayout.addView(secondaryTextLayout);

        outerLayout.addView(innerLayout);

        cardView.addView(outerLayout);

        return cardView;
    }
}


