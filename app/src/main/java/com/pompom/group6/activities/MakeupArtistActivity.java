package com.pompom.group6.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ShadeAdapter;
import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.databinding.ActivityMakeupArtistBinding;
import com.pompom.group6.models.Shade;
import com.pompom.group6.utils.MockAiResponder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AI Makeup Artist — như một cuộc gọi video thật với chuyên gia trang điểm AI (kiểu Messenger):
 * chuyên gia AI là màn hình chính, camera của bạn thu nhỏ ở góc (kèm beauty filter theo tông đã
 * chọn). Mỗi bước hướng dẫn được đọc to (TTS); bấm vào bong bóng thoại để hỏi bằng giọng nói và
 * được trả lời ngay ({@link MockAiResponder}, chạy local — không phụ thuộc mạng/API). Có nút tắt
 * mic/camera như một cuộc gọi thật.
 */
public class MakeupArtistActivity extends SwipeBackActivity {

    private static final int PERMISSION_CODE = 201;
    /** Alpha applied over the preview so a look reads as a soft beauty filter. */
    private static final int FILTER_ALPHA = 0x33;

    private ActivityMakeupArtistBinding binding;
    private String[] guideSteps;
    private int currentStep = 0;
    private int stepsViewed = 1; // bước đầu đã hiện sẵn khi mở màn
    private final java.util.LinkedHashSet<String> shadesTried = new java.util.LinkedHashSet<>();

    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private boolean isMicOn = true;
    private boolean isCameraOn = true;
    private ObjectAnimator avatarAnimator;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = ActivityMakeupArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        guideSteps = getResources().getStringArray(R.array.makeup_guide_steps);

        setupTts();
        setupSpeechRecognizer();
        setupLooks();
        setupGuide();
        setupListeners();

        if (hasAllPermissions()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
        }

        handler.postDelayed(() ->
                speak("Xin chào! Mình là chuyên gia trang điểm AI. Cùng bắt đầu nhé, bấm vào đây bất cứ lúc nào nếu bạn có câu hỏi!"), 1200);
    }

    private void setupLooks() {
        List<Shade> looks = new ArrayList<>();
        looks.add(new Shade("Nude", R.color.shade_nude_pink, 0));
        looks.add(new Shade("Đào", R.color.shade_peach, 0));
        looks.add(new Shade("Cam đất", R.color.shade_terracotta, 0));
        looks.add(new Shade("Đỏ Ruby", R.color.shade_ruby, 0));
        looks.add(new Shade("Hồng Baby", R.color.shade_baby_pink, 0));
        looks.add(new Shade("Mận", R.color.shade_plum, 0));

        ShadeAdapter adapter = new ShadeAdapter(looks, (shade, position) -> applyFilter(shade));
        binding.rvLooks.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvLooks.setAdapter(adapter);

        // Apply the first look by default.
        applyFilter(looks.get(0));
    }

    private void applyFilter(Shade shade) {
        int base = ContextCompat.getColor(this, shade.getColorRes());
        int tinted = Color.argb(FILTER_ALPHA, Color.red(base), Color.green(base), Color.blue(base));
        binding.vFilterOverlay.setBackgroundColor(tinted);
        shadesTried.add(shade.getName());
    }

    private void setupGuide() {
        currentStep = 0;
        binding.tvStepText.setText(guideSteps[currentStep]);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEndCall.setOnClickListener(v -> endSession());
        binding.btnNextStep.setOnClickListener(v -> nextStep());
        binding.cardDialogue.setOnClickListener(v -> startListening());
        binding.btnToggleMic.setOnClickListener(v -> toggleMic());
        binding.btnToggleCamera.setOnClickListener(v -> toggleCamera());
    }

    private void nextStep() {
        currentStep = (currentStep + 1) % guideSteps.length;
        String step = guideSteps[currentStep];
        binding.tvStepText.setText(step);
        speak(step);
        stepsViewed = Math.min(stepsViewed + 1, guideSteps.length);
    }

    /** Lưu tóm tắt buổi trang điểm vào lịch sử AI rồi mở màn Kết quả phân tích. */
    private void endSession() {
        String shadesCsv = String.join(", ", shadesTried);
        String summary = String.format(Locale.getDefault(),
                "Đã thử %d tông trang điểm ảo (%s) và xem %d/%d bước hướng dẫn cùng AI Makeup Artist.",
                shadesTried.size(), shadesCsv.isEmpty() ? "chưa chọn tông nào" : shadesCsv,
                stepsViewed, guideSteps.length);

        long sessionId = new AiSessionDAO(this).saveMakeupSession(summary, shadesCsv);
        if (sessionId != -1) {
            AiResultActivity.start(this, sessionId);
        }
        finish();
    }

    // ── Mic / Camera — giống một cuộc gọi thật ──

    private void toggleMic() {
        isMicOn = !isMicOn;
        if (isMicOn) {
            binding.ivMicStatus.setImageResource(R.drawable.ic_mic);
            binding.tvMicStatus.setText("Tắt mic");
            binding.btnToggleMic.setCardBackgroundColor(Color.parseColor("#40FFFFFF"));
        } else {
            binding.ivMicStatus.setImageResource(R.drawable.ic_mic_off);
            binding.tvMicStatus.setText("Bật mic");
            binding.btnToggleMic.setCardBackgroundColor(Color.parseColor("#FF5252"));
        }
    }

    private void toggleCamera() {
        isCameraOn = !isCameraOn;
        if (isCameraOn) {
            binding.previewView.setVisibility(View.VISIBLE);
            binding.ivCameraStatus.setImageResource(R.drawable.ic_videocam);
            binding.tvCameraStatus.setText("Tắt Camera");
            binding.btnToggleCamera.setCardBackgroundColor(Color.parseColor("#40FFFFFF"));
            startCamera();
        } else {
            binding.previewView.setVisibility(View.INVISIBLE);
            binding.ivCameraStatus.setImageResource(R.drawable.ic_videocam_off);
            binding.tvCameraStatus.setText("Bật Camera");
            binding.btnToggleCamera.setCardBackgroundColor(Color.parseColor("#FF5252"));
            ProcessCameraProvider.getInstance(this).addListener(() -> {
                try {
                    ProcessCameraProvider.getInstance(this).get().unbindAll();
                } catch (Exception ignored) {
                    // Best-effort teardown only.
                }
            }, ContextCompat.getMainExecutor(this));
        }
    }

    // ── Giọng nói: chuyên gia AI đọc hướng dẫn + trả lời câu hỏi bằng giọng nói ──

    private void setupTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS && tts != null) {
                int result = tts.setLanguage(new Locale("vi", "VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale.US);
                }
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override public void onStart(String utteranceId) { runOnUiThread(() -> startAvatarAnimation()); }
                    @Override public void onDone(String utteranceId) { runOnUiThread(() -> stopAvatarAnimation()); }
                    @Override public void onError(String utteranceId) { runOnUiThread(() -> stopAvatarAnimation()); }
                });
            }
        });
    }

    private void speak(String text) {
        if (tts != null && isMicOn) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MAKEUP_GUIDE");
    }

    private void startAvatarAnimation() {
        if (avatarAnimator == null) {
            avatarAnimator = ObjectAnimator.ofFloat(binding.ivAiAvatar, "scaleX", 1f, 1.05f);
            avatarAnimator.setDuration(140);
            avatarAnimator.setRepeatCount(ValueAnimator.INFINITE);
            avatarAnimator.setRepeatMode(ValueAnimator.REVERSE);
        }
        avatarAnimator.start();
        binding.vAvatarOverlay.animate().alpha(0.1f).setDuration(200).start();
    }

    private void stopAvatarAnimation() {
        if (avatarAnimator != null) {
            avatarAnimator.cancel();
            binding.ivAiAvatar.animate().scaleX(1f).setDuration(200).start();
        }
        binding.vAvatarOverlay.animate().alpha(0.35f).setDuration(200).start();
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
                binding.tvStepText.setText("Đang nghe bạn nói...");
            }

            @Override
            public void onResults(Bundle results) {
                isListening = false;
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    respondTo(matches.get(0));
                } else {
                    binding.tvStepText.setText(guideSteps[currentStep]);
                }
            }

            @Override
            public void onError(int error) {
                isListening = false;
                binding.tvStepText.setText(guideSteps[currentStep]);
                Toast.makeText(MakeupArtistActivity.this, "Mình không nghe rõ, bấm vào đây để hỏi lại nhé", Toast.LENGTH_SHORT).show();
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startListening() {
        if (speechRecognizer == null) {
            Toast.makeText(this, "Máy không hỗ trợ nhận diện giọng nói", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMicOn) {
            Toast.makeText(this, "Vui lòng bật mic để nói chuyện", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE);
            return;
        }
        if (!isListening) {
            speechRecognizer.startListening(speechIntent);
        }
    }

    /** Trả lời câu hỏi rồi tự quay lại hiện bước hướng dẫn hiện tại sau vài giây. */
    private void respondTo(String userText) {
        binding.tvStepText.setText("Để mình xem nào...");
        handler.postDelayed(() -> {
            String reply = MockAiResponder.makeupReply(userText);
            binding.tvStepText.setText(reply);
            speak(reply);
            handler.postDelayed(() -> binding.tvStepText.setText(guideSteps[currentStep]), 4000);
        }, 600);
    }

    private boolean hasAllPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview);
            } catch (Exception ignored) {
                // Preview is best-effort in the demo; ignore camera init failures.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE) return;
        for (int i = 0; i < permissions.length; i++) {
            boolean granted = grantResults.length > i && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            if (Manifest.permission.CAMERA.equals(permissions[i]) && granted) {
                startCamera();
            }
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
