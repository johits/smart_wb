package com.example.smart_wb.etc

import android.content.res.Resources
import android.graphics.Color
import java.util.*

class smartcolor {
    /**
     * an "invalid" color that indicates that no color is set
     */
    val COLOR_NONE = 0x00112233

    /**
     * this "color" is used for the Legend creation and indicates that the next
     * form should be skipped
     */
    val COLOR_SKIP = 0x00112234

    /**
     * THE COLOR THEMES ARE PREDEFINED (predefined color integer arrays), FEEL
     * FREE TO CREATE YOUR OWN WITH AS MANY DIFFERENT COLORS AS YOU WANT
     */
    val LIBERTY_COLORS = intArrayOf(
        Color.rgb(207, 248, 246),
        Color.rgb(148, 212, 212),
        Color.rgb(136, 180, 187),
        Color.rgb(118, 174, 175),
        Color.rgb(42, 109, 130)
    )

    val MATERIAL_COLORS = intArrayOf(
        rgb("#2ecc71"), rgb("#f1c40f"), rgb("#e74c3c"), rgb("#3498db")
    )

    /**
     * Converts the given hex-color-string to rgb.
     *
     * @param hex
     * @return
     */
    fun rgb(hex: String): Int {
        val color = hex.replace("#", "").toLong(16).toInt()
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color shr 0 and 0xFF
        return Color.rgb(r, g, b)
    }

    /**
     * Returns the Android ICS holo blue light color.
     *
     * @return
     */
    fun getHoloBlue(): Int {
        return Color.rgb(51, 181, 229)
    }

    /**
     * Sets the alpha component of the given color.
     *
     * @param color
     * @param alpha 0 - 255
     * @return
     */
    fun colorWithAlpha(color: Int, alpha: Int): Int {
        return color and 0xffffff or (alpha and 0xff shl 24)
    }

    /**
     * turn an array of resource-colors (contains resource-id integers) into an
     * array list of actual color integers
     *
     * @param r
     * @param colors an integer array of resource id's of colors
     * @return
     */
    fun createColors(
        r: Resources,
        colors: IntArray
    ): List<Int>? {
        val result: MutableList<Int> = ArrayList()
        for (i in colors) {
            result.add(r.getColor(i))
        }
        return result
    }

    /**
     * Turns an array of colors (integer color values) into an ArrayList of
     * colors.
     *
     * @param colors
     * @return
     */
    fun createColors(colors: IntArray): List<Int>? {
        val result: MutableList<Int> = ArrayList()
        for (i in colors) {
            result.add(i)
        }
        return result
    }
}
