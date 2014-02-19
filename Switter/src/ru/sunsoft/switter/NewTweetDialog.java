package ru.sunsoft.switter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

public class NewTweetDialog extends DialogFragment {

    public final static int MESSAGE_LENGHT = 140;

    public interface NewTweetDialogListener {
        void onSend(String message);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof NewTweetDialogListener)) {
            throw new ClassCastException(activity.toString()
                    + " must implement YesNoListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final EditText messageText = new EditText(getActivity());
        messageText
                .setFilters(new InputFilter[] { new InputFilter.LengthFilter(
                        MESSAGE_LENGHT) });
        final AlertDialog.Builder sendMessageDialog = new AlertDialog.Builder(
                getActivity());

        sendMessageDialog.setTitle(R.string.new_message);
        sendMessageDialog.setPositiveButton(R.string.send,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((NewTweetDialogListener) getActivity())
                                .onSend(messageText.getText().toString());
                    }
                });
        sendMessageDialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        sendMessageDialog.setMessage("140");

        sendMessageDialog.setView(messageText);
        final AlertDialog alert = sendMessageDialog.create();
        messageText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                alert.setMessage(String.valueOf(140 - s.length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return alert;
    }
}
