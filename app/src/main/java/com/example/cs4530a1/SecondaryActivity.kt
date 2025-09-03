package com.example.cs4530a1

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.Toast
import com.example.cs4530a1.databinding.ActivitySecondaryBinding

class SecondaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Create a binding
        val binding = ActivitySecondaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Set an OnClickListener for the back button
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            binding.secondaryScreenText.text = ""
            startActivity(intent)
        }

        val selectedButton = intent.getStringExtra("SELECTED_OPTION")
        binding.secondaryScreenText.text = selectedButton


    }
}