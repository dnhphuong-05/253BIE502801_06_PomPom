package com.pompom.group6.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pompom.group6.databinding.ActivityAiCallBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AiCallActivity extends SwipeBackActivity {

    private ActivityAiCallBinding binding;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListening = false;
    private boolean isAiSpeaking = false;
    private boolean isMicOn = true;
    private boolean isCameraOn = true;
    
    private GenerativeModelFutures model;
    private static final String API_KEY = "AQ.Ab8RN6LN4v_DI8ZuUsFKNxFgbZJb3ijeByaxDW6vFMdh9M_Yuw"; // Placeholder - Need real key for vision
    
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int RECORD_AUDIO_PERMISSION_CODE = 102;
    private final Random random = new Random();
    private final Handler handler = new Handler();
    private ObjectAnimator avatarAnimator;
    private Executor analysisExecutor = Executors.newSingleThreadExecutor();
    private long lastAnalysisTime = 0;
    private static final long ANALYSIS_INTERVAL = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        binding = ActivityAiCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initGemini();
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

    private void initGemini() {
        // Use Gemini 1.5 Flash for current stability
        // IMPORTANT: Replace with a valid API Key starting with AIza...
        if (API_KEY.startsWith("AQ.")) {
            Log.w("Gemini", "Using placeholder API key. AI features will likely fail.");
        }
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEndCall.setOnClickListener(v -> finish());
        binding.cardDialogue.setOnClickListener(v -> startListening());
        
        binding.btnToggleMic.setOnClickListener(v -> toggleMic());
        binding.btnToggleCamera.setOnClickListener(v -> toggleCamera());
    }

    private void toggleMic() {
        isMicOn = !isMicOn;
        if (isMicOn) {
            binding.ivMicStatus.setImageResource(android.R.drawable.stat_notify_call_mute);
            binding.tvMicStatus.setText("Tắt mic");
            binding.btnToggleMic.setCardBackgroundColor(android.graphics.Color.parseColor("#40FFFFFF"));
        } else {
            binding.ivMicStatus.setImageResource(android.R.drawable.stat_notify_call_mute);
            binding.tvMicStatus.setText("Bật mic");
            binding.btnToggleMic.setCardBackgroundColor(android.graphics.Color.parseColor("#FF5252"));
        }
        Toast.makeText(this, isMicOn ? "Microphone On" : "Microphone Off", Toast.LENGTH_SHORT).show();
    }

    private void toggleCamera() {
        isCameraOn = !isCameraOn;
        if (isCameraOn) {
            binding.cameraPreview.setVisibility(View.VISIBLE);
            binding.tvCameraStatus.setText("Tắt Camera");
            binding.btnToggleCamera.setCardBackgroundColor(android.graphics.Color.parseColor("#40FFFFFF"));
            startCamera();
        } else {
            binding.cameraPreview.setVisibility(View.INVISIBLE);
            binding.tvCameraStatus.setText("Bật Camera");
            binding.btnToggleCamera.setCardBackgroundColor(android.graphics.Color.parseColor("#FF5252"));
            ProcessCameraProvider.getInstance(this).addListener(() -> {
                try {
                    ProcessCameraProvider.getInstance(this).get().unbindAll();
                } catch (Exception e) {}
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
                    String userText = matches.get(0);
                    getGeminiResponse(userText);
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
        if (!isListening && !isAiSpeaking && isMicOn) {
            speechRecognizer.startListening(speechIntent);
        } else if (!isMicOn) {
            Toast.makeText(this, "Vui lòng bật mic để nói chuyện", Toast.LENGTH_SHORT).show();
        }
    }

    private void getGeminiResponse(String userText) {
        binding.tvAiSpeech.setText("Bác sĩ đang suy nghĩ...");
        
        Content content = new Content.Builder()
                .addText("Bạn là một bác sĩ da liễu AI tên PomPom. Bạn có quyền truy cập camera để xem da bệnh nhân. Trả lời ngắn gọn (dưới 2 câu) bằng tiếng Việt cho: " + userText)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiText = result.getText();
                runOnUiThread(() -> {
                    binding.tvAiSpeech.setText(aiText);
                    tts.speak(aiText, TextToSpeech.QUEUE_FLUSH, null, "GEMINI_RESP");
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> binding.tvAiSpeech.setText("Kết nối gặp trục trặc."));
            }
        }, ContextCompat.getMainExecutor(this));
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
                
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build();
                
                imageAnalysis.setAnalyzer(analysisExecutor, image -> {
                    long currentTime = System.currentTimeMillis();
                    if (isCameraOn && currentTime - lastAnalysisTime > ANALYSIS_INTERVAL) {
                        lastAnalysisTime = currentTime;
                        
                        // Convert ImageProxy to Bitmap
                        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());
                        
                        // Rotate bitmap if necessary (CameraX front camera is usually rotated)
                        android.graphics.Matrix matrix = new android.graphics.Matrix();
                        matrix.postRotate(image.getImageInfo().getRotationDegrees());
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        runOnUiThread(() -> performRealAnalysis(rotatedBitmap));
                    }
                    image.close();
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
            } catch (Exception e) {
                Log.e("Camera", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void performRealAnalysis(Bitmap bitmap) {
        binding.tvAnalysisStatus.setText("AI đang phân tích da thật...");
        
        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText("Hãy đóng vai bác sĩ da liễu. Phân tích hình ảnh khuôn mặt này và đưa ra các chỉ số về tình trạng da. " +
                        "Chỉ trả về các con số (từ 0-100) theo định dạng chính xác như sau, không thêm lời giải thích: " +
                        "Oil:x, Acne:x, Pores:x, Wrinkles:x, Tone:x")
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                runOnUiThread(() -> {
                    parseAndUpdateMetrics(text);
                    binding.tvAnalysisStatus.setText("Đã cập nhật từ hình ảnh thật");
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Analysis", "Gemini Vision failed: " + t.getMessage(), t);
                runOnUiThread(() -> {
                    String errorMsg = "Lỗi phân tích: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                    binding.tvAnalysisStatus.setText("Lỗi: " + errorMsg);
                    Toast.makeText(AiCallActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void parseAndUpdateMetrics(String text) {
        try {
            // Expected format: Oil:85, Acne:62, Pores:72, Wrinkles:30, Tone:66
            String[] parts = text.split(",");
            for (String part : parts) {
                String[] kv = part.trim().split(":");
                if (kv.length == 2) {
                    String key = kv[0].trim().toLowerCase();
                    int value = Integer.parseInt(kv[1].trim());
                    
                    if (key.contains("oil")) updateProgressDirect(binding.pbOil, binding.tvOilVal, value);
                    else if (key.contains("acne")) updateProgressDirect(binding.pbAcne, binding.tvAcneVal, value);
                    else if (key.contains("pores")) updateProgressDirect(binding.pbPores, binding.tvPoresVal, value);
                    else if (key.contains("wrinkles")) updateProgressDirect(binding.pbWrinkles, binding.tvWrinklesVal, value);
                    else if (key.contains("tone")) updateProgressDirect(binding.pbTone, binding.tvToneVal, value);
                }
            }
        } catch (Exception e) {
            Log.e("Parse", "Error parsing AI response: " + text);
        }
    }

    private void updateProgressDirect(android.widget.ProgressBar pb, android.widget.TextView tv, int value) {
        pb.setProgress(value);
        tv.setText(value + "%");
    }

    private void startSimulatedAnalysis() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Only simulate if real analysis hasn't happened recently or as a minor fluctuation
                if (isCameraOn && System.currentTimeMillis() - lastAnalysisTime > 10000) {
                    updateProgress(binding.pbOil, binding.tvOilVal);
                    updateProgress(binding.pbAcne, binding.tvAcneVal);
                    updateProgress(binding.pbPores, binding.tvPoresVal);
                    updateProgress(binding.pbWrinkles, binding.tvWrinklesVal);
                    updateProgress(binding.pbTone, binding.tvToneVal);
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
