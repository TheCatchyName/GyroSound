package sg.marcu.gyrosound

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.activity.viewModels
import java.io.File


class EditSound : AppCompatActivity() {
    private var editFragmentIds = hashMapOf<Int, Int>(R.id.edit_fragment_key_1 to 0)
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editsound)

        if (savedInstanceState == null) {
        }

        Log.d("CheckViewModel", "Edit Sound ${viewModel}")

    }

    fun goToPlayMode(view: View){
        val it = Intent()
        setResult(RESULT_OK, it)
        finish()
    }

    fun recordClick(view: View) {
        Log.d("record click", "log")
    }

    fun getButtonNum(editKeyFragment: EditKeyFragment): Int {
        return editFragmentIds[editKeyFragment.id]?: -1
    }

    fun updateViewModel(){
        val audioDir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "AudioMemos")
        audioDir.mkdirs()
        val files = audioDir.listFiles()

        for (file in files!!){
            viewModel.addSoundFile(file)
        }
    }
}