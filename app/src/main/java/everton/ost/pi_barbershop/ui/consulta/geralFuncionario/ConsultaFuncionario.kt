package everton.ost.pi_barbershop.ui.consulta.geralFuncionario

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class consultaFuncionario(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()



    fun fetchUserData(
        profileImageView: ImageView,
        editTextNome: TextView,
        editTextCpf: TextView,
        editTextEmail: TextView,
        editTextDataNascimento: TextView,
        editTextCelular: TextView,
        editTextTelefone: TextView,
        editTextCep: TextView,
        editTextCidade: TextView,
        editTextRua: TextView,
        editTextNumero: TextView,
        editTextComplemento: TextView

    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {

            db.collection("profissionais").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nome = document.getString("nome") ?: "Nome não encontrado"
                        val cpf = document.getString("cpf") ?: "CPF NÃO ENCONTRADO"
                        val email = document.getString("email") ?: "Email NÃO ENCONTRADO"
                        val dataNascimento = document.getString("dataNascimento") ?: "Data de Nascimento NÃO ENCONTRADA"
                        val telefone = document.getString("telefone") ?: "Telefone NÃO ENCONTRADO"
                        val celular = document.getString("celular") ?: "Celular NÃO ENCONTRADO"


                        val endereco = document.get("endereco") as? Map<String, String>
                        if (endereco != null) {
                            val cep = endereco["cep"] ?: "CEP NÃO ENCONTRADO"
                            val cidade = endereco["cidade"] ?: "Cidade NÃO ENCONTRADA"
                            val rua = endereco["rua"] ?: "Rua NÃO ENCONTRADA"
                            val numero = endereco["numero"] ?: "Número NÃO ENCONTRADO"
                            val complemento = endereco["complemento"] ?: "Complemento NÃO ENCONTRADO"

                            editTextNome.text = nome
                            editTextCpf.text = cpf
                            editTextEmail.text = email
                            editTextDataNascimento.text = dataNascimento
                            editTextCelular.text = celular
                            editTextTelefone.text = telefone
                            editTextCep.text = cep
                            editTextCidade.text = cidade
                            editTextRua.text = rua
                            editTextNumero.text = numero
                            editTextComplemento.text = complemento
                        } else {

                            editTextNome.text = nome
                            editTextCpf.text = cpf
                            editTextEmail.text = email
                            editTextDataNascimento.text = dataNascimento
                            editTextCelular.text = celular
                            editTextTelefone.text = telefone
                            editTextCep.text = "CEP NÃO ENCONTRADO"
                            editTextCidade.text = "Cidade NÃO ENCONTRADA"
                            editTextRua.text = "Rua NÃO ENCONTRADA"
                            editTextNumero.text = "Número NÃO ENCONTRADO"
                            editTextComplemento.text = "Complemento NÃO ENCONTRADO"
                        }
                    } else {
                        editTextNome.text = "Usuário não encontrado"
                        editTextCpf.text = "Usuário não encontrado"
                        editTextEmail.text = "Usuário não encontrado"
                        editTextDataNascimento.text = "Usuário não encontrado"
                        editTextCelular.text = "Usuário não encontrado"
                        editTextTelefone.text = "Usuário não encontrado"
                        editTextCep.text = "Usuário não encontrado"
                        editTextCidade.text = "Usuário não encontrado"
                        editTextRua.text = "Usuário não encontrado"
                        editTextNumero.text = "Usuário não encontrado"
                        editTextComplemento.text = "Usuário não encontrado"
                    }
                }
                .addOnFailureListener { exception ->
                    editTextNome.text = "Erro ao carregar dados: ${exception.message}"
                    editTextCpf.text = "Erro ao carregar dados: ${exception.message}"
                    editTextEmail.text = "Erro ao carregar dados: ${exception.message}"
                    editTextDataNascimento.text = "Erro ao carregar dados: ${exception.message}"
                    editTextCelular.text = "Erro ao carregar dados: ${exception.message}"
                    editTextTelefone.text = "Erro ao carregar dados: ${exception.message}"
                    editTextCep.text = "Erro ao carregar dados: ${exception.message}"
                    editTextCidade.text = "Erro ao carregar dados: ${exception.message}"
                    editTextRua.text = "Erro ao carregar dados: ${exception.message}"
                    editTextNumero.text = "Erro ao carregar dados: ${exception.message}"
                    editTextComplemento.text = "Erro ao carregar dados: ${exception.message}"
                }


            val storageRef = FirebaseStorage.getInstance().reference.child("perfil/$userId.jpg")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(profileImageView)


                profileImageView.tag = uri
            }.addOnFailureListener {

                Toast.makeText(context, "Erro ao carregar imagem de perfil.", Toast.LENGTH_SHORT).show()
            }


        }
    }
}
