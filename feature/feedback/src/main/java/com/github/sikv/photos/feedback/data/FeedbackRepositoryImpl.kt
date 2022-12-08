package com.github.sikv.photos.feedback.data

import com.github.sikv.photos.account.AccountManager
import com.github.sikv.photos.feedback.domain.Feedback
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class FeedbackRepositoryImpl @Inject constructor(
    private val accountManager: AccountManager
) : FeedbackRepository {

    companion object {
        private const val COLLECTION_FEEDBACK = "Feedback"
    }

    override suspend fun sendFeedback(feedback: Feedback): Boolean {
        return suspendCoroutine { continuation ->
            accountManager.signInAnonymously {
                FirebaseFirestore.getInstance()
                    .collection(COLLECTION_FEEDBACK)
                    .document(getCurrentDateAndTime())
                    .set(feedback)
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            }
        }
    }

    private fun getCurrentDateAndTime() = Calendar.getInstance().time.toString()
}
