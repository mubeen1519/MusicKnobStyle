package com.example.musicknobstyle

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicknobstyle.ui.theme.MusicKnobStyleTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, Color.Green, RoundedCornerShape(10.dp))
                    .padding(30.dp)
            ) {
                var volume by remember {
                    mutableStateOf(0f)
                }
                val barCount = 20
                MusicKnob(
                    modifier = Modifier.size(100.dp)
                ) {
                    volume = it
                }
                Spacer(modifier = Modifier.width(20.dp))
                VolumeBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    activeBar = (barCount * volume).roundToInt(),
                    totalBar = barCount
                )
            }
        }
        }
    }
}

@Composable
fun VolumeBar(
    modifier : Modifier = Modifier,
    activeBar : Int  = 0,
    totalBar : Int = 10
) {
        BoxWithConstraints (
            contentAlignment = Alignment.Center,
            modifier = modifier
                ){
            val barWidth = remember {
                constraints.maxWidth / (2f * totalBar)
            }
            Canvas(modifier = modifier ){
                for(i in 0 until totalBar){
                    drawRoundRect(
                        color = if(i in 0..activeBar) Color.Green else Color.LightGray,
                        topLeft = Offset( i * barWidth * 2f + barWidth / 2f, 0f),
                        size = Size(barWidth, constraints.maxHeight.toFloat()),
                        cornerRadius = CornerRadius(0f)
                    )
                }
            }
        }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicKnob(
    modifier: Modifier = Modifier,
    //This is the degree value of max and min of the volume knob
    limitingAngle : Float = 25f,
    onValueChange : (Float) -> Unit
) {
    var rotationState by remember {
        mutableStateOf(limitingAngle)
    }
    // this is touch position coordinates with x and y coordinates
    var touchX by remember {
        mutableStateOf(0f)
    }
    var touchY by remember {
        mutableStateOf(0f)
    }
    var centerX by remember {
        mutableStateOf(0f)
    }
    var centerY by remember {
        mutableStateOf(0f)
    }
    Image(
        painter = painterResource(id = R.drawable.music_knob),
        contentDescription = "Music Knob",
        modifier = modifier
            .fillMaxSize()
            //this function work with coordinates of the layout
            .onGloballyPositioned {
                //boundsInWindow is the postioning of the image on our screen
                val windowsBounds = it.boundsInWindow()
                //for center the image on display
                centerX = windowsBounds.size.width / 2f
                centerY = windowsBounds.size.width / 2f

            }
            //this function is used for motion event about how many time touch is obtained on the music knob
            .pointerInteropFilter { event ->
                touchX = event.x
                touchY = event.y
                val angle = -atan2(centerX - touchX, centerY - touchY) * (180f / PI).toFloat()
                // this atan means it divide the arc tangent with each other and gives the rotation angle
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitingAngle..limitingAngle) {
                            val fixedAngle = if (angle in -180f..-limitingAngle) {
                                360f + angle
                            } else {
                                angle
                            }
                            rotationState = fixedAngle
                            val percent = (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                            onValueChange(percent)
                            true
                        } else false
                    }
                    else -> false
                }
            }
            .rotate(rotationState)
    )
}

