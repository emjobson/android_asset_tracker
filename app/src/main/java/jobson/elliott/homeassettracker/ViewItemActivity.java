package jobson.elliott.homeassettracker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewItemActivity extends AppCompatActivity {

    private Singleton singleton;

    private ArrayList<String> itemNames;
    private Spinner itemSpinner;
    private HashMap<String, Asset> assetMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        init();
    }

    /*
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("on resume called!");
        if (singleton.areAssetsModified()) {
            System.out.println("assets modified, so syncing with DB");
            syncWithDB();
        }
    }
    */

    /*
     * Protected method
     */
    private void init() {
        System.out.println("view item's init() method called");
        singleton = Singleton.getInstance();
        itemSpinner = findViewById(R.id.item_name_spinner);
        itemNames = new ArrayList<String>();
        assetMap = new HashMap<String, Asset>();

        syncWithDB();
        setupItemSpinner();
    }

    /*
     * Protected method updates itemNames and the assetMap. Called upon initialization of the
     * app and when items are added in AddItemActivity.
     *
     * Update itemNames, update assetMap, set assets modified flag to false.
     */
    public void syncWithDB() {

        // // gather names
        Cursor c = singleton.getDB().rawQuery(
                "SELECT * FROM assets;", null);

        try {
            while (c.moveToNext()) {
                itemNames.add(c.getString(Asset.NAME)); // TODO: might be 0 as innermost arg
            }
        } finally {
            c.close();
        }

        getAssetsFromDB();
        singleton.setAssetsModified(false);

    }

    /*
     * Public method prints all elements in an array. Used for testing.
     */
    public void printArr(ArrayList<String> arr) {
        for (String element : arr) {
            System.out.println(element);
        }
    }

    /*
     * Private helper method called during initialization to query the database,
     * construct Asset objects from the results, and store the Assets in a Map from
     * name -> Asset object.
     *
     * DB --> Map<String, Asset>
     */
    private void getAssetsFromDB() {

        Cursor c = singleton.getDB().rawQuery(
                "SELECT * FROM assets", null);

        while (c.moveToNext()) {

            ArrayList<String> params = new ArrayList<String>(); // setup params
            String name = c.getString(0);
            for (int i = 0; i < Asset.NUM_FIELDS; i++) {

                if (i == Asset.COST || i == Asset.WARRANTY_LENGTH) {
                    params.add(Float.toString(c.getFloat(i)));
                } else {
                    params.add(c.getString(i));
                }
            }

            Asset asset = new Asset(params);
            assetMap.put(name, asset);
        }

        c.close();
    }

    /*
    * Private helper method sets the itemSpinner's onSelected to store the selected item
    * in our singleton, and links the item names with the spinner.
    * Called upon app initialization.
    *
    */
    private void setupItemSpinner() {

        SpinnerAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, itemNames);
        itemSpinner.setAdapter(adapter);

        // set singleton when item selected
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /*
             * got rid of count, so we're allowing it to be called onCreate
             * this is fine, since onItemSelected not registered when you click on
             * the initially-selected item anyways
             */

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    displayClickedTextVersion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO: zero out display
            }
        });

    }



    /*
     * Public method handles when the "Home" button is clicked. Returns the user
     * to the main screen.
     */
    public void homeButtonClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
     * Private helper method for testing. Assumes that our "view assets" page currently
     * displays assets as text, rather than with a custom view. Simply prints the info
     * for the current asset.
     */
    private void displayClickedTextVersion() {

        Spinner sp = findViewById(R.id.item_name_spinner);

        if (sp.getSelectedItem() == null) {

            Toast invalidToast = Toast.makeText(getApplicationContext(), "No item selected to display.", Toast.LENGTH_SHORT);
            invalidToast.show();

        } else {

            String curAssetName = sp.getSelectedItem().toString();
            System.out.println("printing current asset: " + curAssetName);

            TextView curItem = findViewById(R.id.curItem);
            TextView curDescription = findViewById(R.id.curDescription);
            TextView curPhotoName = findViewById(R.id.curPhotoName);
            TextView curPurchaseDate = findViewById(R.id.curPurchaseDate);
            TextView curReceiptPhotoName = findViewById(R.id.curReceiptPhotoName);
            TextView curCost = findViewById(R.id.curCost);
            TextView curWarrantyLength = findViewById(R.id.curWarrantyLength);

            Asset asset = assetMap.get(curAssetName);
            curItem.setText("Item: " + asset.getName());
            curDescription.setText("Description: " + asset.getDescription());
            curPhotoName.setText("Photo Name: " + asset.getPhotoName());

            String dateStr = asset.getPurchaseDate();
            // TODO: fix date formatting
            String dateText = dateStr.substring(0,2) + "/" + dateStr.substring(2,4) + "/" + dateStr.substring(4);
            curPurchaseDate.setText("Purchase Date: " + dateText);
            curReceiptPhotoName.setText("Receipt Photo Name: " + asset.getReceiptPhotoName());
            curCost.setText("Cost: $" + Float.toString(asset.getCost()));
            curWarrantyLength.setText("Warranty Length: " + Float.toString(asset.getWarrantyLength()) + " years");
        }

    }


    // TODO: write this
    public void deleteCurrentClicked(View view) {

    }

    // TODO: write this
    public void editCurrentClicked(View view) {

    }
}
