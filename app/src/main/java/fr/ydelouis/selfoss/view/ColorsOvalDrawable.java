package fr.ydelouis.selfoss.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.List;

public class ColorsOvalDrawable extends Drawable {

    private Paint paint;
    private List<Integer> colors;

    public ColorsOvalDrawable(List<Integer> colors) {
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        this.colors = colors;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
        this.invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        if (colors != null && !colors.isEmpty()) {
            RectF rect = new RectF(getBounds());
            float startAngle = -90;
            float angle = 360 / colors.size();
            for (Integer color : colors) {
                paint.setColor(color);
                canvas.drawArc(rect, startAngle, angle, true, paint);
                startAngle += angle;
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
       paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
       paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }
}
