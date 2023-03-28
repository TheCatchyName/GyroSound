package sg.marcu.gyrosound

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File


class DataRepository{
    private var mLiveSounds = MutableLiveData<HashMap<Int, File>>()
    private var mSoundSelections = IntArray(8)
    private var mSoundStreams = IntArray(8)
    private var mPitches = FloatArray(8)

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

    fun setLiveSounds(sounds: HashMap<Int,File>){
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

    fun setPitch(pitchArray: FloatArray) {
        mPitches = pitchArray
    }

    fun getPitch(): FloatArray {
        return mPitches
    }
}