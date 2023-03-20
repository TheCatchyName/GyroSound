package sg.marcu.gyrosound

import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.hardware.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.util.Half.EPSILON
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlin.math.*
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [KeyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeyFragment : Fragment(), View.OnTouchListener {
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

        val buttonPlay = requireActivity().findViewById<Button>(R.id.buttonPlay)
        buttonPlay.setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        var soundId = 1
        if (isPressed) {
            if (event.getAction()== MotionEvent.ACTION_DOWN) {
//                viewModel.playSound(1)
//                v!!.findViewById<Button>(R.id.buttonPlay).text = "PRESSED"
//            }
//            if(event.getAction()==MotionEvent.ACTION_UP) {
                viewModel.pauseSound(1)
//                viewModel.toastSound()
                v!!.findViewById<Button>(R.id.buttonPlay).text = "PRESS ME"
                isPressed = false
            }
        } else {
            if (event.getAction()== MotionEvent.ACTION_DOWN) {
                viewModel.playSound(1)
                v!!.findViewById<Button>(R.id.buttonPlay).text = "PRESSED"
                isPressed = true
            }
//            if(event.getAction()==MotionEvent.ACTION_UP){
//                viewModel.pauseSound(1)
//                v!!.findViewById<Button>(R.id.buttonPlay).text = "PRESS ME"
//            }
        }
        return true
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