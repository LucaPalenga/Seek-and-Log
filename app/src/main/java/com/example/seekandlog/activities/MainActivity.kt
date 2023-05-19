package com.example.seekandlog.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.seekandlog.R
import com.example.seekandlog.databinding.ActivityMainBinding
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {

    private val dynamicFeatureName by lazy { getString(R.string.seekappfeature_name) }

    private val binding: ActivityMainBinding
            by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val splitInstallManager: SplitInstallManager
            by lazy { SplitInstallManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Observe dynamic module load
        splitInstallManager.registerListener {
            when (it.status()) {

                SplitInstallSessionStatus.INSTALLED -> {
                    binding.loadModuleBtn.text = getString(R.string.install_completed)
                    binding.loadModuleBtn.isEnabled = false

                    if (splitInstallManager.installedModules.contains(dynamicFeatureName)) {
                        startActivity(AppSelectionActivity.buildIntent(this))
                    } else {
                        AlertDialog.Builder(this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.module_not_installed)
                            .show()
                    }
                }

                SplitInstallSessionStatus.DOWNLOADING -> {
                    binding.progressBar.isVisible = true
                    binding.loadModuleBtn.text = getString(R.string.downloading)
                }

                SplitInstallSessionStatus.DOWNLOADED -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.download_completed),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                SplitInstallSessionStatus.FAILED -> {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.download_failed),
                        Toast.LENGTH_SHORT
                    ).show()

                    resetLoadBtn()

                }

                else -> {
                    resetLoadBtn()
                }
            }
        }

        val dynamicModuleInstallRequest = SplitInstallRequest.newBuilder()
            .addModule(dynamicFeatureName)
            .build()

        // start install dynamic module on button click
        binding.loadModuleBtn.setOnClickListener {
            Toast.makeText(
                applicationContext,
                getString(R.string.installing_module),
                Toast.LENGTH_SHORT
            ).show()

            splitInstallManager.startInstall(dynamicModuleInstallRequest)
        }
    }

    override fun onStart() {
        super.onStart()
        resetLoadBtn()
    }

    private fun resetLoadBtn() {
        binding.progressBar.isVisible = false
        binding.loadModuleBtn.text = getString(R.string.load_module)
        binding.loadModuleBtn.isEnabled = true
    }
}