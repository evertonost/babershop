import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage

class EditaProduto(private val produtoRepository: ConsultaProduto) {

    fun editaProduto(
        descricao: String,
        codigo: String,
        precoCusto: String,
        precoVenda: String,
        fornecedor: String,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (codigo.isEmpty() || descricao.isEmpty() || precoCusto.isEmpty() || precoVenda.isEmpty() || fornecedor.isEmpty()) {
            onFailure(Exception("Todos os campos devem ser preenchidos."))
            return
        }

        val produto = Produto(
            descricao = descricao,
            codigo = codigo,
            precoCusto = precoCusto,
            precoVenda = precoVenda,
            fornecedor = fornecedor,
            imagemUrl = null
        )

        produtoRepository.atualizarProduto(produto,
            onSuccess = {
                imageUri?.let { uri ->
                    uploadImage(descricao, uri, onSuccess, onFailure)
                } ?: run {
                    onSuccess()
                }
            },
            onFailure = { exception -> onFailure(exception) }
        )
    }

    private fun uploadImage(descricao: String, imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("produtos/$descricao.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    produtoRepository.atualizarImagemProduto(descricao, uri.toString(),
                        onSuccess = {
                            onSuccess()
                        },
                        onFailure = { exception -> onFailure(exception) }
                    )
                }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}
