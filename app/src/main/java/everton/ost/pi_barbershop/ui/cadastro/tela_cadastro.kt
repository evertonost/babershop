
package everton.ost.pi_barbershop.ui.cadastro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import everton.ost.pi_barbershop.R

class TelaCadastroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_tela_cadastro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCliente= view.findViewById<ImageButton>(R.id.imageCliente)
        val btnServico = view.findViewById<ImageButton>(R.id.imageServico)
        val btnProduto = view.findViewById<ImageButton>(R.id.imageProduto)
        val btnFuncionario = view.findViewById<ImageButton>(R.id.imageFuncionario)

        btnCliente.setOnClickListener {
            findNavController().navigate(R.id.action_telaCadastroFragment_to_cadastro_cliente1)
        }


        btnServico.setOnClickListener {
            findNavController().navigate(R.id.action_telaCadastroFragment_to_telaServicoFragment)
        }

        btnProduto.setOnClickListener {
            findNavController().navigate(R.id.action_telaCadastroFragment_to_telaProdutoFragmentFragment)
        }
        btnFuncionario.setOnClickListener {
            findNavController().navigate(R.id.action_telaCadastroFragment_to_telaFuncionarioFragment)
        }
    }
}
