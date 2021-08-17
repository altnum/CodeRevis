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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PalletsCodes extends AppCompatActivity {

    ListView myCodesListView;
    String[] codes;
    DatabaseHelper myDb;
    boolean deletePressed = false;
    String kindName;
    String colorName;
    List<String> searched;

    @Override
    public void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_pallets_codes);

        onLoad();
    }

    private void onLoad() {
        Intent in = getIntent();
        kindName = in.getStringExtra("com.altnum.coderevis.CHOSEN_KIND_NAME");
        colorName = in.getStringExtra("com.altnum.coderevis.CHOSEN_COLOR_NAME");

        TextView textView = (TextView) findViewById(R.id.chosenKindAtCodes);
        textView.setText(kindName);

        TextView textView1 = (TextView) findViewById(R.id.chosenColorAtCodes);
        textView1.setText(colorName);

        FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.deleteCodeBtn);
        FloatingActionButton addCodeBtn = findViewById(R.id.addCodeBtn);

        searched = new ArrayList<>();
        searchSet();

        addCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addPallet = new Intent(getApplicationContext(), AddPallet.class);
                addPallet.putExtra("com.altnum.coderevis.KIND_NAME", kindName);
                addPallet.putExtra("com.altnum.coderevis.COLOR_NAME", colorName);
                startActivity(addPallet);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePressed = !deletePressed;
                adaptMyCodesList(deletePressed);
            }
        });

        adaptMyCodesList(false);

        myCodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView itemName = (TextView) view.findViewById(R.id.kindNameTextView);

                if (!deletePressed) {
                    openPalletInfo(itemName.getText().toString());
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    myDb.deleteCode(itemName.getText().toString());
                                    Toast.makeText(PalletsCodes.this, "Кодът е изтрит.", Toast.LENGTH_LONG).show();
                                    deletePressed = false;
                                    onLoad();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(PalletsCodes.this);
                    builder.setMessage("Наистина ли искате да изтриете определения елемент?").setPositiveButton("Да", dialogClickListener)
                            .setNegativeButton("Не", dialogClickListener).show();
                }
            }
        });
    }

    private void openPalletInfo(String code) {
        Intent showPalletsActivity = new Intent(getApplicationContext(), PalletInfo.class);
        showPalletsActivity.putExtra("com.altnum.coderevis.CODE", code);
        startActivity(showPalletsActivity);
    }

    private void searchSet() {
        SearchView search = (SearchView) findViewById(R.id.searchBar);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<String> codesList = Arrays.asList(codes);
                if (codesList.contains(query)) {
                    openPalletInfo(query);
                    return true;
                } else {
                    Toast.makeText(PalletsCodes.this, "Кодът не съществува в тази категория", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallets_codes);

        onLoad();
    }

    private void adaptMyCodesList(boolean deletePressed) {
        myCodesListView = (ListView) findViewById(R.id.codesListView);
        getAllCodes();

        ItemAdapter itemAdapter = new ItemAdapter(this, codes, deletePressed);
        myCodesListView.setAdapter(itemAdapter);
    }

    private void getAllCodes() {
        myDb = new DatabaseHelper(this);
        Cursor codesArr = myDb.getCodesByKindAndColor(kindName, colorName);

        List<String> arr = new ArrayList<>();

        while (codesArr.moveToNext()) {
            arr.add(codesArr.getString(0));
        }

        codes = arr.toArray(new String[0]);
    }
}