package com.example.vitomisur.inventoryapk.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class InventoryProvider extends ContentProvider {

    private DbHelper mDbHelper;
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //match the input uri to uri for the single item and all items
    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEMS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    /**
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     * Sets the cursor for the right Uri (single item / all items) and notifier
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // get edit activity inputs and insert the item
    private Uri insertItem(Uri uri, ContentValues values) {
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Item requires valid price");
        }
        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
        if (/*quantity == null || quantity < 0*/ quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Item requires valid quantity");
        }
        String supplier = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Item requires a supplier");
        }
        Integer location = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_LOCATION);
        if (location == null || !InventoryContract.InventoryEntry.isValidLocation(location)) {
            throw new IllegalArgumentException("Item requires valid location");
        }
        String contact = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT);
        if (contact == null) {
            throw new IllegalArgumentException("Item requires supplier contact");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // same as insertItem but update it
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE)) {
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER)) {
            String supplier = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Item requires a supplier");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_LOCATION)) {
            Integer location = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_ITEM_LOCATION);
            if (location == null || !InventoryContract.InventoryEntry.isValidLocation(location)) {
                throw new IllegalArgumentException("Item requires valid location");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT)) {
            String contact = values.getAsString(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT);
            if (contact == null) {
                throw new IllegalArgumentException("Item requires supplier contact");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}