package com.example.android.mychat.ui.ViewMediaFile;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.mychat.R;

/**
 * Lets user add new list item.
 */
public class ViewImageDialogFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_view_image, container,false);


        // Do something else

        return rootView;
    }

}
