package com.example.meteordodge

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var thread: GameThread? = null
    private var player: Player? = null
    private val meteors = mutableListOf<Meteor>()
    private var score = 0
    private var gameOver = false
    
    private val prefs = context.getSharedPreferences("game", Context.MODE_PRIVATE)
    private var highScore = prefs.getInt("highscore", 0)

    private val paintScore = Paint().apply {
        color = Color.WHITE
        textSize = 60f
        isAntiAlias = true
    }
    private val paintGameOver = Paint().apply {
        color = Color.RED
        textSize = 100f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (player == null) {
            player = Player(width, height)
        }
        if (thread == null || !thread!!.isAlive) {
            thread = GameThread(holder, this)
            thread?.running = true
            thread?.start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        thread?.running = false
        while (retry) {
            try {
                thread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        thread = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            if (gameOver) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    resetGame()
                }
                return true
            }
            player?.update(event.x.toInt())
        }
        return true
    }

    fun update() {
        if (gameOver) return
        
        // Spawn meteors
        if (Math.random() < 0.02) { 
            meteors.add(Meteor(width, height))
        }

        val iterator = meteors.iterator()
        while (iterator.hasNext()) {
            val meteor = iterator.next()
            meteor.update()
            
            // Check collision
            if (player != null && android.graphics.Rect.intersects(player!!.rect, meteor.rect)) {
                gameOver = true
                if (score > highScore) {
                    highScore = score
                    prefs.edit().putInt("highscore", highScore).apply()
                }
            }
            
            // Remove off-screen
            if (meteor.y > height) {
                iterator.remove()
                score++
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawColor(Color.BLACK) // Clear screen

        player?.draw(canvas)
        
        for (meteor in meteors) {
            meteor.draw(canvas)
        }

        canvas.drawText("Score: ${score}", 50f, 100f, paintScore)
        
        // Draw High Score (Right aligned logic or just simple offset)
        val highScoreText = "Best: ${highScore}"
        val highScoreWidth = paintScore.measureText(highScoreText)
        canvas.drawText(highScoreText, width - highScoreWidth - 50f, 100f, paintScore)

        if (gameOver) {
            canvas.drawText("GAME OVER", width / 2f, height / 2f, paintGameOver)
            canvas.drawText("Tap to Restart", width / 2f, height / 2f + 120, paintScore.apply { textAlign = Paint.Align.CENTER })
            // Reset text align for score
            paintScore.textAlign = Paint.Align.LEFT
        }
    }
    
    private fun resetGame() {
        meteors.clear()
        score = 0
        gameOver = false
        // Player position reset
        player = Player(width, height)
    }
}
