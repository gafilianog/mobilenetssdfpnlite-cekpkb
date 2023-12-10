package dev.gafilianog.cekpkb

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import dev.gafilianog.cekpkb.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TreeMap

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var detectedPrefixLicense: String
    private lateinit var detectedNumberLicense: String
    private lateinit var detectedSuffixLicense: String
    private lateinit var currentPhotoPath: String
    private val instructions = arrayOf(
        "Tekan tombol 'Foto Plat' di bawah yang akan membuka kamera ponsel",
        "Pegang ponsel secara horizontal/lanskap",
        "Arahkan kamera pada plat nomor dengan jarak yang dekat",
        "Tekan tombol ambil gambar dan klik 'OK' jika gambar dirasa sudah sesuai",
        "Pastikan hasil deteksi nomor polisi yang muncul sudah sesuai",
        "Tekan tombol 'Cari PKB' yang muncul setelah deteksi nomor"
    )
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setViewAndDetect(getCapturedImage())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(
            this,
            R.layout.item_row_instruction,
            R.id.tv_item_instruction,
            instructions
        )
        binding.btnSearchPkb.visibility = View.GONE
        binding.lvInstructions.adapter = adapter
        binding.btnTakePicture.setOnClickListener(this)
        binding.btnSearchPkb.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_take_picture -> {
                takePictureIntent()
            }
            R.id.btn_search_pkb -> {
//                startActivity(
//                    Intent(this, PkbActivity::class.java)
//                        .putExtra("LICENSE_NUMBER", detectedNumberLicense)
//                        .putExtra("LICENSE_SUFFIX", detectedSuffixLicense)
//                )
                startActivity(
                    Intent(this, PkbActivity::class.java)
                        .putExtra("LICENSE_NUMBER", "2487")
                        .putExtra("LICENSE_SUFFIX", "AK")
                )
                overridePendingTransition(0,0)
                finish()
            }
        }
    }

    private fun takePictureIntent() {
//        detectedPrefixLicense = ""
//        detectedNumberLicense = ""
//        detectedSuffixLicense = ""
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (e: IOException) {
                        Log.e(TAG, e.message.toString())
                        null
                    }

                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "dev.gafilianog.cekpkb.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        resultLauncher.launch(takePictureIntent)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun setViewAndDetect(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
    }

    private fun getCapturedImage(): Bitmap {
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(currentPhotoPath, this)
            inJustDecodeBounds = false
            inMutable = true
        }

        return BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        val image = TensorImage.fromBitmap(bitmap)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(10)
            .setScoreThreshold(0.2f)
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "platenum_quant_metadata.tflite",
            options
        )

        val results = detector.detect(image)

        val detectedLicenseNumber = getDetectionLabel(results)

        // TODO: Handle error detection and string modification
        // TODO: Handle data fetch properly
        if (detectedLicenseNumber.length >= 8) {
            detectedPrefixLicense = detectedLicenseNumber.substring(0, 2)
            detectedNumberLicense = detectedLicenseNumber.substring(2, 6)
            detectedSuffixLicense = detectedLicenseNumber.substring(6, detectedLicenseNumber.length)
        }

//        PkbData.getPkbData(detectedNumberLicense, detectedSuffixLicense, this)
        PkbData.getPkbData("2487", "AK", this)

        runOnUiThread {
            binding.tvLicenseNumber.text = detectedLicenseNumber
//            binding.tvLicenseNumber.text = getString(
//                R.string.detected_license_number,
//                detectedPrefixLicense,
//                detectedNumberLicense,
//                detectedSuffixLicense)
            binding.btnSearchPkb.visibility = View.VISIBLE
        }
    }

    private fun getDetectionLabel(results: List<Detection>): String {
        val licenseNumber = StringBuilder()
        val classMap: MutableMap<Float, String> = HashMap()

        results.forEach { obj ->
            obj.categories.forEach { category -> classMap[obj.boundingBox.left] = category.label }
        }

        val sortedClassMap = TreeMap(classMap)
        sortedClassMap.values.forEach { char -> licenseNumber.append(char) }

        return licenseNumber.toString()
    }

    companion object {
        const val TAG = "TFLite - ODT"
    }
}