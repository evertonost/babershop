package everton.ost.pi_barbershop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import everton.ost.pi_barbershop.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Se você não precisa do ViewModel neste fragmento, pode remover também.
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Inflate o layout e retorna a view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Removeu-se o TextView, já que você não quer exibi-lo

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
