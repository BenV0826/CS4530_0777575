package com.example.cs4530a1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cs4530a1.databinding.ActivityMainBinding


import com.example.cs4530a1.R
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Create a binding similarly to emailSplitter activity
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Add an OnClickListener for each button
        binding.button1.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            // Create a bundle
            val argsBundle = Bundle()
            // Put the text of the button into the bundle
            argsBundle.putString("SELECTED_OPTION", binding.button1.text.toString())
            intent.putExtras(argsBundle)
            startActivity(intent)
        }

        // Do the same process for the other buttons
        binding.button2.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            val argsBundle = Bundle()
            argsBundle.putString("SELECTED_OPTION", binding.button2.text.toString())
            intent.putExtras(argsBundle)
            startActivity(intent)
        }
        binding.button3.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            val argsBundle = Bundle()
            argsBundle.putString("SELECTED_OPTION", binding.button3.text.toString())
            intent.putExtras(argsBundle)
            startActivity(intent)

        }
        binding.button4.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            val argsBundle = Bundle()
            argsBundle.putString("SELECTED_OPTION", binding.button4.text.toString())
            intent.putExtras(argsBundle)
            startActivity(intent)
        }
        binding.button5.setOnClickListener {
            val intent = Intent(this, SecondaryActivity::class.java)
            val argsBundle = Bundle()
            argsBundle.putString("SELECTED_OPTION", binding.button5.text.toString())
            intent.putExtras(argsBundle)
            startActivity(intent)
        }
        setContentView(binding.root)
    }
}