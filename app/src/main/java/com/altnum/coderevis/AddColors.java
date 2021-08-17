package com.altnum.coderevis;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddColors extends AppCompatActivity {
    DatabaseHelper myDb;
    EditText color_name;
    Button confirmColorBtn;
    Button rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_colors);
        myDb = new DatabaseHelper(this);

        Intent in = getIntent();
        String chosenKindName = in.getStringExtra("com.altnum.coderevis.CHOSEN_KIND_NAME");

        color_name = (EditText) findViewById(R.id.newColorText);
        confirmColorBtn = (Button) findViewById(R.id.confirmColorBtn);
        rejectBtn = (Button) findViewById(R.id.rejectBtn2);
        addColor(chosenKindName);
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

    private void addColor(String kindName) {
        confirmColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor res = myDb.getColorsByKind(kindName);

                List<String> arr = new ArrayList<>();
                int i = 0;
                while (res.moveToNext()) {
                    arr.add(res.getString(1));
                    i++;
                }

                String strToAdd = color_name.getText().toString();

                boolean emptyString = true;

                for (int j = 0; j < strToAdd.length(); j++) {
                    if (strToAdd.charAt(j) != ' ')
                        emptyString = false;
                }

                if (arr.contains(strToAdd)) {
                    Toast.makeText(AddColors.this, "Цветът вече съществува.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (emptyString) {
                    Toast.makeText(AddColors.this, "Цветът не може да бъде с празно наименование.", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean is_inserted = myDb.insertDataForColor(strToAdd, kindName);

                if (is_inserted)
                    Toast.makeText(AddColors.this, "Цветът е въведен.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AddColors.this, "Грешка в добавянето.", Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }
}
