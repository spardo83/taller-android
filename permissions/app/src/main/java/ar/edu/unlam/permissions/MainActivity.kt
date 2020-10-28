package ar.edu.unlam.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import ar.edu.unlam.permissions.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ActivityResultCallback<Any> {

    private lateinit var binding: ActivityMainBinding

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                setPermissionsText()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setPermissionsText()

        binding.requestCamera.setOnClickListener { requestCameraPermission() }
        binding.requestLocation.setOnClickListener { requestLocationPermission() }
        binding.requestAudio.setOnClickListener { requestAudioPermission() }
    }


    private fun requestCameraPermission() {
        if (!hasCameraPermission()) {
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun requestLocationPermission() {
        if (!hasLocationPermission()) {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    private fun requestAudioPermission() {
        if (!hasAudioPermission()) {
            requestPermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
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


    private fun setPermissionsText() {
        binding.hasCamera.text = if (hasCameraPermission()) "SI" else "NO"
        binding.hasLocation.text = if (hasLocationPermission()) "SI" else "NO"
        binding.hasAudio.text = if (hasAudioPermission()) "SI" else "NO"
    }

    override fun onActivityResult(result: Any?) {
        Log.i("test", "result $result")
    }

}