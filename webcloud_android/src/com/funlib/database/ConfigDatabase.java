package com.funlib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfigDatabase {

	public synchronized static int getInt(String key, Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase base = helper.getReadableDatabase();
		String dbKey;
		String dbValue;
		Cursor cursor = null;
		try {
			cursor = base
					.query("key_value", null, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					dbKey = cursor.getString(cursor
							.getColumnIndexOrThrow("key"));
					dbValue = cursor.getString(cursor
							.getColumnIndexOrThrow("value"));
					if (key.equals(dbKey)) {
						return Integer.valueOf(dbValue);
					}

				} while (cursor.moveToNext());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			if (base != null) {
				base.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return -1;
	}

	public synchronized static void saveInt(String key,
			int newNotificationHash, Context context) {

		DatabaseHelper helper = new DatabaseHelper(context);
		SQLiteDatabase base = helper.getReadableDatabase();
		String dbKey;
		String dbValue;
		Cursor cursor = null;
		boolean isInsert = true;
		try {
			cursor = base
					.query("key_value", null, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					dbKey = cursor.getString(cursor
							.getColumnIndexOrThrow("key"));
					dbValue = cursor.getString(cursor
							.getColumnIndexOrThrow("value"));
					if (key.equals(dbKey)) {
						ContentValues cv = new ContentValues();
						cv.put("key", key);
						cv.put("value", newNotificationHash);
						base.update("key_value", cv, "key = ?",
								new String[] { key });
						isInsert = false;
					}

				} while (cursor.moveToNext());
			}
			if (isInsert) {
				ContentValues cv = new ContentValues();
				cv.put("key", key);
				cv.put("value", newNotificationHash);
				base.insert("key_value", null, cv);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} finally {
			if (base != null) {
				base.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}
