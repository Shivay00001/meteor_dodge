package com.example.meteordodge

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import java.util.Random

class Meteor(val screenWidth: Int, val screenHeight: Int) {
    val width = 80
    val height = 80
    var x = 0
    var y = -height
    var speed = 15

    private val paint = Paint().apply {
        color = Color.RED
    }

    init {
        val random = Random()
        x = random.nextInt(screenWidth - width)
        speed = 10 + random.nextInt(20) // Random speed
    }

    val rect: Rect
        get() = Rect(x, y, x + width, y + height)

    fun update() {
        y += speed
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }
}
