package sg.marcu.gyrosound

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import kotlin.math.sqrt

private lateinit var sensorManager: SensorManager
private lateinit var gyroscope: Sensor
private lateinit var accelerometer: Sensor
private lateinit var magnetometer: Sensor
private var rotationMatrix = FloatArray(9)
private var iMatrix = FloatArray(9)
private var orientation = FloatArray(3)
private var mGeomagnetic = FloatArray(9)
private var mGravity = FloatArray(9)
private var roll = 0.0f
private var azimuth = 0.0f
private var pitch = 0.0f
private var x = 0.0f
private var y = 0.0f
private var z = 0.0f

class MainActivity : AppCompatActivity(), SensorEventListener, LifecycleOwner {
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, gyroscope,1000000)
        sensorManager.registerListener(this, accelerometer,1000000)
        sensorManager.registerListener(this, magnetometer,1000000)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        x = event!!.values[0]
        y = event.values[1]
        z = event.values[2]

        val accel = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values
        }

        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values
        }

        val success = SensorManager.getRotationMatrix(rotationMatrix, iMatrix, mGravity, mGeomagnetic)
        if (success) {
            SensorManager.getOrientation(rotationMatrix, orientation)
            azimuth = orientation[0]
            pitch = orientation[1]
            roll = orientation[2]
        }

        viewModel.changeFreq(roll)
        findViewById<TextView>(R.id.gyroscopeDisplay)?.text = "X: $x\nY: $y\nZ: $z\nazimuth: $azimuth\npitch: $pitch\nroll: $roll\n accel: $accel"

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }


//    fun playSound(params: IntArray) {
//        // action (1 is press, 0 is release), button number (1 to 8), sound to play (1 to x)
//        val action = params[0]
//        val buttonNum = params[1]
//        val soundId = params[2]
//
//        if (action == 1) {
//            val streamId = soundPool.play(soundId, 1F, 1F, 0, -1, 1f)
//            buttonStreams[buttonNum] = streamId
//        } else if (action == 0) {
//            soundPool.stop(buttonStreams[buttonNum])
//        }
//    }
//    fun changeSound() {
//        for (i in 0..7) {
//            if (buttonStreams[i] > 0) {
//                soundPool.setRate(buttonStreams[i], base.pow(x))
//            }
//
//        }
//    }
}