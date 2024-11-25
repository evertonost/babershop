package everton.ost.pi_barbershop.ui.consulta.geralProduto

import CodigoManager
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import everton.ost.pi_barbershop.R
import android.widget.ArrayAdapter
import java.text.NumberFormat
import java.util.Locale

class CadastroServicoFragment : Fragment(R.layout.fragment_cadastro_servico) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var codigoManager: CodigoManager
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var spinnerTempo: Spinner

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

        imageView = view.findViewById(R.id.imagem_Perfil)
        val descricaoServico = view.findViewById<EditText>(R.id.DescServico)
        val codigoServico = view.findViewById<EditText>(R.id.edit_codigo)
        val comissaoServico = view.findViewById<EditText>(R.id.editComissao)
        val precoServico = view.findViewById<EditText>(R.id.editPreco)
        val btnCadastrar = view.findViewById<AppCompatButton>(R.id.btn_excluir)


        spinnerTempo = view.findViewById(R.id.editTempo)
        setupSpinner()


        imageView.setOnClickListener {
            openGallery()
        }


        precoServico.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val preco = precoServico.text.toString().toDoubleOrNull() ?: 0.0
                val comissao = preco * 0.15
                comissaoServico.setText(String.format("%.2f", comissao))
            }
        }

        btnCadastrar.setOnClickListener {
            val descricao = descricaoServico.text.toString().trim()
            val tempoText = spinnerTempo.selectedItem.toString().trim()
            val tempo = tempoText.split(" ")[0].toIntOrNull() ?: 0
            val preco = precoServico.text.toString().toDoubleOrNull() ?: 0.0
            val precoFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(preco)

            if (descricao.isNotEmpty() && tempo > 0 && preco > 0) {

                codigoManager.obterProximoCodigo(
                    onSuccess = { novoCodigo ->
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            val servico = mapOf<String, Any>(
                                "descricao" to descricao,
                                "codigo" to novoCodigo,
                                "tempo" to tempo,
                                "comissao" to (preco * 0.15),
                                "preco" to preco
                            )
                            uploadImageAndRegisterService(userId, descricao, servico)
                        } else {
                            Toast.makeText(requireContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { exception ->
                        Toast.makeText(requireContext(), "Erro ao gerar código: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val tempos = ArrayList<String>()
        for (i in 1..4) {
            tempos.add("${i * 30} minutos")
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, tempos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTempo.adapter = adapter
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageLauncher.launch(intent)
    }

    private fun uploadImageAndRegisterService(userId: String, descricao: String, servico: Map<String, Any>) {
        if (imageUri != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("servicos/$userId/$descricao.jpg")
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val servicoComImagem = servico.toMutableMap()
                        servicoComImagem["imagemUrl"] = uri.toString()
                        saveServiceData(userId, descricao, servicoComImagem)
                    }.addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erro ao obter URL da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                        saveServiceData(userId, descricao, servico)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao fazer upload da imagem: ${e.message}", Toast.LENGTH_SHORT).show()
                    saveServiceData(userId, descricao, servico)
                }
        } else {
            saveServiceData(userId, descricao, servico)
        }
    }

    private fun saveServiceData(userId: String, descricao: String, servico: Map<String, Any>) {
        db.collection("profissionais").document(userId)
            .collection("servicos").document(descricao)
            .set(servico)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Serviço cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao cadastrar serviço: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        view?.findViewById<EditText>(R.id.DescServico)?.text?.clear()
        view?.findViewById<EditText>(R.id.edit_codigo)?.text?.clear()
        view?.findViewById<EditText>(R.id.editComissao)?.text?.clear()
        view?.findViewById<EditText>(R.id.editPreco)?.text?.clear()
        imageView.setImageResource(R.drawable.ic_pesquisa_image)
        imageUri = null
    }
}
