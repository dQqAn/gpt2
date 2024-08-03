import com.google.firebase.auth.FirebaseAuth
import repositories.FirebaseMessageRepository

actual class FirebaseMessageRepositoryImp : FirebaseMessageRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override val currentUserID: String = auth.currentUser!!.uid
    override val currentUserMail: String = auth.currentUser!!.email!!
    override val friendMail: String? = null
    override val otherUserID: String? = null
    override val messageID: String? = null
}