package be.ti.groupe2.projetintegration;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class Boussole extends Activity {

    private Compa compa;

    private SensorManager sensorManager;

    private Sensor sensor;

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            updateOrientation(event.values[SensorManager.DATA_X]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeux_de_piste);
        //compa = (Compa)findViewById(R.id.boussole);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors =sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
        }
    }

    protected void updateOrientation(float rotation) {
        compa.setEtapeOrientation(rotation);
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop(){
        super.onStop();
        sensorManager.unregisterListener(sensorListener);
    }
}