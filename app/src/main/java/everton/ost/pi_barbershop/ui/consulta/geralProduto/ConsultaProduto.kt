import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ConsultaProduto {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun carregarDescricoesDeProdutos(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("produtos")
            .get()
            .addOnSuccessListener { documents ->
                val descricoes = documents.mapNotNull { it.getString("descricao") }
                onSuccess(descricoes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun carregarProdutoPorDescricao(descricao: String, onSuccess: (Produto?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("produtos")
            .whereEqualTo("descricao", descricao)
            .get()
            .addOnSuccessListener { documents ->
                val produto = documents.firstOrNull()?.toProduto()
                onSuccess(produto)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun carregarProdutoPorCodigo(codigo: String, onSuccess: (Produto?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("produtos")
            .whereEqualTo("descricao", codigo)
            .get()
            .addOnSuccessListener { documents ->
                val produto = documents.firstOrNull()?.toProduto()
                onSuccess(produto)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun atualizarProduto(produto: Produto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val produtoId = produto.descricao
        db.collection("produtos").document(produtoId)
            .set(produto)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun atualizarImagemProduto(codigo: String, imagemUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("produtos").document(codigo)
            .update("imagemUrl", imagemUrl)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun DocumentSnapshot.toProduto(): Produto {
        return Produto(
            descricao = getString("descricao") ?: "",

            codigo = getString("codigo") ?: "",
            precoCusto = getString("precoCusto") ?: "0.00",
            precoVenda = getString("precoVenda") ?: "0.00",
            fornecedor = getString("fornecedor") ?: "",
            imagemUrl = getString("imagemUrl")
        )
    }
    fun excluirProduto(descricao: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("produtos").document(descricao)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
