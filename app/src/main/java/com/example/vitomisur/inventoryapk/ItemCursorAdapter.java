package com.example.vitomisur.inventoryapk;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.vitomisur.inventoryapk.data.InventoryContract;
import com.example.vitomisur.inventoryapk.data.InventoryContract.InventoryEntry;

public class ItemCursorAdapter extends CursorAdapter {
    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // fill in the listView UI with cursor data
        TextView nameTextView = view.findViewById(R.id.name_list);
        TextView priceTextView = view.findViewById(R.id.price_list);
        TextView quantityTextView = view.findViewById(R.id.quantity_list);
        final int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME));
        String price = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE)) + "$";
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY));
        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText("" + quantity);
        Button decreaseQuantity = view.findViewById(R.id.sale);
        // logic to decrease quantity for specified item
        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int currentQuantity;
                    currentQuantity = quantity - 1;
                    ContentValues values = new ContentValues();
                    Uri itemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, currentQuantity);
                    int rowsUpdated = context.getContentResolver().update(itemUri, values, null, null);
                    context.getContentResolver().notifyChange(InventoryEntry.CONTENT_URI, null);
                }
            }
        });
    }
}