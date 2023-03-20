package sg.marcu.gyrosound

import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.File
import kotlin.math.abs


private lateinit var soundPool: SoundPool
private var streamId = 0
class KeyFragment: Fragment(), View.OnTouchListener {
    // TODO: Rename and change types of parameters
    private var soundId: Int = 0
    private var soundFile: File? = null
    private val viewModel: MainActivityViewModel by activityViewModels()
    private var isPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setSoundId(   1, 1)
        viewModel.freq().observe( this

        ) {
            view?.findViewById<TextView>(R.id.buttonFreqText)?.text = it.toString()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        soundFile = (activity as MainActivity).getSoundFile(this.id)

        val buttonPlay = requireActivity().findViewById<Button>(R.id.buttonPlay)
//        soundId = soundPool!!.load(soundFile?.absolutePath ?: "", 1)
        buttonPlay.setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
//            val tempSoundFile = (activity as MainActivity).getSoundFile(this.id)
//            if (soundFile != tempSoundFile){
//                soundFile = tempSoundFile
//                soundId = soundPool!!.load(soundFile?.absolutePath ?: "", 1)
//            }
//            streamId = soundPool.play(soundId, 1F, 1F, 0, -1, abs(roll))
            viewModel.playSound(1)
        }
        if(event.action ==MotionEvent.ACTION_UP){
//            soundPool.stop(streamId)
            viewModel.pauseSound(1)
        }
        return true
    }

    companion object {
        fun newInstance(soundFile: File): KeyFragment {
            return KeyFragment()
        }
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment KeyFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            KeyFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}