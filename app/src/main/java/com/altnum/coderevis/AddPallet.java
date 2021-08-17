package com.altnum.coderevis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddPallet extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText pallet_code;
    EditText pallet_volume;
    Button confirmCodeBtn;
    Button rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pallet);
        myDb = new DatabaseHelper(this);

        Intent in = getIntent();
        String chosenKindName = in.getStringExtra("com.altnum.coderevis.KIND_NAME");
        String chosenColorName = in.getStringExtra("com.altnum.coderevis.COLOR_NAME");

        pallet_code = (EditText) findViewById(R.id.codeInput);
        pallet_volume = (EditText) findViewById(R.id.volumeInput);
        confirmCodeBtn = (Button) findViewById(R.id.confirmBtnAddPallet);
        rejectBtn = (Button) findViewById(R.id.rejectBtnAddPallet);
        addColor(chosenKindName, chosenColorName);
        rejectAdding();
    }

    private void rejectAdding() {
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addColor(String kindName, String colorName) {
        confirmCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pallet_volume.getText().toString().length() > 0) {
                    Cursor res = myDb.getCodesByKindAndColor(kindName, colorName);

                    List<String> arr = new ArrayList<>();
                    int i = 0;
                    while (res.moveToNext()) {
                        arr.add(res.getString(0));
                        i++;
                    }

                    String strToAdd = pallet_code.getText().toString();

                    boolean emptyString = true;

                    for (int j = 0; j < strToAdd.length(); j++) {
                        if (strToAdd.charAt(j) != ' ')
                            emptyString = false;
                    }

                    if (arr.contains(strToAdd)) {
                        Toast.makeText(AddPallet.this, "Кодът вече съществува.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (emptyString) {
                        Toast.makeText(AddPallet.this, "Кодът не може да бъде с празен номер.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean is_inserted = myDb.insertDataForCodes(strToAdd, kindName, colorName, pallet_volume.getText().toString());

                    if (is_inserted)
                        Toast.makeText(AddPallet.this, "Кодът е въведен.", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(AddPallet.this, "Грешка в добавянето.", Toast.LENGTH_LONG).show();

                    finish();
                } else {
                    Toast.makeText(AddPallet.this, "Въведете валидна площ.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}