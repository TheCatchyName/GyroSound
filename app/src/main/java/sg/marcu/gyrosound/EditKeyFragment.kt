package sg.marcu.gyrosound

import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class EditKeyFragment(private var keyId: Int, private var currSoundId: Int, private var soundFiles: HashMap<Int, File>) : Fragment() {
    private lateinit var spinnerAdapterInstance: ArrayAdapter<String>
    lateinit var pickedSound: String

    private lateinit var recordButton: Button

    private var output: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var state: Boolean = false
    private var isRecording: Boolean = false
    private var recordingFile: File? = null

    companion object {

        fun newInstance(
            keyId: Int,
            currSoundName: Int,
            soundFiles: HashMap<Int, File>,
            toString: String
        ): EditKeyFragment {
            return EditKeyFragment(keyId, currSoundName, soundFiles)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_key, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        spinnerAdapterInstance = ArrayAdapter<String>(requireActivity().applicationContext, android.R.layout.simple_spinner_item, soundFiles.map{k -> k.value.toString().split("/")[k.value.toString().split("/").size - 1]})
        val spinner = requireActivity().findViewById<Spinner>(R.id.sound_spinner)
        spinner.adapter = spinnerAdapterInstance

        spinner.setSelection(currSoundId)

        spinner.onItemSelectedListener = object: OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p0 !== null) {
                    pickedSound = p0.getItemAtPosition(p2).toString()
                    parentFragmentManager.setFragmentResult("requestKey", bundleOf(
                        "keyId" to keyId,
                        "soundId" to p2
                    ))
                }
            }
        }

        val audioDir = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos")
        audioDir.mkdirs()
        val audioDirPath: String = audioDir.getAbsolutePath()
        Log.d(TAG, "Recording file location: $audioDirPath")

        val currentTime: Date = Calendar.getInstance().getTime() // current time

        val curTimeStr: String = currentTime.toString().replace(" ", "_")

        recordingFile = File("$audioDirPath/$curTimeStr.mp3")
        Log.d(TAG, "Created file: " + recordingFile)


        mediaRecorder = MediaRecorder()

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED) {
            mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder?.setOutputFile(recordingFile)
        }
        else{
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(requireActivity(), permissions,0)
        }

        recordButton = requireActivity().findViewById<Button>(R.id.record_button)
        recordButton.setOnClickListener{view: View -> recordAudio(view)}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
                Log.i("TAG", "Permission granted")
                Log.i("TAG2", Environment.DIRECTORY_MUSIC + output)
                var recordingNum: Int = R.raw::class.java.declaredFields.size
                output = "/recording" + recordingNum.toString() + "1" + ".mp3"
                mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder?.setOutputFile(recordingFile)
            }
        } else {
            Log.d("TAG", "Permission failed")
        }
    }

    fun recordAudio(view: View){
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(requireActivity(), permissions,0)
        }
        else {
            if (!isRecording){
                startRecording()
                recordButton.text = "Stop"
                isRecording = true
            }
            else{
                stopRecording()
                recordButton.text = "Record"
                isRecording = false
            }

        }

    }

    fun startRecording(){
        Log.d("Recording Status", "Start Recording")

        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording(){
        Log.d("Recording Status", "Stop Recording")
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false
        }
        else {
            Log.e("Recording status", "not")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getVideoSaveLocation() {
        var resolver = activity?.contentResolver
        var contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Recordings")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg3")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_RECORDINGS)
    }
}