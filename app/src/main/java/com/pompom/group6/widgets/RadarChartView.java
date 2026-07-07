package com.pompom.group6.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.pompom.group6.R;

/**
 * Biểu đồ radar (lưới hình đa giác + các điểm dữ liệu) cho 5 chỉ số da — "ma trận dạng lưới các
 * điểm" cho màn Beauty Report. Vẽ hoàn toàn bằng Canvas, không cần thư viện chart ngoài.
 */
public class RadarChartView extends View {

    private static final String[] LABELS = {"Dầu", "Mụn", "Lỗ chân lông", "Nếp nhăn", "Đều màu"};
    private static final int RING_COUNT = 4;

    private int[] values = new int[LABELS.length];

    private Paint gridPaint;
    private Paint dataFillPaint;
    private Paint dataStrokePaint;
    private Paint dotPaint;
    private Paint labelPaint;

    public RadarChartView(Context context) {
        super(context);
        init();
    }

    public RadarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int pink = ContextCompat.getColor(getContext(), R.color.brand_pink);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(Color.parseColor("#E8DCDC"));
        gridPaint.setStrokeWidth(2f);

        dataFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dataFillPaint.setStyle(Paint.Style.FILL);
        dataFillPaint.setColor(Color.argb(60, Color.red(pink), Color.green(pink), Color.blue(pink)));

        dataStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dataStrokePaint.setStyle(Paint.Style.STROKE);
        dataStrokePaint.setColor(pink);
        dataStrokePaint.setStrokeWidth(5f);
        dataStrokePaint.setStrokeJoin(Paint.Join.ROUND);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(pink);

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        labelPaint.setTextSize(26f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** @param newValues Oil, Acne, Pores, Wrinkles, Tone theo đúng thứ tự — mỗi giá trị 0..100. */
    public void setValues(int[] newValues) {
        this.values = newValues;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int n = LABELS.length;
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float labelPadding = 70f;
        float radius = Math.min(getWidth(), getHeight()) / 2f - labelPadding;
        if (radius <= 0) return;

        double startAngle = -Math.PI / 2;
        double angleStep = 2 * Math.PI / n;

        // Lưới nhiều vòng (grid rings)
        for (int level = 1; level <= RING_COUNT; level++) {
            float r = radius * level / RING_COUNT;
            Path ring = new Path();
            for (int i = 0; i <= n; i++) {
                double angle = startAngle + (i % n) * angleStep;
                float x = cx + (float) (r * Math.cos(angle));
                float y = cy + (float) (r * Math.sin(angle));
                if (i == 0) ring.moveTo(x, y); else ring.lineTo(x, y);
            }
            canvas.drawPath(ring, gridPaint);
        }

        // Trục + nhãn
        for (int i = 0; i < n; i++) {
            double angle = startAngle + i * angleStep;
            float x = cx + (float) (radius * Math.cos(angle));
            float y = cy + (float) (radius * Math.sin(angle));
            canvas.drawLine(cx, cy, x, y, gridPaint);

            float labelX = cx + (float) ((radius + labelPadding * 0.75f) * Math.cos(angle));
            float labelY = cy + (float) ((radius + labelPadding * 0.75f) * Math.sin(angle)) + labelPaint.getTextSize() / 3f;
            canvas.drawText(LABELS[i], labelX, labelY, labelPaint);
        }

        // Đa giác dữ liệu + các điểm
        Path dataPath = new Path();
        float[] pointsX = new float[n];
        float[] pointsY = new float[n];
        for (int i = 0; i < n; i++) {
            double angle = startAngle + i * angleStep;
            float ratio = Math.max(0, Math.min(100, i < values.length ? values[i] : 0)) / 100f;
            float r = radius * ratio;
            float x = cx + (float) (r * Math.cos(angle));
            float y = cy + (float) (r * Math.sin(angle));
            pointsX[i] = x;
            pointsY[i] = y;
            if (i == 0) dataPath.moveTo(x, y); else dataPath.lineTo(x, y);
        }
        dataPath.close();
        canvas.drawPath(dataPath, dataFillPaint);
        canvas.drawPath(dataPath, dataStrokePaint);

        for (int i = 0; i < n; i++) {
            canvas.drawCircle(pointsX[i], pointsY[i], 9f, dotPaint);
        }
    }
}
