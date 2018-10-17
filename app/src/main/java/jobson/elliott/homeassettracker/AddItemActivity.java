package jobson.elliott.homeassettracker;

import android.app.DialogFragment;
//import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class AddItemActivity extends AppCompatActivity {

    private Singleton singleton;

    private static final int NAME = 0;
    private static final int DESCRIPTION = 1;
    private static final int COST = 2;
    private static final int PURCHASE_DATE = 3;
    private static final int PHOTO_NAME = 4;
    private static final int RECEIPT_PHOTO_NAME = 5;
    private static final int WARRANTY_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        singleton = Singleton.getInstance();
        // upon opening page, need to zero out purchase date
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

        String name = itemInfo.get(NAME);
        if (name == null || name.isEmpty()) {
            return false;
        }

        /*
         * TODO: check for invalid photo name input (might not do this, since it'll
         * ultimately be a camera that takes the photo locally (rather than inputting a weird name)
         */

        return true;
    }

    /*
     * Button handler. Checks user-inputted fields for item validity.
     * Adds item if all fields have been filled out and item otherwise valid (TODO: determine what this means).
     */
    public void addItem(View view) {

        ArrayList<String> itemInfo = getItemInfo();


        if (itemValid(itemInfo)) {

        } else {

        }
    }

    /*
     * Button handler. Takes user to main page.
     */
    public void returnToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
