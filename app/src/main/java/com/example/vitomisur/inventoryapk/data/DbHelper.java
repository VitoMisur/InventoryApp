package com.example.vitomisur.inventoryapk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vitomisur.inventoryapk.data.InventoryContract.InventoryEntry;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String DEFAULT = " DEFAULT " + InventoryEntry.DEFAULT;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                    InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    InventoryEntry.COLUMN_ITEM_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    InventoryEntry.COLUMN_ITEM_PRICE + INTEGER_TYPE + NOT_NULL + COMMA_SEP +
                    InventoryEntry.COLUMN_ITEM_QUANTITY + INTEGER_TYPE + NOT_NULL + DEFAULT + COMMA_SEP +
                    InventoryEntry.COLUMN_ITEM_LOCATION + INTEGER_TYPE + NOT_NULL  + DEFAULT + COMMA_SEP +
                    InventoryEntry.COLUMN_ITEM_SUPPLIER + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT + TEXT_TYPE + NOT_NULL + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;

    public final static String DATABASE_NAME = "Inventory.db";
    private final static int DATABASE_VERSION = 3;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
