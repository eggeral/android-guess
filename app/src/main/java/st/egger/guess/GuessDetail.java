package st.egger.guess;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class GuessDetail extends Activity {

    private static final String TAG = "guess";
    private long id = -1;

    private static String[] GUESS_PROJECTION = new String[] {
        GuessContract.GuessEntry._ID,
        GuessContract.GuessEntry.COLUMN_NAME_NAME,
        GuessContract.GuessEntry.COLUMN_NAME_GRADE,
        GuessContract.GuessEntry.COLUMN_NAME_GUESS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate a "Done" custom action bar view to serve as the "Up" affordance.
        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View customActionBarView = inflater.inflate(
                R.layout.actionbar_custom_view_done, null);
        customActionBarView.findViewById(R.id.actionbar_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addOrUpdateGuessToDb();
                        finish();
                    }
                });

        // Show the custom action bar view and hide the normal Home icon and title.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView);



        setupFields();
    }

    private void setupFields() {
        id = -1;
        setContentView(R.layout.activity_guess_detail);
        if (getIntent().hasExtra(GuessList.ID)) {
            GuessDbHelper mDbHelper = new GuessDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            id = getIntent().getLongExtra(GuessList.ID, -1);
            Cursor cursor = db.query(
                    GuessContract.GuessEntry.TABLE_NAME,  // The table to query
                    GUESS_PROJECTION,                               // The columns to return
                    GuessContract.GuessEntry._ID  + " = " + id,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );
            cursor.moveToFirst();

            setEditTextText(R.id.editTextName, cursor.getString(cursor.getColumnIndex(GuessContract.GuessEntry.COLUMN_NAME_NAME)));
            setEditTextText(R.id.editTextGrade, cursor.getString(cursor.getColumnIndex(GuessContract.GuessEntry.COLUMN_NAME_GRADE)));
            setEditTextText(R.id.editTextGuess, cursor.getString(cursor.getColumnIndex(GuessContract.GuessEntry.COLUMN_NAME_GUESS)));

        }
    }

    private void addOrUpdateGuessToDb() {
        GuessDbHelper mDbHelper = new GuessDbHelper(this);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys


        String name = getEditTextText(R.id.editTextName);
        String grade = getEditTextText(R.id.editTextGrade);
        String guess = getEditTextText(R.id.editTextGuess);

        ContentValues values = new ContentValues();
        values.put(GuessContract.GuessEntry.COLUMN_NAME_NAME, name);
        values.put(GuessContract.GuessEntry.COLUMN_NAME_GRADE, grade);
        values.put(GuessContract.GuessEntry.COLUMN_NAME_GUESS, guess);

        if (id != -1) {
            values.put(GuessContract.GuessEntry._ID, id);
            db.update(GuessContract.GuessEntry.TABLE_NAME,
                    values,
                    GuessContract.GuessEntry._ID + " =  " + id ,
                    null);
        }
        else {
            db.insert(
                GuessContract.GuessEntry.TABLE_NAME,
                null,
                values);
        }
    }

    private String getEditTextText(int id) {
        TextView textView = (TextView) findViewById(id);
        return textView.getText().toString();
    }

    private void setEditTextText(int id, Object value) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(value.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guess_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                addOrUpdateGuessToDb();
                return false;
            case R.id.action_cancel:
                finish();
                return true;
            case R.id.action_delete:
                if (id != -1) {
                    GuessDbHelper mDbHelper = new GuessDbHelper(this);
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    db.delete(GuessContract.GuessEntry.TABLE_NAME,
                            GuessContract.GuessEntry._ID + " = " + id,
                            null);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
