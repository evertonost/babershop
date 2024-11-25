package everton.ost.pi_barbershop.ui.edita.geral

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditaCliente(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun updateUserData(
        view: View,
        nome: String,
        cpf: String,
        email: String,
        dataNascimento: String,
        celular: String,
        telefone: String,
        cep: String,
        cidade: String,
        rua: String,
        numero: String,
        complemento: String,
        imageUri: Uri?
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("clientes").document(userId)

            val userUpdates: Map<String, Any> = mapOf(
                "nome" to nome,
                "cpf" to cpf,
                "email" to email,
                "dataNascimento" to dataNascimento,
                "celular" to celular,
                "telefone" to telefone,
                "endereco" to mapOf(
                    "cep" to cep,
                    "cidade" to cidade,
                    "rua" to rua,
                    "numero" to numero,
                    "complemento" to complemento
                )
            )


            userRef.update(userUpdates)
                .addOnSuccessListener {
                    val snackbar = Snackbar.make(view, "Dados atualizados com sucesso!", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.GREEN)
                    snackbar.show()

                }
                .addOnFailureListener {
                    val snackbar = Snackbar.make(view, "Erro ao atualizar os dados.", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }


            if (imageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference.child("perfil/$userId.jpg")
                storageRef.putFile(imageUri)
                    .addOnSuccessListener {
                        val snackbar = Snackbar.make(view, "Foto Atualizada!", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                    }
                    .addOnFailureListener {
                        val snackbar = Snackbar.make(view, "Erro ao atualizar a imagem.", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.GREEN)
                        snackbar.show()
                    }
            }
        }
    }
}
