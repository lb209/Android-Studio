package com.example.kotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
class MainActivity : AppCompatActivity() {

    lateinit var text1: TextView
    lateinit var edit: EditText
    lateinit var edite: EditText
    lateinit var btn: Button
    lateinit var btn1: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text1 = findViewById(R.id.text1)
        edit = findViewById(R.id.edit)
        edite = findViewById(R.id.edite)
        btn = findViewById(R.id.btn)
        btn1=findViewById(R.id.btn1)

        btn1.setOnClickListener {
            click()
        }



       btn.setOnClickListener(object:View.OnClickListener{
           override fun onClick(e:View?){
               text1.text="I am a Clicked Event"
           }
       })

    }
    fun click() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }
}