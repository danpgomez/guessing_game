package com.example.guessinggame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.guessinggame.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val words = listOf("Android", "Activity", "Fragment")
    private val secretWord = words.random().uppercase()

    // String to display in the secret word field
    private var secretWordDisplay = ""

    private var correctGuesses = "" // Perhaps consider changing this to correctlyGuessedLetters
    private var incorrectGuesses = ""
    private var livesLeft = 8


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val view = binding.root

        secretWordDisplay = deriveSecretWordDisplay()
        updateScreen()

        binding.guessButton.setOnClickListener {
            val playerGuess = binding.guess.text.toString().uppercase()
            makeGuess(playerGuess)
            binding.guess.text = null
            updateScreen()

            if (isWon() || isLost()) {
                val action =
                    GameFragmentDirections.actionGameFragmentToResultFragment(wonLostMessage())
                view.findNavController().navigate(action)
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateScreen() {
        binding.word.text = secretWordDisplay
        binding.lives.text = "You have $livesLeft lives left."
        binding.incorrectGuesses.text = "Incorrect guesses: $incorrectGuesses"
    }

    private fun deriveSecretWordDisplay(): String {
        var display = ""
        secretWord.forEach { letter ->
            display += checkLetter(letter.toString())
        }

        return display
    }

    private fun checkLetter(str: String) = when (correctGuesses.contains(str)) {
        true -> str
        false -> "_"
    }

    private fun makeGuess(guess: String) {
        if (guess.length == 1) { // Why do we need this check?
            if (secretWord.contains(guess)) {
                correctGuesses += guess
                secretWordDisplay = deriveSecretWordDisplay()
            } else {
                incorrectGuesses += "$guess"
                livesLeft--
            }
        }
    }

    private fun isWon() = secretWord.equals(secretWordDisplay, true)
    private fun isLost() = livesLeft <= 0

    private fun wonLostMessage(): String {
        var message = ""
        if (isWon()) message = "You WON!"
        else if (isLost()) message = "You lost."
        message += " The correct word was $secretWord."
        return message
    }
}
