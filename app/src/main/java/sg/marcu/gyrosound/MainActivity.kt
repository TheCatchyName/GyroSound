package sg.marcu.gyrosound

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.FileOutputStream
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
var roll = 0.0f
private var azimuth = 0.0f
private var pitch = 0.0f
private var x = 0.0f
private var y = 0.0f
private var z = 0.0f
private var base = 1.25f

class MainActivity : AppCompatActivity(), SensorEventListener, LifecycleOwner {
    private val viewModel: MainActivityViewModel by viewModels()
    private var keySounds: HashMap<Int, Int> = hashMapOf() //maps button to value soundid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        writeRawFileToExternal("pianogmaj.mp3", R.raw.pianogmaj)
        writeRawFileToExternal("trumpeta3.mp3", R.raw.trumpeta3)
        writeRawFileToExternal("violinc4.mp3", R.raw.violinc4)

        Log.d("CheckViewModel", "Main Activity ${viewModel}")

        viewModel.doUpdate()
        updateViewModel()

        /*
        supportFragmentManager
            .beginTransaction()
            .add(R.id.root_layout, KeyFragment.newInstance(sounds[SOUND_2]!!))
            .commit()
        */
    }

    fun writeRawFileToExternal(filename: String, rawFileID: Int) {
        val audioDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos")
        audioDir.mkdirs()
        val audioDirPath: String = audioDir.getAbsolutePath()
        val recordingFile = File("$audioDirPath/$filename")

        val outputStream = FileOutputStream(recordingFile)
        val buffer = ByteArray(8192)
        var length: Int
        val fis = resources.openRawResource(rawFileID)

        while (fis.read(buffer).also{length = it} > 0){
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
        outputStream.close()
        fis.close()

        //viewModel.addSoundFile(recordingFile)
    }

    fun updateViewModel(){
        val audioDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos")
        audioDir.mkdirs()
        val files = audioDir.listFiles()

        for (file in files!!){
            viewModel.addSoundFile(file)
        }
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

    override fun onStop(){
        super.onStop()
        viewModel.updateData()

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

        viewModel.changeFreq(pitch)
        findViewById<TextView>(R.id.gyroscopeDisplay)?.text = "X: $x\nY: $y\nZ: $z\nazimuth: $azimuth\npitch: $pitch\nroll: $roll\naccel: $accel"

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            //keySounds = it.data!!.getSerializableExtra("sounds") as HashMap<Int, Int>
        }
    }

    fun goToEditMode(view: View) {
        val it = Intent(this, EditSound::class.java)
        getResult.launch(it)
    }

//    fun getSoundFile(id: Int): File? {
//        return sounds[keySounds[id]]
//    }
}