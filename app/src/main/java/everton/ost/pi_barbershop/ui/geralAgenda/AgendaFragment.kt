package everton.ost.pi_barbershop.ui.geralAgenda

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import everton.ost.pi_barbershop.R

class AgendaFragment : Fragment() {

    private lateinit var profissionalSpinner: Spinner
    private lateinit var servicoSpinner: Spinner
    private lateinit var datePicker: DatePicker
    private lateinit var horariosLayout: GridLayout
    private lateinit var btAgendar: Button

    private var profissionalIdSelecionado: String? = null
    private var servicoSelecionado: String? = null
    private val horariosDisponiveis = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
        "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agenda, container, false)

        profissionalSpinner = view.findViewById(R.id.profissionalSpinner)
        servicoSpinner = view.findViewById(R.id.servicoSpinner)
        datePicker = view.findViewById(R.id.datePicker)
        horariosLayout = view.findViewById(R.id.horariosLayout)
        btAgendar = view.findViewById(R.id.btAgendar)


        datePicker.visibility = View.GONE

        carregarProfissionais()

        profissionalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                profissionalIdSelecionado = (parent?.getItemAtPosition(position) as? String) ?: ""
                carregarServicos(profissionalIdSelecionado!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        servicoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                servicoSelecionado = (parent?.getItemAtPosition(position) as? String) ?: ""


                if (profissionalIdSelecionado != null && servicoSelecionado != null) {

                    datePicker.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        datePicker.setOnDateChangedListener { _, year, month, dayOfMonth ->
            if (profissionalIdSelecionado != null && servicoSelecionado != null) {
                carregarHorariosAgendados(year, month, dayOfMonth)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Por favor, selecione o profissional e o serviço primeiro!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btAgendar.setOnClickListener {
            salvarHorarios()
        }

        return view
    }

    private fun carregarProfissionais() {
        val db = FirebaseFirestore.getInstance()
        db.collection("profissionais").get().addOnSuccessListener { result ->
            val profissionaisNomes = mutableListOf<String>()
            val profissionaisIds = mutableListOf<String>()


            profissionaisNomes.add("Selecione o profissional")
            profissionaisIds.add("")

            for (document in result) {
                val nome =
                    document.getString("nome") ?: "Sem nome"
                profissionaisNomes.add(nome)
                profissionaisIds.add(document.id)
            }


            val adapter = object : ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                profissionaisNomes
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view as TextView
                    textView.setTextColor(Color.WHITE) // Altera a cor do texto para branco
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view as TextView
                    textView.setTextColor(Color.WHITE)
                    view.setBackgroundColor(Color.BLACK)
                    return view
                }
            }

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            profissionalSpinner.adapter = adapter


            profissionalSpinner.setSelection(0)


            profissionalSpinner.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.tBlack
                )
            )


            profissionalSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if (position > 0) {
                            profissionalIdSelecionado = profissionaisIds[position]

                            carregarServicos(profissionalIdSelecionado!!)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    private fun carregarServicos(profissionalId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("profissionais").document(profissionalId).collection("servicos").get()
            .addOnSuccessListener { result ->
                val servicos = mutableListOf<String>()


                servicos.add("Selecione o serviço")

                for (document in result) {
                    servicos.add(
                        document.getString("descricao") ?: "Sem nome"
                    )
                }


                val adapter = object : ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    servicos
                ) {
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getView(position, convertView, parent)
                        val textView = view as TextView
                        textView.setTextColor(Color.WHITE)
                        view.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.tBlack
                            )
                        )
                        return view
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        val textView = view as TextView
                        textView.setTextColor(Color.WHITE)
                        view.setBackgroundColor(Color.BLACK)
                        return view
                    }
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                servicoSpinner.adapter = adapter
            }
    }

    private var horarioSelecionadoAnterior: Button? =
        null



    private fun carregarHorariosAgendados(year: Int, month: Int, dayOfMonth: Int) {
        horariosLayout.removeAllViews()
        val diaSelecionado = "${dayOfMonth}_${month + 1}_$year"

        if (profissionalIdSelecionado != null) {
            val db = FirebaseFirestore.getInstance()
            val horariosRef = db.collection("profissionais")
                .document(profissionalIdSelecionado!!)
                .collection("agenda")
                .document(diaSelecionado)

            horariosRef.get().addOnSuccessListener { document ->
                val horariosAgendados = document.data ?: emptyMap<String, Boolean>()

                for (horario in horariosDisponiveis) {

                    val botaoHorario = Button(requireContext())
                    botaoHorario.text = horario


                    if (horariosAgendados[horario] == true) {
                        botaoHorario.isEnabled = true
                        botaoHorario.setBackgroundResource(R.drawable.rounded_button_disponivel)
                    } else {
                        botaoHorario.isEnabled = false
                        botaoHorario.setBackgroundResource(R.drawable.rounded_button_indisponivel)
                    }

                    // Define o comportamento do botão ao ser clicado
                    botaoHorario.setOnClickListener {
                        horarioSelecionadoAnterior?.apply {
                            setBackgroundResource(R.drawable.rounded_button_disponivel)
                            isSelected = false
                        }

                        if (botaoHorario.isSelected) {
                            botaoHorario.setBackgroundResource(R.drawable.rounded_button_disponivel)
                            botaoHorario.isSelected = false
                        } else {
                            botaoHorario.setBackgroundResource(R.drawable.rounded_button_selecionado)
                            botaoHorario.isSelected = true
                        }

                        horarioSelecionadoAnterior = botaoHorario
                        Toast.makeText(requireContext(), "Horário $horario selecionado", Toast.LENGTH_SHORT).show()
                    }


                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.setMargins(10, 10, 10, 10)
                    layoutParams.width = 220
                    layoutParams.height = 100
                    botaoHorario.layoutParams = layoutParams


                    horariosLayout.addView(botaoHorario)
                }
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao carregar horários: $e", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Nenhum profissional selecionado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun salvarHorarios() {
        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        if (usuarioAtual != null) {
            val db = FirebaseFirestore.getInstance()
            val userId = usuarioAtual.uid
            db.collection("clientes").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val nome = document.getString("nome") ?: "Cliente Anônimo"
                    val diaSelecionado =
                        "${datePicker.dayOfMonth}_${datePicker.month + 1}_${datePicker.year}"
                    val horarioSelecionado = horarioSelecionadoAnterior?.text.toString()

                    if (horarioSelecionado.isNotEmpty() && profissionalIdSelecionado != null && servicoSelecionado != null) {
                        val nomeProfissional = profissionalSpinner.selectedItem.toString()

                        // Salvar agendamento
                        val servicoAgendado = hashMapOf(
                            "profissionalNome" to nomeProfissional,
                            "servico" to servicoSelecionado,
                            "data" to diaSelecionado,
                            "horario" to horarioSelecionado,
                            "nome" to nome
                        )


                        db.collection("servicosAgendados").add(servicoAgendado)
                            .addOnSuccessListener {

                                val horariosRef = db.collection("profissionais")
                                    .document(profissionalIdSelecionado!!)
                                    .collection("agenda")
                                    .document(diaSelecionado)


                                horariosRef.update(horarioSelecionado, false).addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Serviço agendado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Erro ao atualizar horário: $e",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao salvar agendamento: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
    }
}

