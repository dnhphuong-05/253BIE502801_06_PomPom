package com.pompom.group6.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.pompom.group6.R;
import com.pompom.group6.network.dto.ApiSkinAnalysis;
import com.pompom.group6.utils.SkinData.Option;

import java.util.List;

/** Các dialog on-brand cho mục "Hồ sơ làn da" (picker + thông tin). */
public final class SkinProfileUi {
    private SkinProfileUi() {}

    public interface OnPick {
        void pick(String code);
    }

    // ---- picker Loại da / Tông da ----

    public static void showTypePicker(Context ctx, String current, OnPick onPick) {
        showPicker(ctx, "Loại da của bạn", SkinData.skinTypes(), current, onPick);
    }

    public static void showTonePicker(Context ctx, String current, OnPick onPick) {
        showPicker(ctx, "Tông da của bạn", SkinData.skinTones(), current, onPick);
    }

    private static void showPicker(Context ctx, String title, List<Option> options,
                                   String current, OnPick onPick) {
        Dialog d = baseDialog(ctx, title, null);
        LinearLayout container = d.findViewById(R.id.sheetContainer);
        for (Option o : options) {
            boolean selected = o.code.equalsIgnoreCase(current);
            container.addView(optionRow(ctx, o.label, selected, () -> {
                onPick.pick(o.code);
                d.dismiss();
            }));
        }
        d.show();
    }

    // ---- Thành phần cần tránh theo loại da ----

    public static void showAvoidInfo(Context ctx, String skinTypeCode) {
        String typeLabel = SkinData.skinTypeLabel(skinTypeCode);
        String sub = typeLabel != null
                ? "Gợi ý cho " + typeLabel + " — nên cân nhắc tránh:"
                : "Gợi ý thành phần nên cân nhắc tránh:";
        Dialog d = baseDialog(ctx, "Thành phần cần tránh", sub);
        LinearLayout container = d.findViewById(R.id.sheetContainer);
        for (String item : SkinData.avoidIngredients(skinTypeCode)) {
            container.addView(bulletRow(ctx, item));
        }
        d.show();
    }

    // ---- Phân tích vấn đề da từ tương tác Community ----

    public static void showConcernAnalysis(Context ctx, ApiSkinAnalysis a) {
        int posts = a != null ? a.interactedPosts : 0;
        boolean hasConcern = a != null && a.concerns != null && !a.concerns.isEmpty();
        String sub = "Dựa trên " + posts + " bài bạn đã tương tác trong Community";
        Dialog d = baseDialog(ctx, "Vấn đề da quan tâm", sub);
        LinearLayout container = d.findViewById(R.id.sheetContainer);

        if (hasConcern) {
            for (ApiSkinAnalysis.Concern c : a.concerns) {
                container.addView(concernRow(ctx, c.label, c.count));
            }
        } else {
            String msg = posts == 0
                    ? "Bạn chưa tương tác với bài viết nào trong Community. Hãy thích/lưu/bình luận các bài về vấn đề da bạn quan tâm để nhận đánh giá."
                    : "Chưa phát hiện vấn đề da rõ rệt từ các bài bạn tương tác. Hãy tương tác thêm các bài về chủ đề da để phân tích chính xác hơn.";
            container.addView(messageRow(ctx, msg));
        }
        d.show();
    }

    // ------------------------------------------------------------- base dialog

    private static Dialog baseDialog(Context ctx, String title, String subtitle) {
        Dialog d = new Dialog(ctx);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = LayoutInflater.from(ctx).inflate(R.layout.dialog_skin_sheet, null);
        d.setContentView(v);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ((TextView) v.findViewById(R.id.tvSheetTitle)).setText(title);
        TextView tvSub = v.findViewById(R.id.tvSheetSubtitle);
        if (subtitle != null) {
            tvSub.setVisibility(View.VISIBLE);
            tvSub.setText(subtitle);
        }
        v.findViewById(R.id.btnSheetClose).setOnClickListener(x -> d.dismiss());
        return d;
    }

    // ------------------------------------------------------------- row builders

    private static View optionRow(Context ctx, String label, boolean selected, Runnable onClick) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pv = dp(ctx, 14), ph = dp(ctx, 6);
        row.setPadding(ph, pv, ph, pv);
        row.setBackgroundResource(selectableItemBg(ctx));
        row.setClickable(true);
        row.setFocusable(true);

        TextView tv = new TextView(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tv.setLayoutParams(lp);
        tv.setText(label);
        tv.setTextSize(15);
        tv.setTextColor(ContextCompat.getColor(ctx, selected ? R.color.brand_pink : R.color.text_primary));
        tv.setTypeface(ResourcesCompat.getFont(ctx,
                selected ? R.font.poppins_medium : R.font.poppins_regular));
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

    private static View bulletRow(Context ctx, String text) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        int pv = dp(ctx, 7);
        row.setPadding(0, pv, 0, pv);

        TextView dot = new TextView(ctx);
        dot.setText("•");
        dot.setTextSize(15);
        dot.setTextColor(ContextCompat.getColor(ctx, R.color.brand_pink));
        dot.setPadding(0, 0, dp(ctx, 8), 0);
        row.addView(dot);

        TextView body = new TextView(ctx);
        body.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        body.setText(text);
        body.setTextSize(13.5f);
        body.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        body.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
        row.addView(body);
        return row;
    }

    private static View concernRow(Context ctx, String label, int count) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pv = dp(ctx, 9);
        row.setPadding(0, pv, 0, pv);

        TextView dot = new TextView(ctx);
        dot.setText("•");
        dot.setTextSize(16);
        dot.setTextColor(ContextCompat.getColor(ctx, R.color.brand_pink));
        dot.setPadding(0, 0, dp(ctx, 8), 0);
        row.addView(dot);

        TextView tv = new TextView(ctx);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tv.setText(label);
        tv.setTextSize(14.5f);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_primary));
        tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_medium));
        row.addView(tv);

        TextView badge = new TextView(ctx);
        badge.setText(count + " bài");
        badge.setTextSize(12.5f);
        badge.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        badge.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
        row.addView(badge);
        return row;
    }

    private static View messageRow(Context ctx, String text) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextSize(13.5f);
        tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.poppins_regular));
        tv.setPadding(0, dp(ctx, 6), 0, dp(ctx, 6));
        return tv;
    }

    private static int selectableItemBg(Context ctx) {
        TypedValue tv = new TypedValue();
        ctx.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
        return tv.resourceId;
    }

    private static int dp(Context ctx, float dp) {
        return (int) (dp * ctx.getResources().getDisplayMetrics().density);
    }
}
