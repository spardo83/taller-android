package ar.edu.unlam.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ar.edu.unlam.permissions.databinding.ActivityMainBinding

class MainSelfHandledActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val REQUEST_CODE = 200


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setPermissionsText()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding.requestCamera.setOnClickListener {
                requestCameraPermission()
            }
            binding.requestLocation.setOnClickListener { requestLocationPermission() }
            binding.requestAudio.setOnClickListener { requestAudioPermission() }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        if (!hasCameraPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocationPermission() {
        if (!hasLocationPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestAudioPermission() {
        if (!hasAudioPermission()) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE
            )
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun hasAudioPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    setPermissionsText()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setPermissionsText() {
        binding.hasCamera.text = if (hasCameraPermission()) "SI" else "NO"
        binding.hasLocation.text = if (hasLocationPermission()) "SI" else "NO"
        binding.hasAudio.text = if (hasAudioPermission()) "SI" else "NO"
    }
}