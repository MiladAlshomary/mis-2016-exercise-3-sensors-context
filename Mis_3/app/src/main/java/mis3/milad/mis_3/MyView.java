package mis3.milad.mis_3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by milad on 5/17/16.
 */
public class MyView extends View {

    private float x = 0;
    private float y = 0;
    private float z = 0;
    private double maqnitude;

    private Path xPath = new Path();
    private Path yPath = new Path();
    private Path zPath = new Path();
    private Path magPath = new Path();

    private boolean firstTimeDraw = true;
    private int count = 0;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(count == 0) {
            resetPaths();
        }

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
        canvas.drawText("x :" + this.x, 10, 50, paint);
        canvas.drawText("y :" + this.y, 10, 100, paint);
        canvas.drawText("z :" + this.z, 10, 150, paint);


        paint.setStyle(Paint.Style.STROKE);
        //Drawing x
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawPath(xPath, paint);

        //Drawing y
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        canvas.drawPath(yPath, paint);

        //Drawing z
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        canvas.drawPath(zPath, paint);

        //Drawing mag
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawPath(magPath, paint);

    }

    public void updateAccelerometerInfo(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.maqnitude = Math.sqrt(x*x + y*y + z*z);

        if(count >= getWidth()){
            resetPaths();
        }

        int midLine = getHeight()/2;

        xPath.lineTo(count, midLine - this.x);
        yPath.lineTo(count, midLine - this.y);
        zPath.lineTo(count, midLine - this.z);
        magPath.lineTo(count, (float)(midLine - this.maqnitude));


        count+= 5;
    }

    private void resetPaths() {
        xPath.reset();
        xPath.moveTo(0, getHeight() / 2);
        yPath.reset();
        yPath.moveTo(0, getHeight() / 2);
        zPath.reset();
        zPath.moveTo(0, getHeight() / 2);
        magPath.reset();
        magPath.moveTo(0, getHeight() / 2);
        count = 0;

    }
}
