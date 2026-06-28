package com.pompom.group6.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pompom.group6.utils.Constants;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private final Context context;
    private final String dbPath;

    public DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        this.context = context;
        this.dbPath = context.getDatabasePath(Constants.DATABASE_NAME).getPath();
        initializeDatabase();
    }

    private void initializeDatabase() {
        if (checkDatabase()) {
            Log.d(TAG, "Database already exists.");
            return;
        }

        // Try to copy the binary DB first
        if (!copyDatabase()) {
            Log.w(TAG, "Binary database not found. Attempting to initialize from SQL script.");
            // Force onCreate to be called if copying fails
            getWritableDatabase().close();
        }
    }

    private boolean copyDatabase() {
        try (InputStream input = context.getAssets().open("databases/" + Constants.DATABASE_NAME);
             OutputStream output = new FileOutputStream(dbPath)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            Log.d(TAG, "Binary database copied successfully.");
            return true;

        } catch (IOException e) {
            Log.w(TAG, "Could not copy binary database: " + e.getMessage());
            return false;
        }
    }

    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            // Database doesn't exist
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called. Initializing database schema.");
        db.beginTransaction();
        try {
            executeSqlScript(db, "pompom.sql");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void executeSqlScript(SQLiteDatabase db, String scriptName) {
        try (InputStream is = context.getAssets().open(scriptName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            StringBuilder currentStatement = new StringBuilder();
            String line;
            int statementCount = 0;
            while ((line = reader.readLine()) != null) {
                // Strip single-line comments (--) 
                // DO NOT strip // as it breaks https:// URLs
                String cleanLine = line;
                int dashComment = line.indexOf("--");
                if (dashComment != -1) cleanLine = line.substring(0, dashComment);
                
                String trimmedLine = cleanLine.trim();
                if (trimmedLine.isEmpty()) {
                    continue;
                }

                // Skip transaction markers from the file as we wrap the whole script in a transaction
                if (trimmedLine.toUpperCase().startsWith("BEGIN TRANSACTION") || 
                    trimmedLine.toUpperCase().startsWith("COMMIT")) {
                    continue;
                }
                
                currentStatement.append(trimmedLine).append(" ");
                if (trimmedLine.endsWith(";")) {
                    String sql = currentStatement.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            db.execSQL(sql);
                            statementCount++;
                        } catch (Exception e) {
                            Log.e(TAG, "SQL execution failed: " + sql, e);
                        }
                    }
                    currentStatement = new StringBuilder();
                }
            }
            Log.d(TAG, "Database initialization completed. Executed " + statementCount + " statements.");
            verifyBannersTable(db);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read SQL script", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute SQL script", e);
        }
    }

    private void verifyBannersTable(SQLiteDatabase db) {
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) as count FROM banners", null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Banners table contains " + count + " records.");
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error verifying banners table: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade called. Resetting database schema.");
        // Drop all tables
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name!='android_metadata'", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                if (!tableName.startsWith("sqlite_")) {
                    db.execSQL("DROP TABLE IF EXISTS " + tableName);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        onCreate(db);
    }

    public void resetDatabaseForDebug(Context context) {
        Log.d(TAG, "Resetting database for debug purposes...");
        context.deleteDatabase(Constants.DATABASE_NAME);
        Log.d(TAG, "Database file deleted via context. Will reinitialize on next access.");
        // Re-initialize logic
        SQLiteDatabase db = getWritableDatabase();
        onCreate(db);
        db.close();
    }

    public void printDatabaseLocation() {
        Log.d(TAG, "Database path: " + dbPath);
        java.io.File dbFile = new java.io.File(dbPath);
        Log.d(TAG, "Database file exists: " + dbFile.exists());
        if (dbFile.exists()) {
            Log.d(TAG, "Database file size: " + dbFile.length() + " bytes");
        }
    }
}
