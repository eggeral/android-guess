package st.egger.guess;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.Date;

import st.egger.guess.GuessContract;
import st.egger.guess.GuessDbHelper;
import st.egger.guess.GuessDetail;
import st.egger.guess.R;

public class GuessList extends Activity {

    public static final String ID = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_list);

        GuessDbHelper mDbHelper = new GuessDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();



        // How you want the results sorted in the resulting Cursor
        String sortOrder = GuessContract.GuessEntry._ID + " DESC";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortPref = preferences.getString("sort_order","number");
        if (sortPref.equals("name")) {
            sortOrder = GuessContract.GuessEntry.COLUMN_NAME_NAME;
        }

        Cursor cursor = db.query(
                GuessContract.GuessEntry.TABLE_NAME,  // The table to query
                getProjection(),                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        startManagingCursor(cursor);

        // the desired columns to be bound
        String[] columns = new String[] { GuessContract.GuessEntry._ID,
                GuessContract.GuessEntry.COLUMN_NAME_NAME,
                GuessContract.GuessEntry.COLUMN_NAME_GRADE,
                GuessContract.GuessEntry.COLUMN_NAME_GUESS };
        // the XML defined views which the data will be bound to
        int[] to = new int[] { R.id.listItemId, R.id.listItemName, R.id.listItemGrade, R.id.listItemGuess };

        // create the adapter using the cursor pointing to the desired data as well as the layout information
        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_guesses, cursor, columns, to);

        // set this adapter as your ListActivity's adapter
        ListView list = (ListView) findViewById(R.id.listView);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent add = new Intent(GuessList.this, GuessDetail.class);

                add.putExtra(ID, id);
                startActivity(add);
            }
        });
        

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guess_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.add_item:
                Intent add = new Intent(this, GuessDetail.class);
                startActivity(add);
                return true;
            case R.id.action_result:
                Intent result = new Intent(this, Result.class);
                startActivity(result);
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this, Settings.class);
                startActivity(settings);
                return true;
            case R.id.action_backup:
                backup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backup() {
        GuessDbHelper mDbHelper = new GuessDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // How you want the results sorted in the resulting Cursor
        String sortOrder = "diff";

        Cursor cursor = db.query(
                GuessContract.GuessEntry.TABLE_NAME,  // The table to query
                getProjection(),                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );


        PrintWriter dst = null;
        try {

            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath = "guess_game_backup-"+(new Date()).getTime()+".csv";
                File backupDB = new File(sd, backupDBPath);
                dst = new PrintWriter(new FileOutputStream(backupDB));
                while (cursor.moveToNext()) {
                    for (int i=0; i<cursor.getColumnCount(); i++) {
                        dst.print(cursor.getString(i));
                        dst.print(",");
                    }
                    dst.println();
                }
                Toast.makeText(this,"Backup erfolgreich",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"SD Karte nicht schreibbar!",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"Fehler beim Schreiben des Backups!",Toast.LENGTH_LONG).show();
        } finally {
            if (dst != null) {
                dst.close();
            }
        }


    }

    private String[] getProjection() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int result = Integer.parseInt(preferences.getString("answer_value","0"));
        return new String[] {
                GuessContract.GuessEntry._ID,
                GuessContract.GuessEntry.COLUMN_NAME_NAME,
                GuessContract.GuessEntry.COLUMN_NAME_GRADE,
                GuessContract.GuessEntry.COLUMN_NAME_GUESS,
                "ABS("+result+"-"+GuessContract.GuessEntry.COLUMN_NAME_GUESS+") 'diff'"

        };
    }


}
