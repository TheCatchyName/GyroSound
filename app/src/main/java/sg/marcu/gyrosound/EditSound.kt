package sg.marcu.gyrosound

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class EditSound : AppCompatActivity() {
    private lateinit var sounds: HashMap<Int, Int>
    private var soundsNames: Map<Int, String> = R.raw::class.java.declaredFields.map { k -> k.getInt(k) to k.name }.toMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editsound)

        sounds = intent.getSerializableExtra("sounds") as HashMap<Int, Int>

        if (savedInstanceState == null) {
            sounds.forEach { k ->
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.edit_root_layout, EditKeyFragment.newInstance(k.key, soundsNames.get(k.value)!!), k.key.toString())
                    .commit()
            }
        }

        supportFragmentManager
            .setFragmentResultListener("requestKey", this) { requestKey, bundle ->
                // We use a String here, but any type that can be put in a Bundle is supported
                val id = bundle.getInt("keyId")
                val sound = bundle.getInt("soundId")
                if (sounds.containsKey(id)){
                    sounds.set(id, sound)
                }
                else{
                    sounds[id] = sound
                }
                Log.d("soundsset", sounds.get(id).toString())
                // Do something with the result
            }
    }

    fun goToPlayMode(view: View){
        val it = Intent()
        it.putExtra("sounds", sounds)
        setResult(RESULT_OK, it)
        finish()
    }

    fun recordClick(view: View) {
        Log.d("record click", "log")
    }
}