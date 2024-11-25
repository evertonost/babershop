package everton.ost.pi_barbershop.ui.geralAgenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import everton.ost.pi_barbershop.R
import java.util.*

class ConsultarAgendaHorariosFragment : Fragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var horariosLayout: GridLayout
    private lateinit var btSalvarHorarios: Button

    private val horariosDisponiveis = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cadastro_horario, container, false)


        datePicker = view.findViewById(R.id.datePicker)
        horariosLayout = view.findViewById(R.id.horariosLayout)
        btSalvarHorarios = view.findViewById(R.id.btSalvarHorarios)


        datePicker.init(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            datePicker.visibility = View.GONE

            carregarHorariosAgendados(year, month, dayOfMonth)
        }


        btSalvarHorarios.setOnClickListener {
            salvarHorarios()
        }

        return view
    }


    private fun carregarHorariosAgendados(year: Int, month: Int, dayOfMonth: Int) {

        horariosLayout.removeAllViews()


        val diaSelecionado = "${dayOfMonth}_${month + 1}_$year"


        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {

            val db = FirebaseFirestore.getInstance()
            val horariosRef = db.collection("profissionais")
                .document(userId)
                .collection("agenda")
                .document(diaSelecionado)


            horariosRef.get().addOnSuccessListener { document ->
                val horariosAgendados = document.data ?: emptyMap<String, Boolean>()


                for (horario in horariosDisponiveis) {
                    val checkBox = CheckBox(requireContext())
                    checkBox.text = horario
                    checkBox.setTextColor(resources.getColor(android.R.color.white))
                    checkBox.setPadding(40, 40, 40, 40)
                    checkBox.setButtonDrawable(R.drawable.checkbox_selector)


                    checkBox.isChecked = horariosAgendados[horario] == true

                    horariosLayout.addView(checkBox)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao carregar horários: $e", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun salvarHorarios() {
        val horariosSelecionados = mutableMapOf<String, Boolean>()


        for (i in 0 until horariosLayout.childCount) {
            val checkBox = horariosLayout.getChildAt(i) as CheckBox
            val horario = checkBox.text.toString()
            horariosSelecionados[horario] = checkBox.isChecked
        }

        if (horariosSelecionados.isNotEmpty()) {

            val diaSelecionado = "${datePicker.dayOfMonth}_${datePicker.month + 1}_${datePicker.year}"


            val db = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {

                val horariosRef = db.collection("profissionais")
                    .document(userId)
                    .collection("agenda")
                    .document(diaSelecionado)


                horariosRef.set(horariosSelecionados)
                    .addOnSuccessListener {

                        Toast.makeText(requireContext(), "Horários salvos com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->

                        Toast.makeText(requireContext(), "Erro ao salvar os horários: $e", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Nenhum horário selecionado!", Toast.LENGTH_SHORT).show()
        }
    }
}
