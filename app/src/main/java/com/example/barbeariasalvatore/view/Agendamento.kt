package com.example.barbeariasalvatore.view

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.example.barbeariasalvatore.databinding.ActivityAgendamentoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    @RequiresApi(Build.VERSION_CODES.N)
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora:String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        val nome = intent.extras?.getString("nome").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->

            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10){
                dia = "0$dayOfMonth"
            }
            if (monthOfYear < 10){
                mes = "" + (monthOfYear+1)
            }else{
                mes = (monthOfYear +1).toString()
            }

            data =  "$dia / $mes / $year"
        }

        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val minuto: String

            if(minute < 10){
                minuto = "0$minute"
            }else{
                minuto = minute.toString()
            }

            hora = "$hourOfDay:$minuto" //19:00
        }
        binding.timePicker.setIs24HourView(true)

        binding.btAgendar.setOnClickListener{

            val barbeiro1 = binding.barbeiro1

            when {
                hora.isEmpty() -> {
                    mensagem(it, "Preencha o Horario", "#FF0000")

                }

                hora < "8:00" && hora > "19:00" -> {
                    mensagem(
                        it,
                        "Barbearia Salvatore está fechado - Horario de atendimento das 08:00 as 19:00!",
                        "#FF0000"
                    )

                }

                data.isEmpty() -> {
                    mensagem(it, "Coloque uma data!", "#FF0000")

                }

                barbeiro1.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it,nome,"Eduardo Salvatore",data,hora)

                }

                else -> {
                    mensagem(it, "Escolha um Barbeiro!","#FF0000")

                }
            }
        }
    }
    private fun mensagem(view: View, mensagem: String, cor: String){
        val snackbar = Snackbar.make(view,mensagem,Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun salvarAgendamento(view: View, cliente: String, barbeiro: String, data: String, hora:String){

        val db = FirebaseFirestore.getInstance()

        val dadosUsuarios = hashMapOf(
            "cliente" to cliente,
            "barbeiro" to barbeiro,
            "data" to data,
            "hora" to hora,
        )

        db.collection("Agendamento").document(cliente).set(dadosUsuarios).addOnCompleteListener {
            mensagem(view, "Agendamento realizado com sucesso!", "#FF03DAC5")
        }.addOnFailureListener(){
            mensagem(view, "Erro no Servidor", "#FF0000")
        }
    }
}