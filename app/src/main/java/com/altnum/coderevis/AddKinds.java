package com.altnum.coderevis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddKinds extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText kind_name;
    Button addKind;
    Button rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_k_inds);
        myDb = new DatabaseHelper(this);

        kind_name = (EditText) findViewById(R.id.newKindText);
        addKind = (Button) findViewById(R.id.confirmBtn);
        rejectBtn = (Button) findViewById(R.id.rejectBtn);
        addKind();
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

    private void addKind() {
        addKind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getAllKinds();

                List<String> arr = new ArrayList<>();
                int i = 0;
                while (res.moveToNext()) {
                    arr.add(res.getString(1));
                    i++;
                }

                String strToAdd = kind_name.getText().toString();

                boolean emptyString = true;

                for (int j = 0; j < strToAdd.length(); j++) {
                    if (strToAdd.charAt(j) != ' ')
                        emptyString = false;
                }

                if (arr.contains(strToAdd)) {
                    Toast.makeText(AddKinds.this, "Видът вече съществува.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (emptyString) {
                    Toast.makeText(AddKinds.this, "Видът не може да бъде с празно наименование.", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean is_inserted = myDb.insertDataForKinds(strToAdd);

                if (is_inserted)
                    Toast.makeText(AddKinds.this, "Видът е въведен.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AddKinds.this, "Грешка в добавянето.", Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}