package dev.gafilianog.cekpkb

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.gafilianog.cekpkb.databinding.ActivityPkbBinding

class PkbActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPkbBinding
    private lateinit var detectedNumberLicense: String
    private lateinit var detectedSuffixLicense: String
    private lateinit var data: Pkb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPkbBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarResult)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detectedNumberLicense = intent.getStringExtra("LICENSE_NUMBER")!!
        detectedSuffixLicense = intent.getStringExtra("LICENSE_SUFFIX")!!

        data = PkbData.pkbData
        setUpDataInfo()
        binding.toolbarResult.subtitle = getString(
            R.string.txt_detected_nopol,
            "AB",
            detectedNumberLicense,
            detectedSuffixLicense
        )
    }

    private fun setUpDataInfo() {
        if (data.isFound) {
            binding.tvVehicleMerek.text = data.merek
            binding.tvVehicleModel.text = data.model
            binding.tvVehicleTahun.text = data.tahun

            binding.tvVehiclePkbPokok.text = getString(R.string.currency_template, data.pkbPokok)
            binding.tvVehiclePkbDenda.text = getString(R.string.currency_template, data.pkbDenda)
            binding.tvVehiclePkb.text = getString(R.string.currency_template, data.pkb)
            binding.tvVehicleSwdklljPokok.text = getString(R.string.currency_template, data.swdklljPokok)
            binding.tvVehicleSwdklljDenda.text = getString(R.string.currency_template, data.swdklljDenda)
            binding.tvVehicleSwdkllj.text = getString(R.string.currency_template, data.swdkllj)

            binding.tvTotalPaymentValue.text = getString(R.string.currency_template, data.total)
            binding.tvDueDateValue.text = data.tanggal
        } else {
            binding.tvNotFoundResponse.visibility = View.VISIBLE

            binding.cardVehicleTaxInfo.visibility = View.GONE
            binding.tvTotalPaymentInfo.visibility = View.GONE
            binding.tvTotalPaymentValue.visibility = View.GONE
            binding.tvDueDateInfo.visibility = View.GONE
            binding.tvDueDateValue.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}