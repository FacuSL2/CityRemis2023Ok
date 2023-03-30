package com.creativedesign.CityRemis;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * @author Marlon Viana on 26/07/2019
 * @email 92marlonViana@gmail.com
 */
@SuppressLint("ValidFragment")
public class DialogCobrar extends DialogFragment {
    private View v;
    private String monto;
    private EventDialogCobrar eventDialogCobrar;


    public DialogCobrar(String monto, EventDialogCobrar eventDialogCobrar) {
        this.monto= monto;
        this.eventDialogCobrar = eventDialogCobrar;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_cobro, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textViewMondo = v.findViewById(R.id.textViewMondo);
        textViewMondo.setText(monto);

        Button buttonCobro = v.findViewById(R.id.buttonCobro);
        buttonCobro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                eventDialogCobrar.cobrado();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
