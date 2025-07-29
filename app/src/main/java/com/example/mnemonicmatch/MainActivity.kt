package com.example.mnemonicmatch

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var seedText: TextView
    private lateinit var wordGrid: GridLayout
    private lateinit var resultText: TextView
    private lateinit var checkButton: Button
    private lateinit var restartButton: Button
    private lateinit var levelSpinner: Spinner
    private lateinit var showKeyButton: Button

    private var seedString = ""
    private var correctWords = listOf<String>()
    private val selectedWords = mutableListOf<String>()

    private var wordCount = 24
    private var seedLength = 48

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seedText = findViewById(R.id.seedText)
        wordGrid = findViewById(R.id.wordGrid)
        resultText = findViewById(R.id.resultText)
        checkButton = findViewById(R.id.checkButton)
        restartButton = findViewById(R.id.btnRestart)
        levelSpinner = findViewById(R.id.levelSpinner)
        showKeyButton = findViewById(R.id.btnShowKey)

        // Setup level spinner
        val levels = listOf("Very Easy", "Easy", "Medium", "Hard", "Very Hard")
        levelSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, levels)

        levelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { seedLength = 16; wordCount = 8 }
                    1 -> { seedLength = 24; wordCount = 12 }
                    2 -> { seedLength = 32; wordCount = 16 }
                    3 -> { seedLength = 40; wordCount = 20 }
                    4 -> { seedLength = 48; wordCount = 24 }
                }
                setupGame()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        checkButton.setOnClickListener {
            if (selectedWords == correctWords) {
                resultText.text = "✅ Correct!"
            } else {
                resultText.text = "❌ Wrong order! Try again."
                showKeyButton.visibility = View.VISIBLE
            }
        }

        restartButton.setOnClickListener {
            setupGame()
        }

        showKeyButton.setOnClickListener {
            resultText.text = "🔑 Correct Order:\n${correctWords.joinToString(" ")}"
        }

        // Initial setup
        setupGame()
    }

    private fun setupGame() {
        selectedWords.clear()
        wordGrid.removeAllViews()
        showKeyButton.visibility = View.GONE

        seedString = generateRandomSeed(seedLength)
        correctWords = generateWordsFromSeed(seedString)

        seedText.text = "Seed: $seedString"

        val shuffledWords = correctWords.shuffled()

        for (word in shuffledWords) {
            val button = Button(this).apply {
                text = word
                setOnClickListener {
                    if (selectedWords.size < wordCount) {
                        selectedWords.add(word)
                        text = "✔ $word"
                        isEnabled = false
                    }
                }
            }
            wordGrid.addView(button)
        }

        resultText.text = ""
    }

    private fun generateRandomSeed(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        return (1..length).map { chars.random() }.joinToString("")
    }

    private fun generateWordsFromSeed(seed: String): List<String> {
        val words = mutableListOf<String>()
        for (i in 0 until seed.length step 2) {
            if (i + 2 <= seed.length) {
                val chunk = seed.substring(i, i + 2)
                words.add("word$chunk")
            }
        }
        return words.take(wordCount)
    }
}
