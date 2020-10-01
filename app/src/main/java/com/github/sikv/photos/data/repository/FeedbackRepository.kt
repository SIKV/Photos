package com.github.sikv.photos.data.repository

import com.github.sikv.photos.model.Feedback
import com.github.sikv.photos.account.AccountManager
import com.github.sikv.photos.util.Utils
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
        private val accountManager: AccountManager
) {

    companion object {
        private const val COLLECTION_FEEDBACK = "Feedback"
    }

    @Suppress("UNCHECKED_CAST")
    fun sendFeedback(feedback: Feedback, completion: (Boolean) -> Unit) {
        accountManager.signInAnonymously {
            FirebaseFirestore.getInstance()
                    .collection(COLLECTION_FEEDBACK)
                    .document(Utils.getCurrentDateAndTime())
                    .set(feedback)
                    .addOnSuccessListener {
                        completion(true)
                    }.addOnFailureListener {
                        completion(false)
                    }
        }
    }
}