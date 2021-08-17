package com.altnum.coderevis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "CodeRevis.db";

    public static final String KINDS_TABLE = "kinds";
    public static final String KINDS_COL_1 = "ID";
    public static final String KINDS_COL_2 = "NAME";

    public static final String COLORS_TABLE = "colors";
    public static final String COLORS_COL_1 = "ID";
    public static final String COLORS_COL_2 = "NAME";

    public static final String KIND_COLORS_TABLE = "kind_colors";
    public static final String KIND_COLORS_COL_1 = "ID";
    public static final String KIND_COLORS_COL_2 = "KIND_ID";
    public static final String KIND_COLORS_COL_3 = "COLOR_ID";

    public static final String PALLETS_TABLE = "pallets";
    public static final String PALLETS_COL_1 = "ID";
    public static final String PALLETS_COL_2 = "KIND_NAME";
    public static final String PALLETS_COL_3 = "COLOR_NAME";
    public static final String PALLETS_COL_4 = "VOLUME";
    public static final String PALLETS_COL_5 = "CODE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + KINDS_TABLE + " (" + KINDS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KINDS_COL_2 + " TEXT)");
        db.execSQL("create table " + COLORS_TABLE + " (" + COLORS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLORS_COL_2 + " TEXT)");
        db.execSQL("create table " + KIND_COLORS_TABLE + " (" + KIND_COLORS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KIND_COLORS_COL_2 + " INTEGER, " + KIND_COLORS_COL_3 +
                " INTEGER, FOREIGN KEY(" + KIND_COLORS_COL_2 + ") REFERENCES kinds(" + KINDS_COL_1 + "), FOREIGN KEY(" + KIND_COLORS_COL_3 + ") REFERENCES colors(" + COLORS_COL_1 + "))");
        db.execSQL("create table " + PALLETS_TABLE + " (" + PALLETS_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PALLETS_COL_2 + " TEXT, " + PALLETS_COL_3 + " TEXT, " + PALLETS_COL_4 + " TEXT, " + PALLETS_COL_5 + " TEXT UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KINDS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COLORS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + KIND_COLORS_TABLE);

        onCreate(db);
    }

    public boolean insertDataForKinds(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KINDS_COL_2, name);
        long res = db.insert(KINDS_TABLE, null, contentValues);

        return res != -1;
    }

    public boolean insertDataForColor(String colorName, String kindName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLORS_COL_2, colorName);

        Cursor colorExist = db.rawQuery("select * from " + COLORS_TABLE + " WHERE NAME = ?", new String[] { colorName });
        long res = 0;

        if (colorExist.getCount() <= 0)
            res = db.insert(COLORS_TABLE, null, contentValues);

        String kindId = getKindIdByName(kindName, db);
        String colorId = getColorIdByName(colorName, db);

        db.execSQL("INSERT INTO " + KIND_COLORS_TABLE + " (" + KIND_COLORS_COL_2 + ", " + KIND_COLORS_COL_3 + ") VALUES (?, ?)", new String[] { kindId, colorId });

        colorExist.close();
        return res != -1;
    }

    public String getKindIdByName(String kindName, SQLiteDatabase db) {
        Cursor kindID = db.rawQuery("select * from " + KINDS_TABLE + " WHERE NAME = ?", new String[] { kindName });
        kindID.moveToNext();
        String res = String.valueOf(kindID.getInt(0));
        kindID.close();

        return res;
    }

    public String getColorIdByName(String colorName, SQLiteDatabase db) {
        Cursor colorID = db.rawQuery("select * from " + COLORS_TABLE + " WHERE NAME = ?", new String[] { colorName });
        colorID.moveToNext();
        String res = String.valueOf(colorID.getInt(0));
        colorID.close();

        return res;
    }

    public Cursor getAllKinds() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + KINDS_TABLE, null);
    }

    public void deleteKinds(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String kindId = getKindIdByName(name, db);

        db.delete(KIND_COLORS_TABLE, "KIND_ID = ?", new String[] { kindId });

        db.delete(KINDS_TABLE, "NAME = ?", new String[]{name});
    }

    public void deleteColor(String colorName, String kindName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String kindId = getKindIdByName(kindName, db);
        String colorId = getColorIdByName(colorName, db);

        Cursor colorArr = db.rawQuery("SELECT * FROM " + KIND_COLORS_TABLE + " WHERE " + KIND_COLORS_COL_3 + " = ?", new String[] { colorId });

        if (colorArr.getCount() <= 1) {
            colorArr.close();
            db.delete(COLORS_TABLE, "NAME = ?", new String[]{colorName});
            db.delete(KIND_COLORS_TABLE, "KIND_ID = ? AND COLOR_ID = ?", new String[]{kindId, colorId});
        }
        else {
            colorArr.close();
            db.delete(KIND_COLORS_TABLE, "KIND_ID = ? AND COLOR_ID = ?", new String[]{kindId, colorId});
        }
    }

    public Cursor getColorsByKind(String kindName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String kindId = getKindIdByName(kindName, db);

        return db.rawQuery("SELECT color.id, color.name FROM colors color JOIN kind_colors k_c ON color.id = k_c.COLOR_ID WHERE k_c.KIND_ID = ?", new String[] { kindId });
    }

    public Cursor getCodesByKindAndColor(String kindName, String colorName) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("SELECT pallet.code FROM pallets pallet WHERE pallet.KIND_NAME = ? AND pallet.COLOR_NAME = ?", new String[] { kindName, colorName });
    }

    public boolean insertDataForCodes(String code, String kindName, String colorName, String volume) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PALLETS_COL_2, kindName);
        contentValues.put(PALLETS_COL_3, colorName);
        contentValues.put(PALLETS_COL_4, volume);
        contentValues.put(PALLETS_COL_5, code);


        long res = db.insert(PALLETS_TABLE, null, contentValues);
        return res != -1;
    }

    public void deleteCode(String pallet_code) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PALLETS_TABLE, "CODE = ?", new String[] { pallet_code });
    }

    public String getVolumeByCode(String pallet_code) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor volume = db.rawQuery("SELECT pallet.VOLUME FROM pallets pallet WHERE pallet.CODE = ?", new String[] { pallet_code });
        volume.moveToNext();
        String res = volume.getString(0);
        volume.close();

        return res;
    }

    public int saveNewVolume(String pallet_code, String volume) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PALLETS_COL_4, volume);

        return db.update(PALLETS_TABLE, contentValues, "CODE = ?", new String[] { pallet_code });
    }

    public int calculateAllVolume() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor volumes = db.rawQuery("SELECT pallet.VOLUME FROM pallets pallet", new String[] { });

        int vol = 0;
        while (volumes.moveToNext()) {
            vol += Integer.parseInt(volumes.getString(0));
        }

        volumes.close();
        return vol;
    }

    public int getVolumePerKind(String kindName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor volumes = db.rawQuery("SELECT pallet.VOLUME FROM pallets pallet WHERE pallet.KIND_NAME = ? ", new String[] { kindName });

        int vol = 0;
        while (volumes.moveToNext()) {
            vol += Integer.parseInt(volumes.getString(0));
        }

        volumes.close();
        return vol;
    }
}
