package ar.edu.unlam.layouts.constraint

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.edu.unlam.layouts.R

private const val DOCS_URL = "https://developer.android.com/training/constraint-layout"

class ConstraintLayoutActivity : AppCompatActivity(R.layout.activity_constraint_layout) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setearListeners()
    }

    private fun setearListeners() {
        setearBoton()
        setearAyuda()
        setearRegistrar()
    }

    private fun setearBoton() {
        val emailEditText = findViewById<EditText>(R.id.email)
        findViewById<Button>(R.id.botonLogin).setOnClickListener {
            val email = emailEditText.text?.toString()
            Toast.makeText(this, "Ingresando con usuario: $email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setearAyuda() {
        findViewById<TextView>(R.id.textViewAyuda).setOnClickListener {
            navegarADocumentacion()
        }
    }

    private fun setearRegistrar() {
        findViewById<TextView>(R.id.textViewRegistrar).setOnClickListener {
            navegarADocumentacion()
        }
    }

    private fun navegarADocumentacion() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DOCS_URL))
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                "Ups! Tenes que instalar un browser para poder ver la documentación!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}