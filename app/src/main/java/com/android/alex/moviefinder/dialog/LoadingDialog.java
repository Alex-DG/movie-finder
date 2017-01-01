package com.android.alex.moviefinder.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.android.alex.moviefinder.R;

import butterknife.ButterKnife;

/**
 * Created by Alex on 30/12/2016.
 */

public class LoadingDialog extends Dialog {

    private Context context;

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        ButterKnife.bind(this);
    }
}
