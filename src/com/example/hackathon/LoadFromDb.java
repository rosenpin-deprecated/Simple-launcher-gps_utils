package com.example.hackathon;

import android.database.Cursor;

public class LoadFromDb {
    public static float GetColumnValue(Cursor cur, String ColumnName) {
        try {
            return cur.getFloat(cur.getColumnIndex(ColumnName));
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String GetColumnValueString(Cursor cur, String ColumnName) {
        try {
            return cur.getString(cur.getColumnIndex(ColumnName));
        } catch (Exception ex) {
            return "";
        }
    }
}
