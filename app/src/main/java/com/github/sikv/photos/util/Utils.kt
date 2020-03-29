package com.github.sikv.photos.util

import android.util.Log
import java.text.DateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.sqrt

const val SPAN_COUNT_LIST = 1
const val SPAN_COUNT_GRID = 3

const val PHOTO_TRANSITION_DURATION = 500

object Utils {

    fun<T> log(clazz: Class<T>, text: String) {
        Log.i(clazz.simpleName, text)
    }

    fun formatCreatedAtDate(date: Long): String {
        return DateFormat.getDateInstance().format(Date(date))
    }

    fun calculateP(x: Double, y: Double, z: Double,
                   viewX: Float, viewY: Float, viewZ: Float,
                   lastGravity0: Double, lastGravity1: Double
    ): Triple<Float, Float, Pair<Double, Double>>? {

        // http://vitiy.info/how-to-create-parallax-effect-using-accelerometer

        var gX = x
        var gY = y
        var gZ = z

        var roll = 0.0
        var pitch = 0.0

        val gSum = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gSum != 0.0) {
            gX /= gSum
            gY /= gSum
            gZ /= gSum
        }

        if (gZ != 0.0) {
            roll = atan2(gX, gZ) * 180 / Math.PI
        }

        pitch = sqrt(gX * gX + gZ * gZ)

        if (pitch != 0.0) {
            pitch = atan2(gY, pitch) * 180 / Math.PI
        }

        var dgX = roll - lastGravity0
        var dgY = pitch - lastGravity1

        if (gY > 0.99) {
            dgX = 0.0
        }
        if (dgX > 180) {
            dgX = 0.0
        }
        if (dgX < -180) {
            dgX = 0.0
        }
        if (dgY > 180) {
            dgY = 0.0
        }
        if (dgY < -180) {
            dgY = 0.0
        }

        val lastGravityPair = Pair(roll, pitch)

        return if ((dgX != 0.0) || (dgY != 0.0)) {
            val newX = (viewX + dgX * (1.0 + 100.0 * viewZ)).toFloat()
            val newY = (viewY - dgY * (1.0 + 100.0 * viewZ)).toFloat()

            Triple(newX, newY, lastGravityPair)

        } else {
            null
        }
    }
}