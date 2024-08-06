import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository
import util.GetCurrentDate

actual class FirebaseMessageRepositoryImp(
    val filteredList: MutableStateFlow<List<String?>>,
    val _otherUserID: MutableStateFlow<String?>
) : FirebaseMessageRepository, KoinComponent {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database: AnswerDatabase by inject()

    override val currentUserID: String = auth.currentUser!!.uid
    override val currentUserMail: String = auth.currentUser!!.email!!
    override val friendID: StateFlow<String?> = _otherUserID.asStateFlow()
    override val messageID: String? = null

    private val firestore = Firebase.firestore

    private val databaseMessaging: DatabaseReference =
        FirebaseDatabase.getInstance("https://gpt-chat-6c38c-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Message")

    override fun otherUserID(mail: String) {
        firestore.collection("Users").document(mail)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                    return@addSnapshotListener
                }
                if (value?.exists() == true) {
                    val data = value.get("Basic Information") as? HashMap<*, *>
                    _otherUserID.update {
                        data?.get("id").toString()
                    }
                }
            }
    }

    override fun getMailtoFirestore(mail: String) {
        firestore.collection("Users").orderBy(FieldPath.documentId())
            .startAt(mail)
            .endAt(mail + "\uf8ff")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                    return@addSnapshotListener
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

    override suspend fun addAnswer(message: String, chatID: String, senderID: String, receiverID: String) {
        val key = databaseMessaging.child(senderID)
            .child(receiverID).push().key
        if (key != null) {
            val messageObject = AnswerEntity(
                chatID = chatID,
                role = "user",
                content = message,
                senderID = senderID,
                receiverID = receiverID,
                date = GetCurrentDate(),
                messageID = key
            )

            database.answerDao().addAnswer(messageObject)

            databaseMessaging.child(senderID).child(receiverID)
                .child(key).setValue(messageObject).addOnSuccessListener {
                    if (receiverID != senderID) {
                        databaseMessaging.child(receiverID).child(senderID)
                            .child(key).setValue(messageObject).addOnSuccessListener {

                            }.addOnFailureListener {
                                println(it.message)
                            }
                    }
                }.addOnFailureListener {
                    println(it.message)
                }
        } else {
            println("Error getting key value!")
        }
    }

    override fun getAnswer(chatID: String, senderID: String, receiverID: String) {

    }
}