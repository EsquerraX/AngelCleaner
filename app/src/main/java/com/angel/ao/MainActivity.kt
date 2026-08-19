
package com.angel.ao
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.angel.ao.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import java.io.File
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    private val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) { mInterstitialAd = ad }
            override fun onAdFailedToLoad(p0: LoadAdError) { mInterstitialAd = null }
        })
        binding.btnLimpiar.setOnClickListener { limpiarReal() }
        binding.btnLimpiarJuegos.setOnClickListener { abrirLimpiador() }
    }
    fun limpiarReal() {
        var bytes = 0L
        try {
            bytes += deleteCache(cacheDir)
            externalCacheDir?.let { bytes += deleteCache(it) }
            codeCacheDir?.let { bytes += deleteCache(it) }
            val mb = bytes / 1024 / 1024
            binding.txtResultado.text = "✅ REAL: $mb MB liberados"
            mInterstitialAd?.show(this)
            Toast.makeText(this, "Limpieza REAL $mb MB", Toast.LENGTH_LONG).show()
        } catch (e: Exception) { binding.txtResultado.text = "Error: ${e.message}" }
    }
    fun deleteCache(dir: File): Long {
        var size = 0L
        if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.forEach { f -> size += if (f.isDirectory) deleteCache(f) else f.length(); f.delete() }
        } else if (dir.exists()) { size = dir.length(); dir.delete() }
        return size
    }
    fun abrirLimpiador() {
        try { startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)) }
        catch (e: Exception) { startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)) }
    }
}
