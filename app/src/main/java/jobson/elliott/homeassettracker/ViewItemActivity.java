package jobson.elliott.homeassettracker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewItemActivity extends AppCompatActivity {

    private Singleton singleton;
    private static final String IMG_DIR = "imageDir";
    private ArrayList<String> itemNames;
    private Spinner itemSpinner;
    private HashMap<String, Asset> assetMap;
    private boolean TEXT_VERSION = false;

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
                displayClicked();
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


    private void displayClicked() {

        Spinner sp = findViewById(R.id.item_name_spinner);

        if (sp.getSelectedItem() == null) {

            Toast invalidToast = Toast.makeText(getApplicationContext(), "No item selected to display.", Toast.LENGTH_SHORT);
            invalidToast.show();

        } else {
            String curAssetName = sp.getSelectedItem().toString();
        //    updateDisplayText(curAssetName);
            updateDisplayImage(curAssetName);
        }
    }

    /*
     * Private helper called when there's an asset selected and we need to update the UI's text
     * display. Takes in the current asset's name and updates the display.
     *
     * Inputting the empty string will trigger the default, empty display.
     */
    private void updateDisplayImage(String assetName) {

        String name, description, date, cost, warrantyLength;
        name = description = date = cost = warrantyLength = "";


        // TODO: grab background from drawables
        /*
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_launcher_background);
        Bitmap photo = ((BitmapDrawable) myDrawable).getBitmap();
        */
    //    Bitmap photo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        Bitmap photo = null;

        TextView curItem = findViewById(R.id.curItem);
        TextView curDescription = findViewById(R.id.curDescription);
        TextView curPurchaseDate = findViewById(R.id.curPurchaseDate);
        TextView curCost = findViewById(R.id.curCost);
        TextView curWarrantyLength = findViewById(R.id.curWarrantyLength);
        ImageView imgView = findViewById(R.id.imageView);

        if (!assetName.isEmpty()) {

            Asset asset = assetMap.get(assetName);
            name = asset.getName();
            description = asset.getDescription();
            date = DatePickerFragment.addDateSeparators(asset.getPurchaseDate(), "/");
            cost = Float.toString(asset.getCost());
            warrantyLength = Float.toString(asset.getWarrantyLength());

            // get bitmap for our asset
            String selectStr = "SELECT * FROM assets WHERE itemName='" + name + "';";
            Cursor c = singleton.getDB().rawQuery(selectStr, null);
            c.moveToFirst();
            String imgName = c.getString(Asset.PHOTO_NAME);
            photo = loadImageFromStorage(imgName);
        }

        curItem.setText("Item: " + name);
        curDescription.setText("Description: " + description);
        curPurchaseDate.setText("Purchase Date: " + date);
        imgView.setImageBitmap(photo);

        if (!assetName.isEmpty()) {
            curCost.setText("Cost: $" + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength + " years");
        } else {
            curCost.setText("Cost: " + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength);
        }

    }

    /*
     * Private helper called when there's an asset selected and we need to update the UI's text
     * display. Takes in the current asset's name and updates the display.
     *
     * Inputting the empty string will trigger the default, empty display.
     * Commented out instead of deleted because it relies on XML that is only present during testing.
     */
    /*
    private void updateDisplayText(String curAssetName) {

        String name, description, photoName, date, cost, warrantyLength;
        name = description = photoName = date = cost = warrantyLength = "";

        TextView curItem = findViewById(R.id.curItem);
        TextView curDescription = findViewById(R.id.curDescription);
        TextView curPhotoName = findViewById(R.id.curPhotoName);
        TextView curPurchaseDate = findViewById(R.id.curPurchaseDate);
        TextView curCost = findViewById(R.id.curCost);
        TextView curWarrantyLength = findViewById(R.id.curWarrantyLength);

        if (!curAssetName.isEmpty()) {

            Asset asset = assetMap.get(curAssetName);
            name = asset.getName();
            description = asset.getDescription();
            photoName = asset.getPhotoName();
            date = DatePickerFragment.addDateSeparators(asset.getPurchaseDate(), "/");
            cost = Float.toString(asset.getCost());
            warrantyLength = Float.toString(asset.getWarrantyLength());
        }

        curItem.setText("Item: " + name);
        curDescription.setText("Description: " + description);
        curPhotoName.setText("Photo Name: " + photoName);
        curPurchaseDate.setText("Purchase Date: " + date);

        if (!curAssetName.isEmpty()) {
            curCost.setText("Cost: $" + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength + " years");
        } else {
            curCost.setText("Cost: " + cost);
            curWarrantyLength.setText("Warranty Length: " + warrantyLength);
        }
    }
    */


        /*
     * From: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     * Takes in path, loads bitmap from phone's internal memory, and returns bitmap.
     */

    private Bitmap loadImageFromStorage(String imgName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMG_DIR, Context.MODE_PRIVATE);

        try {
            File f = new File(directory, imgName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * Private helper method takes in the spinner and removes the currentAsset
     * it from our database, itemNames, and assetMap. Assumes that the named asset exists.
     */
    private void deleteAsset(Spinner sp) {
        String assetName = sp.getSelectedItem().toString();
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

            deleteAsset(sp);
        //    updateDisplayText("");
            updateDisplayImage("");
        }

    }
}
