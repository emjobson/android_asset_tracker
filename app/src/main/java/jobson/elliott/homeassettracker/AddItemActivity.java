package jobson.elliott.homeassettracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

// much of camera code from top post in:

/*
 * "new cam code" from: https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity
 * saving images in internal storage from: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
 */

public class AddItemActivity extends AppCompatActivity {

    private Singleton singleton;
    private DatePickerFragment datePicker;
    private String imgName;

    // new cam code
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final String IMG_DIR = "imageDir";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        singleton = Singleton.getInstance();
        imgName = ""; //TODO: verify that img resets properly after leaving and returning to page

        dateInit();
        cameraInit(); // new cam code

    }

    // new cam code
    private void cameraInit() {
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);

                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    //new cam code
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    // new cam code
    /*
     * After taking photo, sets the bitmap of our imageView and saves the name of the image.
     * The name will be used to store the bitmap in the phone's internal memory.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            imgName = Asset.getNextImgName();
            imageView.invalidate();

        }
    }

    /*
     * Private helper method called upon opening the add view. Initializes the datePicker,
     * and indicates that the default date selected is the current date by updating the relevant
     * TextView.
     */
    private void dateInit() {

        datePicker = new DatePickerFragment();
        String date = datePicker.getDate();
        String separatedDate = DatePickerFragment.addDateSeparators(date, "/");
        TextView dateText = findViewById(R.id.date_id);

        dateText.setText("Date Chosen: " + separatedDate);
    }

    // https://stackoverflow.com/questions/27514338/cannot-resolve-method-showandroid-support-v4-app-fragmentmanager-java-lang-st
    public void showDatePickerDialog (View view) {
    //    DialogFragment newFragment = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "datePicker");
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
    //    EditText editPhotoName = findViewById(R.id.photo_name_id);
        EditText editWarranty = findViewById(R.id.warranty_length_id);

        String name = editName.getText().toString();
        String description = editDescription.getText().toString();
        String cost = editCost.getText().toString();

    //    String purchaseDate = singleton.getPurchaseDate();
        String purchaseDate = datePicker.getDate();

    //    String photoName = editPhotoName.getText().toString();
        String warrantyLength = editWarranty.getText().toString();

        ret.add(name);
        ret.add(description);
        ret.add(cost);
        ret.add(purchaseDate);
        ret.add(imgName);
        ret.add(warrantyLength);

        return ret;
    }

    private boolean dbContainsName(String name) {
        Cursor c = singleton.getDB().rawQuery(
                "SELECT * FROM assets;", null);

        try {
            while (c.moveToNext()) {
                if (c.getString(Asset.NAME).equals(name)) return true;
            }
        } finally {
            c.close();
        }

        return false;
    }

    // Private helper method used by item validation to make sure all fields are fille.d
    private boolean arrContainsEmptyString(ArrayList<String> arr) {
        for (String elem : arr) {
            if (elem.isEmpty()) return true;
        }
        return false;
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

        if (arrContainsEmptyString(itemInfo)) {
            Toast invalidToast = Toast.makeText(getApplicationContext(), "Item invalid: one or more fields missing", Toast.LENGTH_SHORT);
            invalidToast.show();
            return false;

        } else if (dbContainsName(name)) {
            Toast invalidToast = Toast.makeText(getApplicationContext(), "Item invalid: can't have duplicate names", Toast.LENGTH_SHORT);
            invalidToast.show();
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
                + "'," + Float.parseFloat(itemInfo.get(Asset.WARRANTY_LENGTH)) + ");";

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

        Toast addToast = Toast.makeText(getApplicationContext(), "Item added", Toast.LENGTH_SHORT);
        addToast.show();

        String insertString = constructDBInsertString(itemInfo);
        singleton.getDB().execSQL(insertString);
    }

    /*
     * From: https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
     * Saves the bitmap associated with imageView internally, and returns the absolute path.
     * Also uses the imgName ivar to specify the full path.
     *
     * Assumes that imgName is set to a valid name and that the Bitmap exists.
     */
    private String saveToInternalStorage(){

        imageView.invalidate(); // <---- TODO: not sure we need this line
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmapImage = drawable.getBitmap();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(IMG_DIR, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, imgName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /*
     * Button handler. Checks user-inputted fields for item validity.
     * Adds item if all fields have been filled out and item otherwise valid
     */
    public void saveItemClicked(View view) {

        ArrayList<String> itemInfo = getItemInfo();

        if (itemValid(itemInfo)) {

            addItemToDB(itemInfo);
            saveToInternalStorage();
            resetFields();
        }
    }

    /*
     * Private helper method zeros out fields after a successful addition.
     */
    private void resetFields() {

        EditText editName = findViewById(R.id.name_id);
        EditText editDescription = findViewById(R.id.description_id);
        EditText editCost = findViewById(R.id.cost_id);
        EditText editWarranty = findViewById(R.id.warranty_length_id);

        editName.setText("");
        editDescription.setText("");
        editCost.setText("");
        editWarranty.setText("");

        imageView.setImageBitmap(null);
    }

    /*
     * Button handler. Takes user to main page.
     */
    public void returnToMainClicked(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
