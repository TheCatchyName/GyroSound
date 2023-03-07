package sg.marcu.gyrosound

import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(this, R.raw.key02, 1)
    }
}