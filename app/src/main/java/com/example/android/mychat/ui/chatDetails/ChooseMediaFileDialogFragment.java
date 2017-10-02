package com.example.android.mychat.ui.chatDetails;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.mychat.R;
import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Lets user add new list item.
 */
public class ChooseMediaFileDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_choose_mediafile, container,false);
        getDialog().setTitle("Choose your action");

        // Do something else

        return rootView;
    }

}
