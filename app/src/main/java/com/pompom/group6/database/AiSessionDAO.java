package com.pompom.group6.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pompom.group6.models.AiSession;

import java.util.ArrayList;
import java.util.List;

/** Lưu/đọc lịch sử các lượt dùng AI Dermatologist & AI Makeup Artist — bảng ai_sessions +
 * ai_dermatologist/ai_makeup_artist đã có sẵn trong assets/pompom.sql. */
public class AiSessionDAO {
    private static final String TAG = "AiSessionDAO";
    private final DatabaseHelper dbHelper;

    private static final String SELECT_JOINED =
            "SELECT s.id, s.ai_type, s.created_at, s.output_data, " +
            "d.skin_analysis, d.recommendation_skincare, d.confidence, " +
            "m.makeup_products_used " +
            "FROM ai_sessions s " +
            "LEFT JOIN ai_dermatologist d ON d.ai_session_id = s.id " +
            "LEFT JOIN ai_makeup_artist m ON m.ai_session_id = s.id ";

    public AiSessionDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /** Lưu kết quả một lượt gọi AI Dermatologist. @return id của session vừa lưu, -1 nếu lỗi. */
    public long saveDermatologistSession(String outputSummary, String skinAnalysisJson,
                                         String recommendation, float confidence) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues sessionValues = new ContentValues();
            sessionValues.put("ai_type", AiSession.TYPE_DERMATOLOGIST);
            sessionValues.put("output_data", outputSummary);
            long sessionId = db.insert("ai_sessions", null, sessionValues);
            if (sessionId == -1) return -1;

            ContentValues detailValues = new ContentValues();
            detailValues.put("ai_session_id", sessionId);
            detailValues.put("skin_analysis", skinAnalysisJson);
            detailValues.put("recommendation_skincare", recommendation);
            detailValues.put("confidence", confidence);
            db.insert("ai_dermatologist", null, detailValues);

            db.setTransactionSuccessful();
            return sessionId;
        } catch (Exception e) {
            Log.e(TAG, "Error saving dermatologist session: " + e.getMessage());
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    /** Lưu kết quả một lượt dùng AI Makeup Artist. @return id của session vừa lưu, -1 nếu lỗi. */
    public long saveMakeupSession(String outputSummary, String productsUsedCsv) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues sessionValues = new ContentValues();
            sessionValues.put("ai_type", AiSession.TYPE_MAKEUP_ARTIST);
            sessionValues.put("output_data", outputSummary);
            long sessionId = db.insert("ai_sessions", null, sessionValues);
            if (sessionId == -1) return -1;

            ContentValues detailValues = new ContentValues();
            detailValues.put("ai_session_id", sessionId);
            detailValues.put("makeup_products_used", productsUsedCsv);
            db.insert("ai_makeup_artist", null, detailValues);

            db.setTransactionSuccessful();
            return sessionId;
        } catch (Exception e) {
            Log.e(TAG, "Error saving makeup session: " + e.getMessage());
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    /** Xoá toàn bộ lịch sử AI đã lưu (cả Dermatologist lẫn Makeup Artist) — dùng cho "Cài đặt AI".
     * Xoá thủ công cả 3 bảng vì schema tạo với PRAGMA foreign_keys=off nên ON DELETE CASCADE
     * không tự chạy. */
    public void deleteAllHistory() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("ai_dermatologist", null, null);
            db.delete("ai_makeup_artist", null, null);
            db.delete("ai_sessions", null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing AI history: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /** Lịch sử tất cả lượt dùng AI, mới nhất trước. @param aiType null = mọi loại. */
    public List<AiSession> getHistory(String aiType, int limit) {
        List<AiSession> sessions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuilder query = new StringBuilder(SELECT_JOINED);
        List<String> args = new ArrayList<>();
        if (aiType != null) {
            query.append("WHERE s.ai_type = ? ");
            args.add(aiType);
        }
        query.append("ORDER BY s.id DESC LIMIT ?");
        args.add(String.valueOf(limit));

        try (Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]))) {
            while (cursor.moveToNext()) {
                sessions.add(fromCursor(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching AI history: " + e.getMessage());
        }
        return sessions;
    }

    /** Chi tiết một session cụ thể — dùng cho màn Kết quả phân tích khi mở lại từ Lịch sử. */
    public AiSession getSessionById(long sessionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = SELECT_JOINED + "WHERE s.id = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(sessionId)})) {
            if (cursor.moveToFirst()) {
                return fromCursor(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching AI session " + sessionId + ": " + e.getMessage());
        }
        return null;
    }

    private AiSession fromCursor(Cursor cursor) {
        AiSession session = new AiSession();
        session.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        session.setAiType(cursor.getString(cursor.getColumnIndexOrThrow("ai_type")));
        session.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
        session.setOutputData(cursor.getString(cursor.getColumnIndexOrThrow("output_data")));
        session.setSkinAnalysis(cursor.getString(cursor.getColumnIndexOrThrow("skin_analysis")));
        session.setRecommendationSkincare(cursor.getString(cursor.getColumnIndexOrThrow("recommendation_skincare")));
        session.setConfidence(cursor.getFloat(cursor.getColumnIndexOrThrow("confidence")));
        session.setMakeupProductsUsed(cursor.getString(cursor.getColumnIndexOrThrow("makeup_products_used")));
        return session;
    }
}
