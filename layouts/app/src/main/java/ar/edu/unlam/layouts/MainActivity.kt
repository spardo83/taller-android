package ar.edu.unlam.layouts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ar.edu.unlam.layouts.constraint.ConstraintLayoutActivity
import ar.edu.unlam.layouts.linear.LinearOrientationActivity
import ar.edu.unlam.layouts.linear.WeightSumSampleOneActivity
import ar.edu.unlam.layouts.linear.WeightSumSampleTwoActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.linear_orientation -> navigate(
            LinearOrientationActivity::class.java
        )
        R.id.linear_weight_sum -> navigate(WeightSumSampleOneActivity::class.java)
        R.id.linear_weight_sum_two -> navigate(WeightSumSampleTwoActivity::class.java)
        R.id.constraint_layout -> navigate(ConstraintLayoutActivity::class.java)
        else -> super.onOptionsItemSelected(item)
    }

    private fun navigate(activity: Class<*>): Boolean {
        startActivity(Intent(this, activity))
        return true
    }
}
