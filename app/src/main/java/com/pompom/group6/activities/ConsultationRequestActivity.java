package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityConsultationRequestBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiExpert;
import com.pompom.group6.network.dto.ConsultationRequestBody;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Form gửi yêu cầu tư vấn tới chuyên gia/bác sĩ PomPom. */
public class ConsultationRequestActivity extends SwipeBackActivity {

    public static final String EXTRA_EXPERT_ID = "extra_expert_id";
    public static final String EXTRA_ARTICLE_ID = "extra_article_id";

    // Nhãn hiển thị -> giá trị gửi lên backend
    private static final Map<String, String> SKIN_TYPES = new LinkedHashMap<>();
    static {
        SKIN_TYPES.put("Da dầu", "oily");
        SKIN_TYPES.put("Da khô", "dry");
        SKIN_TYPES.put("Da hỗn hợp", "combination");
        SKIN_TYPES.put("Da nhạy cảm", "sensitive");
        SKIN_TYPES.put("Da thường", "normal");
    }

    private ActivityConsultationRequestBinding binding;
    private String fixedExpertId;
    private String selectedExpertId;
    private final Map<String, String> expertIdByName = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultationRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.btnBack.setOnClickListener(v -> finish());
        binding.header.tvHeaderTitle.setText("Liên hệ tư vấn");

        fixedExpertId = getIntent().getStringExtra(EXTRA_EXPERT_ID);
        selectedExpertId = fixedExpertId;

        String userName = Session.getUserName(this);
        if (userName != null) binding.etName.setText(userName);

        ArrayAdapter<String> skinAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(SKIN_TYPES.keySet()));
        binding.actSkinType.setAdapter(skinAdapter);

        loadExperts();

        binding.btnSubmitConsultation.setOnClickListener(v -> submit());
    }

    private void loadExperts() {
        ApiClient.get().getExperts().enqueue(new Callback<List<ApiExpert>>() {
            @Override
            public void onResponse(Call<List<ApiExpert>> call, Response<List<ApiExpert>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;

                if (fixedExpertId != null) {
                    for (ApiExpert e : resp.body()) {
                        if (fixedExpertId.equals(e.id)) {
                            binding.cardSelectedExpert.setVisibility(View.VISIBLE);
                            binding.tvSelectedExpertName.setText(e.name + " • " + e.title);
                            Glide.with(ConsultationRequestActivity.this)
                                    .load(e.avatarUrl)
                                    .placeholder(R.drawable.ic_avatar)
                                    .into(binding.ivSelectedExpertAvatar);
                            break;
                        }
                    }
                } else {
                    List<String> names = new ArrayList<>();
                    for (ApiExpert e : resp.body()) {
                        String label = e.name + " • " + e.title;
                        expertIdByName.put(label, e.id);
                        names.add(label);
                    }
                    binding.layoutExpertPicker.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultationRequestActivity.this,
                            android.R.layout.simple_dropdown_item_1line, names);
                    binding.actExpertPicker.setAdapter(adapter);
                    binding.actExpertPicker.setOnItemClickListener((parent, view, position, id) ->
                            selectedExpertId = expertIdByName.get(names.get(position)));
                    if (!names.isEmpty()) {
                        binding.actExpertPicker.setText(names.get(0), false);
                        selectedExpertId = expertIdByName.get(names.get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ApiExpert>> call, Throwable t) {
                Toast.makeText(ConsultationRequestActivity.this,
                        "Không tải được danh sách chuyên gia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String selectedSkinType() {
        String label = binding.actSkinType.getText().toString();
        return SKIN_TYPES.get(label);
    }

    private String selectedPreferredChannel() {
        int checkedId = binding.rgPreferredChannel.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMessenger) return "messenger";
        if (checkedId == R.id.rbPhone) return "phone";
        if (checkedId == R.id.rbEmail) return "email";
        return "zalo";
    }

    private void submit() {
        String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String topic = binding.etTopic.getText() != null ? binding.etTopic.getText().toString().trim() : "";
        String message = binding.etMessage.getText() != null ? binding.etMessage.getText().toString().trim() : "";

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên và số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedExpertId == null) {
            Toast.makeText(this, "Vui lòng chọn chuyên gia", Toast.LENGTH_SHORT).show();
            return;
        }
        if (topic.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập chủ đề cần tư vấn", Toast.LENGTH_SHORT).show();
            return;
        }

        String articleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);
        ConsultationRequestBody body = new ConsultationRequestBody(
                Session.getUserOid(this), selectedExpertId, articleId,
                name, phone, email.isEmpty() ? null : email, selectedSkinType(),
                topic, message, selectedPreferredChannel());

        binding.btnSubmitConsultation.setEnabled(false);
        ApiClient.get().submitConsultationRequest(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                binding.btnSubmitConsultation.setEnabled(true);
                if (resp.isSuccessful()) {
                    new AlertDialog.Builder(ConsultationRequestActivity.this)
                            .setTitle("Đã gửi yêu cầu")
                            .setMessage("Đội ngũ tư vấn PomPom sẽ liên hệ với bạn sớm nhất.")
                            .setPositiveButton("Đóng", (d, w) -> finish())
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(ConsultationRequestActivity.this,
                            "Gửi yêu cầu thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                binding.btnSubmitConsultation.setEnabled(true);
                Toast.makeText(ConsultationRequestActivity.this,
                        "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
