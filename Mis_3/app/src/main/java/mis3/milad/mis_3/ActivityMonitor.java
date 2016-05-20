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
import android.util.Log;
import android.widget.SeekBar;

public class ActivityMonitor extends IntentService implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer;


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

    public ActivityMonitor(){
        super("my service");
        Log.i("monitor", "starting background service..");
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
            this.updateAccelerometerInfo(x, y, z);
        }
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String dataString = workIntent.getDataString();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        resetFFt();

    }

    private void updateAccelerometerInfo(float x, float y, float z) {
        double maqnitude = Math.sqrt(x*x + y*y + z*z);

        if(windowcount >= windowSize) {
            //compute fft and update view
            windowcount = 0;
            this.fft.fft(this.x, this.y);

            //new fft values
            double mag = Math.sqrt(this.x[0] * this.x[0] + this.y[0] * this.y[0]);
            this.updateFFTRecordings(mag);
        }

        this.x[windowcount] = maqnitude;
        this.y[windowcount] = 0;
        windowcount++;
    }

    private void resetFFt() {
        windowSize = 32;
        windowcount = 0;
        fft = new FFT(windowSize);
        x = new double[windowSize];
        y = new double[windowSize];
    }

    private   void updateFFTRecordings(double mag) {
        if(readingsIdx >= 15){
            //figure out what kind of activity is going on from the fftReadings
            this.updateActivityState();
            this.fftRecordings = new double[15];
            this.readingsIdx = 0;
        }

        Log.i("monitor", "mag: " + mag);
        this.fftRecordings[readingsIdx] = mag;
        readingsIdx++;
    }

    private void updateActivityState() {
        float averageDeff = 0;
        int numberOfJumps = 0;
        for (int i=0; i< fftRecordings.length - 1; i++) {
           if(Math.abs(fftRecordings[i] - fftRecordings[i+1]) > 5){
               numberOfJumps ++;
           }
            averageDeff += Math.abs(fftRecordings[i+1] - fftRecordings[i]);
        }

        averageDeff = averageDeff/15;
        Log.i("monitor", "ftt average: "  + averageDeff);
        Log.i("monitor", "ftt jumps: "  + numberOfJumps);

        if(numberOfJumps  == 0 && this.userActivity != RESTING_MODE) {
            this.userActivity = RESTING_MODE;
            //create notification for resting mode
            createNotification("you are in resting mode");
            Log.i("app", "Entering resting mode");
            return;
        }

        if(numberOfJumps < 10) {//we will ignore this movement
            return;
        }

        if(averageDeff < 10 && this.userActivity != RESTING_MODE) {
            this.userActivity = RESTING_MODE;
            //create notification for resting mode
            createNotification("you are in resting mode");
            Log.i("app", "Entering resting mode");
            return;
        }

        if(averageDeff > 10 && averageDeff < 20 && this.userActivity != WALKING_MODE) {
            this.userActivity = WALKING_MODE;
            //create notification for resting mode
            createNotification("you are in walking mode");
            Log.i("app", "Entering walking mode");
            return;
        }

        if(averageDeff > 20 && this.userActivity != RUNNING_MODE) {
            this.userActivity = RUNNING_MODE;
            //create notification for resting mode
            createNotification("you are in running mode");
            Log.i("app", "Entering Running mode");
            return;
        }

//        if(averageDeff >= 10 && averageDeff < 20 && this.userActivity != CYCLING_MODE) {
//            this.userActivity = CYCLING_MODE;
//            //create notification for resting mode
//            createNotification("you are in cycling mode");
//            Log.i("app", "Entering cycling mode");
//            return;
//        }
    }


    private void createNotification(String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("User Activity")
                        .setContentText(text);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
