package com.example.composebugstuckcomposition

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.composebugstuckcomposition.ui.theme.ComposeBugStuckCompositionTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val progress = remember { mutableStateOf(false) }
            Content(progress.value,
                triggerBug = {
                    println("Clicked triggerBug")
                    addPreDrawListener()
                },
                toggleProgress = {
                    println("Clicked toggleProgress")
                    progress.value = !progress.value
                })
        }
    }

    // Breaks recomposition or something fundamental in Compose runtime.
    // Buttons stop responding to clicks and animation is stuck.
    // The only notable thing in logs is:
    // The RippleDrawable.STYLE_PATTERNED animation is not supported for a non-hardware accelerated Canvas. Skipping animation.
    // But that's just a result of attempting to render ripple on a software canvas below.
    private fun addPreDrawListener() {
        val c = Canvas(Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888))
        val root = window.decorView
        root.viewTreeObserver.addOnPreDrawListener {
            root.draw(c)
            true
        }
    }
}

@Composable
fun Content(
    progress: Boolean,
    triggerBug: () -> Unit,
    toggleProgress: () -> Unit,
) {
    ComposeBugStuckCompositionTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (progress) {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }
                Button(onClick = { triggerBug() }) {
                    Text(text = "Trigger bug")
                }
                Button(onClick = { toggleProgress() }) {
                    Text(text = "Toggle progress")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeBugStuckCompositionTheme {
        Content(true, {}, {})
    }
}
