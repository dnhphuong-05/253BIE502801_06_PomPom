package com.pompom.group6.utils;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pompom.group6.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Trò chuyện bằng giọng nói ngay trên mascot nổi — long-press mascot để nói, KHÔNG mở màn hình
 * chat: mascot rung nhẹ ("lắng nghe") trong lúc nghe/chờ trả lời, rồi trả lời bằng bong bóng
 * thoại nổi ngay trên đầu mascot + đọc to (TTS) tại chỗ. Câu trả lời dùng {@link MockAiResponder}
 * chạy local, KHÔNG gọi API Gemini (key hiện có chỉ là placeholder, luôn lỗi thật khi gọi mạng).
 */
public final class MascotVoiceAssistant {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 401;

    private final Activity activity;
    private final View mascot;
    private final ViewGroup root;
    private final Handler handler = new Handler();

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private ObjectAnimator pulseX, pulseY;
    private TextView speechBubble;
    private boolean isListening = false;

    private final Runnable hideBubbleRunnable = this::hideSpeechBubble;

    public MascotVoiceAssistant(Activity activity, View mascot, ViewGroup root) {
        this.activity = activity;
        this.mascot = mascot;
        this.root = root;
        setupTts();
        setupSpeechRecognizer();
    }

    private void setupTts() {
        tts = new TextToSpeech(activity, status -> {
            if (status == TextToSpeech.SUCCESS && tts != null) {
                int result = tts.setLanguage(new Locale("vi", "VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
    }

    private void setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(activity)) return;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    respondTo(matches.get(0));
                } else {
                    isListening = false;
                    stopListeningEffect();
                }
            }

            @Override
            public void onError(int error) {
                isListening = false;
                stopListeningEffect();
                Toast.makeText(activity, "Mình không nghe rõ, thử long-press lại nhé", Toast.LENGTH_SHORT).show();
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    /** Bắt đầu nghe: xin quyền mic nếu cần, bật hiệu ứng "lắng nghe" trên mascot. */
    public void startListening() {
        if (speechRecognizer == null) {
            Toast.makeText(activity, "Máy không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_CODE);
            return;
        }
        if (isListening) return;
        isListening = true;
        startListeningEffect();
        speechRecognizer.startListening(speechIntent);
    }

    /** Gọi từ Activity#onRequestPermissionsResult. @return true nếu request này thuộc về đây. */
    public boolean onPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode != RECORD_AUDIO_PERMISSION_CODE) return false;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
        return true;
    }

    /** Vẫn giữ hiệu ứng "lắng nghe" chạy tiếp trong lúc "suy nghĩ", để không có khoảng lặng
     * hình ảnh giữa lúc nghe xong và lúc có câu trả lời. */
    private void respondTo(String userText) {
        handler.postDelayed(() -> reply(MockAiResponder.reply(userText)), 700);
    }

    private void reply(String text) {
        isListening = false;
        stopListeningEffect();
        showSpeechBubble(text);
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MASCOT_REPLY");
    }

    // ── Hiệu ứng "lắng nghe" trên mascot ──

    private void startListeningEffect() {
        if (pulseX == null) {
            pulseX = ObjectAnimator.ofFloat(mascot, "scaleX", 1f, 1.18f);
            pulseX.setDuration(450);
            pulseX.setRepeatCount(ValueAnimator.INFINITE);
            pulseX.setRepeatMode(ValueAnimator.REVERSE);

            pulseY = ObjectAnimator.ofFloat(mascot, "scaleY", 1f, 1.18f);
            pulseY.setDuration(450);
            pulseY.setRepeatCount(ValueAnimator.INFINITE);
            pulseY.setRepeatMode(ValueAnimator.REVERSE);
        }
        pulseX.start();
        pulseY.start();
    }

    private void stopListeningEffect() {
        if (pulseX != null) pulseX.cancel();
        if (pulseY != null) pulseY.cancel();
        mascot.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
    }

    // ── Bong bóng thoại nổi trên đầu mascot ──

    private void showSpeechBubble(String text) {
        if (speechBubble == null) {
            speechBubble = new TextView(activity);
            speechBubble.setBackgroundResource(R.drawable.bg_chat_ai);
            speechBubble.setTextColor(ContextCompat.getColor(activity, R.color.text_primary));
            speechBubble.setTextSize(13f);
            speechBubble.setPadding(dp(14), dp(10), dp(14), dp(10));
            speechBubble.setMaxWidth(dp(220));
            speechBubble.setElevation(dp(4));
            root.addView(speechBubble, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        root.removeCallbacks(hideBubbleRunnable);
        speechBubble.setText(text);
        speechBubble.setAlpha(0f);
        speechBubble.setVisibility(View.VISIBLE);
        speechBubble.post(() -> {
            positionBubbleAboveMascot();
            speechBubble.animate().alpha(1f).setDuration(200).start();
        });
        root.postDelayed(hideBubbleRunnable, Math.max(3000, text.length() * 90L));
    }

    private void hideSpeechBubble() {
        if (speechBubble == null) return;
        speechBubble.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> speechBubble.setVisibility(View.GONE)).start();
    }

    private void positionBubbleAboveMascot() {
        float mascotCenterX = mascot.getX() + mascot.getWidth() / 2f;
        float bubbleX = mascotCenterX - speechBubble.getWidth() / 2f;
        float margin = dp(12);
        float maxX = root.getWidth() - margin - speechBubble.getWidth();
        bubbleX = Math.max(margin, Math.min(bubbleX, maxX));
        float bubbleY = mascot.getY() - speechBubble.getHeight() - dp(12);
        speechBubble.setX(bubbleX);
        speechBubble.setY(Math.max(dp(12), bubbleY));
    }

    private int dp(int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density);
    }

    public void destroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        stopListeningEffect();
        root.removeCallbacks(hideBubbleRunnable);
        handler.removeCallbacksAndMessages(null);
    }
}
