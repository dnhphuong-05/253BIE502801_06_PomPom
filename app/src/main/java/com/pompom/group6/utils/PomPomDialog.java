package com.pompom.group6.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.pompom.group6.R;

/**
 * Popup DÙNG CHUNG theo concept PomPom (nền bo góc, icon tròn, poppins, nút hồng).
 * Thay cho {@code AlertDialog} mặc định của Material để mọi popup đồng nhất một phong cách.
 *
 * <ul>
 *   <li>{@link #confirm} — hộp xác nhận 2 nút (Huỷ / Đồng ý).</li>
 *   <li>{@link #info} — hộp thông tin 1 nút.</li>
 *   <li>{@link #pickList} — chọn 1 mục từ danh sách (kèm dấu tick mục đang chọn).</li>
 * </ul>
 */
public final class PomPomDialog {
    private PomPomDialog() {}

    /** Hành động chạy khi người dùng bấm nút đồng ý / khi hộp thông tin đóng lại. */
    public interface OnAction {
        void run();
    }

    /** Chọn một mục theo chỉ số trong danh sách. */
    public interface OnPick {
        void pick(int index);
    }

    // ------------------------------------------------------------------ confirm

    /**
     * Hộp xác nhận 2 nút. {@code onConfirm} chỉ chạy khi bấm nút đồng ý (không chạy khi huỷ/thoát).
     *
     * @param emoji       icon emoji hiển thị trong vòng tròn (vd "📦", "🗑️"); null -> "💬".
     * @param confirmText nhãn nút đồng ý (hồng).
     * @param cancelText  nhãn nút huỷ (viền hồng).
     */
    public static AlertDialog confirm(Context ctx, String emoji, String title, String message,
                                      String confirmText, String cancelText, OnAction onConfirm) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_confirm_pompom, null);
        setIcon(v, emoji, "💬");
        setText(v, R.id.tvPompomTitle, title);
        setMessage(v, message);

        MaterialButton btnCancel = v.findViewById(R.id.btnPompomCancel);
        MaterialButton btnConfirm = v.findViewById(R.id.btnPompomConfirm);
        btnCancel.setText(cancelText != null ? cancelText : "Huỷ");
        btnConfirm.setText(confirmText != null ? confirmText : "Đồng ý");

        AlertDialog dialog = build(ctx, v);
        btnCancel.setOnClickListener(x -> dialog.dismiss());
        btnConfirm.setOnClickListener(x -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });
        dialog.show();
        return dialog;
    }

    // --------------------------------------------------------------------- info

    /**
     * Hộp thông tin 1 nút. {@code onClosed} chạy đúng một lần khi hộp đóng lại
     * (bấm nút, bấm ra ngoài, hoặc nút back) — tiện cho việc {@code finish()} sau khi đóng.
     */
    public static AlertDialog info(Context ctx, String emoji, String title, String message,
                                   String buttonText, OnAction onClosed) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_confirm_pompom, null);
        setIcon(v, emoji, "✅");
        setText(v, R.id.tvPompomTitle, title);
        setMessage(v, message);

        // Chỉ còn 1 nút -> ẩn nút huỷ, nút còn lại tự giãn hết bề ngang.
        v.findViewById(R.id.btnPompomCancel).setVisibility(View.GONE);
        MaterialButton btnConfirm = v.findViewById(R.id.btnPompomConfirm);
        btnConfirm.setText(buttonText != null ? buttonText : "Đóng");

        AlertDialog dialog = build(ctx, v);
        btnConfirm.setOnClickListener(x -> dialog.dismiss());
        if (onClosed != null) dialog.setOnDismissListener(d -> onClosed.run());
        dialog.show();
        return dialog;
    }

    // ----------------------------------------------------------------- pickList

    /**
     * Chọn 1 mục từ danh sách, phong cách sheet PomPom (dấu tick ở mục đang chọn).
     *
     * @param selectedIndex mục đang chọn (để tick); -1 nếu không có.
     */
    public static Dialog pickList(Context ctx, String title, String[] options,
                                  int selectedIndex, OnPick onPick) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_skin_sheet, null);
        d.setContentView(v);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ((TextView) v.findViewById(R.id.tvSheetTitle)).setText(title);
        v.findViewById(R.id.btnSheetClose).setOnClickListener(x -> d.dismiss());

        LinearLayout container = v.findViewById(R.id.sheetContainer);
        for (int i = 0; i < options.length; i++) {
            final int index = i;
            container.addView(optionRow(ctx, options[i], i == selectedIndex, () -> {
                d.dismiss();
                if (onPick != null) onPick.pick(index);
            }));
        }
        d.show();
        return d;
    }

    // ------------------------------------------------------------------ helpers

    private static AlertDialog build(Context ctx, View content) {
        AlertDialog dialog = new AlertDialog.Builder(ctx).setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    private static void setIcon(View root, String emoji, String fallback) {
        ((TextView) root.findViewById(R.id.tvPompomIcon)).setText(emoji != null ? emoji : fallback);
    }

    private static void setText(View root, int id, String text) {
        ((TextView) root.findViewById(id)).setText(text != null ? text : "");
    }

    private static void setMessage(View root, String message) {
        TextView tv = root.findViewById(R.id.tvPompomMsg);
        if (message != null && !message.isEmpty()) {
            tv.setText(message);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    /** Một hàng lựa chọn trong pickList (nhãn + dấu tick nếu đang chọn). */
    private static View optionRow(Context ctx, String label, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pv = dp(ctx, 14), ph = dp(ctx, 6);
        row.setPadding(ph, pv, ph, pv);
        row.setClickable(true);
        row.setFocusable(true);

        TextView tv = new TextView(ctx);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tv.setText(label);
        tv.setTextSize(15);
        tv.setTextColor(ContextCompat.getColor(ctx, selected ? R.color.brand_pink : R.color.text_primary));
        tv.setTypeface(ResourcesCompat.getFont(ctx, selected ? R.font.poppins_medium : R.font.poppins_regular));
        row.addView(tv);

        ImageView check = new ImageView(ctx);
        int s = dp(ctx, 20);
        check.setLayoutParams(new LinearLayout.LayoutParams(s, s));
        check.setImageResource(R.drawable.ic_check);
        check.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        row.addView(check);

        row.setOnClickListener(x -> onClick.run());
        return row;
    }

    private static int dp(Context ctx, float dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density);
    }
}
