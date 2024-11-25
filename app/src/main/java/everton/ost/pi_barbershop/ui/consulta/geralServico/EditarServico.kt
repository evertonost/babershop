package everton.ost.pi_barbershop.data

import Servico
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth


class EditaServico {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun editarServico(servico: Servico, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val servicosRef = db.collection("profissionais")
                .document(userId)
                .collection("servicos")
                .document(servico.descricao)


            val updatedServico = mapOf(
                "descricao" to servico.descricao,
                "comissao" to servico.comissao,
                "preco" to servico.preco,
                "tempo" to servico.tempo,
                "imagemUrl" to servico.imagemUrl
            )


            servicosRef.update(updatedServico)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("Usuário não autenticado"))
        }
    }
}
