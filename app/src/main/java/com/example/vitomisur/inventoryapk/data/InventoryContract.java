package com.example.vitomisur.inventoryapk.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.vitomisur.inventoryapk";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ITEMS = "inventory";

    // table info
    public static final class InventoryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public final static String TABLE_NAME = "inventory";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";
        public final static String COLUMN_ITEM_SUPPLIER = "supplier";
        public final static String COLUMN_ITEM_LOCATION = "location";
        public final static String COLUMN_ITEM_SUPPLIER_CONTACT = "contact";
        public final static int DEFAULT = 0;
        public final static int LOCATION_SUPPLIER = 0;
        public final static int LOCATION_STOCK = 1;
        public final static int LOCATION_STORE = 2;

        public static boolean isValidLocation(int location) {
            if (location == LOCATION_SUPPLIER || location == LOCATION_STOCK || location == LOCATION_STORE) {
                return true;
            }
            return false;
        }
    }
}
