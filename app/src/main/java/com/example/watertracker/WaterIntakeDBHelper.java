package com.example.watertracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class WaterIntakeDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "water_intake_db";

    private static final String TABLE_NAME = "water_intake";
    private static final String KEY_DATE = "date";
    private static final String KEY_WATER_ML = "water_ml";
    private static final String KEY_GOAL_ML = "goal_ml";

    private static final String TABLE_WATER_LOGS = "water_logs";
    private static final String KEY_LOG_ID = "id";
    private static final String KEY_LOG_DATE = "log_date";
    private static final String KEY_LOG_TIME = "log_time";
    private static final String KEY_LOG_AMOUNT_ML = "amount_ml";

    public WaterIntakeDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSummaryTable = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_DATE + " TEXT PRIMARY KEY,"
                + KEY_WATER_ML + " INTEGER,"
                + KEY_GOAL_ML + " INTEGER"
                + ")";

        String createLogsTable = "CREATE TABLE " + TABLE_WATER_LOGS + "("
                + KEY_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LOG_DATE + " TEXT,"
                + KEY_LOG_TIME + " TEXT,"
                + KEY_LOG_AMOUNT_ML + " INTEGER"
                + ")";

        db.execSQL(createSummaryTable);
        db.execSQL(createLogsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addOrUpdateIntakeRecord(IntakeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{KEY_WATER_ML, KEY_GOAL_ML},
                KEY_DATE + " = ?",
                new String[]{record.getDate()},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int currentWaterMl = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WATER_ML));
            int newWaterMl = currentWaterMl + record.getWaterMl();

            ContentValues updateValues = new ContentValues();
            updateValues.put(KEY_WATER_ML, newWaterMl);
            updateValues.put(KEY_GOAL_ML, record.getGoalMl());

            db.update(TABLE_NAME, updateValues, KEY_DATE + " = ?", new String[]{record.getDate()});
        } else {
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, record.getDate());
            values.put(KEY_WATER_ML, record.getWaterMl());
            values.put(KEY_GOAL_ML, record.getGoalMl());

            db.insert(TABLE_NAME, null, values);
        }

        cursor.close();
        db.close();
    }

    public void updateIntakeRecord(IntakeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WATER_ML, record.getWaterMl());
        values.put(KEY_GOAL_ML, record.getGoalMl());

        db.update(TABLE_NAME, values, KEY_DATE + " = ?", new String[]{record.getDate()});
        db.close();
    }

    public void resetTodayIntake(String date, int newGoalMl) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_WATER_ML, 0);
        values.put(KEY_GOAL_ML, newGoalMl);

        int rowsUpdated = db.update(TABLE_NAME, values, KEY_DATE + " = ?", new String[]{date});

        if (rowsUpdated == 0) {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }

    public void deleteRecordByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_DATE + " = ?", new String[]{date});
        db.delete(TABLE_WATER_LOGS, KEY_LOG_DATE + " = ?", new String[]{date});
        db.close();
    }

    public IntakeRecord getIntakeRecordByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{KEY_DATE, KEY_WATER_ML, KEY_GOAL_ML},
                KEY_DATE + "=?",
                new String[]{date},
                null,
                null,
                null,
                null
        );

        IntakeRecord record = null;

        if (cursor.moveToFirst()) {
            record = new IntakeRecord(
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WATER_ML)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GOAL_ML))
            );
        }

        cursor.close();
        db.close();

        return record;
    }

    public List<IntakeRecord> getAllIntakeRecords() {
        List<IntakeRecord> intakeRecords = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + KEY_WATER_ML + " > 0 " +
                        " ORDER BY " + KEY_DATE + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                IntakeRecord record = new IntakeRecord();
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                record.setWaterMl(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_WATER_ML)));
                record.setGoalMl(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_GOAL_ML)));
                intakeRecords.add(record);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return intakeRecords;
    }

    public long insertWaterLog(WaterLog waterLog) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOG_DATE, waterLog.getDate());
        values.put(KEY_LOG_TIME, waterLog.getTime());
        values.put(KEY_LOG_AMOUNT_ML, waterLog.getAmountMl());

        long insertedId = db.insert(TABLE_WATER_LOGS, null, values);
        db.close();
        return insertedId;
    }

    public List<WaterLog> getWaterLogsByDate(String date) {
        List<WaterLog> waterLogs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_WATER_LOGS,
                new String[]{KEY_LOG_ID, KEY_LOG_DATE, KEY_LOG_TIME, KEY_LOG_AMOUNT_ML},
                KEY_LOG_DATE + " = ?",
                new String[]{date},
                null,
                null,
                KEY_LOG_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                WaterLog waterLog = new WaterLog();
                waterLog.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LOG_ID)));
                waterLog.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOG_DATE)));
                waterLog.setTime(cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOG_TIME)));
                waterLog.setAmountMl(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LOG_AMOUNT_ML)));
                waterLogs.add(waterLog);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return waterLogs;
    }

    public void deleteWaterLogById(int logId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WATER_LOGS, KEY_LOG_ID + " = ?", new String[]{String.valueOf(logId)});
        db.close();
    }

    public int getTotalWaterIntakeForDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + KEY_LOG_AMOUNT_ML + ") FROM " + TABLE_WATER_LOGS + " WHERE " + KEY_LOG_DATE + " = ?",
                new String[]{date}
        );

        int total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getInt(0);
        }

        cursor.close();
        return total;
    }

    public void updateDailySummary(String date, int goalMl) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + KEY_LOG_AMOUNT_ML + ") FROM " + TABLE_WATER_LOGS + " WHERE " + KEY_LOG_DATE + " = ?",
                new String[]{date}
        );

        int totalWaterMl = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            totalWaterMl = cursor.getInt(0);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, date);
        values.put(KEY_WATER_ML, totalWaterMl);
        values.put(KEY_GOAL_ML, goalMl);

        int rowsUpdated = db.update(TABLE_NAME, values, KEY_DATE + " = ?", new String[]{date});

        if (rowsUpdated == 0) {
            db.insert(TABLE_NAME, null, values);
        }

        db.close();
    }
}