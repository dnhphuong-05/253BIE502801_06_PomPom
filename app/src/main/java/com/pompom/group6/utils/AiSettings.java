package com.pompom.group6.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** Tuỳ chọn cho các tính năng AI — đọc/ghi qua SharedPreferences, dùng chung cho
 * AiChatActivity, MascotVoiceAssistant, MainActivity và AiSettingsActivity. */
public final class AiSettings {
    private static final String PREFS = "ai_settings";
    private static final String KEY_AUTO_SPEAK = "auto_speak_replies";
    private static final String KEY_MASCOT_SOUND = "mascot_touch_sound";

    private AiSettings() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Đọc to mọi câu trả lời của AI, kể cả khi gõ chữ (mặc định: chỉ đọc khi hỏi bằng giọng nói). */
    public static boolean isAutoSpeakEnabled(Context context) {
        return prefs(context).getBoolean(KEY_AUTO_SPEAK, false);
    }

    public static void setAutoSpeakEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_AUTO_SPEAK, enabled).apply();
    }

    /** Phát âm thanh khi long-press mascot nổi (mặc định: bật). */
    public static boolean isMascotSoundEnabled(Context context) {
        return prefs(context).getBoolean(KEY_MASCOT_SOUND, true);
    }

    public static void setMascotSoundEnabled(Context context, boolean enabled) {
        prefs(context).edit().putBoolean(KEY_MASCOT_SOUND, enabled).apply();
    }
}
