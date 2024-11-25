package everton.ost.pi_barbershop

import Usuario
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import everton.ost.pi_barbershop.databinding.ActivityCadastroBinding
import java.text.SimpleDateFormat
import java.util.*

class CadastroActivity : AppCompatActivity() {
    lateinit var binding: ActivityCadastroBinding
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageView = binding.imagemPerfil

        imageView.setOnClickListener {
            openGallery()
        }

        binding.btnCadastrar.setOnClickListener { view ->
            registerUser(view)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imageLauncher.launch(intent)
    }

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data
            imageView.setImageURI(imageUri)
        }
    }

    fun registerUser(view: View) {
        val perfil: String = when {
            binding.checkBoxCliente.isChecked -> "cliente"
            binding.checkBoxProfissional.isChecked -> "profissional"
            else -> "nenhum"
        }


        val email = binding.editEmail.text.toString().trim()
        val senha = binding.editSenha.text.toString().trim()
        val nome = binding.nomecompleto.text.toString().trim()
        val cpf = binding.editCpf.text.toString().trim()
        val dataNascimento = binding.editNascimento.text.toString().trim()
        val celular = binding.editCelular.text.toString().trim()
        val telefone = binding.editTelefone.text.toString().trim()
        val endereco = mapOf(
            "cep" to binding.editCep.text.toString().trim(),
            "rua" to binding.editRua.text.toString().trim(),
            "numero" to binding.editCasa.text.toString().trim(),
            "complemento" to binding.editComplemento.text.toString().trim(),
            "cidade" to binding.editCidade.text.toString().trim()
        )


        if (perfil == "nenhum") {
            Toast.makeText(this, "Selecione um tipo de cadastro!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isValidCPF(cpf)) {
            showSnackbar(view, "CPF inválido! Deve ter 11 dígitos.", Color.RED)
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showSnackbar(view, "E-mail inválido!", Color.RED)
            return
        }
        if (!isValidDate(dataNascimento)) {
            showSnackbar(view, "Data de nascimento inválida! Use o formato dd/MM/yyyy.", Color.RED)
            return
        }
        if (email.isEmpty() || senha.isEmpty() || nome.isEmpty() || cpf.isEmpty()) {
            showSnackbar(view, "Preencha todos os Campos", Color.RED)
            return
        }


        binding.progressBar.isVisible = true


        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { cadastro ->
                if (cadastro.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val usuario = Usuario(
                        nome = nome,
                        cpf = cpf,
                        dataNascimento = dataNascimento,
                        celular = celular,
                        telefone = telefone,
                        email = email,
                        endereco = endereco
                    )

                    uploadImageAndRegisterUser(usuario, view, perfil, userId)
                }
            }
            .addOnFailureListener { excecao ->
                binding.progressBar.isVisible = false
                handleAuthFailure(view, excecao)
            }
    }

    private fun uploadImageAndRegisterUser(usuario: Usuario, view: View, perfil: String, userId: String) {
        if (imageUri != null) {
            // Salva a imagem no caminho desejado
            val storageReference = FirebaseStorage.getInstance().reference.child("perfil/$userId.jpg")
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        saveUserData(usuario.copy(imagemUrl = uri.toString()), view, perfil)
                    }.addOnFailureListener { e ->
                        showSnackbar(view, "Erro ao obter URL da imagem: ${e.message}", Color.RED)
                        // Continua o cadastro sem a imagem
                        saveUserData(usuario, view, perfil)
                    }
                }
                .addOnFailureListener { e ->
                    showSnackbar(view, "Erro ao fazer upload da imagem: ${e.message}", Color.RED)
                    // Continua o cadastro sem a imagem
                    saveUserData(usuario, view, perfil)
                }
        } else {

            val imagemPadrao = "https://example.com/default_profile_image.png"
            saveUserData(usuario.copy(imagemUrl = imagemPadrao), view, perfil)
        }
    }

    private fun saveUserData(usuario: Usuario, view: View, perfil: String) {
        val collectionName = if (perfil == "cliente") "clientes" else "profissionais"
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            db.collection(collectionName).document(uid).set(usuario)
                .addOnSuccessListener {
                    binding.progressBar.isVisible = false
                    showSnackbar(view, "Cadastrado com Sucesso!", Color.GREEN)
                    clearFields()
                }
                .addOnFailureListener { e ->
                    binding.progressBar.isVisible = false
                    showSnackbar(view, "Erro ao salvar dados: ${e.message}", Color.RED)
                }
        }
    }

    fun isValidCPF(cpf: String): Boolean {
        return cpf.length == 11 && cpf.all { it.isDigit() }
    }

    private fun isValidDate(date: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun clearFields() {
        binding.editEmail.text.clear()
        binding.editSenha.text.clear()
        binding.nomecompleto.text.clear()
        binding.editCpf.text.clear()
        binding.editNascimento.text.clear()
        binding.editCelular.text.clear()
        binding.editTelefone.text.clear()
        binding.editCep.text.clear()
        binding.editRua.text.clear()
        binding.editCasa.text.clear()
        binding.editComplemento.text.clear()
        binding.editCidade.text.clear()
        imageView.setImageResource(R.drawable.pesquisar)
    }

    private fun showSnackbar(view: View, message: String, color: Int) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(color)
        snackbar.show()
    }

    private fun handleAuthFailure(view: View, excecao: Exception) {
        val mensagemErro = when (excecao) {
            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres!"
            is FirebaseAuthInvalidCredentialsException -> "Digite um email válido!"
            is FirebaseAuthUserCollisionException -> "Usuário já cadastrado!"
            is FirebaseNetworkException -> "Sem conexão com a Internet"
            else -> "Erro ao cadastrar usuário!"
        }
        showSnackbar(view, mensagemErro, Color.RED)
    }
}
