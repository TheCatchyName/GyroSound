package sg.marcu.gyrosound

import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.viewModelFactory
import java.io.File

class KeyFragment: Fragment(), View.OnTouchListener {
    // TODO: Rename and change types of parameters
    private val fragmentId = 0
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("CheckViewModel", "Key Fragment ${viewModel}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel.freq().observe( viewLifecycleOwner
        ) {
            view?.findViewById<TextView>(R.id.buttonFreqText)?.text = it.toString()
        }
        return inflater.inflate(R.layout.fragment_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val buttonPlay = requireActivity().findViewById<Button>(R.id.buttonPlay)
        viewModel.setSelection(fragmentId, 1)
        buttonPlay.setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewModel.playSound(fragmentId)
            activity?.findViewById<Button>(R.id.buttonPlay)?.text = "PRESSED"
        }
        if(event.action ==MotionEvent.ACTION_UP){
            viewModel.pauseSound(fragmentId)
            activity?.findViewById<Button>(R.id.buttonPlay)?.text = "PRESS ME"
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