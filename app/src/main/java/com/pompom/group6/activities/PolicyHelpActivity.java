package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityPolicyHelpBinding;
import com.pompom.group6.databinding.ItemPolicySectionBinding;
import com.pompom.group6.utils.StatusBarUtils;

/** "Chính sách & trợ giúp": nội dung tĩnh dạng accordion (bấm mở/đóng từng mục). */
public class PolicyHelpActivity extends SwipeBackActivity {

    private ActivityPolicyHelpBinding binding;

    /** Danh sách mục: {tiêu đề, nội dung} theo string resource (đã đa ngôn ngữ). */
    private static final int[][] SECTIONS = {
            {R.string.policy_about_title, R.string.policy_about_body},
            {R.string.policy_privacy_title, R.string.policy_privacy_body},
            {R.string.policy_returns_title, R.string.policy_returns_body},
            {R.string.policy_shipping_title, R.string.policy_shipping_body},
            {R.string.policy_faq_title, R.string.policy_faq_body},
            {R.string.policy_contact_title, R.string.policy_contact_body},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPolicyHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText(R.string.policy_title);
        binding.header.btnBack.setOnClickListener(v -> finish());

        buildSections();
    }

    private void buildSections() {
        for (int i = 0; i < SECTIONS.length; i++) {
            ItemPolicySectionBinding row = ItemPolicySectionBinding.inflate(
                    LayoutInflater.from(this), binding.sectionsContainer, false);
            row.tvSectionTitle.setText(SECTIONS[i][0]);
            row.tvSectionBody.setText(SECTIONS[i][1]);
            // Mở sẵn mục đầu tiên để người dùng thấy ngay có nội dung.
            boolean expanded = i == 0;
            row.tvSectionBody.setVisibility(expanded ? View.VISIBLE : View.GONE);
            row.ivChevron.setRotation(expanded ? 90f : 270f);
            row.rowHeader.setOnClickListener(v -> toggle(row));
            binding.sectionsContainer.addView(row.getRoot());
        }
    }

    private void toggle(ItemPolicySectionBinding row) {
        boolean show = row.tvSectionBody.getVisibility() != View.VISIBLE;
        row.tvSectionBody.setVisibility(show ? View.VISIBLE : View.GONE);
        row.ivChevron.setRotation(show ? 90f : 270f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
