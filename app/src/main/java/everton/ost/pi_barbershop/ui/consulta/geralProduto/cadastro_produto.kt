package everton.ost.pi_barbershop.ui.consulta.geralProduto

import CodigoManager
import android.companion.CompanionDeviceManager.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.appcompat.widget.AppCompatButton
import everton.ost.pi_barbershop.R
class CadastroProdutoFragment : Fragment(R.layout.fragment_cadastro_produto) {

    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private lateinit var codigoManager: CodigoManager
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codigoManager = CodigoManager(db)

        imageView = view.findViewById(R.id.imagemProduto)
        val descricaoProduto = view.findViewById<EditText>(R.id.Desc_produto)
        val fornecedorProduto = view.findViewById<EditText>(R.id.edit_fornecedor)
        val precoCusto = view.findViewById<EditText>(R.id.edit_custo)
        val precoVenda = view.findViewById<EditText>(R.id.edit_venda)
        val btnCadastrar = view.findViewById<AppCompatButton>(R.id.btn_Cadastrar)

        imageView.setOnClickListener {
            openGallery()
        }

        btnCadastrar.setOnClickListener {
            val descricao = descricaoProduto.text.toString().trim()
            val fornecedor = fornecedorProduto.text.toString().trim()
            val custo = precoCusto.text.toString().trim()
            val venda = precoVenda.text.toString().trim()

            if (descricao.isNotEmpty() && fornecedor.isNotEmpty() && custo.isNotEmpty() && venda.isNotEmpty()) {

                codigoManager.obterProximoCodigo(
                    onSuccess = { codigo ->
                        val produto = hashMapOf(
                            "descricao" to descricao,
                            "codigo" to codigo,
                            "fornecedor" to fornecedor,
                            "precoCusto" to custo,
                            "precoVenda" to venda
                        )
                        uploadImageAndRegisterProduct(descricao, produto)
                    },
                    onFailure = { exception ->
                        Toast.makeText(requireContext(), "Erro ao obter próximo código: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageLauncher.launch(intent)
    }

    private fun uploadImageAndRegisterProduct(descricao: String, produto: HashMap<String, String>) {
        if (imageUri != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("produtos/$descricao.jpg")
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        produto["imagemUrl"] = uri.toString()
                        saveProductData(descricao, produto)
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erro ao obter URL da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                        saveProductData(descricao, produto)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao fazer upload da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                    saveProductData(descricao, produto)
                }
        } else {
            saveProductData(descricao, produto)
        }
    }

    private fun saveProductData(descricao: String, produto: HashMap<String, String>) {
        db.collection("produtos").document(descricao).set(produto)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao cadastrar produto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        view?.findViewById<EditText>(R.id.Desc_produto)?.text?.clear()
        view?.findViewById<EditText>(R.id.edit_fornecedor)?.text?.clear()
        view?.findViewById<EditText>(R.id.edit_custo)?.text?.clear()
        view?.findViewById<EditText>(R.id.edit_venda)?.text?.clear()
        imageView.setImageResource(R.drawable.ic_pesquisa_image)
        imageUri = null
    }
}
