package jobson.elliott.homeassettracker;

import android.support.v7.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;

import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ArrayAdapter;
import android.content.Intent;



public class MainActivity extends AppCompatActivity {

    private Singleton singleton;
    private ArrayList<String> itemNames;
    private Spinner itemSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        singleton = Singleton.getInstance();
        itemSpinner = findViewById(R.id.item_name_spinner);

        if (singleton.getDB() == null) {
            singleton.setDB(openOrCreateDatabase("assetTrackerDB", MODE_PRIVATE, null));
        }

        Cursor tablesCursor = singleton.getDB().rawQuery(
          "SELECT * FROM sqlite_master WHERE type='table' AND name='assets';",
                null);
        if (tablesCursor.getCount() == 0) {
            resetAssetTable();
        }

        setupItemSpinner();

    }

    private void resetAssetTable() {
        String resetStr = "DROP TABLE IF EXISTS assets";
        singleton.getDB().execSQL(resetStr);
        setupAssetTable();
    }

    private void setupAssetTable() {
        String setupStr = "CREATE TABLE assets ("
                + "itemName TEXT, description TEXT, photoName TEXT,"
                + "purchaseDate TEXT, receiptPhotoName TEXT, cost INT,"
                + "warrantyLength INT";
        singleton.getDB().execSQL(setupStr);
    }

    /*
     * Private helper method gathers the names of the current items and stores
     * them in an ArrayList, sets the itemSpinner's onSelected to store the selected item
     * in our singleton, and links the item names with the spinner.
     */
    private void setupItemSpinner() {

        itemNames = new ArrayList<String>();
        Cursor c = singleton.getDB().rawQuery( // gather names
                "SELECT itemName FROM assets;", null);

        try {
            while (c.moveToNext()) {
                itemNames.add(c.getString(c.getColumnIndex("itemName")));
            }
        } finally {
            c.close();
        }

        // set singleton when item selected
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int count = 0; // don't want this called during onCreate
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (count >= 1) {
                    String item = itemSpinner.getSelectedItem().toString();
                    singleton.setCurrentItem(item);
                }
                count++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });

        SpinnerAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, itemNames);
        itemSpinner.setAdapter(adapter);
    }


    /*
     * Button handler. Takes user to add item page.
     */
    public void addItemScreen(View view) {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    /*
     * Button handler. Takes user to view item page.
     */
    public void viewItemScreen(View view) {
        Intent intent = new Intent(this, ViewItemActivity.class);
        startActivity(intent);
    }
}
