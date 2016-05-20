package mis3.milad.mis_3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by milad on 5/17/16.
 */
public class FftView extends View {

    private int fftcount = 0;
    private Path fftPath = new Path();
    private boolean firstTimeDraw = true;

    private double currentMagTransformation;


    public FftView(Context context) {
        super(context);
        init();
    }

    public FftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        fftcount = 0;

        fftPath.reset();
        fftPath.moveTo(0, getHeight() - 10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(firstTimeDraw) {
            resetPaths();
            firstTimeDraw = false;
        }
        //Drawing background
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        canvas.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        canvas.drawText("FFT Magnitude :" + currentMagTransformation, 10, 50, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawPath(fftPath, paint);


    }

    public void updateFftView(double[] x, double y[]) {
        if(fftcount >= getWidth()){
            resetPaths();
        }
        this.addPointsToPath(x, y);
    }

    public void resetPaths() {
        fftcount = 0;
        fftPath.reset();
        fftPath.moveTo(0, getHeight() - 10);
    }

    private void addPointsToPath(double[]x, double[] y) {
        this.currentMagTransformation = Math.sqrt(x[0] * x[0] + y[0] * y[0]);
        for(int i=0; i< x.length; i++) {
            double mag = Math.sqrt(x[i] * x[i] + y[i] * y[i]);
            double midLine = getHeight() - 10;
            fftPath.lineTo(fftcount, (float)(midLine - mag));
            fftcount+=1;
        }
    }
}
