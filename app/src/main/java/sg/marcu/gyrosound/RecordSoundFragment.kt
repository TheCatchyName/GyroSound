package sg.marcu.gyrosound

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import java.io.File

class RecordSoundFragment(recordingFile: File?, buttonNum: Int) : DialogFragment() {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private var recordingFile: File? = recordingFile
    private var buttonNum: Int = buttonNum
    private var soundId = -1
    private var streamId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_sound, container, false)
    }

    override fun onResume() {
        if (recordingFile != null){
            soundId = viewModel.loadSound(recordingFile!!)
        }
        val playButton = (requireView()).findViewById<Button>(R.id.play_button)
        playButton.setOnClickListener{v -> playClick(v as Button)}

        val saveButton = (requireView()).findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener{v -> saveClick(v as Button)}

        val cancelButton = (requireView()).findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener{v -> cancelClick(v as Button)}

        val editFilename = (requireView()).findViewById<EditText>(R.id.edit_filename)
        val filename = extractFilename(recordingFile!!)
        editFilename.setText(filename.substring(0,filename.length - 4))

        super.onResume()
    }

    fun extractFilename(file: File): String{
        val filepathSize = recordingFile!!.absolutePath.split("/").size
        val filename = recordingFile!!.absolutePath.split("/")[filepathSize - 1]
        return filename
    }

    fun createFilename(str: String): File {
        val audioDir = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos")
        audioDir.mkdirs()
        val audioDirPath: String = audioDir.getAbsolutePath()

        return File("$audioDirPath/$str.mp3")
    }

    override fun getTheme(): Int {
        return R.style.RecordFragment
    }

    companion object {
        private var INSTANCE: RecordSoundFragment? = null
        fun getInstance(recordingFile: File?, buttonNum: Int): RecordSoundFragment {
            if (INSTANCE == null) {
                INSTANCE = RecordSoundFragment(recordingFile, buttonNum)
            }
            else{
                INSTANCE!!.setRecordingFile(recordingFile)
                INSTANCE!!.setButtonNum(buttonNum)
            }
            return INSTANCE as RecordSoundFragment
        }
    }

    private fun setRecordingFile(recordingFile: File?) {
        this.recordingFile = recordingFile
    }

    private fun setButtonNum(buttonNum: Int) {
        this.buttonNum = buttonNum
    }

    private fun playClick(v: Button) {
        if (v.text == "Play") {
            streamId = viewModel.playSound(soundId, recordingFile!!)
            v.setText("Pause")
        } else if (v.text == "Pause") {
            viewModel.pauseSound(streamId, recordingFile!!)
            v.setText("Play")
        }
    }

    private fun saveClick(v: Button) {
        val filename: String = (requireView()).findViewById<EditText>(R.id.edit_filename).text.toString().replace(" ","")
        if (filename.length == 0){
            Toast.makeText(context, "Enter filename", Toast.LENGTH_SHORT).show()
            return
        }
        val file = createFilename(filename)
        recordingFile!!.renameTo(file)
        viewModel.addSoundFile(file)

        Toast.makeText(context, "Sound saved as ${filename}.mp3", Toast.LENGTH_SHORT).show()
        dismiss()
        //viewModel.setSelection(buttonNum, viewModel.getKeyOfSoundFile(recordingFile!!))
    }

    private fun cancelClick(v: Button) {
        recordingFile!!.delete()
        Toast.makeText(context, "Sound deleted", Toast.LENGTH_SHORT).show()
        dismiss()
    }

}