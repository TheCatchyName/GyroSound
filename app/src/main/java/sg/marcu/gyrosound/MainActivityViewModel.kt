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
    private lateinit var soundPool: SoundPool
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
        sounds = DataRepository.getInstance().getSounds()
        if (!sounds.values.contains(file)) {
            val newSoundId =
                soundPool.load(file.absolutePath ?: "", 1) // load the corresponding track
            sounds.set(newSoundId, file)
            liveSounds.postValue(sounds)
            DataRepository.getInstance().setLiveSounds(sounds)
            Log.d("sounds", "added file at ${file.absolutePath} soundId is ${newSoundId}")
        }
    }

    fun updateSounds(pSounds: HashMap<Int, File>){
        sounds = pSounds
        liveSounds.postValue(sounds)
        DataRepository.getInstance().setLiveSounds(sounds)

        var i = 0
        for (sound in soundSelections){
            if (!sounds.containsKey(sound)){
                setSelection(i, sounds.keys.first())
            }
            i ++
        }
    }

    fun freq() : LiveData<Float> {
        return _freq
    }

    fun setSelection(buttonNum: Int, selectionNum: Int) {
        if (soundSelections[buttonNum] == selectionNum){
            return
        }
        soundSelections[buttonNum] = selectionNum
        streamIds[buttonNum] = 0
        DataRepository.getInstance().setSoundSelections(soundSelections)
        DataRepository.getInstance().setStreamId(streamIds)
    }

    fun playSound(buttonNum: Int) {
        soundSelections = DataRepository.getInstance().getSoundSelection()
        streamIds = DataRepository.getInstance().getStreamId()
        semitone = DataRepository.getInstance().getSemitone()
        octave = DataRepository.getInstance().getOctave()
        if (streamIds[buttonNum] != 0) {
            soundPool.resume(streamIds[buttonNum])
            Log.d("sounds", "resume playing: stream: ${streamIds[buttonNum]}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        }
        else {
            val newStreamId = soundPool.play(soundSelections[buttonNum], 1F, 1F, 0, -1, baseVal.pow(_freq.value!!.toFloat()) * two.pow((semitone[buttonNum] + 12 * octave[buttonNum])/12f))
            streamIds[buttonNum] = newStreamId
            DataRepository.getInstance().setStreamId(streamIds)
            Log.d("sounds", "created: newStreamId: ${newStreamId}, buttonnnum: $buttonNum, soundid: ${soundSelections[buttonNum]}")
        }
    }

    /*
    Code for loading/playing/pausing sounds based off of file input rather than button input
     */
    fun loadSound(file: File): Int {
        val soundId = soundPool.load(file.absolutePath?: "", 1)
        return soundId
    }

    fun playSound(soundId: Int, file: File): Int {
        val streamId = soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
        return streamId
    }

    fun pauseSound(streamId: Int, file: File){
        soundPool.pause(streamId)
    }

    fun pauseSound(buttonNum: Int) {
        soundPool.pause(streamIds[buttonNum])
//        streamIds[buttonNum] = 0
    }

    fun changeFreq(newFreq: Float) { //sets the base frequency according to gyroscope inout
        _freq.value = newFreq
        semitone = DataRepository.getInstance().getSemitone()
        octave = DataRepository.getInstance().getOctave()
        /*
        for (buttonNum in streamIds) { //iterate through all buttons and change their stream if it is not 0
            if (streamIds[buttonNum] != 0) {
                soundPool.setRate(streamIds[buttonNum], baseVal.pow(_freq.value!!.toFloat()) * two.pow((semitone[buttonNum] + 12 * octave[buttonNum])/12))
            }
        }
        */
        for (buttonNum in 0 ..7){
            if (streamIds[buttonNum] != 0) {
                soundPool.setRate(streamIds[buttonNum], baseVal.pow(_freq.value!!.toFloat()) * two.pow((semitone[buttonNum] + 12 * octave[buttonNum])/12f))
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

        doUpdate()
    }

    fun getSoundFiles(): LiveData<HashMap<Int,File>> {
        return DataRepository.getInstance().getLiveSounds()
    }

    fun getSoundSelected(buttonNum: Int) : Int {
        soundSelections = DataRepository.getInstance().getSoundSelection()

        return soundSelections[buttonNum]
    }

    fun getKeyOfSoundFile(file: File): Int{
        sounds.forEach {
            k ->
            if (k.value == file){
                return k.key
            }
        }
        return -1
    }

    fun updateData() {
        DataRepository.getInstance().setSoundPool(soundPool)
    }

    fun doUpdate(){
        if (DataRepository.getInstance().getSoundPool() == null){
            soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
            DataRepository.getInstance().setSoundPool(soundPool)
        }
        else{
            soundPool = DataRepository.getInstance().getSoundPool()!!
        }
    }

    fun getButtonText(buttonNum: Int) : String {
        val fileNames = DataRepository.getInstance().getSounds()
        val fileNameIterator = fileNames.map{k -> k.value.toString().split("/")[k.value.toString().split("/").size - 1]}
        val soundId = DataRepository.getInstance().getSoundSelection()[buttonNum]
        return "${fileNameIterator[soundId - 1]}\nO: ${getSelectedOctave(buttonNum)} S: ${getSelectedSemitone(buttonNum)}"
    }
}