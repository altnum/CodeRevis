package com.altnum.coderevis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ColorsActivity extends AppCompatActivity {
    ListView myColorsListView;
    String[] colors;
    DatabaseHelper myDb;
    boolean deletePressed = false;
    String kindName;

    @Override
    public void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_colors);

        onLoad();
    }

    private void onLoad() {
        Intent in = getIntent();
        kindName = in.getStringExtra("com.altnum.coderevis.KIND_NAME");

        TextView textView = (TextView) findViewById(R.id.chosenKind);
        textView.setText(kindName);

        FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.deleteColorBtn);
        FloatingActionButton addColorBtn = findViewById(R.id.addColorBtn);

        addColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSomeColor = new Intent(getApplicationContext(), AddColors.class);
                addSomeColor.putExtra("com.altnum.coderevis.CHOSEN_KIND_NAME", kindName);
                startActivity(addSomeColor);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePressed = !deletePressed;
                adaptMyColorsList(deletePressed);
            }
        });

        adaptMyColorsList(false);

        TextView volTV = (TextView) findViewById(R.id.allVolumeAtColors);
        int allVolPerKind = myDb.getVolumePerKind(kindName);
        String volStr = allVolPerKind + " кв.м";
        volTV.setText(volStr);

        myColorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView itemName = (TextView) view.findViewById(R.id.kindNameTextView);

                if (!deletePressed) {
                    Intent showPalletsActivity = new Intent(getApplicationContext(), PalletsCodes.class);
                    showPalletsActivity.putExtra("com.altnum.coderevis.CHOSEN_KIND_NAME", kindName);
                    showPalletsActivity.putExtra("com.altnum.coderevis.CHOSEN_COLOR_NAME", itemName.getText().toString());
                    startActivity(showPalletsActivity);
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    myDb.deleteColor(itemName.getText().toString(), kindName);
                                    Toast.makeText(ColorsActivity.this, "Цветът е изтрит.", Toast.LENGTH_LONG).show();
                                    deletePressed = false;
                                    onLoad();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ColorsActivity.this);
                    builder.setMessage("Наистина ли искате да изтриете определения елемент?").setPositiveButton("Да", dialogClickListener)
                            .setNegativeButton("Не", dialogClickListener).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors);

        onLoad();
    }

    private void adaptMyColorsList(boolean deletePressed) {
        myColorsListView = (ListView) findViewById(R.id.codesListView);
        getAllColors();

        ItemAdapter itemAdapter = new ItemAdapter(this, colors, deletePressed);
        myColorsListView.setAdapter(itemAdapter);
    }

    private void getAllColors() {
        myDb = new DatabaseHelper(this);
        Cursor colorsArr = myDb.getColorsByKind(kindName);

        List<String> arr = new ArrayList<>();
        int i = 0;
        while (colorsArr.moveToNext()) {
            arr.add(colorsArr.getString(1));
            i++;
        }

        colors = arr.toArray(new String[0]);
    }
}