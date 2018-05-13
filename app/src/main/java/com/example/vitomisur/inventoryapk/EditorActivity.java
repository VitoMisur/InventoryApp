package com.example.vitomisur.inventoryapk;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vitomisur.inventoryapk.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentItemUri;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplierEditText;

    private EditText mContactEditText;

    private Spinner mLocationSpinner;

    private int mlocation = InventoryEntry.LOCATION_SUPPLIER;

    private boolean mItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // compare Uri and change the title
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        mNameEditText = findViewById(R.id.edit_name);
        mPriceEditText = findViewById(R.id.edit_price);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mContactEditText = findViewById(R.id.edit_supplier_contact);
        mLocationSpinner = findViewById(R.id.spinner_location);

        // listens for the changes in fields
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mContactEditText.setOnTouchListener(mTouchListener);
        mLocationSpinner.setOnTouchListener(mTouchListener);
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_location_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mLocationSpinner.setAdapter(genderSpinnerAdapter);

        mLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.location_stock))) {
                        mlocation = InventoryEntry.LOCATION_STOCK;
                    } else if (selection.equals(getString(R.string.location_store))) {
                        mlocation = InventoryEntry.LOCATION_STORE;
                    } else {
                        mlocation = InventoryEntry.LOCATION_SUPPLIER;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mlocation = InventoryEntry.LOCATION_SUPPLIER;
            }
        });
    }

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String contactString = mContactEditText.getText().toString().trim();

        // check if data is valid
        if (TextUtils.isEmpty(supplierString) || TextUtils.isEmpty(contactString) || TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, this.getString(R.string.missing_data),
                    Toast.LENGTH_SHORT).show();
        } else {
            int quantity = 0;
            if (!(TextUtils.isEmpty(quantityString))) {
                quantity = Integer.parseInt(quantityString);
            }
            int price = Integer.parseInt(priceString);
            if ((TextUtils.isEmpty(contactString)) || contactString.equals("")
                    || (TextUtils.isEmpty(nameString)) || nameString.equals("")
                    || (TextUtils.isEmpty(supplierString)) || supplierString.equals("")
                    || price <= 0 || (contactString.length() != 9)) {
                Toast.makeText(this, this.getString(R.string.validate_data),
                        Toast.LENGTH_SHORT).show();
            } else {
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
                values.put(InventoryEntry.COLUMN_ITEM_PRICE, price);
                values.put(InventoryEntry.COLUMN_ITEM_QUANTITY, quantity);
                values.put(InventoryEntry.COLUMN_ITEM_LOCATION, mlocation);
                values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER, supplierString);
                values.put(InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT, contactString);

                if (mCurrentItemUri == null) {
                    Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                    if (newUri == null) {
                        Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

                    if (rowsAffected == 0) {
                        Toast.makeText(this, getString(R.string.editor_update_item_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, getString(R.string.editor_update_item_successful),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // if listener works and user touched the fields
                // it will ask user if he really wants to leave the editing
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_QUANTITY,
                InventoryEntry.COLUMN_ITEM_LOCATION,
                InventoryEntry.COLUMN_ITEM_SUPPLIER,
                InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT};

        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QUANTITY);
            int locationColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_LOCATION);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER);
            int contactColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_SUPPLIER_CONTACT);

            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int location = cursor.getInt(locationColumnIndex);
            String contact = cursor.getString(contactColumnIndex);

            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mContactEditText.setText(contact);

            switch (location) {
                case InventoryEntry.LOCATION_STOCK:
                    mLocationSpinner.setSelection(1);
                    break;
                case InventoryEntry.LOCATION_STORE:
                    mLocationSpinner.setSelection(2);
                    break;
                default:
                    mLocationSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mContactEditText.setText("");
        mLocationSpinner.setSelection(0);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    // dialog before discard
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // dialog before user click back
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // dialog to confirm item deleting
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}