package ar.edu.unlam.mvvmylivedata.ui.view

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ar.edu.unlam.mvvmylivedata.R
import ar.edu.unlam.mvvmylivedata.ui.vieModel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var shouldDeployButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vm: MainViewModel by viewModels()

        shouldDeployButton = findViewById<Button>(R.id.shouldIDeployButton)

        vm.model.observe(this, {
            it?.run {
                onTextChange(it.message)
                changeBackgroundDeploy(it.shouldideploy)
            }
        })

        shouldIDeployButton.setOnClickListener {

            if (liveEditText.text.toString() == "") {
                vm.fetchApiData("UTC")
            } else {
                vm.fetchApiData(liveEditText.text.toString())
            }
        }
    }

    private fun onTextChange(text: String) {
        liveTextView.text = text
    }

    private fun changeBackgroundDeploy(shouldDeploy: Boolean) {
        if (!shouldDeploy) {
            shouldDeployHolder.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )
        } else {
            shouldDeployHolder.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        }

    }
}