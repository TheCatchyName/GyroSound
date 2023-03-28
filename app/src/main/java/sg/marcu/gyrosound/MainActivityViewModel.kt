package sg.marcu.gyrosound

import android.app.Application
import android.media.AudioManager
import android.media.SoundPool
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import kotlin.math.pow

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    // for all functions, a button id corresponds to a specific slot in the following 2 arrays
    private var soundSelections = IntArray(8) // the soundSelections (any int) of button 1 to 8 (aka the track id that is selected, number ranges from 1 to as many tracks as therer are)
    private var streamIds = IntArray(8) // the streamid (any int) of button 1 to 8 (stream ids are generated dynamically)
    private var sounds: HashMap<Int, File> = hashMapOf<Int, File>() //maps soundid to soundfile
    private var pitches = FloatArray(8)

    private var liveSounds = MutableLiveData<HashMap<Int,File>>()
    private var liveSoundSelection: MutableLiveData<IntArray> = MutableLiveData<IntArray>()

    private var _freq = MutableLiveData<Float>() //global frequency modiifier according tto gyroscope
    private var baseVal = 1.25f

    fun addSoundFile(file: File ) {
        val newSoundId = soundPool.load(file.absolutePath ?: "", 1) // load the corresponding track
        sounds.set(newSoundId, file)
        liveSounds.postValue(sounds)
        DataRepository.getInstance().setLiveSounds(sounds)
        Log.d("sounds", "added file at ${file.absolutePath}")
    }

    fun freq() : LiveData<Float> {
        return _freq
    }

    fun setSelection(buttonNum: Int, selectionNum: Int) {
        soundSelections[buttonNum] = selectionNum
        DataRepository.getInstance().setSoundSelections(soundSelections)
    }

    fun playSound(buttonNum: Int) {
        soundSelections = DataRepository.getInstance().getSoundSelection()
        streamIds = DataRepository.getInstance().getStreamId()
        pitches = DataRepository.getInstance().getPitch()
        if (streamIds[buttonNum] != 0) {
            soundPool.resume(streamIds[buttonNum])
            Log.d("sounds", "resume playing: stream: ${streamIds[buttonNum]}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        } else {
            val newStreamId = soundPool.play(soundSelections[buttonNum], 1F, 1F, 0, -1, _freq.value!!.toFloat() * pitches[buttonNum])
            streamIds[buttonNum] = newStreamId
            DataRepository.getInstance().setStreamId(streamIds)
            Log.d("sounds", "created: newStreamId: ${newStreamId}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        }
    }

    fun pauseSound(buttonNum: Int) {
        soundPool.pause(streamIds[buttonNum])
//        streamIds[buttonNum] = 0
    }

    fun changeFreq(newFreq: Float) {
        _freq.value = newFreq
        pitches = DataRepository.getInstance().getPitch()
        for (buttonNumber in streamIds) { //iterate through all buttons and change their stream if it is not 0
            if (streamIds[buttonNumber] != 0) {
                soundPool.setRate(streamIds[buttonNumber], baseVal.pow(_freq.value!!) * pitches[buttonNumber])
            }
        }
    }

    fun setBaseFreq(buttonNum: Int, freqFloat: Float) {
        pitches[buttonNum] = freqFloat
        DataRepository.getInstance().setPitch(pitches)
    }

    fun getSoundFiles(): LiveData<HashMap<Int,File>> {
        return DataRepository.getInstance().getLiveSounds()
    }

    fun getSoundSelected(buttonNum: Int) : Int {
        soundSelections = DataRepository.getInstance().getSoundSelection()

        return soundSelections[buttonNum]
    }
}