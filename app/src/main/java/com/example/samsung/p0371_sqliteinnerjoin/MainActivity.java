package com.example.samsung.p0371_sqliteinnerjoin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLog", DB_NAME = "myDb",
            TABLE_NAME_POSITION = "position",
            TABLE_NAME_PEOPLE = "people";
    final int DB_VERSION = 1;

    int[] position_id = {1, 2, 3, 4};
    String[] position_name = {"Директор", "Программист", "Бухгалтер", "Охранник"};
    int[] posinion_salary = {80000, 60000, 40000, 20000};

    String[] people_name = {"Максим", "Сергей", "Руслан", "Наталья", "Иван", "Мария", "Светлана", "Григорий"};
    int[] people_position_id = {2, 3, 2, 2, 3, 1, 2, 4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor;

        Log.d(LOG_TAG, "--- Table " + TABLE_NAME_POSITION + " ---");
        cursor = sqLiteDatabase.query(TABLE_NAME_POSITION, null, null, null, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "----  ----");

        Log.d(LOG_TAG, "--- Table " + TABLE_NAME_PEOPLE + " ---");
        cursor = sqLiteDatabase.query(TABLE_NAME_PEOPLE, null, null, null, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "----  ----");

        Log.d(LOG_TAG, "--- INNER JOIN with rawQuery ---");
        String sqlCommand = "select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                + "inner join position as PS "
                + "on PL.position_id = PS.id "
                + "where salary > ?";
        cursor = sqLiteDatabase.rawQuery(sqlCommand, new String[] {"40000"});
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "----  ----");

        Log.d(LOG_TAG, "--- INNER JOIN with query ---");
        String table = TABLE_NAME_PEOPLE + " as PL inner join "
                + TABLE_NAME_POSITION + " as PS "
                + "on PL.position_id = PS.id";
        String[] columns = {"PL.name as Name", "PS.name as Position", "salary as Salary"};
        String selection = "salary < ?";
        String[] selectionArgs = {"40000"};
        cursor = sqLiteDatabase.query(table, columns, selection, selectionArgs, null, null, null);
        logCursor(cursor);
        cursor.close();
        Log.d(LOG_TAG, "----  ----");

        dbHelper.close();
    }

    void logCursor (Cursor cursor) {
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String string;
                do {
                    string = "";
                    for (String cn : cursor.getColumnNames()) {
                        string = string.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, string);
                } while (cursor.moveToNext());
            }
        } else {
            Log.d(LOG_TAG, "!!! Cursor is null !!!");
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");

            ContentValues contentValues = new ContentValues();
            String sqlCommand = "create table " + TABLE_NAME_POSITION + " ("
                    + "id integer primary key, "
                    + "name text, " + "salary integer)";
            db.execSQL(sqlCommand);

            for (int i = 0; i < position_id.length; i++) {
                contentValues.clear();
                contentValues.put("id", position_id[i]);
                contentValues.put("name", position_name[i]);
                contentValues.put("salary", posinion_salary[i]);
                db.insert(TABLE_NAME_POSITION, null, contentValues);
            }

            sqlCommand = "create table " + TABLE_NAME_PEOPLE + " ("
                    + "id integer primary key autoincrement, "
                    + "name text, " + "position_id integer)";
            db.execSQL(sqlCommand);

            for (int i = 0; i < people_name.length; i++) {
                contentValues.clear();
                contentValues.put("name", people_name[i]);
                contentValues.put("position_id", people_position_id[i]);
                db.insert(TABLE_NAME_PEOPLE, null, contentValues);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
