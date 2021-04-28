package com.example.wallet5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class DepositDialog extends AppCompatDialogFragment {
    private EditText editTextDepositAmount;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.deposit_dialog, null);

        builder.setView(view)
                .setTitle("Make a Deposit")
                .setNegativeButton("Cancel Transaction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"You cancelled the deposit, scan code again to make another one",Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Deposit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String deposit = editTextDepositAmount.getText().toString();
                        try{
                            Float val=Float.parseFloat(deposit);
                            if(val>0){
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("wallet").document("transactions")
                                        .update(
                                                "amount", deposit,
                                                "deposit",true,
                                                "withdraw",false
                                        );
                                Toast.makeText(getContext(),"You are depositing "+deposit+" Dai",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(),MainActivity.class));
                            }
                            else{
                                Toast.makeText(getContext(),"Invalid entry, please enter a valid number greater than zero",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getContext(),MainActivity.class));
                            }

                        }catch(NumberFormatException nfe) {
                            Toast.makeText(getContext(),"Invalid entry, please enter a valid number greater than zero",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(),MainActivity.class));
                        }

                    }
                });

        editTextDepositAmount = view.findViewById(R.id.withdrawAmount);

        return builder.create();
    }

}