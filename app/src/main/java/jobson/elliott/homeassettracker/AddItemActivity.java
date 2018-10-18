package jobson.elliott.homeassettracker;

import android.app.DialogFragment;
//import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private Singleton singleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        singleton = Singleton.getInstance();
        // upon opening page, zero out purchase date
        singleton.setPurchaseDate("");
    }

    // https://stackoverflow.com/questions/27514338/cannot-resolve-method-showandroid-support-v4-app-fragmentmanager-java-lang-st
    public void showDatePickerDialog (View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    /*
     * Private helper method gathers the user's input from the EditTexts views and the
     * purchase date from the singleton, placing the data as strings into an arraylist.
     * Returns this list of user inputs.
     *
     * EditTextViews --> array<string> representation of item
     */
    private ArrayList<String> getItemInfo() {

        ArrayList<String> ret = new ArrayList<String>();

        EditText editName = findViewById(R.id.name_id);
        EditText editDescription = findViewById(R.id.description_id);
        EditText editCost = findViewById(R.id.cost_id);
        EditText editPhotoName = findViewById(R.id.photo_name_id);
        EditText editReceipt = findViewById(R.id.receipt_photo_name_id);
        EditText editWarranty = findViewById(R.id.warranty_length_id);

        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        String cost = editCost.getText().toString();
        String purchaseDate = singleton.getPurchaseDate();
        String photoName = editPhotoName.getText().toString();
        String receiptPhotoName = editReceipt.getText().toString();
        String warrantyLength = editWarranty.getText().toString();

        ret.add(name);
        ret.add(description);
        ret.add(cost);
        ret.add(purchaseDate);
        ret.add(photoName);
        ret.add(receiptPhotoName);
        ret.add(warrantyLength);

        return ret;
    }

    /*
     * Private helper method determines whether the user input is valid.
     * Returns a String ArrayList of the item information if the user's input is valid,
     * null otherwise. Displays a Toast indicating whether or not user info was valid.
     * Requirements for valid user input:
     * 1. contains name
     * 2. string names for photos are valid
     * 3. ?
     */
    private boolean itemValid(ArrayList<String> itemInfo) {

        String name = itemInfo.get(Asset.NAME);
        if (name == null || name.isEmpty()) {
            return false;
        }

        /*
         * TODO: check for invalid photo name input (might not do this, since it'll
         * ultimately be a camera that takes the photo locally (rather than inputting a weird name)
         */

        return true;
    }

    private String constructDBInsertString(ArrayList<String> itemInfo) {
        String ret = "INSERT INTO assets VALUES";
        ret += "('" + itemInfo.get(Asset.NAME)
                + "','" + itemInfo.get(Asset.DESCRIPTION)
                + "'," + Float.parseFloat(itemInfo.get(Asset.COST))
                + ",'" + itemInfo.get(Asset.PURCHASE_DATE)
                + "','" + itemInfo.get(Asset.PHOTO_NAME)
                + "','" +  itemInfo.get(Asset.RECEIPT_PHOTO_NAME)
                + "'," + Float.parseFloat(itemInfo.get(Asset.WARRANTY_LENGTH)) + ");";

        System.out.println("db string (add): " + ret);

        return ret;
    }


    /*
     * Private helper method take in an ArrayList string representation of the item to add and updates
     * our Singleton's database.
     *
     * The actual initialization and storage of the Asset objects will occur in ViewItemActivity.
     * ViewItemActivity, upon opening, will check the Singleton assetsModified flag to determine
     * whether or not to re-sync with the database.
     */
    private void addItemToDB(ArrayList<String> itemInfo) {

        Toast addToast = Toast.makeText(getApplicationContext(), "Item Added", Toast.LENGTH_SHORT);
        addToast.show();

        String insertString = constructDBInsertString(itemInfo);
        singleton.getDB().execSQL(insertString);
        singleton.setAssetsModified(true);

    }


    /*
     * Button handler. Checks user-inputted fields for item validity.
     * Adds item if all fields have been filled out and item otherwise valid (TODO: determine what this means).
     */
    public void addItemClicked(View view) {

        ArrayList<String> itemInfo = getItemInfo();

        if (itemValid(itemInfo)) {
            addItemToDB(itemInfo);

        } else {
            // TODO: make message more specific
            Toast invalidToast = Toast.makeText(getApplicationContext(), "Item Invalid", Toast.LENGTH_SHORT);
            invalidToast.show();
        }
    }

    /*
     * Button handler. Takes user to main page.
     */
    public void returnToMainClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
