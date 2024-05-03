package com.example.entrega1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import java.util.Timer
import kotlin.concurrent.schedule

class GameActivity : AppCompatActivity() {

    private var category: String? = ""
    private lateinit var questionList: ArrayList<Question>
    private var totalQuestions = 0
    private var correctQuestions = 0
    private var currentQuestion = -1
    private var comodin = 0

    private lateinit var textScore: TextView
    private lateinit var textCategory: TextView
    private lateinit var questionView: TextView
    private lateinit var optionsArray: Array<Button>
    private lateinit var btnComodin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extras = intent.extras
        this.category = extras?.getString("category")
        loadQuestionsFromJSON(category)

        textCategory = findViewById(R.id.textCategory)
        textCategory.text = category?.uppercase()

        questionView = findViewById(R.id.question)
        textScore = findViewById(R.id.textScore)
        val button1 = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 = findViewById<Button>(R.id.button4)
        optionsArray = arrayOf(button1, button2, button3, button4)
        btnComodin = findViewById<Button>(R.id.btnComodin)

        setNextQuestion()

        for (i in optionsArray.indices){
            optionsArray[i].setOnClickListener{ answer(i, optionsArray[i])}
        }
        btnComodin.setOnClickListener { comodin().also { btnComodin.text = "X"
                                                        btnComodin.isClickable = false } }
        }

    private fun loadQuestionsFromJSON(category: String?) {
        val fileName = "$category.json"
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val text = String(buffer, Charsets.UTF_8)

        val jsonArray = JSONArray(text)
        totalQuestions = jsonArray.length()
        questionList = ArrayList<Question>(totalQuestions)
        for (i in 0..<totalQuestions) {
            val jsonObject = jsonArray.getJSONObject(i)
            val question = jsonObject.getString("question")
            val optionsArray = jsonObject.getJSONArray("options")
            val options = ArrayList<String>()
            for (j in 0..<optionsArray.length())
                options.add(optionsArray.getString(j))
            val correctAnswerIndex = jsonObject.getInt("correctAnswerIndex")
            val q = Question(question, options, correctAnswerIndex)
            questionList.add(q)
        }
    }

    private fun setNextQuestion(){

        fun reiniciarColores(b: Button){
            b.setBackgroundColor(getColor(R.color.original))
        }

        currentQuestion++
        if(currentQuestion<totalQuestions) {
            textScore.text = "${correctQuestions}/5"
            questionView.text = questionList[currentQuestion].getQuestion()
            for (i in optionsArray.indices){
                reiniciarColores(optionsArray[i])
                optionsArray[i].text = questionList[currentQuestion].getOptions()[i]
            }
        }else{
            showResults()
        }
    }

    private fun answer(ans: Int, b: Button){
        val correct = questionList[currentQuestion].getCorrectAnswerIndex()
        if(correct == ans){
            correctQuestions++
            b.setBackgroundColor(getColor(R.color.green))
        }else{
            b.setBackgroundColor(getColor(R.color.red))
            optionsArray[correct].setBackgroundColor(getColor(R.color.gray))
        }
        Timer().schedule(2000) {

            runOnUiThread{ setNextQuestion()}
        }
        //Thread.sleep(1500)
    }

    private fun comodin(){
        this.comodin = 1
        this.setNextQuestion()
    }

    private fun showResults(){
        val promedio:Double =(correctQuestions.toDouble()/(totalQuestions-comodin).toDouble())
        println("correctas: $correctQuestions")
        println("total: $totalQuestions")
        println("comodin: $comodin")
        println("promedio: $promedio")
        this.runOnUiThread(Runnable {
            Toast.makeText(this, "Puntaje obtenido: ${promedio*10}", Toast.LENGTH_SHORT).show()
        })
        this.finish()
    }

}