package sg.marcu.gyrosound

import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var soundPool: SoundPool
private var streamId = 0

/**
 * A simple [Fragment] subclass.
 * Use the [KeyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class KeyFragment : Fragment(), View.OnTouchListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
//
//        if(requireActivity().intent!=null) {
            val buttonPlay = requireActivity().findViewById<Button>(R.id.buttonPlay)
            soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
            soundPool!!.load(activity?.applicationContext, R.raw.key01, 1)
            buttonPlay.setOnTouchListener(this)

//        }
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val mp = MediaPlayer.create(activity?.applicationContext, R.raw.key01);
        var soundId = 1
        if (event.getAction()== MotionEvent.ACTION_DOWN) {
            streamId = soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            soundPool.stop(streamId)
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