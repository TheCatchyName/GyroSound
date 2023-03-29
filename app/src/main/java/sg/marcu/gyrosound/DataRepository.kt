package sg.marcu.gyrosound

import android.media.SoundPool
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File


class DataRepository{
    private var mSoundPool: SoundPool? = null
    private var mLiveSounds = MutableLiveData<HashMap<Int, File>>()
    private var mSounds = HashMap<Int, File>()
    private var mSoundSelections = IntArray(8)
    private var mSoundStreams = IntArray(8)
    private var mSemitones = IntArray(8)
    private var mOctaves = IntArray(8)

    companion object {
        private var INSTANCE: DataRepository? = null
        fun getInstance(): DataRepository {
            if (INSTANCE == null) {
                INSTANCE = DataRepository()
            }
            return INSTANCE as DataRepository
        }
    }

    fun getLiveSounds(): LiveData<HashMap<Int,File>>{
        return mLiveSounds
    }

    fun getSounds(): HashMap<Int,File>{
        return mSounds
    }

    fun getSoundPool(): SoundPool? {
        if (mSoundPool == null){
            return null
        }
        return mSoundPool
    }

    fun setSoundPool(soundPool: SoundPool){
        mSoundPool = soundPool
    }

    fun setLiveSounds(sounds: HashMap<Int,File>){
        mLiveSounds.postValue(sounds)
        setSounds(sounds)
    }

    fun setSounds(sounds: HashMap<Int,File>){
        mSounds = sounds
        mLiveSounds.postValue(sounds)
    }

    fun setSoundSelections(soundSelections: IntArray) {
        mSoundSelections = soundSelections
    }

    fun getSoundSelection(): IntArray {
        return mSoundSelections
    }

    fun setStreamId(streamIds: IntArray) {
        mSoundStreams = streamIds
    }

    fun getStreamId(): IntArray {
        return mSoundStreams
    }

    fun setSemitone(semitoneArray: IntArray) {
        mSemitones = semitoneArray
    }

    fun getSemitone(): IntArray {
        return mSemitones
    }

    fun setOctave(octaveArray: IntArray) {
        mOctaves = octaveArray
    }

    fun getOctave(): IntArray {
        return mOctaves
    }
}