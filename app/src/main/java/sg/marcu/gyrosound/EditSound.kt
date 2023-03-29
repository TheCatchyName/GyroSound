package sg.marcu.gyrosound

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class EditSound : AppCompatActivity() {
    private var editFragmentIds = hashMapOf<Int, Int>(R.id.edit_fragment_key_1 to 0)
    private val viewModel: MainActivityViewModel by viewModels()

    val displayMetrics: DisplayMetrics = DisplayMetrics()
    var statusBarHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editsound)

        if (savedInstanceState == null) {
        }

        statusBarHeight = getStatusBarHeightMet()

        Log.d("CheckViewModel", "Edit Sound ${viewModel}")
        viewModel.doUpdate()
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

    fun getStatusBarHeightMet(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}