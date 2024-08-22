import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.AndroidActivityViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repositories.FirebaseMessageRepository
import util.GetCurrentDate
import java.io.File

actual class FirebaseMessageRepositoryImp(
    val _filteredList: MutableStateFlow<List<String?>>,
    val _otherUserID: MutableStateFlow<String?>,
) : FirebaseMessageRepository, KoinComponent {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val database: AnswerDatabase by inject()
    private val messageRepository: MessageRepository by inject()
    private val context: Context by inject()
    private val androidActivityViewModel: AndroidActivityViewModel by inject()

    override val currentUserID: String = auth.currentUser!!.uid
    override val currentUserMail: String = auth.currentUser!!.email!!
    override val friendID: StateFlow<String?> = _otherUserID.asStateFlow()
    override val messageID: String? = null

    override val messageList: MutableStateFlow<List<AnswerEntity?>> = MutableStateFlow(emptyList())

    private val firestore = Firebase.firestore
    private val storageRef = Firebase.storage.reference

    private val databaseMessaging: DatabaseReference =
        FirebaseDatabase.getInstance("https://gpt-chat-6c38c-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Message")

    override fun otherUserID(mail: String?) {
        if (mail != null) {
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
        } else {
            _otherUserID.update {
                null
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
                if (!value?.documents.isNullOrEmpty()) {
                    val tempList = ArrayList<String?>()
                    for (data in value!!.documentChanges) {
                        if (data?.document?.id != null) {
                            tempList.add(data.document.id)
                            _filteredList.update {
                                tempList
                            }
                        }
                    }
                }
            }
    }

    override suspend fun addAnswer(
        content: String,
        contentType: String,
        chatID: String,
        senderID: String,
        receiverID: String
    ) {
        val key = databaseMessaging.child(senderID)
            .child(receiverID).push().key
        if (key != null) {
            val messageObject = AnswerEntity(
                chatID = chatID,
                role = "user",
                contentType = contentType,
                content = content,
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

    override suspend fun getAnswer(chatID: String, senderID: String, receiverID: String?) {
        receiverID?.let {
            databaseMessaging.child(senderID).child(receiverID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (postSnapshot in snapshot.children) {
                            val message: AnswerEntity? = postSnapshot?.getValue(AnswerEntity::class.java)
                            if (message != null) {
                                if (!messageList.value.contains(message)) {
                                    messageList.update {
                                        messageList.value + message
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
        }
    }

    override suspend fun uploadFile() {

    }

    @Composable
    override fun permissionManager(
        openGallery: MutableState<Boolean>,
        showRationalDialog: MutableState<Boolean>,
        selectedImages: MutableState<List<File?>>
    ) {
        androidActivityViewModel.permissionManager(openGallery, showRationalDialog, selectedImages)
    }
}