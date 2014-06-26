package st.egger.guess;

import android.provider.BaseColumns;

public final class GuessContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private GuessContract() {}

    /* Inner class that defines the table contents */
    public static abstract class GuessEntry implements BaseColumns {
        public static final String TABLE_NAME = "guess";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_GRADE = "grade";
        public static final String COLUMN_NAME_GUESS = "guess";
    }
}