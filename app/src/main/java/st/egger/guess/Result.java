package st.egger.guess;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Result extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int result = Integer.parseInt(preferences.getString("answer_value","0"));

        String[] guessProjection = {
                GuessContract.GuessEntry._ID,
                GuessContract.GuessEntry.COLUMN_NAME_NAME,
                GuessContract.GuessEntry.COLUMN_NAME_GRADE,
                GuessContract.GuessEntry.COLUMN_NAME_GUESS,
                "ABS("+result+"-"+GuessContract.GuessEntry.COLUMN_NAME_GUESS+") 'diff'"

        };

        setContentView(R.layout.activity_result);

        GuessDbHelper mDbHelper = new GuessDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder = "diff";

        Cursor cursor = db.query(
                GuessContract.GuessEntry.TABLE_NAME,  // The table to query
                guessProjection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        startManagingCursor(cursor);

        // the desired columns to be bound
        String[] columns = new String[] { GuessContract.GuessEntry.COLUMN_NAME_NAME,
                GuessContract.GuessEntry.COLUMN_NAME_GRADE,
                "diff",
                GuessContract.GuessEntry.COLUMN_NAME_GUESS };
        // the XML defined views which the data will be bound to
        int[] to = new int[] { R.id.listItemName, R.id.listItemGrade,R.id.listItemDiff, R.id.listItemGuess };

        // create the adapter using the cursor pointing to the desired data as well as the layout information
        RowNumberCursorAdapter mAdapter = new RowNumberCursorAdapter(this, R.layout.list_item_result, cursor, columns, to);

        // set this adapter as your ListActivity's adapter
        ListView list = (ListView) findViewById(R.id.listViewResult);
        list.setAdapter(mAdapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent intent = new Intent(this, GuessDetail.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
