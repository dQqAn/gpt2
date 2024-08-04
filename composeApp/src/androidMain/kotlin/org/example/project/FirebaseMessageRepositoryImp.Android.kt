import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import repositories.FirebaseMessageRepository

actual class FirebaseMessageRepositoryImp(
    val filteredList: MutableStateFlow<List<String?>>
) : FirebaseMessageRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override val currentUserID: String = auth.currentUser!!.uid
    override val currentUserMail: String = auth.currentUser!!.email!!
    override val friendMail: String? = null
    override val otherUserID: String? = null
    override val messageID: String? = null

    private val firestore = Firebase.firestore

    override fun getMailtoFirestore(mail: String) {
        firestore.collection("Users").orderBy(FieldPath.documentId())
            .startAt(mail)
            .endAt(mail + "\uf8ff")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                }
                val tempList = ArrayList<String?>()
                if (!value?.documents.isNullOrEmpty()) {
                    for (data in value!!.documentChanges) {
                        if (data?.document?.id != null) {
                            tempList.add(data.document.id)
                            filteredList.update {
                                tempList
                            }
                        }
                    }
                }
            }
    }
}