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
    private var semitone = IntArray(8)
    private var octave = IntArray(8)

    private var liveSounds = MutableLiveData<HashMap<Int,File>>()
    private var liveSoundSelection: MutableLiveData<IntArray> = MutableLiveData<IntArray>()

    private var _freq = MutableLiveData<Float>() //global frequency modiifier according tto gyroscope
    private var baseVal = 1.25f
    private var two = 2.0f

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
        semitone = DataRepository.getInstance().getSemitone()
        octave = DataRepository.getInstance().getOctave()
        if (streamIds[buttonNum] != 0) {
            soundPool.resume(streamIds[buttonNum])
            Log.d("sounds", "resume playing: stream: ${streamIds[buttonNum]}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        } else {
            val newStreamId = soundPool.play(soundSelections[buttonNum], 1F, 1F, 0, -1, baseVal.pow(_freq.value!!.toFloat()) * two.pow((semitone[buttonNum] + 12 * octave[buttonNum])/12))
            streamIds[buttonNum] = newStreamId
            DataRepository.getInstance().setStreamId(streamIds)
            Log.d("sounds", "created: newStreamId: ${newStreamId}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        }
    }

    fun pauseSound(buttonNum: Int) {
        soundPool.pause(streamIds[buttonNum])
//        streamIds[buttonNum] = 0
    }

    fun changeFreq(newFreq: Float) { //sets the base frequency according to gyroscope inout
        _freq.value = newFreq
        semitone = DataRepository.getInstance().getSemitone()
        octave = DataRepository.getInstance().getOctave()
        for (buttonNum in streamIds) { //iterate through all buttons and change their stream if it is not 0
            if (streamIds[buttonNum] != 0) {
                soundPool.setRate(streamIds[buttonNum], baseVal.pow(_freq.value!!.toFloat()) * two.pow((semitone[buttonNum] + 12 * octave[buttonNum])/12))
            }
        }
    }

    fun setSemitone(buttonNum: Int, newSemitone: Int) {
        semitone[buttonNum] = newSemitone
        DataRepository.getInstance().setSemitone(semitone)
        Log.d("sounds", "set semitone for buttonnnum $buttonNum, semitone selection: ${newSemitone}")
    }

    fun getSelectedSemitone(buttonNum: Int): Int {
        return DataRepository.getInstance().getSemitone()[buttonNum]
    }

    fun getSelectedOctave(buttonNum: Int): Int {
        return DataRepository.getInstance().getOctave()[buttonNum]
    }

    fun setOctave(buttonNum: Int, newOctave: Int) {
        octave[buttonNum] = newOctave
        DataRepository.getInstance().setOctave(octave)
        Log.d("sounds", "set octave for buttonnnum $buttonNum, octave selection: ${newOctave}")
    }

    fun init() {
        var semitoneBase = -2
        for (i in 0 .. 7) {
            setSemitone(i, semitoneBase)
            setOctave(i, 0)
            setSelection(i, 3)
            semitoneBase += 1
        }
    }

    fun getSoundFiles(): LiveData<HashMap<Int,File>> {
        return DataRepository.getInstance().getLiveSounds()
    }

    fun getSoundSelected(buttonNum: Int) : Int {
        soundSelections = DataRepository.getInstance().getSoundSelection()

        return soundSelections[buttonNum]
    }
}