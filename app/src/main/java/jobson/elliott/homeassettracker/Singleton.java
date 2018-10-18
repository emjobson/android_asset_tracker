package jobson.elliott.homeassettracker;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ElliottJobson on 10/16/18.
 *
 * Singleton info:
 * https://cocoacasts.com/are-singletons-bad
 * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
 *
 */


public class Singleton {


    private static Singleton instance;

    private SQLiteDatabase db;
    private String item;
    private String purchaseDate; // ddmmyyyy
    private boolean assetsModified;

    private Singleton() { // private to avoid client use of contructor

        db = null; // TODO: might init db here
        item = null; // TODO: necessary?
        assetsModified = false;
    }

    // lazy init, not thread safe
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public SQLiteDatabase getDB(){
        return db;
    }
    public void setDB(SQLiteDatabase db){
        this.db = db;
    }


    public String getPurchaseDate() { return this.purchaseDate; }
    public void setPurchaseDate(String date) { this.purchaseDate = date; }

    public boolean areAssetsModified() { return this.assetsModified; }
    public void setAssetsModified(boolean bool) { this.assetsModified = bool; }

}
