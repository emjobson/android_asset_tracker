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
    private String receiptPhotoName;
    private float cost;
    private float warrantyLength;

    protected static final int NAME = 0;
    protected static final int DESCRIPTION = 1;
    protected static final int COST = 2;
    protected static final int PURCHASE_DATE = 3;
    protected static final int PHOTO_NAME = 4;
    protected static final int RECEIPT_PHOTO_NAME = 5;
    protected static final int WARRANTY_LENGTH = 6;
    protected static final int NUM_FIELDS = 7;

    public Asset(String name, String description, String photoName, String purchaseDate,
                 String receiptPhotoName, float cost, float warrantyLength) {

        this.name = name;
        this.description = description;
        this.photoName = photoName;
        this.purchaseDate = purchaseDate;
        this.receiptPhotoName = receiptPhotoName;
        this.cost = cost;
        this.warrantyLength = warrantyLength;

    }

    /*
     * Alternate constructor takes in ArrayList of item attributes represented as Strings.
     */
    public Asset(ArrayList<String> params) {

        this.name = params.get(NAME);
        this.description = params.get(DESCRIPTION);
        this.photoName = params.get(PHOTO_NAME);
        this.purchaseDate = params.get(PURCHASE_DATE);
        this.receiptPhotoName = params.get(RECEIPT_PHOTO_NAME);
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
    public String getReceiptPhotoName() { return receiptPhotoName; }
    public float getCost() { return cost; }
    public float getWarrantyLength() { return warrantyLength; }
}
