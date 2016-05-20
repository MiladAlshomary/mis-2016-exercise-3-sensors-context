package mis3.milad.mis_3;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private Intent mServiceIntent;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private MyView myView;
    private FftView fftView;
    private SeekBar seekBar;
    private SeekBar fftSeekBar;

    private int windowSize;
    private int windowcount = 0;
    private double[] x;
    private double[] y;
    private  FFT fft = null;

    private int userActivity = -1;
    private double fftRecordings[] = new double[15];
    private int readingsIdx = 0;
    public final int RESTING_MODE = 0;
    public final int WALKING_MODE = 1;
    public final int RUNNING_MODE = 2;
    public final int CYCLING_MODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.myView  = (MyView) findViewById(R.id.myviewer);
        this.seekBar = (SeekBar) findViewById(R.id.seekBar1);
        this.fftView  = (FftView) findViewById(R.id.fftviewer);
        this.fftSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        this.resetFFt();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        //starting background service
        mServiceIntent = new Intent(this, ActivityMonitor.class);
        this.startService(mServiceIntent);

        final MainActivity mActivity = this;
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSensorManager.unregisterListener(mActivity);
                mSensorManager.registerListener(mActivity, accelerometer, progress);
                Log.i("app", "changing sr: " + progress);
                myView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.fftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mActivity.updateWindowSize(progress);
                fftView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            myView.updateAccelerometerInfo(x, y, z);
            this.updateAccelerometerInfo(x, y, z);
            myView.postInvalidate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void updateAccelerometerInfo(float x, float y, float z) {
        double maqnitude = Math.sqrt(x*x + y*y + z*z);

        if(windowcount >= windowSize) {
            //compute fft and update view
            windowcount = 0;
            this.fft.fft(this.x, this.y);
            this.fftView.updateFftView(this.x, this.y);

            //new fft values
            double mag = Math.sqrt(this.x[0] * this.x[0] + this.y[0] * this.y[0]);
            Log.i("app", "fft: " + windowcount + " , " + mag);
            fftView.postInvalidate();
        }

        this.x[windowcount] = maqnitude;
        this.y[windowcount] = 0;
        windowcount++;
    }

    private void resetFFt(){
        windowSize = 32;
        windowcount = 0;
        fft = new FFT(windowSize);
        x   = new double[windowSize];
        y   = new double[windowSize];
    }

    public void updateWindowSize(int progress) {
        if(progress == 0) {
            return;
        }
        double power = Math.ceil(Math.log(progress)/Math.log(2));
        int p = (int)Math.pow(2.0, power);
        Log.i("app", "changing window size to: " + p);
        fft = new FFT(p);
        this.windowSize = p;
        this.x = new double[p];
        this.y = new double[p];
        fftView.resetPaths();

    }

}
