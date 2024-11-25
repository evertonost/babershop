package everton.ost.pi_barbershop

import Servico
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class ConsultaServico {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()



    fun obterServicos(onSuccess: (List<Servico>) -> Unit, onFailure: (Exception) -> Unit): ListenerRegistration? {
        val userId = auth.currentUser?.uid
        return if (userId != null) {

            db.collection("profissionais").document(userId)
                .collection("servicos")
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {

                        onFailure(exception)
                        return@addSnapshotListener
                    }

                    if (querySnapshot != null) {

                        val servicos = querySnapshot.documents.map { document ->
                            val data = document.data ?: emptyMap<String, Any>()

                            Servico(
                                codigo = data["codigo"] as? String ?: "",
                                comissao = (data["comissao"] as? Number)?.toDouble() ?: 0.0,
                                descricao = data["descricao"] as? String ?: "",
                                imagemUrl = data["imagemUrl"] as? String ?: "",
                                preco = (data["preco"] as? Number)?.toDouble() ?: 0.0,
                                tempo = (data["tempo"] as? Long)?.toInt() ?: 0
                            )
                        }

                        onSuccess(servicos)
                    }
                }
        } else {
            onFailure(Exception("Usuário não autenticado"))
            null
        }
    }
}
