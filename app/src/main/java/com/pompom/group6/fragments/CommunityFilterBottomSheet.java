package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pompom.group6.R;
import com.pompom.group6.databinding.DialogCommunityFilterBinding;

/**
 * Bộ lọc feed "Bài viết" — thay cho ô tìm kiếm cũ. Lọc/sắp xếp hoàn toàn cục bộ trên danh sách
 * đã tải (cùng cách tiếp cận với bộ lọc nguồn ở tab "Thước phim"), không cần gọi lại API.
 */
public class CommunityFilterBottomSheet extends BottomSheetDialogFragment {

    private static final String SHEET_TAG = "CommunityFilterSheet";

    public interface OnFilterAppliedListener {
        void onFilterApplied(boolean sortPopular, boolean followingOnly);
    }

    private DialogCommunityFilterBinding binding;
    private OnFilterAppliedListener listener;
    private boolean initialSortPopular;
    private boolean initialFollowingOnly;
    private boolean sortPopular;
    private boolean followingOnly;

    public static void show(FragmentManager fm, boolean currentSortPopular, boolean currentFollowingOnly,
                             OnFilterAppliedListener listener) {
        CommunityFilterBottomSheet sheet = new CommunityFilterBottomSheet();
        sheet.listener = listener;
        sheet.initialSortPopular = currentSortPopular;
        sheet.initialFollowingOnly = currentFollowingOnly;
        sheet.show(fm, SHEET_TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogCommunityFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sortPopular = initialSortPopular;
        followingOnly = initialFollowingOnly;
        renderChips();

        binding.chipSortNewest.setOnClickListener(v -> { sortPopular = false; renderChips(); });
        binding.chipSortPopular.setOnClickListener(v -> { sortPopular = true; renderChips(); });
        binding.chipShowAll.setOnClickListener(v -> { followingOnly = false; renderChips(); });
        binding.chipShowFollowing.setOnClickListener(v -> { followingOnly = true; renderChips(); });

        binding.btnResetCommunityFilter.setOnClickListener(v -> {
            sortPopular = false;
            followingOnly = false;
            renderChips();
        });

        binding.btnApplyCommunityFilter.setOnClickListener(v -> {
            if (listener != null) listener.onFilterApplied(sortPopular, followingOnly);
            dismiss();
        });
    }

    private void renderChips() {
        setChipSelected(binding.chipSortNewest, !sortPopular);
        setChipSelected(binding.chipSortPopular, sortPopular);
        setChipSelected(binding.chipShowAll, !followingOnly);
        setChipSelected(binding.chipShowFollowing, followingOnly);
    }

    private void setChipSelected(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_label_pink : R.drawable.bg_white_pill_button);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_secondary));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
