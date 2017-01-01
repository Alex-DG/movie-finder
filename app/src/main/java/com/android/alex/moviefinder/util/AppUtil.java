package com.android.alex.moviefinder.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.alex.moviefinder.application.MovieApp;

/**
 * Created by Alex on 29/12/2016.
 */

public class AppUtil {

    public static void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) MovieApp.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
