package everton.ost.pi_barbershop.ui.consulta.geralCliente

import ExcluirCliente
import ExcluirFuncionario
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import everton.ost.pi_barbershop.R
import everton.ost.pi_barbershop.ui.consulta.geralFuncionario.consultaFuncionario
import everton.ost.pi_barbershop.ui.edita.geral.EditaCliente
import everton.ost.pi_barbershop.ui.edita.geral.EditaFuncionario


class Consulta_cadastro_funcionarioFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var editTextNome: TextView
    private lateinit var editTextCpf: TextView
    private lateinit var editTextEmail: TextView
    private lateinit var editTextDataNascimento: TextView
    private lateinit var editTextCelular: TextView
    private lateinit var editTextTelefone: TextView
    private lateinit var editTextCep: TextView
    private lateinit var editTextCidade: TextView
    private lateinit var editTextRua: TextView
    private lateinit var editTextNumero: TextView
    private lateinit var editTextComplemento: TextView
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private lateinit var consultaFuncionario: consultaFuncionario
    private lateinit var editaFuncionario: EditaFuncionario
    private lateinit var excluirFuncionario: ExcluirFuncionario
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consulta_cadastro_funcionario, container, false)


        profileImageView = view.findViewById(R.id.imagem_Perfil)
        editTextNome = view.findViewById(R.id.nomecompleto)
        editTextCpf = view.findViewById(R.id.edit_cpf)
        editTextEmail = view.findViewById(R.id.edit_email)
        editTextDataNascimento = view.findViewById(R.id.edit_nascimento)
        editTextCelular = view.findViewById(R.id.edit_celular)
        editTextTelefone = view.findViewById(R.id.edit_telefone)
        editTextCep = view.findViewById(R.id.edit_cep)
        editTextCidade = view.findViewById(R.id.edit_cidade)
        editTextRua = view.findViewById(R.id.edit_rua)
        editTextNumero = view.findViewById(R.id.edit_casa)
        editTextComplemento = view.findViewById(R.id.edit_complemento)
        btnSalvar = view.findViewById(R.id.btn_Salvar)
        btnExcluir = view.findViewById(R.id.btn_excluir)

        consultaFuncionario = consultaFuncionario(requireContext())
        editaFuncionario = EditaFuncionario(requireContext())
        excluirFuncionario = ExcluirFuncionario(requireContext())


        consultaFuncionario.fetchUserData(
            profileImageView,
            editTextNome,
            editTextCpf,
            editTextEmail,
            editTextDataNascimento,
            editTextCelular,
            editTextTelefone,
            editTextCep,
            editTextCidade,
            editTextRua,
            editTextNumero,
            editTextComplemento
        )


        profileImageView.setOnClickListener {
            openGallery()
        }


        btnSalvar.setOnClickListener {

            editaFuncionario.updateUserData(
                view,
                editTextNome.text.toString(),
                editTextCpf.text.toString(),
                editTextEmail.text.toString(),
                editTextDataNascimento.text.toString(),
                editTextCelular.text.toString(),
                editTextTelefone.text.toString(),
                editTextCep.text.toString(),
                editTextCidade.text.toString(),
                editTextRua.text.toString(),
                editTextNumero.text.toString(),
                editTextComplemento.text.toString(),
                selectedImageUri
            )
        }


        btnExcluir.setOnClickListener {

            excluirFuncionario.deleteUser(view)
        }

        return view
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {

                profileImageView.setImageURI(selectedImageUri)
            }
        }
    }
}
