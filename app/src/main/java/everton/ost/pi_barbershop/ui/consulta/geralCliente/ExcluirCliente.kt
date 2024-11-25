import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import everton.ost.pi_barbershop.FormLoginActivity

class ExcluirCliente(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun deleteUser(view: View) {
        val userId = auth.currentUser?.uid
        if (userId != null) {

            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Confirmar Exclusão")
            alertDialog.setMessage("Você tem certeza que deseja excluir sua conta?")

            alertDialog.setPositiveButton("Sim") { dialog: DialogInterface, _: Int ->

                val userRef = db.collection("clientes").document(userId)


                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {

                        userRef.delete()
                            .addOnSuccessListener {

                                auth.currentUser?.delete()?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Toast.makeText(context, "Conta deletada com sucesso!", Toast.LENGTH_SHORT).show()


                                        val intent = Intent(context, FormLoginActivity::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        val snackbar = Snackbar.make(view, "Erro ao excluir conta do usuário: ${task.exception?.message}", Snackbar.LENGTH_SHORT)
                                        snackbar.setBackgroundTint(android.graphics.Color.RED)
                                        snackbar.show()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ExcluirCliente", "Erro ao excluir dados do Firestore: ", e)
                                val snackbar = Snackbar.make(view, "Erro ao excluir os dados do cliente: ${e.message}", Snackbar.LENGTH_SHORT)
                                snackbar.setBackgroundTint(android.graphics.Color.RED)
                                snackbar.show()
                            }
                    } else {
                        val snackbar = Snackbar.make(view, "Cliente não encontrado.", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(android.graphics.Color.RED)
                        snackbar.show()
                    }
                }.addOnFailureListener { e ->
                    Log.e("ExcluirCliente", "Erro ao verificar cliente no Firestore: ", e)
                    val snackbar = Snackbar.make(view, "Erro ao verificar cliente: ${e.message}", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(android.graphics.Color.RED)
                    snackbar.show()
                }
            }
            alertDialog.setNegativeButton("Não") { dialog: DialogInterface, _: Int ->

                dialog.cancel()
            }


            alertDialog.show()
        } else {
            val snackbar = Snackbar.make(view, "Usuário não encontrado.", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(android.graphics.Color.RED)
            snackbar.show()
        }
    }
}
