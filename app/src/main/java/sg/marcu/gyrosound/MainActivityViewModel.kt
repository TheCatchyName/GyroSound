package sg.marcu.gyrosound

import android.app.Application
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.pow
import kotlin.properties.Delegates

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private var playBoolean by Delegates.notNull<Boolean>()
    private var soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
    private var streamIds = IntArray(8)
    private var soundIds = IntArray(8)
    private var _freq = MutableLiveData<Float>()
    private var baseVal = 1.25f

    fun freq() : MutableLiveData<Float> {
        return _freq
    }

    fun setSoundId(buttonNum: Int, soundId: Int) {
        soundIds[buttonNum] = soundId
        //todo set a switch case for the sound ids
        soundPool.load(getApplication<Application>().applicationContext, R.raw.violinc4, 1)
    }

    fun playSound(buttonNum: Int) {
        streamIds[buttonNum] =  soundPool.play(soundIds[buttonNum], 1F, 1F, 0, -1, _freq.value!!)
    }

    fun pauseSound(buttonNum: Int) {
        soundPool.stop(streamIds[buttonNum])
    }

    fun changeFreq(newFreq: Float) {
        _freq.value = newFreq
        Log.d("testing", _freq.value.toString())
        changeSound(1)
    }

    fun changeSound(buttonNum: Int) {
        soundPool.setRate(streamIds[buttonNum], baseVal.pow(_freq.value!!))
    }
}