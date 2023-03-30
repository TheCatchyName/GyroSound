package sg.marcu.gyrosound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import java.io.File

class SoundManagerFragment : DialogFragment() {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private lateinit var soundFiles: HashMap<Int, File>

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
        return inflater.inflate(R.layout.fragment_sound_manager, container, false)
    }

    override fun onResume() {
        viewModel.getSoundFiles().observe(viewLifecycleOwner, {
            soundFiles = it
            setRadioGroup()
        })

        val backButton = requireView().findViewById<Button>(R.id.sound_manager_back_button)
        backButton.setOnClickListener{cancelClick(it as Button)}

        val deleteButton = requireView().findViewById<Button>(R.id.sound_manager_delete_button)
        deleteButton.setOnClickListener{deleteClick(it as Button)}

        super.onResume()
    }

    fun setRadioGroup(){
        val soundsGroup = requireView().findViewById<RadioGroup>(R.id.sound_manager_sounds)

        soundsGroup.removeAllViews()

        soundFiles.forEach { entry ->
            val radioBtn = RadioButton(context)
            val value = entry.value.absolutePath.split("/")
            val str = value[value.size - 1]

            radioBtn.id = View.generateViewId()
            radioBtn.tag = entry.key
            radioBtn.setText(str)
            soundsGroup.addView(radioBtn)
        }

    }

    fun removeFromRadioGroup(rg: RadioGroup, id: Int){
        var removeViewId: Int = -1
        for (i in 0 until rg.getChildCount()) {
            if (rg.getChildAt(i).tag == id){
                removeViewId = i
            }
        }
        if (removeViewId > -1){
            rg.removeViewAt(removeViewId)
        }
    }

    override fun getTheme(): Int {
        return R.style.RecordFragment
    }

    private fun cancelClick(v: Button) {
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    private fun deleteClick(v: Button){
        val soundsGroup = requireView().findViewById<RadioGroup>(R.id.sound_manager_sounds)
        soundsGroup.children.forEach{child ->
            if ((child as RadioButton).isChecked()){
                soundFiles.get(child.tag)!!.delete()
                Toast.makeText(context, "Deleted file ${soundFiles.get(child.tag)}", Toast.LENGTH_SHORT).show()
                soundFiles.remove(child.tag)
                viewModel.updateSounds(soundFiles)

                //removeFromRadioGroup(soundsGroup, child.tag.toString().toInt())
            }
        }
    }


    companion object {
        private var INSTANCE: SoundManagerFragment? = null
        fun getInstance(): SoundManagerFragment {
            if (INSTANCE == null) {
                INSTANCE = SoundManagerFragment()
            }
            else{
                //INSTANCE!!.setRecordingFile(recordingFile)
                //INSTANCE!!.setButtonNum(buttonNum)
            }
            return INSTANCE as SoundManagerFragment
        }
    }
}