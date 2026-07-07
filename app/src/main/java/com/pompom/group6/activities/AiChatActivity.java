package com.pompom.group6.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityAiChatBinding;
import com.pompom.group6.utils.MockAiResponder;
import com.pompom.group6.utils.StatusBarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Trợ lý ảo PomPom — gõ chữ hoặc bấm mic để nói (giọng nói được đọc lại bằng TTS). Câu trả lời
 * dùng {@link MockAiResponder} chạy local, KHÔNG gọi API Gemini (key hiện có chỉ là placeholder,
 * luôn lỗi thật khi gọi mạng) để demo luôn chạy ổn định. Mở từ tap vào mascot ở MainActivity
 * (long-press mascot thì trò chuyện giọng nói ngay tại chỗ, xem
 * {@link com.pompom.group6.utils.MascotVoiceAssistant}).
 */
public class AiChatActivity extends SwipeBackActivity {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 301;

    private ActivityAiChatBinding binding;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtils.applyPinkHeader(this);

        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupTTS();
        setupSpeechRecognizer();
        setupListeners();

        addAiBubble("Xin chào! Mình là trợ lý ảo PomPom 🐰 Bạn cần mình tư vấn gì hôm nay?");
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSend.setOnClickListener(v -> submitTypedMessage());
        binding.btnMic.setOnClickListener(v -> startVoiceInput());
        binding.etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                submitTypedMessage();
                return true;
            }
            return false;
        });
    }

    private void submitTypedMessage() {
        String text = binding.etMessage.getText().toString().trim();
        if (text.isEmpty()) return;
        binding.etMessage.setText("");
        sendMessage(text, false);
    }

    /** @param speakReply true nếu tin nhắn đến từ giọng nói — đọc câu trả lời lên bằng TTS.
     * Có một nhịp "đang trả lời" ngắn cho giống thật, thay vì trả lời tức thì. */
    private void sendMessage(String text, boolean speakReply) {
        addUserBubble(text);
        TextView thinking = addAiBubble("Đang trả lời...");
        handler.postDelayed(() -> {
            String reply = MockAiResponder.reply(text);
            thinking.setText(reply);
            scrollToBottom();
            if (speakReply || com.pompom.group6.utils.AiSettings.isAutoSpeakEnabled(this)) speak(reply);
        }, 700);
    }

    // ── Bong bóng chat ──

    private void addUserBubble(String text) {
        View row = getLayoutInflater().inflate(R.layout.item_chat_message_user, binding.layoutMessages, false);
        ((TextView) row.findViewById(R.id.tv_message)).setText(text);
        ((TextView) row.findViewById(R.id.tv_time)).setText(currentTime());
        binding.layoutMessages.addView(row);
        scrollToBottom();
    }

    private TextView addAiBubble(String text) {
        View row = getLayoutInflater().inflate(R.layout.item_chat_message_ai, binding.layoutMessages, false);
        TextView tvMessage = row.findViewById(R.id.tv_message);
        tvMessage.setText(text);
        ((TextView) row.findViewById(R.id.tv_time)).setText(currentTime());
        binding.layoutMessages.addView(row);
        scrollToBottom();
        return tvMessage;
    }

    private String currentTime() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void scrollToBottom() {
        binding.scrollChat.post(() -> binding.scrollChat.fullScroll(View.FOCUS_DOWN));
    }

    // ── Giọng nói: SpeechRecognizer (nghe) + TextToSpeech (đọc trả lời) ──

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && tts != null) {
                int result = tts.setLanguage(new Locale("vi", "VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }
            }
        });
    }

    private void speak(String text) {
        if (tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AI_REPLY");
    }

    private void setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isListening = true;
                Toast.makeText(AiChatActivity.this, "Đang nghe bạn nói...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                isListening = false;
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    sendMessage(matches.get(0), true);
                }
            }

            @Override
            public void onError(int error) {
                isListening = false;
                Toast.makeText(AiChatActivity.this, "Mình không nghe rõ, hãy bấm mic để nói lại nhé", Toast.LENGTH_SHORT).show();
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startVoiceInput() {
        if (speechRecognizer == null) {
            Toast.makeText(this, "Máy không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasRecordAudioPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
            return;
        }
        if (!isListening) {
            speechRecognizer.startListening(speechIntent);
        }
    }

    private boolean hasRecordAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE
                && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceInput();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
