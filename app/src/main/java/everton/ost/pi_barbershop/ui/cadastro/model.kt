data class Usuario(
    val nome: String,
    val cpf: String,
    val dataNascimento: String,
    val celular: String,
    val telefone: String,
    val email: String,
    val endereco: Map<String, String>,
    val imagemUrl: String? = null
)
