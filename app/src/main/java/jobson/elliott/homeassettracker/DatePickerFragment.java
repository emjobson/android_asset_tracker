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

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private String selectedDate;

    public DatePickerFragment() {
        super();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        selectedDate = intDateToString(year, month, day); // defaults to current date for TextView
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH); // for default date of datePicker

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public String getDate() { return this.selectedDate; }

    /*
     * Private helper method takes in integer representations of the date, returning it as an
     * 8-digit string in the form mmddyyyy.
     */
    private String intDateToString(int year, int month, int day) {

        String monthStr = Integer.toString(month+1); // months were 0-indexed
        monthStr = monthStr.length() == 1 ? "0" + monthStr : monthStr;
        String dayStr = Integer.toString(day);
        dayStr = dayStr.length() == 1 ? "0" + dayStr : dayStr;
        String yearStr = Integer.toString(year); // assuming length of all years is 4
        String date = monthStr + dayStr + yearStr;

        return date;
    }

    /*
     * Private helper method takes in a String date in the form mmddyyyy, and returns
     * a separated version of the form mm<sep>dd<sep>yyyy.
     */
    protected static String addDateSeparators(String date, String separator) {
        return date.substring(0,2) + separator + date.substring(2,4) + separator + date.substring(4);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        // Do something with the date chosen by the user
        String date = intDateToString(year, month, day);

    //    Singleton.getInstance().setPurchaseDate(date);
        selectedDate = date;

        String separatedDate = addDateSeparators(date, "/");

        TextView dateText = getActivity().findViewById(R.id.date_id);
        dateText.setText("Date Chosen: " + separatedDate);

    }
}
