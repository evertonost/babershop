package everton.ost.pi_barbershop.ui.consulta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import everton.ost.pi_barbershop.R
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class Tela_consulta : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tela_consulta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnCliente = view.findViewById<ImageButton>(R.id.imageCliente)
        val textCliente = view.findViewById<TextView>(R.id.textCliente)
        val btnFuncionario = view.findViewById<ImageButton>(R.id.imageFuncionario)
        val textFuncionario = view.findViewById<TextView>(R.id.textFuncionario)
        val btnServico = view.findViewById<ImageButton>(R.id.imageServico)
        val btnProduto = view.findViewById<ImageButton>(R.id.imageProduto)


        btnCliente.visibility = View.GONE
        textCliente.visibility = View.GONE
        btnFuncionario.visibility = View.GONE
        textFuncionario.visibility = View.GONE
        btnServico.visibility = View.GONE
        btnProduto.visibility = View.GONE


        userId?.let { id ->

            db.collection("clientes").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {

                        btnCliente.visibility = View.VISIBLE
                        textCliente.visibility = View.VISIBLE

                    } else {

                        db.collection("profissionais").document(id).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {

                                    btnFuncionario.visibility = View.VISIBLE
                                    textFuncionario.visibility = View.VISIBLE
                                    btnServico.visibility = View.VISIBLE
                                    btnProduto.visibility = View.VISIBLE
                                } else {

                                    Toast.makeText(context, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show()
                }
        }


        btnCliente.setOnClickListener {
            findNavController().navigate(R.id.action_telaConsultaFragment_to_telaconsultacadastroFragment)
        }
        btnFuncionario.setOnClickListener {
            findNavController().navigate(R.id.action_telaConsultaFragment_to_telaconsultacadastroFuncionarioFragment)
        }
        btnServico.setOnClickListener {
            findNavController().navigate(R.id.action_telaConsultaFragment_to_nav_consulta_servico)
        }
        btnProduto.setOnClickListener {
            findNavController().navigate(R.id.action_telaConsultaFragment_to_nav_consulta_produto)
        }
    }
}
