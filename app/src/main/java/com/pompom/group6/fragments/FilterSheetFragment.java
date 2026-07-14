package com.pompom.group6.fragments;

import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.pompom.group6.R;
import com.pompom.group6.models.FilterState;

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
    private Spinner spinnerSortAlpha;
    private Spinner spinnerSortPrice;
    private MaterialButton btnToggleNewest;
    private MaterialButton btnTogglePopular;
    private ChipGroup chipGroupRating;
    private EditText etMinPrice;
    private EditText etMaxPrice;

    // Toggle state
    private boolean isNewestActive  = false;
    private boolean isPopularActive = false;

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
        dimBackground    = view.findViewById(R.id.dimBackground);
        filterPanel      = view.findViewById(R.id.filterPanel);
        spinnerSortAlpha = view.findViewById(R.id.spinnerSortAlpha);
        spinnerSortPrice = view.findViewById(R.id.spinnerSortPrice);
        btnToggleNewest  = view.findViewById(R.id.btnToggleNewest);
        btnTogglePopular = view.findViewById(R.id.btnTogglePopular);
        chipGroupRating  = view.findViewById(R.id.chipGroupRating);
        etMinPrice       = view.findViewById(R.id.etMinPrice);
        etMaxPrice       = view.findViewById(R.id.etMaxPrice);

        // Push the pink header below the status bar (its pink fills the status-bar
        // area → synced colour) and keep the footer buttons above the nav bar.
        View filterHeader = view.findViewById(R.id.filterHeader);
        View filterFooter = view.findViewById(R.id.filterFooter);
        final int footerBasePadBottom = filterFooter.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(filterPanel, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            filterHeader.setPadding(filterHeader.getPaddingLeft(), bars.top,
                    filterHeader.getPaddingRight(), filterHeader.getPaddingBottom());
            filterFooter.setPadding(filterFooter.getPaddingLeft(), filterFooter.getPaddingTop(),
                    filterFooter.getPaddingRight(), footerBasePadBottom + bars.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(filterPanel);

        setupSlideInAnimation();
        setupSpinners();
        setupToggles();
        applyInitialState();
        setupButtons(view);

        // Rating chips: update fill/stroke colors whenever selection changes
        chipGroupRating.setOnCheckedChangeListener((group, checkedId) -> updateRatingChipColors());

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

    // ── Spinners ──────────────────────────────────────────────────────────

    private void setupSpinners() {
        // Position 0 = "Mặc định" (no sort) so each spinner is optional.
        String[] alphaOptions = {"Mặc định", "A đến Z", "Z đến A"};
        spinnerSortAlpha.setAdapter(buildSpinnerAdapter(alphaOptions));
        spinnerSortAlpha.setPopupBackgroundResource(R.drawable.bg_spinner_popup);

        String[] priceOptions = {"Mặc định", "Giá tăng dần", "Giá giảm dần"};
        spinnerSortPrice.setAdapter(buildSpinnerAdapter(priceOptions));
        spinnerSortPrice.setPopupBackgroundResource(R.drawable.bg_spinner_popup);
    }

    private ArrayAdapter<String> buildSpinnerAdapter(String[] options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.item_spinner_selected, options);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        return adapter;
    }

    // ── Toggle buttons (Mới nhất / Phổ biến) ─────────────────────────────

    private void setupToggles() {
        btnToggleNewest.setOnClickListener(v -> {
            isNewestActive = !isNewestActive;
            animateToggle(btnToggleNewest, isNewestActive);
        });
        btnTogglePopular.setOnClickListener(v -> {
            isPopularActive = !isPopularActive;
            animateToggle(btnTogglePopular, isPopularActive);
        });
    }

    /** Smoothly animates a toggle button between inactive (outline) and active (pink fill) states. */
    private void animateToggle(MaterialButton btn, boolean active) {
        int fromColor = active ? Color.WHITE
                : ContextCompat.getColor(requireContext(), R.color.brand_pink);
        int toColor   = active ? ContextCompat.getColor(requireContext(), R.color.brand_pink)
                : Color.WHITE;
        int toText    = active ? Color.WHITE
                : ContextCompat.getColor(requireContext(), R.color.brand_pink);

        ValueAnimator anim = ValueAnimator.ofArgb(fromColor, toColor);
        anim.setDuration(200);
        anim.addUpdateListener(va ->
                btn.setBackgroundTintList(ColorStateList.valueOf((int) va.getAnimatedValue())));
        anim.start();
        btn.setTextColor(toText);
    }

    /** Applies toggle active state instantly (no animation — used on initial load). */
    private void setToggleState(MaterialButton btn, boolean active) {
        int bgColor   = active ? ContextCompat.getColor(requireContext(), R.color.brand_pink)
                : Color.WHITE;
        int textColor = active ? Color.WHITE
                : ContextCompat.getColor(requireContext(), R.color.brand_pink);
        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
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

        // Spinner: alphabetical sort — 0=Mặc định, 1=A đến Z (asc), 2=Z đến A (desc)
        spinnerSortAlpha.setSelection(
                "asc".equals(initialState.sortAlpha) ? 1
                        : "desc".equals(initialState.sortAlpha) ? 2 : 0);

        // Spinner: price sort — 0=Mặc định, 1=Giá tăng dần (asc), 2=Giá giảm dần (desc)
        spinnerSortPrice.setSelection(
                "asc".equals(initialState.sortPrice) ? 1
                        : "desc".equals(initialState.sortPrice) ? 2 : 0);

        // Toggles
        isNewestActive  = initialState.sortNewest;
        isPopularActive = initialState.sortPopular;
        setToggleState(btnToggleNewest,  isNewestActive);
        setToggleState(btnTogglePopular, isPopularActive);

        // Rating chip (expanded: 1★ – 4★)
        if (initialState.minRating >= 4f) {
            Chip chip = requireView().findViewById(R.id.chipRating4);
            if (chip != null) chip.setChecked(true);
        } else if (initialState.minRating >= 3f) {
            Chip chip = requireView().findViewById(R.id.chipRating3);
            if (chip != null) chip.setChecked(true);
        } else if (initialState.minRating >= 2f) {
            Chip chip = requireView().findViewById(R.id.chipRating2);
            if (chip != null) chip.setChecked(true);
        } else if (initialState.minRating >= 1f) {
            Chip chip = requireView().findViewById(R.id.chipRating1);
            if (chip != null) chip.setChecked(true);
        } else {
            Chip chip = requireView().findViewById(R.id.chipRatingAll);
            if (chip != null) chip.setChecked(true);
        }
        updateRatingChipColors();
    }

    // ── Buttons ───────────────────────────────────────────────────────────

    private void setupButtons(View root) {
        root.findViewById(R.id.btnCloseFilter).setOnClickListener(v -> dismissWithAnimation());

        root.findViewById(R.id.btnResetFilter).setOnClickListener(v -> {
            // Reset spinners
            spinnerSortAlpha.setSelection(0);
            spinnerSortPrice.setSelection(0);
            // Reset toggles
            isNewestActive  = false;
            isPopularActive = false;
            setToggleState(btnToggleNewest,  false);
            setToggleState(btnTogglePopular, false);
            // Reset rating to "Tất cả"
            Chip chipAll = root.findViewById(R.id.chipRatingAll);
            if (chipAll != null) chipAll.setChecked(true);
            updateRatingChipColors();
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

    // ── Rating chip color management ──────────────────────────────────────

    /**
     * Updates each rating chip's background and text color:
     * selected = brand_pink fill + white text
     * unselected = white fill + pink text + pink border (border set in XML)
     */
    private void updateRatingChipColors() {
        if (chipGroupRating == null || getContext() == null) return;
        int selectedId = chipGroupRating.getCheckedChipId();
        int[] chipIds = {R.id.chipRatingAll, R.id.chipRating1, R.id.chipRating2,
                         R.id.chipRating3, R.id.chipRating4};
        for (int id : chipIds) {
            Chip chip = requireView().findViewById(id);
            if (chip == null) continue;
            boolean checked = (id == selectedId);
            chip.setChipBackgroundColor(ColorStateList.valueOf(
                    checked ? ContextCompat.getColor(requireContext(), R.color.brand_pink)
                            : Color.WHITE));
            chip.setTextColor(checked ? Color.WHITE
                    : ContextCompat.getColor(requireContext(), R.color.brand_pink));
        }
    }

    // ── Collect current filter state from UI ──────────────────────────────

    private FilterState collectFilterState() {
        FilterState state = new FilterState();

        // Danh mục được chọn ở dải chip ngang của ShopFragment (màn này không có UI riêng để
        // đổi danh mục) — phải giữ nguyên từ initialState, nếu không "Áp dụng" sẽ âm thầm xoá
        // mất lựa chọn danh mục đang có.
        state.categoryIds = new java.util.HashSet<>(initialState.categoryIds);

        // Price range
        try {
            String minStr = etMinPrice.getText().toString().replaceAll("[^0-9]", "");
            if (!minStr.isEmpty()) state.minPrice = Long.parseLong(minStr);
        } catch (NumberFormatException ignored) {}
        try {
            String maxStr = etMaxPrice.getText().toString().replaceAll("[^0-9]", "");
            if (!maxStr.isEmpty()) state.maxPrice = Long.parseLong(maxStr);
        } catch (NumberFormatException ignored) {}

        // Alphabetical sort — 0=Mặc định (""), 1=A đến Z (asc), 2=Z đến A (desc)
        int alphaPos = spinnerSortAlpha.getSelectedItemPosition();
        state.sortAlpha = alphaPos == 1 ? "asc" : alphaPos == 2 ? "desc" : "";

        // Price sort — 0=Mặc định (""), 1=Giá tăng dần (asc), 2=Giá giảm dần (desc)
        int pricePos = spinnerSortPrice.getSelectedItemPosition();
        state.sortPrice = pricePos == 1 ? "asc" : pricePos == 2 ? "desc" : "";

        // Toggles
        state.sortNewest  = isNewestActive;
        state.sortPopular = isPopularActive;

        // Rating (expanded: 1★ – 4★)
        int selectedRatingId = chipGroupRating.getCheckedChipId();
        if (selectedRatingId == R.id.chipRating4) {
            state.minRating = 4f;
        } else if (selectedRatingId == R.id.chipRating3) {
            state.minRating = 3f;
        } else if (selectedRatingId == R.id.chipRating2) {
            state.minRating = 2f;
        } else if (selectedRatingId == R.id.chipRating1) {
            state.minRating = 1f;
        } else {
            state.minRating = 0f;
        }

        return state;
    }
}
