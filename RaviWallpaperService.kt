package com.raviai.assistant

import android.service.wallpaper.WallpaperService
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.Canvas
import android.view.SurfaceHolder
import android.os.Handler
import android.os.Looper
import android.graphics.Paint

class RaviWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = RaviEngine()

    inner class RaviEngine : Engine() {
        private val handler = Handler(Looper.getMainLooper())
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        private val bitmap by lazy {
            BitmapFactory.decodeResource(resources, R.drawable.ravi_ai_female_wallpaper)
        }
        private var visible = false
        private val drawTask = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) handler.postDelayed(this, 1000L / 24L)
            }
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            handler.removeCallbacks(drawTask)
            if (visible) handler.post(drawTask)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame()
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            handler.removeCallbacks(drawTask)
            super.onSurfaceDestroyed(holder)
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas()
                if (canvas != null) {
                    val b = bitmap
                    val scale = maxOf(canvas.width.toFloat() / b.width, canvas.height.toFloat() / b.height)
                    val w = b.width * scale
                    val h = b.height * scale
                    val left = (canvas.width - w) / 2f
                    val top = (canvas.height - h) / 2f
                    canvas.drawBitmap(b, null, Rect(left.toInt(), top.toInt(), (left+w).toInt(), (top+h).toInt()), paint)
                }
            } catch (_: Exception) {
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
