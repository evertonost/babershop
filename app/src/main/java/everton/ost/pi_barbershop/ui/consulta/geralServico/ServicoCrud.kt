package everton.ost.pi_barbershop.ui.consulta.geralServico

import Servico
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import everton.ost.pi_barbershop.ConsultaServico
import everton.ost.pi_barbershop.R
import everton.ost.pi_barbershop.data.ExcluirServico
import everton.ost.pi_barbershop.data.EditaServico

class ServicoConsulta : Fragment() {

    private lateinit var descServico: AutoCompleteTextView
    private lateinit var editCodigo: EditText
    private lateinit var editComissao: EditText
    private lateinit var editPreco: EditText
    private lateinit var editTempo: EditText
    private lateinit var imagemServico: ImageView
    private lateinit var buttonDelete: Button
    private lateinit var buttonSave: Button

    private val consultaServico = ConsultaServico()
    private val editaServico = EditaServico()
    private var adapterInitialized = false
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consulta_servico, container, false)
        initializeViews(view)
        setListeners()
        return view
    }

    private fun initializeViews(view: View) {
        // Inicializando as views
        descServico = view.findViewById(R.id.DescServico)
        editCodigo = view.findViewById(R.id.edit_codigo)
        editComissao = view.findViewById(R.id.editComissao)
        editPreco = view.findViewById(R.id.editPreco)
        editTempo = view.findViewById(R.id.editTempo)
        imagemServico = view.findViewById(R.id.imagemServico)
        buttonDelete = view.findViewById(R.id.btn_excluir)
        buttonSave = view.findViewById(R.id.btn_Salvar)


        imagemServico.setOnClickListener {
            openGallery()
        }
    }

    private fun setListeners() {

        descServico.setOnClickListener {
            if (!adapterInitialized) {
                carregarDescricoesDeServicos()
            } else {
                descServico.showDropDown()
            }
        }


        descServico.setOnItemClickListener { parent, _, position, _ ->
            val selectedDescription = parent.getItemAtPosition(position) as String
            carregarServicoPorDescricao(selectedDescription)
        }


        editCodigo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val codigo = editCodigo.text.toString()
                if (codigo.isNotEmpty()) {
                    carregarServicoPorCodigo(codigo)
                }
            }
        }


        buttonDelete.setOnClickListener {
            excluirServico()
        }


        buttonSave.setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun salvarAlteracoes() {
        val codigo = editCodigo.text.toString().trim()
        val descricao = descServico.text.toString().trim()
        val comissao = editComissao.text.toString().toDoubleOrNull() ?: 0.0
        val preco = editPreco.text.toString().toDoubleOrNull() ?: 0.0
        val tempo = editTempo.text.toString().toIntOrNull() ?: 0


        if (descricao.isEmpty() || codigo.isEmpty()) {
            showError("Preencha todos os campos obrigatórios!")
            return
        }


        val servicoEditado = Servico(
            descricao = descricao,
            codigo = codigo,
            comissao = comissao,
            preco = preco,
            tempo = tempo,
            imagemUrl = imageUri?.toString()
        )


        editaServico.editarServico(servicoEditado, onSuccess = {
            Toast.makeText(requireContext(), "Serviço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
        }, onFailure = { exception ->
            showError("Erro ao atualizar  ${exception.message}")
        })
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imagemServico.setImageURI(imageUri)
        }
    }

    private fun carregarDescricoesDeServicos() {
        consultaServico.obterServicos(onSuccess = { servicos ->
            val descricoes = servicos.map { it.descricao }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, descricoes)
            descServico.setAdapter(adapter)
            adapterInitialized = true
            descServico.showDropDown()
        }, onFailure = { exception ->
            showError("Erro ao carregar serviços: ${exception.message}")
        })
    }

    private fun carregarServicoPorDescricao(descricao: String) {
        consultaServico.obterServicos(onSuccess = { servicos ->
            val servico = servicos.find { it.descricao == descricao }
            servico?.let {

                editCodigo.setText(it.codigo)
                editComissao.setText(it.comissao.toString())
                editPreco.setText(it.preco.toString())
                editTempo.setText(it.tempo.toString())

                if (!it.imagemUrl.isNullOrEmpty()) {

                    Glide.with(requireContext())
                        .load(it.imagemUrl)
                        .into(imagemServico)
                } else {
                    imagemServico.setImageResource(R.drawable.ic_pesquisa_image)
                }
            }
        }, onFailure = { exception ->
            showError("Erro ao carregar o serviço: ${exception.message}")
        })
    }

    private fun carregarServicoPorCodigo(codigo: String) {
        consultaServico.obterServicos(onSuccess = { servicos ->
            val servico = servicos.find { it.codigo == codigo }
            servico?.let {

                editCodigo.setText(it.codigo)
                editComissao.setText(it.comissao.toString())
                editPreco.setText(it.preco.toString())
                editTempo.setText(it.tempo.toString())

                if (!it.imagemUrl.isNullOrEmpty()) {
                    imagemServico.setImageURI(Uri.parse(it.imagemUrl))
                } else {
                    imagemServico.setImageResource(R.drawable.ic_pesquisa_image)
                }
            }
        }, onFailure = { exception ->
            showError("Erro ao carregar o serviço: ${exception.message}")
        })
    }

    private fun excluirServico() {
        val descricaoServico = descServico.text.toString().trim()

        if (descricaoServico.isNotEmpty()) {
            val excluirServico = ExcluirServico()
            excluirServico.excluirServico(descricaoServico, onSuccess = {
                Toast.makeText(requireContext(), "Serviço excluído com sucesso!", Toast.LENGTH_SHORT).show()
                limparCampos()
            }, onFailure = { exception ->
                showError("Erro ao excluir serviço: ${exception.message}")
            })
        } else {
            showError("Selecione um serviço para excluir.")
        }
    }

    private fun limparCampos() {

        descServico.text.clear()
        editCodigo.text.clear()
        editComissao.text.clear()
        editPreco.text.clear()
        editTempo.text.clear()
        imagemServico.setImageResource(R.drawable.ic_pesquisa_image)
    }
}
