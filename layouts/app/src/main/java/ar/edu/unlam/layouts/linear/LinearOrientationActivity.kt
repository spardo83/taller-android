package ar.edu.unlam.layouts.linear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import ar.edu.unlam.layouts.R
import kotlinx.android.synthetic.main.activity_linear_orientation.*

class LinearOrientationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linear_orientation)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnCambiar.setOnClickListener {
            cambiar()
        }

    }

    fun cambiar() {
        when (linearContainer.orientation) {
            LinearLayout.HORIZONTAL -> {
                linearContainer.orientation = LinearLayout.VERTICAL
                titulo.text = getString(R.string.orientation_vertical)
                btnCambiar.text = getString(R.string.cambiar_horizontal)
            }
            LinearLayout.VERTICAL -> {
                linearContainer.orientation = LinearLayout.HORIZONTAL
                titulo.text = getString(R.string.orientation_horizontal)
                btnCambiar.text = getString(R.string.cambiar_vertical)

            }
        }
    }

}
