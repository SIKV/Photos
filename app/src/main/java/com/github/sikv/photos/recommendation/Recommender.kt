package com.github.sikv.photos.recommendation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class Recommender @Inject constructor() {

    private var recommendations: Queue<String>? = null

    /**
     * Returns the next recommended query and a flag if more are available
     */
    suspend fun getNextRecommendation(): Pair<String?, Boolean> {
        return withContext(Dispatchers.IO) {
            if (recommendations == null) {
                populateRecommendations()
            }

            val nextItem = recommendations?.poll()
            val isLastItem = recommendations?.isEmpty() ?: true

            Pair(nextItem, !isLastItem)
        }
    }

    fun reset() {
        recommendations = null
    }

    private fun populateRecommendations() {
        recommendations = LinkedList()

        // TODO Populate recommendations
    }
}