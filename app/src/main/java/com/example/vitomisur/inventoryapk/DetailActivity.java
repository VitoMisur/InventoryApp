package com.example.vitomisur.inventoryapk;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vitomisur.inventoryapk.data.InventoryContract;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentItemUri;
    private static final int ITEM_LOADER = 0;
    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mLocationTextView;
    private TextView mSupplierTextView;
    private TextView mContactTextView;
    private Button quantityDecreaseButton;
    private Button quantityIncreaseButton;
    private ImageView contactSupplierImageView;

    int quantity = 0;
    String contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // get the data from(Uri) from the parent activity(main)
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        mNameTextView = findViewById(R.id.detail_name);
        mPriceTextView = findViewById(R.id.detail_price);
        mQuantityTextView = findViewById(R.id.detail_quantity);
        mSupplierTextView = findViewById(R.id.detail_supplier_name);
        mLocationTextView = findViewById(R.id.detail_location);
        mContactTextView = findViewById(R.id.detail_supplier_contact);

        // sets button to decrease quantity of the item
        quantityDecreaseButton = findViewById(R.id.detail_decrease);
        quantityDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity--;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);
                    int rowsUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);
                }
            }
        });
        // sets button to increase quantity of the item
        quantityIncreaseButton = findViewById(R.id.detail_increase);
        quantityIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);
                int rowsUpdated = getContentResolver().update(mCurrentItemUri, values, null, null);

            }
        });
        // on click use the intent to call the "contributor"
        contactSupplierImageView = findViewById(R.id.contact_supplier);
        contactSupplierImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact));
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(ITEM_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                intent.setData(mCurrentItemUri);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // sets the cursor with data for specific item and return the cursor loader of it
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_ITEM_NAME,
                InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_ITEM_LOCATION,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER,
                InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    // fill in the UI with data from the cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_QUANTITY);
            int locationColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_LOCATION);
            int supplierColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER);
            int contactColumnIndex = data.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT);

            String name = data.getString(nameColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            int price = data.getInt(priceColumnIndex);
            quantity = data.getInt(quantityColumnIndex);
            int location = data.getInt(locationColumnIndex);
            contact = data.getString(contactColumnIndex);

            mNameTextView.setText(name);
            mPriceTextView.setText(Integer.toString(price) + " $");
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierTextView.setText(supplier);
            mContactTextView.setText(contact);

            switch (location) {
                case InventoryContract.InventoryEntry.LOCATION_STOCK:
                    mLocationTextView.setText(getString(R.string.location_store));
                    break;
                case InventoryContract.InventoryEntry.LOCATION_STORE:
                    mLocationTextView.setText(getString(R.string.location_stock));
                    break;
                default:
                    mLocationTextView.setText(getString(R.string.location_supplier));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
