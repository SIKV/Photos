package com.github.sikv.photos.data.repository.impl

import com.github.sikv.photos.manager.AccountManager
import com.github.sikv.photos.data.repository.FeedbackRepository
import com.github.sikv.photos.model.Feedback
import com.github.sikv.photos.util.Utils
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FeedbackRepositoryImpl @Inject constructor(
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
                        .document(Utils.getCurrentDateAndTime())
                        .set(feedback)
                        .addOnSuccessListener {
                            continuation.resume(true)
                        }.addOnFailureListener {
                            continuation.resume(false)
                        }
            }
        }
    }
}