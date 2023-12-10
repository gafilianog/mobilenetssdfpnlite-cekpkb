package dev.gafilianog.cekpkb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.gafilianog.cekpkb.databinding.ActivityPkbBinding

class PkbActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPkbBinding
    private lateinit var detectedNumberLicense: String
    private lateinit var detectedSuffixLicense: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPkbBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Enable back button at topbar
        detectedNumberLicense = intent.getStringExtra("LICENSE_NUMBER")!!
        detectedSuffixLicense = intent.getStringExtra("LICENSE_SUFFIX")!!

        setUpDataInfo()
        binding.toolbarResult.subtitle = getString(
            R.string.detected_license_number,
            "AB",
            detectedNumberLicense,
            detectedSuffixLicense)
    }

    private fun setUpDataInfo() {
        // TODO: Handle number/money symbol
        binding.tvVehicleMerek.text = PkbData.pkbData.merek
        binding.tvVehicleModel.text = PkbData.pkbData.model
        binding.tvVehicleTahun.text = PkbData.pkbData.tahun

        binding.tvVehiclePkbPokok.text = PkbData.pkbData.pkbPokok
        binding.tvVehiclePkbDenda.text = PkbData.pkbData.pkbDenda
        binding.tvVehiclePkb.text = PkbData.pkbData.pkb
        binding.tvVehicleSwdklljPokok.text = PkbData.pkbData.swdklljPokok
        binding.tvVehicleSwdklljDenda.text = PkbData.pkbData.swdklljDenda
        binding.tvVehicleSwdkllj.text = PkbData.pkbData.swdkllj

        binding.tvTotalPaymentValue.text = "Rp${PkbData.pkbData.total}"
        binding.tvDueDateValue.text = "${PkbData.pkbData.tanggal}"
    }
}