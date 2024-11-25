package everton.ost.pi_barbershop.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class ExcluirServico {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    fun excluirServico(descricaoServico: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {

            db.collection("profissionais")
                .document(userId)
                .collection("servicos")
                .whereEqualTo("descricao", descricaoServico)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {

                        val serviceDoc = querySnapshot.documents.first()
                        val serviceId = serviceDoc.id // Obtém o ID do documento


                        db.collection("profissionais")
                            .document(userId)
                            .collection("servicos")
                            .document(serviceId)
                            .delete()
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    } else {
                        onFailure(Exception("Serviço não encontrado com a descrição fornecida")) // Caso não encontre o serviço
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("Usuário não autenticado"))
        }
    }
}
