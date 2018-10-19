package jobson.elliott.homeassettracker;

import android.graphics.Canvas;

import java.util.ArrayList;

/**
 * Created by ElliottJobson on 10/17/18.
 */

public class Asset {

    private String name;
    private String description;
    private String photoName;
    private String purchaseDate;
    private float cost;
    private float warrantyLength;

    protected static final int NAME = 0;
    protected static final int DESCRIPTION = 1;
    protected static final int COST = 2;
    protected static final int PURCHASE_DATE = 3;
    protected static final int PHOTO_NAME = 4;
    protected static final int WARRANTY_LENGTH = 5;
    protected static final int NUM_FIELDS = 6;

    private static int imgNum = 0;

    public Asset(String name, String description, String photoName, String purchaseDate,
                 float cost, float warrantyLength) {

        this.name = name;
        this.description = description;
        this.photoName = photoName;
        this.purchaseDate = purchaseDate;
        this.cost = cost;
        this.warrantyLength = warrantyLength;

    }

    /*
     * Simple static method used to uniquely name images in AddItemActivity.
     * The image names will link the bitmap's stored path to the Asset represented in our database.
     */
    protected static String getNextImgName() {
        imgNum++;
        return Integer.toString(imgNum) + ".jpg";
    }


    /*
     * Alternate constructor takes in ArrayList of item attributes represented as Strings.
     */
    public Asset(ArrayList<String> params) {

        this.name = params.get(NAME);
        this.description = params.get(DESCRIPTION);
        this.photoName = params.get(PHOTO_NAME);
        this.purchaseDate = params.get(PURCHASE_DATE);
        this.cost = Float.parseFloat(params.get(COST));
        this.warrantyLength = Float.parseFloat(params.get(WARRANTY_LENGTH));
    }

    /*
     * todo: public method to draw asset on custom view
     */
    public void draw(Canvas canvas) {

    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPhotoName() { return photoName; }
    public String getPurchaseDate() { return purchaseDate; }
    public float getCost() { return cost; }
    public float getWarrantyLength() { return warrantyLength; }
}
