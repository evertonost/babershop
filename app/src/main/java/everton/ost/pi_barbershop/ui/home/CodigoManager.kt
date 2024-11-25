import com.google.firebase.firestore.FirebaseFirestore

class CodigoManager(private val db: FirebaseFirestore) {

    private val codigoRef = db.collection("configuracoes").document("codigo")

    fun obterProximoCodigo(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.runTransaction { transaction ->
            val snapshot = transaction.get(codigoRef)
            val proximoCodigo = snapshot.getLong("proximoCodigo") ?: 0L
            transaction.update(codigoRef, "proximoCodigo", proximoCodigo + 1)
            return@runTransaction (proximoCodigo + 1).toString() // Retorna o próximo código como String
        }.addOnSuccessListener { novoCodigo ->
            onSuccess(novoCodigo)
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}
