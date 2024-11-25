package everton.ost.pi_barbershop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import everton.ost.pi_barbershop.databinding.ActivityPrincipalBinding

class Principal : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = Color.BLACK

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_principal)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_agenda, R.id.nav_cadastro_tela, R.id.nav_consulta_tela
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val headerView = navView.getHeaderView(0)
        val textViewNomeCliente = headerView.findViewById<TextView>(R.id.textViewNomeCliente)
        val profileImageView = headerView.findViewById<ImageView>(R.id.imagem_Perfil)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            firestore = FirebaseFirestore.getInstance()

            firestore.collection("clientes").document(userId)
                .get()
                .addOnSuccessListener { clientDocument ->
                    if (clientDocument.exists()) {

                        val nome = clientDocument.getString("nome")
                        textViewNomeCliente.text = nome ?: "Nome não encontrado"


                        ajustarMenu(navView.menu, isProfissional = false)
                    } else {
                        // Verificar se o usuário é um profissional
                        firestore.collection("profissionais").document(userId)
                            .get()
                            .addOnSuccessListener { professionalDocument ->
                                if (professionalDocument.exists()) {

                                    val nome = professionalDocument.getString("nome")
                                    textViewNomeCliente.text = nome ?: "Nome não encontrado"


                                    ajustarMenu(navView.menu, isProfissional = true)
                                } else {
                                    textViewNomeCliente.text = "Usuário não encontrado"
                                }
                            }.addOnFailureListener {
                                Toast.makeText(this, "Erro ao carregar dados do profissional", Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar dados do cliente", Toast.LENGTH_SHORT).show()
                }


            val storageRef = FirebaseStorage.getInstance().reference.child("perfil/$userId.jpg")
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(profileImageView)
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar a imagem de perfil", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun ajustarMenu(menu: Menu, isProfissional: Boolean) {
        if (isProfissional) {

            menu.findItem(R.id.nav_agenda).isVisible = false
            menu.findItem(R.id.nav_consulta_agendamento).isVisible = true
            menu.findItem(R.id.nav_agenda_consulta).isVisible = true


            menu.findItem(R.id.nav_cadastro_tela).isVisible = true
            menu.findItem(R.id.nav_consulta_tela).isVisible = true
        } else {

            menu.findItem(R.id.nav_cadastro_tela).isVisible = false
            menu.findItem(R.id.nav_consulta_tela).isVisible = true


            menu.findItem(R.id.nav_agenda).isVisible = true
            menu.findItem(R.id.nav_consulta_agendamento).isVisible = true
            menu.findItem(R.id.nav_agenda_consulta).isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logoff -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, FormLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_principal)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}