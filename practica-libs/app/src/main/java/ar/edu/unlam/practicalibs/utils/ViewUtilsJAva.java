package ar.edu.unlam.practicalibs.utils;

import android.view.View;

public class ViewUtilsJAva {


    public static Boolean isVisible(View view){
        return view.getVisibility() == View.VISIBLE;
    }
}
