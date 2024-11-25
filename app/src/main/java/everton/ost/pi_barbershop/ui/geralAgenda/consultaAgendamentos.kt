package everton.ost.pi_barbershop.ui.geralAgenda

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import everton.ost.pi_barbershop.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ConsultaAgendamentosFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var linearLayoutAgendamentos: LinearLayout
    private var usuarioNome: String? = null
    private var selectedDate: String? = null
    private val dateFormat = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_consulta_agendamentos, container, false)


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        linearLayoutAgendamentos = rootView.findViewById(R.id.linearLayoutAgendamentos)


        val buttonSelectDate: Button = rootView.findViewById(R.id.textViewData)
        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }


        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            checkUserTypeAndFetchAgendamentos(userId)
        } else {
            addErrorMessage("Usuário não logado.")
        }

        return rootView
    }

    private fun checkUserTypeAndFetchAgendamentos(userId: String) {

        db.collection("clientes").document(userId).get()
            .addOnSuccessListener { clientDocument ->
                if (clientDocument.exists()) {

                    usuarioNome = clientDocument.getString("nome")
                    Log.d("ConsultaAgendamentosFragment", "Nome do cliente: $usuarioNome")
                } else {

                    db.collection("profissionais").document(userId).get()
                        .addOnSuccessListener { professionalDocument ->
                            if (professionalDocument.exists()) {

                                usuarioNome = professionalDocument.getString("nome")
                                Log.d("ConsultaAgendamentosFragment", "Nome do profissional: $usuarioNome")
                            } else {
                                addErrorMessage("Usuário não encontrado como cliente nem profissional")
                            }
                        }
                        .addOnFailureListener {
                            addErrorMessage("Erro ao buscar dados de profissional")
                        }
                }
            }
            .addOnFailureListener {
                addErrorMessage("Erro ao buscar dados de cliente")
            }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->

                calendar.set(year, month, dayOfMonth)
                selectedDate = dateFormat.format(calendar.time)


                Log.d("ConsultaAgendamentosFragment", "Data selecionada: $selectedDate")

                // Chama o método para buscar os agendamentos com base na data selecionada
                fetchAgendamentosPorNome(usuarioNome ?: "", selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun fetchAgendamentosPorNome(nomeUsuario: String, dataSelecionada: String?) {
        if (dataSelecionada == null) {
            addErrorMessage("Por favor, selecione uma data.")
            return
        }


        db.collection("servicosAgendados")
            .whereEqualTo("data", dataSelecionada)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot: QuerySnapshot? = task.result
                    linearLayoutAgendamentos.removeAllViews()
                    if (querySnapshot != null && querySnapshot.size() > 0) {
                        for (document in querySnapshot.documents) {

                            val clienteNome = document.getString("nome")
                            val profissionalNome = document.getString("profissionalNome")


                            if (clienteNome == nomeUsuario || profissionalNome == nomeUsuario) {
                                val data = document.getString("data") ?: "Sem data"
                                val horario = document.getString("horario") ?: "Sem horário"
                                val servico = document.getString("servico") ?: "Sem serviço"
                                val cliente = clienteNome ?: "Cliente não disponível"
                                val profissional = profissionalNome ?: "Profissional não disponível"

                                addAgendamentoToLayout(horario, cliente, data, servico, profissional)
                            }
                        }
                    } else {
                        addNoAgendamentosMessage()
                    }
                } else {
                    addErrorMessage("Erro ao carregar agendamentos")
                }
            }
    }

    private fun addAgendamentoToLayout(horario: String, cliente: String, data: String, servico: String, profissionalNome: String) {

        val cardView = CardView(requireContext())


        cardView.setCardBackgroundColor(resources.getColor(R.color.tBlack, null))

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        cardView.layoutParams = layoutParams


        val textView = TextView(requireContext())
        textView.text = "Data: $data\nHorário: $horario\nCliente: $cliente\nServiço: $servico\nProfissional: $profissionalNome"
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)


        textView.setTextColor(resources.getColor(R.color.white, null))


        cardView.addView(textView)


        linearLayoutAgendamentos.addView(cardView)
    }



    private fun addNoAgendamentosMessage() {
        val noAgendamentosTextView = TextView(requireContext())
        noAgendamentosTextView.text = "Não há agendamentos para a data selecionada."
        noAgendamentosTextView.textSize = 16f
        linearLayoutAgendamentos.addView(noAgendamentosTextView)
    }

    private fun addErrorMessage(message: String) {
        val errorMessageTextView = TextView(requireContext())
        errorMessageTextView.text = message
        errorMessageTextView.textSize = 16f
        linearLayoutAgendamentos.addView(errorMessageTextView)
    }
}
