package com.example.visionary.Activities

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.transition.Visibility
import com.example.visionary.R
import com.example.visionary.RoomDatabase.VideoDatabase
import com.example.visionary.RoomDatabase.VideoEntity
import com.example.visionary.databinding.ActivityPlayerBinding
import com.example.visionary.databinding.MoreFeatureBinding
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import im.zego.zegoexpress.ZegoExpressEngine
import im.zego.zegoexpress.constants.ZegoRoomState
import im.zego.zegoexpress.constants.ZegoUpdateType
import im.zego.zegoexpress.entity.ZegoUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.abs


class PlayerActivity : AppCompatActivity(), AudioManager.OnAudioFocusChangeListener,
    GestureDetector.OnGestureListener {
    private val binding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private lateinit var runnable: Runnable
    private var isSubtitle: Boolean = true
    private lateinit var gestureDetectorCompact: GestureDetectorCompat



    companion object {
        private lateinit var videoID:String
        private lateinit var player: ExoPlayer
        private var isFullScreen: Boolean = false
        private var isLocked: Boolean = false
        lateinit var trackSelecter: DefaultTrackSelector
        private var audioManager: AudioManager? = null
        var pipStatus: Int = 0
        private var brightness: Int = 0
        private var volume: Int = 0

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        setTheme(R.style.playerActivityteam)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        gestureDetectorCompact = GestureDetectorCompat(this, this)

        ///for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        initalizeLayout()

        initalizedBinding()


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initalizedBinding() {


        binding.addFriend.setOnClickListener {
            val database = VideoDatabase.getDatabase(this)
            downloadAndSaveVideo(this, videoID,database)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.playPauseBtn.setOnClickListener {
            if (player.isPlaying) pauseVideo()
            else playVideo()
        }

        binding.fullScreenBtn.setOnClickListener {
            if (isFullScreen) {
                isFullScreen = false
                playingFullScreen(false)
            } else {
                isFullScreen = true
                playingFullScreen(true)
            }
        }

        binding.lockBtn.setOnClickListener {
            if (!isLocked) {
                isLocked = true
                binding.playerView.hideController()
                binding.playerView.useController = false
                binding.lockBtn.setImageResource(R.drawable.close_lock_icon)

            } else {
                isLocked = false
                binding.playerView.useController = true
                binding.playerView.showController()
                binding.playerView.useController = true
                binding.lockBtn.setImageResource(R.drawable.baseline_lock_open_24)
            }


        }

        binding.adjustVideo.setOnClickListener {
            adjustVideoQuality()
        }






        binding.moreFeatureBtn.setOnClickListener {
            pauseVideo()
            val customDialog =
                LayoutInflater.from(this).inflate(R.layout.more_feature, binding.root, false)
            val bindingMF = MoreFeatureBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                .setOnCancelListener {
                    playVideo()
                }
                .setBackground(ColorDrawable(0x806B728E.toInt()))
                .create()


            dialog.show()

            bindingMF.audioTrack.setOnClickListener {
                dialog.dismiss()
                playVideo()
                //// to get all language from video
                val audioTrack = ArrayList<String>()
                for (i in 0 until player.currentTrackGroups.length) {
                    if (player.currentTrackGroups.get(i)
                            .getFormat(0).selectionFlags == C.SELECTION_FLAG_DEFAULT
                    ) {
                        audioTrack.add(
                            Locale(
                                player.currentTrackGroups.get(i).getFormat(0).language.toString()
                            ).displayLanguage
                        )
                    }
                }


                /////all audio to char sequence
                val tempTrack = audioTrack.toArray(arrayOfNulls<CharSequence>(audioTrack.size))

                MaterialAlertDialogBuilder(this, R.style.alertDialog)
                    .setTitle("Select Language")
                    .setOnCancelListener {
                        playVideo()
                    }
                    .setBackground(ColorDrawable(0x806B728E.toInt()))
                    //set language to it
                    .setItems(tempTrack) { _, position ->
                        Toast.makeText(
                            this,
                            audioTrack[position] + " " + "Selected",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        trackSelecter.setParameters(
                            trackSelecter.buildUponParameters()
                                .setPreferredAudioLanguages(audioTrack[position])
                        )
                    }
                    .create()
                    .show()
            }

            bindingMF.subtitlesBtn.setOnClickListener {
                if (isSubtitle) {
                    trackSelecter.parameters =
                        DefaultTrackSelector.ParametersBuilder(this).setRendererDisabled(
                            C.TRACK_TYPE_VIDEO, true
                        ).build()
                    Toast.makeText(this, "Subtitles Off", Toast.LENGTH_SHORT).show()
                    isSubtitle = false
                } else {
                    trackSelecter.parameters =
                        DefaultTrackSelector.ParametersBuilder(this).setRendererDisabled(
                            C.TRACK_TYPE_VIDEO, false
                        ).build()
                    Toast.makeText(this, "Subtitles On", Toast.LENGTH_SHORT).show()
                    isSubtitle = true
                }


                dialog.dismiss()
                playVideo()
            }

            bindingMF.pipMode.setOnClickListener {
                val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val status = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appOps.checkOpNoThrow(
                        AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                        android.os.Process.myUid(),
                        packageName
                    ) == AppOpsManager.MODE_ALLOWED
                } else {
                    false
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (status) {
                        this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
                        dialog.dismiss()
                        binding.playerView.hideController()
                        playVideo()
                        pipStatus = 0
                    } else {
                        val intent = Intent(
                            "android.settings.PICTURE_IN_PICTURE_SETTINGS",
                            Uri.parse("package:$packageName")
                        )
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Feature Not Supported", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    playVideo()
                }


            }

        }
    }

    private fun playVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.baseline_pause_24)
        player.play()
    }

    private fun pauseVideo() {
        binding.playPauseBtn.setImageResource(R.drawable.baseline_play_arrow_24)
        player.pause()
    }


    private fun initalizeLayout() {
        val videoUri = intent.getStringExtra("uri").toString()
        val videoName = intent.getStringExtra("movieName").toString()
        val videoImage = intent.getStringExtra("videoImage").toString()
        videoID=intent.getStringExtra("videoID").toString()




        createPlayer(videoUri, videoImage, videoName)
    }

    private fun createPlayer(videoUri: String, videoImage: String, videoName: String) {
        binding.videoName.text = videoName
        ///for title secroll
        binding.videoName.isSelected = true
        trackSelecter = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters() // Set default video quality (e.g., 480p)
                    .setMaxVideoSize(854, 480) // Max resolution: 480p
            )
        }
        player = ExoPlayer.Builder(this).setTrackSelector(trackSelecter).build()
        binding.playerView.player = player
        doubleTapenable()

        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)

        playingFullScreen(isFullScreen)
        setVisiblity()
        player.prepare()
        playVideo()
    }

    private fun playingFullScreen(enable: Boolean) {
        if (enable) {
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            binding.fullScreenBtn.setImageResource(R.drawable.baseline_fullscreen_exit_24)
        } else {
            binding.playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            binding.fullScreenBtn.setImageResource(R.drawable.baseline_fullscreen_24)
        }
    }

    private fun setVisiblity() {
        runnable = Runnable {
            if (binding.playerView.isControllerVisible) {
                changeVisiblity(View.VISIBLE)
            } else {
                changeVisiblity(View.INVISIBLE)
            }

            Handler(Looper.getMainLooper()).postDelayed(runnable, 300)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)

    }

    private fun changeVisiblity(visibility: Int) {
        binding.topController.visibility = visibility
        binding.bottomController.visibility = visibility
        binding.playPauseBtn.visibility = visibility
        binding.lockBtn.visibility = visibility
        if (isLocked) binding.lockBtn.visibility = View.VISIBLE


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun doubleTapenable() {


        binding.ytOverlay.performListener(object : YouTubeOverlay.PerformListener {
            override fun onAnimationEnd() {
                binding.ytOverlay.visibility = View.GONE
            }

            override fun onAnimationStart() {
                binding.ytOverlay.visibility = View.VISIBLE
            }
        })

        binding.ytOverlay.player(player)


        /// volume and brightness
        binding.playerView.setOnTouchListener { _, motionEvent ->


            binding.playerView.isDoubleTapEnabled = false

            if (!isLocked) {
                binding.playerView.isDoubleTapEnabled = true
                gestureDetectorCompact.onTouchEvent(motionEvent)
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    binding.brightnessBtn.visibility = View.GONE
                    binding.volumeBtn.visibility = View.GONE


                    ///for immersive mode
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    WindowInsetsControllerCompat(window, binding.root).let { controller ->
                        controller.hide(WindowInsetsCompat.Type.systemBars())
                        controller.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            }
            return@setOnTouchListener false
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        player.release()
        audioManager?.abandonAudioFocus(this)
    }

    override fun onDown(p0: MotionEvent): Boolean = false

    override fun onShowPress(p0: MotionEvent) = Unit

    override fun onSingleTapUp(p0: MotionEvent): Boolean = false

    override fun onScroll(
        event: MotionEvent?,
        event1: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val sWidth = Resources.getSystem().displayMetrics.widthPixels
        val sheight = Resources.getSystem().displayMetrics.heightPixels
        val border = 100 * Resources.getSystem().displayMetrics.density.toInt()
        if (event!!.x < border || event.y < border || event.x > sWidth - border || event.y > sheight - border)
            return false

        if (abs(distanceX) < abs(distanceY)) {
            if (event!!.x < sWidth / 2) {
                ///brightness related work
                binding.brightnessBtn.visibility = View.VISIBLE
                binding.volumeBtn.visibility = View.GONE

                val increase = distanceY > 0
                val newValue = if (increase) brightness + 1 else brightness - 1

                if (newValue in 0..15) brightness = newValue
                binding.brightnessBtn.text = brightness.toString()

                setScreenBrightness(brightness)
            } else {
                ///volume related work
                binding.volumeBtn.visibility = View.VISIBLE
                binding.brightnessBtn.visibility = View.GONE
                val maxVolume = audioManager!!.getStreamMaxVolume((AudioManager.STREAM_MUSIC))

                val increase = distanceY > 0
                val newValue = if (increase) volume + 1 else volume - 1

                if (newValue in 0..maxVolume) volume = newValue
                binding.volumeBtn.text = volume.toString()

                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

            }
        }
        return true
    }

    override fun onLongPress(p0: MotionEvent) = Unit

    override fun onFling(
        event: MotionEvent?,
        event1: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean = false


    private fun setScreenBrightness(value: Int) {
        val d = 1.0f / 15
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
    }

    override fun onResume() {
        super.onResume()
        if (audioManager == null) audioManager =
            getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        if (brightness != 0) setScreenBrightness(brightness)
    }

    override fun onAudioFocusChange(p0: Int) {
        if (p0 <= 0) pauseVideo()
    }


    private fun adjustVideoQuality() {
        pauseVideo()
        // this is help to play video in many qualitities
        val qualityOptions = arrayOf("144p", "240p", "360p", "480p", "720p", "1080p", "Original")
        val resolutions = arrayOf(
            Pair(256, 144),   // 144p
            Pair(426, 240),   // 240p
            Pair(640, 360),   // 360p
            Pair(854, 480),   // 480p
            Pair(1280, 720),  // 720p
            Pair(1920, 1080), // 1080p
            null              // Original quality (no restriction)
        )


        // Show dialog to select quality
        AlertDialog.Builder(this)
            .setTitle("Select Video Quality")
            .setItems(qualityOptions) { _, which ->
                val selectedResolution = resolutions[which]

                // Update TrackSelectionParameters based on selected resolution
                if (selectedResolution != null) {
                    // Set max video resolution
                    val newParams = trackSelecter.buildUponParameters()
                        .setMaxVideoSize(selectedResolution.first, selectedResolution.second)
                        .build()
                    trackSelecter.parameters = newParams
                } else {
                    // Clear constraints for "Auto" option
                    val newParams = trackSelecter.buildUponParameters()
                        .clearVideoSizeConstraints()
                        .build()
                    trackSelecter.parameters = newParams
                }
            }
            .show()

    }


    fun downloadAndSaveVideo(context: Context, videoId: String, database: VideoDatabase) {
        val firestore = FirebaseFirestore.getInstance()

        // Fetch video URL from Firestore
        firestore.collection("Banner").document("6WDOTl6Dozl0s3TOWO39").get()
            .addOnSuccessListener { document ->
                val videoUrl = document.getString("movieVideo") ?: return@addOnSuccessListener
                val videoName = document.getString("movieName") ?: "downloaded_video.mp4"

                val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl)
                val localFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                    videoName
                )

                storageRef.getFile(localFile)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Video downloaded!", Toast.LENGTH_SHORT).show()

                        // Save the video path in Room Database
                        CoroutineScope(Dispatchers.IO).launch {
                            val videoEntity = VideoEntity(videoName = videoName, videoPath = localFile.absolutePath)
                            database.videoDao().insertVideo(videoEntity)
                        }

                    }.addOnFailureListener {
                        Toast.makeText(context, "Download Failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get video URL from Firestore", Toast.LENGTH_LONG).show()
            }
    }
}