package sg.marcu.gyrosound

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.pow
import kotlin.properties.Delegates

private lateinit var sensorManager: SensorManager
private lateinit var gyroscope: Sensor
private lateinit var accelerometer: Sensor
private lateinit var magnetometer: Sensor
private var NS2S = 1.0f / 1000000000.0f
private var deltaRotationVector = FloatArray(4)
private var timestamp by Delegates.notNull<Float>()
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
private var base = 1.25f

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var keySounds: HashMap<Int, Int> = hashMapOf(0 to R.raw.key01)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(this, R.raw.key02, 1)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        var count = 0
        supportFragmentManager.fragments.forEach {k ->
            keySounds.put(count, R.raw.key01)
        }
    }


    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, gyroscope,500000)
        sensorManager.registerListener(this, accelerometer,500000)
        sensorManager.registerListener(this, magnetometer,500000)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        x = event!!.values[0]
        y = event!!.values[1]
        z = event!!.values[2]

        val accel = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values
        }

        if (mGravity != null && mGeomagnetic != null) {

            val success = SensorManager.getRotationMatrix(rotationMatrix, iMatrix, mGravity, mGeomagnetic);
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientation);
                azimuth = orientation[0]
                pitch = orientation[1]
                roll = orientation[2]
//                if (streamId > 0) {
//                    soundPool.setRate(streamId, base.pow(roll))
//                }
            }
        }

        findViewById<TextView>(R.id.gyroscopeDisplay)?.text = "X: $x\nY: $y\nZ: $z\nazimuth: $azimuth\npitch: $pitch\nroll: $roll"

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            keySounds = it.data!!.getSerializableExtra("sounds") as HashMap<Int, Int>
        }
    }

    fun goToEditMode(view: View) {
        val it = Intent(this, EditSound::class.java)
        it.putExtra("sounds", keySounds)
        getResult.launch(it)
    }
}