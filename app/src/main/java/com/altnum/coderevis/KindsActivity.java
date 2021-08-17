package com.altnum.coderevis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.altnum.coderevis.ItemAdapter;
import com.altnum.coderevis.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KindsActivity extends AppCompatActivity {
    ListView myKindsListView;
    String[] kinds;
    DatabaseHelper myDb;
    boolean deletePressed;

    @Override
    public void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_kinds);

        onLoad();
    }


    public void getAllKinds() {
        Cursor res = myDb.getAllKinds();

        List<String> arr = new ArrayList<>();
        int i = 0;
        while (res.moveToNext()) {
            arr.add(res.getString(1));
            i++;
        }

        kinds = arr.toArray(new String[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinds);

        onLoad();
    }

    private void onLoad() {
        myDb = new DatabaseHelper(this);
        FloatingActionButton addBtn = (FloatingActionButton) findViewById(R.id.addBtn);
        FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.deleteBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePressed = !deletePressed;
                adaptMyKindsList(deletePressed);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addSomeKind = new Intent(getApplicationContext(), AddKinds.class);
                startActivity(addSomeKind);
            }
        });

        adaptMyKindsList(false);
        int allVolume = myDb.calculateAllVolume();

        TextView volText = (TextView) findViewById(R.id.allVolumeAtKinds);
        String volStr = allVolume + " кв.м";
        volText.setText(volStr);

        myKindsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView itemName = (TextView) view.findViewById(R.id.kindNameTextView);

                if (!deletePressed) {
                    Intent showColorsActivity = new Intent(getApplicationContext(), ColorsActivity.class);

                    showColorsActivity.putExtra("com.altnum.coderevis.KIND_NAME", itemName.getText().toString());
                    startActivity(showColorsActivity);
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch(which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    myDb.deleteKinds(itemName.getText().toString());
                                    Toast.makeText(KindsActivity.this, "Видът е изтрит.", Toast.LENGTH_LONG).show();
                                    deletePressed = false;
                                    onLoad();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(KindsActivity.this);
                    builder.setMessage("Наистина ли искате да изтриете определения елемент?").setPositiveButton("Да", dialogClickListener)
                            .setNegativeButton("Не", dialogClickListener).show();
                }
            }
        });
    }

    private void adaptMyKindsList(boolean deletePressed) {
        myKindsListView = (ListView) findViewById(R.id.kindsListView);
        getAllKinds();

        ItemAdapter itemAdapter = new ItemAdapter(this, kinds, deletePressed);
        myKindsListView.setAdapter(itemAdapter);
    }
}