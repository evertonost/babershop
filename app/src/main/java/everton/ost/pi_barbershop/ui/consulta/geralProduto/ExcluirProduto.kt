class ExcluirProduto(private val produtoRepository: ConsultaProduto) {

    fun excluirProduto(descricao: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        produtoRepository.excluirProduto(descricao,
            onSuccess = { onSuccess() },
            onFailure = { exception -> onFailure(exception) }
        )
    }
}
