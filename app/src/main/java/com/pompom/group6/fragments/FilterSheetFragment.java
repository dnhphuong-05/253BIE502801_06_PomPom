package com.pompom.group6.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pompom.group6.R;
import com.pompom.group6.database.CategoryDAO;
import com.pompom.group6.models.Category;
import com.pompom.group6.models.FilterState;

import java.util.List;
import java.util.Set;

/**
 * Full-screen filter sheet that slides in from the right.
 *
 * Add via:
 *   getParentFragmentManager()
 *       .beginTransaction()
 *       .add(android.R.id.content, sheet, "filter_sheet")
 *       .addToBackStack("filter_sheet")
 *       .commit();
 */
public class FilterSheetFragment extends Fragment {

    public interface OnFilterAppliedListener {
        void onFilterApplied(FilterState state);
    }

    private OnFilterAppliedListener filterListener;
    private FilterState initialState;

    // Views
    private View dimBackground;
    private View filterPanel;
    private ChipGroup chipGroupCategories;
    private ChipGroup chipGroupRating;
    private EditText etMinPrice;
    private EditText etMaxPrice;

    public static FilterSheetFragment newInstance(FilterState currentState) {
        FilterSheetFragment f = new FilterSheetFragment();
        f.initialState = currentState != null ? currentState.copy() : new FilterState();
        return f;
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.filterListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_filter_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        dimBackground      = view.findViewById(R.id.dimBackground);
        filterPanel        = view.findViewById(R.id.filterPanel);
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        chipGroupRating    = view.findViewById(R.id.chipGroupRating);
        etMinPrice         = view.findViewById(R.id.etMinPrice);
        etMaxPrice         = view.findViewById(R.id.etMaxPrice);

        setupSlideInAnimation();
        setupCategoryChips();
        applyInitialState();
        setupButtons(view);

        dimBackground.setOnClickListener(v -> dismissWithAnimation());
    }

    // ── Slide-in from right ───────────────────────────────────────────────

    private void setupSlideInAnimation() {
        dimBackground.setAlpha(0f);
        dimBackground.animate().alpha(1f).setDuration(250).start();

        filterPanel.post(() -> {
            filterPanel.setTranslationX(filterPanel.getWidth());
            filterPanel.animate()
                    .translationX(0)
                    .setDuration(280)
                    .start();
        });
    }

    private void dismissWithAnimation() {
        if (filterPanel == null) return;
        float panelWidth = filterPanel.getWidth();
        filterPanel.animate()
                .translationX(panelWidth)
                .setDuration(250)
                .withEndAction(() -> {
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                })
                .start();
        dimBackground.animate().alpha(0f).setDuration(250).start();
    }

    // ── Category chips (dynamic, multi-select) ────────────────────────────

    private void setupCategoryChips() {
        CategoryDAO dao = new CategoryDAO(requireContext());
        List<Category> categories = dao.getAllCategories();

        chipGroupCategories.removeAllViews();
        for (Category cat : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(cat.getName());
            chip.setTag(cat.getId());
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.brand_pink_light);
            chip.setChipStrokeColorResource(R.color.brand_pink);
            chip.setChipStrokeWidth(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()));
            chip.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
            chipGroupCategories.addView(chip);
        }
    }

    // ── Apply the initial state passed in ─────────────────────────────────

    private void applyInitialState() {
        if (initialState == null) return;

        // Price fields
        if (initialState.minPrice > 0) {
            etMinPrice.setText(String.valueOf(initialState.minPrice));
        }
        if (initialState.maxPrice > 0) {
            etMaxPrice.setText(String.valueOf(initialState.maxPrice));
        }

        // Category chips
        Set<Integer> selectedCats = initialState.categoryIds;
        for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
            View child = chipGroupCategories.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                Object tag = chip.getTag();
                if (tag instanceof Integer) {
                    chip.setChecked(selectedCats.contains((Integer) tag));
                }
            }
        }

        // Rating chip
        if (initialState.minRating >= 4f) {
            Chip chip4 = requireView().findViewById(R.id.chipRating4);
            if (chip4 != null) chip4.setChecked(true);
        } else if (initialState.minRating >= 3f) {
            Chip chip3 = requireView().findViewById(R.id.chipRating3);
            if (chip3 != null) chip3.setChecked(true);
        } else {
            Chip chipAll = requireView().findViewById(R.id.chipRatingAll);
            if (chipAll != null) chipAll.setChecked(true);
        }
    }

    // ── Buttons ───────────────────────────────────────────────────────────

    private void setupButtons(View root) {
        root.findViewById(R.id.btnCloseFilter).setOnClickListener(v -> dismissWithAnimation());

        root.findViewById(R.id.btnResetFilter).setOnClickListener(v -> {
            // Clear all chips
            for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
                View child = chipGroupCategories.getChildAt(i);
                if (child instanceof Chip) ((Chip) child).setChecked(false);
            }
            // Reset rating to "Tất cả"
            Chip chipAll = root.findViewById(R.id.chipRatingAll);
            if (chipAll != null) chipAll.setChecked(true);
            // Clear price fields
            etMinPrice.setText("");
            etMaxPrice.setText("");
        });

        root.findViewById(R.id.btnApplyFilter).setOnClickListener(v -> {
            FilterState state = collectFilterState();
            if (filterListener != null) {
                filterListener.onFilterApplied(state);
            }
            dismissWithAnimation();
        });
    }

    // ── Collect current filter state from UI ──────────────────────────────

    private FilterState collectFilterState() {
        FilterState state = new FilterState();

        // Selected categories
        for (int i = 0; i < chipGroupCategories.getChildCount(); i++) {
            View child = chipGroupCategories.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked() && chip.getTag() instanceof Integer) {
                    state.categoryIds.add((Integer) chip.getTag());
                }
            }
        }

        // Price range
        try {
            String minStr = etMinPrice.getText().toString().replaceAll("[^0-9]", "");
            if (!minStr.isEmpty()) state.minPrice = Long.parseLong(minStr);
        } catch (NumberFormatException ignored) {}
        try {
            String maxStr = etMaxPrice.getText().toString().replaceAll("[^0-9]", "");
            if (!maxStr.isEmpty()) state.maxPrice = Long.parseLong(maxStr);
        } catch (NumberFormatException ignored) {}

        // Rating
        int selectedRatingId = chipGroupRating.getCheckedChipId();
        if (selectedRatingId == R.id.chipRating4) {
            state.minRating = 4f;
        } else if (selectedRatingId == R.id.chipRating3) {
            state.minRating = 3f;
        } else {
            state.minRating = 0f;
        }

        return state;
    }
}
