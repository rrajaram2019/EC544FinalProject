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

public class WithdrawDialog extends AppCompatDialogFragment {
    private EditText editTextWithdrawAmount;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.withdraw_dialog, null);

        builder.setView(view)
                .setTitle("Make a Withdrawal")
                .setNegativeButton("Cancel Transaction", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(),"You cancelled the withdrawal, scan code again to make another one",Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Withdraw", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String withdraw = editTextWithdrawAmount.getText().toString();
                        try{
                            Float val = Float.parseFloat(withdraw);
                            if(val>0){
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("wallet").document("transactions")
                                        .update(
                                                "amount", withdraw,
                                                "withdraw",true,
                                                "deposit",false
                                        );
                                Toast.makeText(getContext(),"You are withdrawing "+withdraw+" Dai",Toast.LENGTH_SHORT).show();
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

        editTextWithdrawAmount = view.findViewById(R.id.withdrawAmount);

        return builder.create();
    }

}