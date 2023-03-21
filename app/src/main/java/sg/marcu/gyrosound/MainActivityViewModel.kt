package sg.marcu.gyrosound

import android.app.Application
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.selects.select
import java.io.File
import kotlin.math.pow

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    // for all functions, a button id corresponds to a specific slot in the following 2 arrays
    private var soundSelections = IntArray(8) // the soundSelections (any int) of button 1 to 8 (aka the track id that is selected, number ranges from 1 to as many tracks as therer are)
//    private var soundIds = IntArray(8) // the soundIds in soundpool
    private var streamIds = IntArray(8) // the streamid (any int) of button 1 to 8 (stream ids are generated dynamically)
    private var sounds: HashMap<Int, File> = hashMapOf<Int, File>() //maps soundid to soundfile
//    private var sounds: HashMap<Int, File> = hashMapOf() //maps soundselection to soundfile

    private var _freq = MutableLiveData<Float>() //global frequency modiifier according tto gyroscope
    private var baseVal = 1.25f

    fun addSoundFile(file: File ) {
        sounds.set(sounds.size, file)
        Log.d("sounds", "added file at ${file.absolutePath}")
    }

    fun freq() : LiveData<Float> {
        return _freq
    }

//    fun setSoundId(buttonNum: Int, soundSelection: Int) {
//        soundSelections[buttonNum] = soundSelection //set the selected track globally, ie so that the editsound fragment knows what is selected
//        val newSoundId = soundPool.load(sounds[soundSelection]?.absolutePath ?: "", 1) // load the corresponding track
////        soundIds[buttonNum] = newSoundId //store the loaded sound id in the sound pool, linking it to the button
//        Log.d("sounds", "setting: newsoundid: $newSoundId, buttonnnum: $buttonNum, soundselection: $soundSelection, soundfp: ${sounds[soundSelection]}")
//    }

    fun setSelection(buttonNum: Int, selectionNum: Int) {
        soundSelections[buttonNum] = selectionNum
    }

    fun playSound(buttonNum: Int) {
        val newStreamId = soundPool.play(soundSelections[buttonNum], 1F, 1F, 0, -1, _freq.value!!.toFloat())
        streamIds[buttonNum] = newStreamId
        Log.d("sounds", "playing: newStreamId: ${newStreamId}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
    }

    fun pauseSound(buttonNum: Int) {
        soundPool.stop(streamIds[buttonNum])
        streamIds[buttonNum] = 0
    }

    fun changeFreq(newFreq: Float) {
        _freq.value = newFreq
        for (buttonNumber in streamIds) { //iterate through all buttons and change their stream if it is not 0
            if (streamIds[buttonNumber] != 0) {
                soundPool.setRate(streamIds[buttonNumber], baseVal.pow(_freq.value!!))
            }
        }
    }


}