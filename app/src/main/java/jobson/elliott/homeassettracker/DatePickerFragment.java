package jobson.elliott.homeassettracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by ElliottJobson on 10/16/18.
 * Code from: https://developer.android.com/guide/topics/ui/controls/pickers
 */

public class DatePickerFragment extends DialogFragment // TODO: make static once again?
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Do something with the date chosen by the user
        String monthStr = Integer.toString(month+1); // months were 0-indexed
        monthStr = monthStr.length() == 1 ? "0" + monthStr : monthStr;
        String dayStr = Integer.toString(day);
        dayStr = dayStr.length() == 1 ? "0" + dayStr : dayStr;
        String yearStr = Integer.toString(year); // assuming length of all years is 4
        String date = monthStr + dayStr + yearStr;

        Singleton.getInstance().setPurchaseDate(date);

        TextView dateText = getActivity().findViewById(R.id.date_id);
        dateText.setText("Date Chosen: " + monthStr + "/" + dayStr + "/" + yearStr);

    }
}
