package com.example.android.singlescreenapplication;

//icu.calendar caused problems - android studio automatically imported it

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    /* Declaring views as globals to limit the use of findViewById method */
    SwipeRefreshLayout dateRefresh;
    TextView openHoursText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateRefresh = (SwipeRefreshLayout) findViewById(R.id.activity_main);
        openHoursText = (TextView) findViewById(R.id.open_hours_text);

        /* Show open hours open current date */
        checkDate();
        /* Setup the refresh listener for the SwipeRefreshLayout */
        setupScrollRefresh();

    }

    /**
     * Method to check if the current day is a working day and adjust open hours accordingly.
     */
    protected void checkDate() {
        java.util.Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_WEEK);
        TextView openHoursText = (TextView) findViewById(R.id.open_hours_text);
        if (openHoursText == null)
            return;
        if (day == 0 || day == 6)
            openHoursText.setText(getString(R.string.weekend_hours));
        else
            openHoursText.setText(getString(R.string.workday_hours));
    }

    /**
     * Setup the onRefresh listener in order to renew open hours when scrolling down.
     */
    protected void setupScrollRefresh(){

        dateRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                /* Update open hours */
                checkDate();
                /* If refresh wheel is still visible then make it disappear */
                if (dateRefresh.isRefreshing())
                    dateRefresh.setRefreshing(false);
            }
        });
    }
}
