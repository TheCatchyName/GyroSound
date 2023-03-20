package sg.marcu.gyrosound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class EditSound : AppCompatActivity() {
    private lateinit var keySounds: HashMap<Int, Int>
    private lateinit var soundFiles: Map<Int, File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editsound)

        keySounds = intent.getSerializableExtra("sounds") as HashMap<Int, Int>
        soundFiles = intent.getSerializableExtra("soundFiles") as HashMap<Int, File>

        if (savedInstanceState == null) {
            keySounds.forEach { k ->
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.edit_root_layout, EditKeyFragment.newInstance(k.key, k.value,
                        soundFiles as HashMap<Int, File>, k.key.toString()))
                    .commit()
            }
        }

        supportFragmentManager
            .setFragmentResultListener("requestKey", this) { requestKey, bundle ->
                // We use a String here, but any type that can be put in a Bundle is supported
                val id = bundle.getInt("keyId")
                val sound = bundle.getInt("soundId")
                if (keySounds.containsKey(id)){
                    keySounds.set(id, sound)
                }
                else{
                    keySounds[id] = sound
                }
                Log.d("soundsset", keySounds.get(id).toString())
                // Do something with the result
            }
    }

    fun goToPlayMode(view: View){
        val it = Intent()
        it.putExtra("sounds", keySounds)
        setResult(RESULT_OK, it)
        finish()
    }

    fun recordClick(view: View) {
        Log.d("record click", "log")
    }
}