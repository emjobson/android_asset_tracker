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
     * Protected method
     */
    private void init() {
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
     * Updates itemNames and updates assetMap.
     */
    public void syncWithDB() {

        // // gather names
        Cursor c = singleton.getDB().rawQuery(
                "SELECT * FROM assets;", null);

        try {
            while (c.moveToNext()) {
                itemNames.add(c.getString(Asset.NAME));
            }
        } finally {
            c.close();
        }

        getAssetsFromDB();

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

            ArrayList<String> params = new ArrayList<String>();
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
     * Helper method to connect spinner to arraylist. Called during setup, erase, and delete
     * to make sure the spinner displays the current items.
     */
    private void connectSpinnerToList() {
        SpinnerAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, itemNames);
        itemSpinner.setAdapter(adapter);
    }

    /*
    * Private helper method sets the itemSpinner's onSelected to store the selected item
    * in our singleton, and links the item names with the spinner.
    * Called upon app initialization.
    *
    */
    private void setupItemSpinner() {

        connectSpinnerToList();

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
            updateDisplayText(curAssetName);
        }
    }

    /*
     * Private helper called when there's an asset selected and we need to update the UI's text
     * display. Takes in the current asset's name and updates the display.
     *
     * Inputting the empty string will trigger the default, empty display.
     */
    private void updateDisplayText(String curAssetName) {

        String name, description, photoName, date, receipt, cost, warrantyLength;
        name = description = photoName = date = receipt = cost = warrantyLength = "";

        TextView curItem = findViewById(R.id.curItem);
        TextView curDescription = findViewById(R.id.curDescription);
        TextView curPhotoName = findViewById(R.id.curPhotoName);
        TextView curPurchaseDate = findViewById(R.id.curPurchaseDate);
        TextView curReceiptPhotoName = findViewById(R.id.curReceiptPhotoName);
        TextView curCost = findViewById(R.id.curCost);
        TextView curWarrantyLength = findViewById(R.id.curWarrantyLength);

        if (!curAssetName.isEmpty()) {

            Asset asset = assetMap.get(curAssetName);
            name = asset.getName();
            description = asset.getDescription();
            photoName = asset.getPhotoName();
            date = DatePickerFragment.addDateSeparators(asset.getPurchaseDate(), "/");
            receipt = asset.getReceiptPhotoName();
            cost = Float.toString(asset.getCost());
            warrantyLength = Float.toString(asset.getWarrantyLength());
        }

        curItem.setText("Item: " + name);
        curDescription.setText("Description: " + description);
        curPhotoName.setText("Photo Name: " + photoName);
        curPurchaseDate.setText("Purchase Date: " + date);
        curReceiptPhotoName.setText("Receipt Photo Name: " + receipt);

        if (!curAssetName.isEmpty()) {
            curCost.setText("Cost: $" + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength + " years");
        } else {
            curCost.setText("Cost: " + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength);
        }
    }



    /*
     * Private helper method takes in the name of an asset to be deleted, and removes
     * it from our database, itemNames, and assetMap. Assumes that the named asset exists.
     */
    private void deleteAsset(String assetName, Spinner sp) {
        String deleteStr = "DELETE FROM assets WHERE itemName='" + assetName + "';";
        singleton.getDB().execSQL(deleteStr);

        itemNames.remove(assetName);
        assetMap.remove(assetName);

        connectSpinnerToList();
    }

    /*
     * Public method called when the user clicks the delete button. If there's an item selected
     * in the spinner, handles deletion by removing the item from our database, removing
     * the item from assetMap and itemNames, and updating the UI.
     */
    public void deleteClicked(View view) {

        Spinner sp = findViewById(R.id.item_name_spinner);

        if (sp.getSelectedItem() == null) {
            Toast invalidToast = Toast.makeText(getApplicationContext(), "No item selected to delete.", Toast.LENGTH_SHORT);
            invalidToast.show();

        } else { // fastest to handle all deletions individually, rather than deleting from other db or data structures then syncing

            String assetName = sp.getSelectedItem().toString();
            deleteAsset(assetName, sp);
            updateDisplayText("");
        }

    }

    // TODO: write this
    public void editCurrentClicked(View view) {

    }
}
