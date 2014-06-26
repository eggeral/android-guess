package st.egger.guess;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class RowNumberCursorAdapter extends SimpleCursorAdapter{
    public RowNumberCursorAdapter(Context context, int layout, Cursor c,
                              String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        int temp =cursor.getPosition()+1;
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.findViewById(R.id.listItemPosition);
        tv.setText(""+temp);
        super.bindView(view, context, cursor);
    }

}
