package jobson.elliott.homeassettracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewItemActivity extends AppCompatActivity {

    private Singleton singleton;
    private String currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        singleton = Singleton.getInstance();
        currentItem = singleton.getCurrentItem();
    }
}
