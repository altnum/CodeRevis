package com.altnum.coderevis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PalletInfo extends AppCompatActivity {
    DatabaseHelper myDb = new DatabaseHelper(this);
    String volume;
    TextView newVolumeTxtV;
    String newVolume;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_info);

        TextView initialVolume = (TextView) findViewById(R.id.initialVolume);
        newVolumeTxtV = (TextView) findViewById(R.id.newVolume);

        Intent in = getIntent();
        code = in.getStringExtra("com.altnum.coderevis.CODE");

        volume = myDb.getVolumeByCode(code);
        String volume1 = volume + " кв. м";
        initialVolume.setText(volume1);

        calculateBtn();
        confirmNewInfo();
        editInfo();
    }

    private void editInfo() {
        FloatingActionButton editEndVolume = (FloatingActionButton) findViewById(R.id.editNewVolumeBtn);

        editEndVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCategoryDialogBox();
            }
        });
    }

    private void openCategoryDialogBox() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.add_new_volume, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Променете текущата площ:");
        alert.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.etVolume);

        input.requestFocus();
        input.setHint("Площ в кв. м");
        input.setTextColor(Color.BLACK);

        alert.setPositiveButton("Запази", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String editedNewVolume = input.getText().toString();

                if (editedNewVolume.equals("")) {
                    Toast.makeText(PalletInfo.this, "Добавете валидна площ или отхвърлете.", Toast.LENGTH_LONG).show();
                    openCategoryDialogBox();
                } else {
                    myDb.saveNewVolume(code, editedNewVolume);
                    finish();
                }
            }
        });

        alert.setNegativeButton("ОТХВЪРЛИ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getApplicationContext(),
                                "Промените бяха отхвърлени.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void confirmNewInfo() {
        FloatingActionButton confirmChanges = (FloatingActionButton) findViewById(R.id.confirmChangesBtn);

        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newVolume != null) {
                    if (Integer.parseInt(newVolume) > 0) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        int res = myDb.saveNewVolume(code, newVolume);

                                        if (res > 0)
                                            Toast.makeText(PalletInfo.this, "Информацията бе променена.", Toast.LENGTH_LONG).show();
                                        else
                                            Toast.makeText(PalletInfo.this, "Неуспешна промяна на информацията.", Toast.LENGTH_LONG).show();

                                        finish();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Toast.makeText(PalletInfo.this, "Промяната на информацията беше отменена.", Toast.LENGTH_LONG).show();

                                        finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(PalletInfo.this);
                        builder.setMessage("Приемате ли промените?").setPositiveButton("Да", dialogClickListener)
                                .setNegativeButton("Не", dialogClickListener).show();
                    } else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        myDb.deleteCode(code);
                                        Toast.makeText(PalletInfo.this, "Кодът бе изтрит.", Toast.LENGTH_LONG).show();
                                        finish();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Toast.makeText(PalletInfo.this, "Промяната на информацията беше отменена.", Toast.LENGTH_LONG).show();

                                        finish();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(PalletInfo.this);
                        builder.setMessage("Палето е с нулева площ. Кодът и палето ще бъдат изтрити. Приемате ли?").setPositiveButton("Да", dialogClickListener)
                                .setNegativeButton("Не", dialogClickListener).show();
                    }



                } else {
                    Toast.makeText(PalletInfo.this, "Няма промяна в информацията.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    public void calculateBtn() {
        Button calc = (Button) findViewById(R.id.calculateNewVolumeBtn);
        EditText takenVol = (EditText) findViewById(R.id.soldVolumeInput);

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (takenVol.getText().toString().length() > 0) {
                    int resultVol = Integer.parseInt(volume) - Integer.parseInt(takenVol.getText().toString());

                    if (resultVol < 0)
                        resultVol = 0;

                    newVolume = String.valueOf(resultVol);
                    String resVol = newVolume + " кв. м";
                    newVolumeTxtV.setText(resVol);
                } else {
                    Toast.makeText(PalletInfo.this, "Въведете валидни кв.м!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}