package wordsvirtuoso

import java.io.File
import kotlin.random.Random

var tries = 0
val guessList = mutableListOf<String>()
var start = System.currentTimeMillis()
val wrongGuessSet = mutableSetOf<Char>()

fun main(args: Array<String>) {
    try {
        if (args.size != 2) throw Exception("Wrong number of arguments.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        return
    }
    var toCheckFileIncorrectWordsCounter = 0
    var patternFileIncorrectWordsCounter = 0
    val patternFileName = args[0]
    val patternFile = File(patternFileName)
    try {
        if (!patternFile.exists()) throw Exception("The words file $patternFileName doesn't exist.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        return
    }
    val toCheckFileName = args[1]
    val toCheckFile = File(toCheckFileName)
    try {
        if (!toCheckFile.exists()) throw Exception("The candidate words file $toCheckFileName doesn't exist.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        return
    }
    val correctWordsSet = mutableSetOf<String>()
    patternFile.forEachLine {
        if (!Regex("[a-zA-Z]{5}").matches(it)) {
            patternFileIncorrectWordsCounter++
        } else {
            for (i in 0 until it.lastIndex) {
                if (it[i] == it[i + 1]) {
                    patternFileIncorrectWordsCounter++
                    break
                }
            }
        }
        correctWordsSet += it.lowercase()
    }
    try {
        if (patternFileIncorrectWordsCounter != 0) throw Exception("$patternFileIncorrectWordsCounter invalid words were found in the $patternFileName file.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        return
    }
    var excludedWordsCounter = 0
    val candidatesSet = mutableSetOf<String>()
    toCheckFile.forEachLine {
        if (!Regex("[a-zA-Z]{5}").matches(it)) {
            toCheckFileIncorrectWordsCounter++
        } else {
            for (i in 0 until it.lastIndex) {
                if (it[i] == it[i + 1]) {
                    toCheckFileIncorrectWordsCounter++
                    break
                }
            }
        }
        if (!(correctWordsSet.contains(it.lowercase()))) excludedWordsCounter++
        else candidatesSet += it
    }

    try {
        if (toCheckFileIncorrectWordsCounter != 0) throw Exception("$toCheckFileIncorrectWordsCounter invalid words were found in the $toCheckFileName file.")
        if (excludedWordsCounter != 0) throw Exception("$excludedWordsCounter candidate words are not included in the $patternFileName file.")
    } catch (e: Exception) {
        println("Error: ${e.message}")
        return
    }

    val word = candidatesSet.toList()[Random.nextInt(candidatesSet.size)]
    println("Words Virtuoso")
    game(correctWordsSet, word)

}

fun game(allWordsSet: Set<String>, word: String) {
    println()
    println("Input a 5-letter word:")
    tries++
    val inputWord = readln().lowercase()
    val end = System.currentTimeMillis()
    if (inputWord.length > 1) {
        for (i in 0 until inputWord.length - 1) {
            if (inputWord[i] == inputWord[i + 1]) {
                println("The input has duplicate letters.")
                game(allWordsSet, word)
                return
            }
        }
    }
    when {
        (inputWord == word) -> {
            println()
            if (tries == 1) {
                for (i in word.uppercase()) {
                    print("\u001B[48:5:10m$i\u001B[0m")
                }
                println()
                println("Correct!")
                println("Amazing luck! The solution was found at once.")
            } else {
                val trialFeedback = mutableListOf<String>()
                for (i in 0 until 5) {
                    when {
                        word[i] == inputWord[i] -> trialFeedback.add(
                            "\u001B[48:5:10m${
                                inputWord[i].toString().uppercase()
                            }\u001B[0m"
                        )

                        word.contains(inputWord[i]) -> trialFeedback.add(
                            "\u001B[48:5:11m${
                                inputWord[i].toString().uppercase()
                            }\u001B[0m"
                        )

                        else -> {
                            wrongGuessSet.add(inputWord[i])
                            trialFeedback.add("\u001B[48:5:7m${inputWord[i].toString().uppercase()}\u001B[0m")
                        }
                    }
                }

                for (i in trialFeedback) guessList.add(i)
                for (i in guessList.indices) {
                    print(guessList[i])
                    if ((i + 1) % 5 == 0) println()
                }
                println()
                println("Correct!")
                println("The solution was found after $tries tries in ${((end - start) / 1000)} seconds.")
            }
            return
        }

        (inputWord == "exit") -> {
            println("The game is over.")
            return
        }

        (inputWord.length != 5) -> {
            println("The input isn't a 5-letter word.")
            game(allWordsSet, word)
            return
        }

        (!(Regex("[a-z]{5}").matches(inputWord))) -> {
            println("One or more letters of the input aren't valid.")
            game(allWordsSet, word)
            return
        }

        (!allWordsSet.contains(inputWord)) -> {
            println("The input word isn't included in my words list.")
            game(allWordsSet, word)
            return
        }
    }


    val trialFeedback = mutableListOf<String>()
    for (i in 0 until 5) {
        when {
            word[i] == inputWord[i] -> trialFeedback.add(
                "\u001B[48:5:10m${
                    inputWord[i].toString().uppercase()
                }\u001B[0m"
            )

            word.contains(inputWord[i]) -> trialFeedback.add(
                "\u001B[48:5:11m${
                    inputWord[i].toString().uppercase()
                }\u001B[0m"
            )

            else -> {
                wrongGuessSet.add(inputWord[i])
                trialFeedback.add("\u001B[48:5:7m${inputWord[i].toString().uppercase()}\u001B[0m")
            }
        }
    }

    for (i in trialFeedback) guessList.add(i)
    for (i in guessList.indices) {
        print(guessList[i])
        if ((i + 1) % 5 == 0) println()
    }
    println()
    var wrongWord = ""
    for (i in wrongGuessSet.toMutableList().sorted()) {
        wrongWord += i.uppercase()
    }
    print("\u001B[48:5:14m$wrongWord\u001B[0m")
    println()
    game(allWordsSet, word)
}
/*fun colorer(word: String, inputWord: String) {
    var trialFeedback = "_____"
    for (i in trialFeedback.indices) {
        when {
            word[i] == inputWord[i] -> trialFeedback = trialFeedback.replaceRange(i, i + 1, "\u001B[48;5;10m${inputWord[i].uppercase()}\u001B[0m")
            word.contains(inputWord[i]) -> trialFeedback = trialFeedback.replaceRange(i, i + 1, "\u001b[48;5;11m${inputWord[i].uppercase()}\u001B[0m")
            else -> {
                trialFeedback = trialFeedback.replaceRange(i, i + 1, "\u001b[48;5;7m${inputWord[i].uppercase()}\u001b[0m")
                wrongGuessSet.add(inputWord[i])

            }
        }
    }

    guessList += trialFeedback
}*/
