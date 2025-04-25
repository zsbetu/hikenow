package com.example.hikenow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class StatusDialogFragment extends DialogFragment {

    public interface DialogListener{
        void onButtonClicked();
    }
    private DialogListener listener;
    public void setDialogListener(DialogListener listener){
        this.listener = listener;
    }
    private String title;
    private String message;

    public static StatusDialogFragment newInstance (String title, String message){
        StatusDialogFragment fragment = new StatusDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        if (getArguments() != null) {
            title = getArguments().getString("title");
            message = getArguments().getString("message");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (listener != null) {
                        listener.onButtonClicked();
                    }
                    dialog.dismiss();
                });

        return builder.create();
    }
}
