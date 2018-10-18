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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        singleton = Singleton.getInstance();

        if (singleton.getDB() == null) {
            singleton.setDB(openOrCreateDatabase("assetTrackerDB", MODE_PRIVATE, null));
        }



        Cursor tablesCursor = singleton.getDB().rawQuery(
          "SELECT * FROM sqlite_master WHERE type='table' AND name='assets';",
                null);
        if (tablesCursor.getCount() == 0) {
            resetAssetTable();
        }

    }

    private void resetAssetTable() {
        String resetStr = "DROP TABLE IF EXISTS assets;";
        singleton.getDB().execSQL(resetStr);
        setupAssetTable();
    }

    private void setupAssetTable() {
        String setupStr = "CREATE TABLE assets ("
                + "itemName TEXT, description TEXT, cost REAL, "
                + "purchaseDate INT, photoName TEXT, receiptPhotoName TEXT, "
                + "warrantyLength REAL);";
        singleton.getDB().execSQL(setupStr);
    }

    public void resetButtonClicked(View view) {
        resetAssetTable();
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
