package com.jbapplab.tasks.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jbapplab.tasks.activity.R;
import com.jbapplab.tasks.interfaces.OnEditFinished;

import com.jbapplab.tasks.fragment.TaskEditFragment;

import static com.jbapplab.tasks.fragment.TaskEditFragment.*;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.save_confirmation)
                .setTitle(R.string.confirmation)
                .setPositiveButton(R.string.positive_button,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        //perform an action eg. saving
                        //save();
                       //((OnEditFinished) getActivity()).finishEditingTask(); //brought from taskEditFragment to return to the list of tasks after edit
                    }
                })
                .setNegativeButton(R.string.negative_button,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                dialog.cancel();
                            }
                        });
        setCancelable(false); //if you press back you cannot close the dialogue box.
        // I had to remove it from the builder to make it work. It is because a fragment is wrapping the dialog. We are dealing with the fragment not the dialog.
    return builder.create();
    }


}

