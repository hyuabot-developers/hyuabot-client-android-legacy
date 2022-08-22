package app.kobuggi.hyuabot.ui

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.kobuggi.hyuabot.GlobalActivity
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ActivityMainBinding
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : GlobalActivity(), DialogInterface.OnDismissListener {
    private val vm by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    lateinit var navController: NavController
    private lateinit var assetPackManager: AssetPackManager
    private val fastFollowAssetPack = "fast_follow_pack"
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = vm

        val navigationFragmentHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as? NavHostFragment
        navController = navigationFragmentHost?.navController ?: return
        binding.bottomNavigationMenu.setupWithNavController(navController)
        initAssetPackManager()
        firebaseAnalytics = Firebase.analytics
        if (Build.VERSION.SDK_INT >= 33){
            askNotificationPermission()
        }
        Firebase.messaging.subscribeToTopic("notification")
            .addOnSuccessListener { Log.d("MainActivity", "Successfully subscribed to topic: notification") }
    }

    override fun onDismiss(dialogInterface: DialogInterface?) {
        recreate()
    }

    private fun initAssetPackManager() {
        assetPackManager = AssetPackManagerFactory.getInstance(applicationContext)
        val assetPath = getAbsolutePath(fastFollowAssetPack, "app.db")
        if (assetPath != null) {
            vm.upgradeDatabase(assetPath)
        }
        registerListener()
    }

    private fun getAbsolutePath(assetPackName: String, relativeAssetPath: String): String? {
        val assetPackPath = assetPackManager.getPackLocation(assetPackName) ?: return null

        val assetsFolderPath = assetPackPath.assetsPath()
        return "$assetsFolderPath/$relativeAssetPath"
    }

    private fun registerListener(){
        val fastFollowAssetPackPath = getAbsolutePath(fastFollowAssetPack, "")
        if (fastFollowAssetPackPath == null){
            assetPackManager.registerListener(assetPackStateUpdateListener)
            val assetPackList = arrayListOf<String>()
            assetPackList.add(fastFollowAssetPack)
            assetPackManager.fetch(assetPackList)
        } else {
            initFastFollow()
        }
    }

    private val assetPackStateUpdateListener =
        AssetPackStateUpdateListener { state ->
            when(state.status()){
                AssetPackStatus.PENDING -> {
                    Log.i("AssetPackManager", "PENDING")
                }
                AssetPackStatus.DOWNLOADING -> {
                    Log.i("AssetPackManager", "DOWNLOADING")
                }
                AssetPackStatus.COMPLETED -> {
                    Log.i("AssetPackManager", "INSTALLED")
                    initFastFollow()
                    recreate()
                }
                else -> {
                    Log.i("AssetPackManager", "UNKNOWN")
                }
            }
        }

    private fun initFastFollow() {
        val assetsPath = getAbsolutePath(fastFollowAssetPack, "app.db")
        vm.initializeDatabase(assetsPath!!)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (!isGranted){
            Toast.makeText(this, R.string.need_permission_get_notification, Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(33)
    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            TODO()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            TODO()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        assetPackManager.unregisterListener(assetPackStateUpdateListener)
    }
}