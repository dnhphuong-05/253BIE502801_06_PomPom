package com.pompom.group6.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.pompom.group6.R;
import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.databinding.ActivityAiCallBinding;
import com.pompom.group6.utils.MockAiResponder;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

/**
 * AI Dermatologist — cuộc gọi video demo với "bác sĩ da liễu AI". Chỉ số da và câu trả lời
 * đều được mô phỏng local ({@link MockAiResponder}) — KHÔNG gọi API Gemini (key hiện có chỉ là
 * placeholder, luôn lỗi thật khi gọi mạng) để demo luôn chạy ổn định, không phụ thuộc mạng.
 */
public class AiCallActivity extends SwipeBackActivity {

    private ActivityAiCallBinding binding;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private boolean isAiSpeaking = false;
    private boolean isMicOn = true;
    private boolean isCameraOn = true;

    private static final int CAMERA_PERMISSION_CODE = 101;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private ObjectAnimator avatarAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        binding = ActivityAiCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
        setupTTS();
        setupSpeechRecognizer();

        if (checkPermissions()) {
            startCamera();
        } else {
            requestPermissions();
        }

        handler.postDelayed(this::startAiIntro, 2000);
        startSimulatedAnalysis();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEndCall.setOnClickListener(v -> endCall());
        binding.cardDialogue.setOnClickListener(v -> startListening());

        binding.btnToggleMic.setOnClickListener(v -> toggleMic());
        binding.btnToggleCamera.setOnClickListener(v -> toggleCamera());
    }

    /** Lưu kết quả khám da của cuộc gọi vào lịch sử AI rồi mở màn Kết quả phân tích. */
    private void endCall() {
        int oil = binding.pbOil.getProgress();
        int acne = binding.pbAcne.getProgress();
        int pores = binding.pbPores.getProgress();
        int wrinkles = binding.pbWrinkles.getProgress();
        int tone = binding.pbTone.getProgress();

        String skinAnalysis = String.format(Locale.ROOT, "Oil:%d,Acne:%d,Pores:%d,Wrinkles:%d,Tone:%d",
                oil, acne, pores, wrinkles, tone);
        String recommendation = MockAiResponder.skincareRecommendation(oil, acne, pores, wrinkles, tone);
        float confidence = 0.85f + random.nextFloat() * 0.1f;

        long sessionId = new AiSessionDAO(this)
                .saveDermatologistSession(recommendation, skinAnalysis, recommendation, confidence);
        if (sessionId != -1) {
            AiResultActivity.start(this, sessionId);
        }
        finish();
    }

    private void toggleMic() {
        isMicOn = !isMicOn;
        if (isMicOn) {
            binding.ivMicStatus.setImageResource(R.drawable.ic_mic);
            binding.tvMicStatus.setText("Tắt mic");
            binding.btnToggleMic.setCardBackgroundColor(android.graphics.Color.parseColor("#40FFFFFF"));
        } else {
            binding.ivMicStatus.setImageResource(R.drawable.ic_mic_off);
            binding.tvMicStatus.setText("Bật mic");
            binding.btnToggleMic.setCardBackgroundColor(android.graphics.Color.parseColor("#FF5252"));
        }
        Toast.makeText(this, isMicOn ? "Microphone On" : "Microphone Off", Toast.LENGTH_SHORT).show();
    }

    private void toggleCamera() {
        isCameraOn = !isCameraOn;
        if (isCameraOn) {
            binding.cameraPreview.setVisibility(View.VISIBLE);
            binding.ivCameraStatus.setImageResource(R.drawable.ic_videocam);
            binding.tvCameraStatus.setText("Tắt Camera");
            binding.btnToggleCamera.setCardBackgroundColor(android.graphics.Color.parseColor("#40FFFFFF"));
            startCamera();
        } else {
            binding.cameraPreview.setVisibility(View.INVISIBLE);
            binding.ivCameraStatus.setImageResource(R.drawable.ic_videocam_off);
            binding.tvCameraStatus.setText("Bật Camera");
            binding.btnToggleCamera.setCardBackgroundColor(android.graphics.Color.parseColor("#FF5252"));
            ProcessCameraProvider.getInstance(this).addListener(() -> {
                try {
                    ProcessCameraProvider.getInstance(this).get().unbindAll();
                } catch (Exception e) {
                    // Best-effort teardown only.
                }
            }, ContextCompat.getMainExecutor(this));
        }
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(new Locale("vi", "VN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Vietnamese voice not found. Using English.", Toast.LENGTH_SHORT).show();
                    tts.setLanguage(Locale.US);
                }
                tts.setPitch(1.1f);
                tts.setSpeechRate(0.9f);

                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        runOnUiThread(() -> startAvatarAnimation());
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        runOnUiThread(() -> stopAvatarAnimation());
                    }

                    @Override
                    public void onError(String utteranceId) {
                        runOnUiThread(() -> stopAvatarAnimation());
                    }
                });
            }
        });
    }

    private void startAvatarAnimation() {
        isAiSpeaking = true;
        if (avatarAnimator == null) {
            avatarAnimator = ObjectAnimator.ofFloat(binding.ivAiAvatar, "scaleX", 1f, 1.08f);
            avatarAnimator.setDuration(120);
            avatarAnimator.setRepeatCount(ValueAnimator.INFINITE);
            avatarAnimator.setRepeatMode(ValueAnimator.REVERSE);
        }
        avatarAnimator.start();
        binding.vAvatarOverlay.animate().alpha(0.1f).setDuration(200).start();
    }

    private void stopAvatarAnimation() {
        isAiSpeaking = false;
        if (avatarAnimator != null) {
            avatarAnimator.cancel();
            binding.ivAiAvatar.animate().scaleX(1f).setDuration(200).start();
        }
        binding.vAvatarOverlay.animate().alpha(0.4f).setDuration(200).start();
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
                binding.tvAiSpeech.setText("Đang nghe bạn nói...");
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    respondTo(matches.get(0));
                }
                isListening = false;
            }

            @Override
            public void onError(int error) {
                isListening = false;
                binding.tvAiSpeech.setText("Tôi không nghe rõ, hãy nhấn để nói lại.");
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
        if (!isListening && !isAiSpeaking && isMicOn) {
            speechRecognizer.startListening(speechIntent);
        } else if (!isMicOn) {
            Toast.makeText(this, "Vui lòng bật mic để nói chuyện", Toast.LENGTH_SHORT).show();
        }
    }

    /** Trả lời câu hỏi của người dùng bằng MockAiResponder — có một nhịp "đang suy nghĩ" ngắn
     * cho giống thật, thay vì trả lời tức thì. */
    private void respondTo(String userText) {
        binding.tvAiSpeech.setText("Bác sĩ đang suy nghĩ...");
        handler.postDelayed(() -> {
            String reply = MockAiResponder.reply(userText);
            binding.tvAiSpeech.setText(reply);
            tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "MOCK_RESP");
        }, 700);
    }

    private void startAiIntro() {
        String text = "Xin chào! Tôi là bác sĩ da liễu AI của bạn. Tôi có thể giúp gì cho làn da của bạn hôm nay?";
        binding.tvAiSpeech.setText(text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "Intro");

        binding.cardDialogue.setAlpha(0f);
        binding.cardDialogue.setVisibility(View.VISIBLE);
        binding.cardDialogue.animate().alpha(1f).setDuration(500).start();
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, CAMERA_PERMISSION_CODE);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview);
            } catch (Exception e) {
                Log.e("Camera", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /** Số liệu da mô phỏng — dao động nhẹ dần đều, không cần phân tích ảnh thật. */
    private void startSimulatedAnalysis() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCameraOn) {
                    updateProgress(binding.pbOil, binding.tvOilVal);
                    updateProgress(binding.pbAcne, binding.tvAcneVal);
                    updateProgress(binding.pbPores, binding.tvPoresVal);
                    updateProgress(binding.pbWrinkles, binding.tvWrinklesVal);
                    updateProgress(binding.pbTone, binding.tvToneVal);
                    binding.tvAnalysisStatus.setText("Đang phân tích...");
                }
                handler.postDelayed(this, 2000);
            }
        }, 3000);
    }

    private void updateProgress(android.widget.ProgressBar pb, android.widget.TextView tv) {
        int current = pb.getProgress();
        int next = Math.max(10, Math.min(95, current + random.nextInt(7) - 3));
        pb.setProgress(next);
        tv.setText(next + "%");
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
