package com.example.meteordodge

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

class Player(val screenWidth: Int, val screenHeight: Int) {
    val width = 100
    val height = 100
    var x = screenWidth / 2 - width / 2
    var y = screenHeight - height - 50
    
    private val paint = Paint().apply {
        color = Color.CYAN
    }

    val rect: Rect
        get() = Rect(x, y, x + width, y + height)

    fun update(touchX: Int?) {
        if (touchX != null) {
            x = touchX - width / 2
        }
        // Clamp to screen
        if (x < 0) x = 0
        if (x > screenWidth - width) x = screenWidth - width
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }
}
