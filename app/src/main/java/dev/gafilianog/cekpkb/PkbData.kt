package dev.gafilianog.cekpkb

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

object PkbData {
    var pkbData: Pkb = Pkb()

    fun getPkbData(nomer: String, kodeBelakang: String, ctx: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = PkbBantenApi.retrofitService.getPkbData(nomer, kodeBelakang)

                if (response.isSuccessful && response.body() != null) {
                    val htmlDoc = response.body().let { Jsoup.parse(it!!) }
                    if (htmlDoc.toString().contains("Data Tidak ditemukan!")) {
                        pkbData.isFound = false
                    } else {
                        pkbData.merek = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "Merk")).text()
                        pkbData.model = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "Model")).text()
                        pkbData.tahun = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "Tahun")).text()

                        pkbData.pkbPokok = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "PKB POKOK")).text()
                        pkbData.pkbDenda = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "PKB DENDA")).text()
                        pkbData.pkb = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "PKB")).text()
                        pkbData.swdklljPokok = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "SWDKLLJ POKOK")).text()
                        pkbData.swdklljDenda = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "SWDKLLJ DENDA")).text()
                        pkbData.swdkllj = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "SWDKLLJ")).text()
                        pkbData.total = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "TOTAL PAJAK")).text()
                        pkbData.tanggal = htmlDoc.selectXpath(ctx.getString(R.string.xpath_value, "TGL AKHIR PKB")).text()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERR_RUNTIME", e.message.toString())
            }
        }
    }
}