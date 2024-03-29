package sg.marcu.gyrosound

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.io.File

class KeyFragment: Fragment(), View.OnTouchListener {
    // TODO: Rename and change types of parameters
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
//        viewModel.freq().observe( viewLifecycleOwner
//        ) {
//            view?.findViewById<TextView>(R.id.buttonFreqText)?.text = it.toString()
//        }
        return inflater.inflate(R.layout.fragment_key, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val tab = requireActivity().findViewById<TableLayout>(R.id.tableKeyLayout)
        for (i in 0..tab.childCount - 1) {
            val row = tab.getChildAt(i) as TableRow
            for (j in 0..row.childCount - 1) {
                val button = row.getChildAt(j) as Button
                if (viewModel.getSoundSelected(button.tag.toString().toInt()) == 0){
                    viewModel.setSelection(button.tag.toString().toInt(), 1)
                }
                button.text = viewModel.getButtonText(button.tag.toString().toInt())

                button.setOnTouchListener(this)
            }
        }

    }

    override fun onStart(){
        super.onStart()
        val tab = requireActivity().findViewById<TableLayout>(R.id.tableKeyLayout)
        for (i in 0..tab.childCount - 1) {
            val row = tab.getChildAt(i) as TableRow
            for (j in 0..row.childCount - 1) {
                val button = row.getChildAt(j) as Button
                if (viewModel.getSoundSelected(button.tag.toString().toInt()) == 0){
                    viewModel.setSelection(button.tag.toString().toInt(), 1)
                }
                button.text = viewModel.getButtonText(button.tag.toString().toInt())
            }
        }

    }



    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val button = v as Button
        val buttonId = button.tag.toString().toInt()
        Log.d("sounds", "button {$buttonId} pressed")
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewModel.playSound(buttonId)
            button.text = "${viewModel.getButtonText(buttonId)} *"
        }
        if(event.action ==MotionEvent.ACTION_UP){
            viewModel.pauseSound(buttonId)
            button.text = "${viewModel.getButtonText(buttonId)}"
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