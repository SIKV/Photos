package com.github.sikv.photos.data.repository

import com.github.sikv.photos.model.SearchTag
import com.github.sikv.photos.util.AccountManager
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchTagRepository @Inject constructor(
        private val accountManager: AccountManager
) {

    companion object {
        private const val COLLECTION_SEARCH_TAGS = "SearchTags"
        private const val FIELD_TAGS = "tags"
    }

    @Suppress("UNCHECKED_CAST")
    fun getTags(language: String, completion: (List<SearchTag>) -> Unit) {
        accountManager.signInAnonymously {
            FirebaseFirestore.getInstance()
                    .collection(COLLECTION_SEARCH_TAGS)
                    .document(language)
                    .get()
                    .addOnSuccessListener { document ->
                        val tags = (document.get(FIELD_TAGS) as? List<String>) ?: emptyList()
                        val searchTags = tags.map { SearchTag(it) }

                        completion(searchTags)

                    }.addOnFailureListener {
                        completion(emptyList())
                    }
        }
    }
}