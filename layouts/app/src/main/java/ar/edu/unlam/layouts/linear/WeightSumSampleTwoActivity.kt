package ar.edu.unlam.layouts.linear

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ar.edu.unlam.layouts.R

class WeightSumSampleTwoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weight_sum_sample_two)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
