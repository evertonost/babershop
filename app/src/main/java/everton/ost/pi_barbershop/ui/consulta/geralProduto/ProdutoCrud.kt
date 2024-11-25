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
import everton.ost.pi_barbershop.R

class ConsultaCadastroProduto : Fragment() {

    private lateinit var descProduto: AutoCompleteTextView
    private lateinit var editCodigo: EditText
    private lateinit var editPrecoCusto: EditText
    private lateinit var editPrecoVenda: EditText
    private lateinit var editFornecedor: EditText
    private lateinit var imageProduto: ImageView
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button

    private val produtoRepository = ConsultaProduto()
    private val editaProduto = EditaProduto(produtoRepository)
    private val excluiProduto = ExcluirProduto(produtoRepository)
    private var adapterInitialized = false
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consulta__produto, container, false)
        initializeViews(view)
        setListeners()
        return view
    }

    private fun initializeViews(view: View) {
        descProduto = view.findViewById(R.id.Desc_produto)
        editCodigo = view.findViewById(R.id.edit_codigo)
        editPrecoCusto = view.findViewById(R.id.edit_custo)
        editPrecoVenda = view.findViewById(R.id.edit_venda)
        editFornecedor = view.findViewById(R.id.edit_fornecedor)
        imageProduto = view.findViewById(R.id.imagemProduto)
        buttonSave = view.findViewById(R.id.btn_salvar)
        buttonDelete = view.findViewById(R.id.btn_excluir)

        imageProduto.setOnClickListener {
            openGallery()
        }
    }

    private fun setListeners() {
        descProduto.setOnClickListener {
            if (!adapterInitialized) {
                carregarDescricoesDeProdutos()
            } else {
                descProduto.showDropDown()
            }
        }

        descProduto.setOnItemClickListener { parent, _, position, _ ->
            val selectedDescription = parent.getItemAtPosition(position) as String
            carregarProdutoPorDescricao(selectedDescription)
        }

        editCodigo.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val codigo = editCodigo.text.toString()
                if (codigo.isNotEmpty()) {
                    carregarProdutoPorCodigo(codigo)
                }
            }
        }

        buttonSave.setOnClickListener {
            editarProduto()
        }

        buttonDelete.setOnClickListener {
            excluirProduto()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageProduto.setImageURI(imageUri)
        }
    }

    private fun carregarDescricoesDeProdutos() {
        produtoRepository.carregarDescricoesDeProdutos(
            onSuccess = { descricoes ->
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, descricoes)
                descProduto.setAdapter(adapter)
                adapterInitialized = true
                descProduto.showDropDown()
            },
            onFailure = { exception ->
                showError("Erro ao carregar produtos: ${exception.message}")
            }
        )
    }

    private fun carregarProdutoPorDescricao(descricao: String) {
        produtoRepository.carregarProdutoPorDescricao(
            descricao,
            onSuccess = { produto -> produto?.let { preencherCampos(it) } },
            onFailure = { exception ->
                showError("Erro ao carregar o produto: ${exception.message}")
            }
        )
    }

    private fun carregarProdutoPorCodigo(codigo: String) {
        produtoRepository.carregarProdutoPorCodigo(
            codigo,
            onSuccess = { produto -> produto?.let { preencherCampos(it) } },
            onFailure = { exception ->
                showError("Erro ao consultar produto: ${exception.message}")
            }
        )
    }

    private fun preencherCampos(produto: Produto) {
        editCodigo.setText(produto.codigo)
        editPrecoCusto.setText("R$ ${produto.precoCusto}")
        editPrecoVenda.setText("R$ ${produto.precoVenda}")
        editFornecedor.setText(produto.fornecedor)

        produto.imagemUrl?.let {
            Glide.with(this).load(it).into(imageProduto)
        } ?: imageProduto.setImageResource(R.drawable.ic_pesquisa_image)
    }

    private fun editarProduto() {
        val codigo = editCodigo.text.toString()
        val descricao = descProduto.text.toString()
        val precoCusto = editPrecoCusto.text.toString().replace("R$", "").trim()
        val precoVenda = editPrecoVenda.text.toString().replace("R$", "").trim()
        val fornecedor = editFornecedor.text.toString()

        editaProduto.editaProduto(
            descricao = descricao,
            codigo = codigo,
            precoCusto = precoCusto,
            precoVenda = precoVenda,
            fornecedor = fornecedor,
            imageUri = imageUri,
            onSuccess = {
                showSuccess("Produto atualizado com sucesso!")
                limparCampos()
            },
            onFailure = { exception ->
                showError("Erro ao atualizar produto: ${exception.message}")
            }
        )
    }

    private fun excluirProduto() {
        val descricao = descProduto.text.toString()

        if (descricao.isEmpty()) {
            showError("Código do produto deve ser preenchido.")
            return
        }

        excluiProduto.excluirProduto(descricao,
            onSuccess = {
                showSuccess("Produto excluído com sucesso!")
                limparCampos()
            },
            onFailure = { exception ->
                showError("Erro ao excluir produto: ${exception.message}")
            }
        )
    }

    private fun limparCampos() {
        editCodigo.text.clear()
        descProduto.text.clear()
        editPrecoCusto.text.clear()
        editPrecoVenda.text.clear()
        editFornecedor.text.clear()
        imageProduto.setImageResource(R.drawable.ic_pesquisa_image)
        imageUri = null
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
