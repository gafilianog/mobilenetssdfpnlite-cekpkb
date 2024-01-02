package dev.gafilianog.cekpkb

import org.jsoup.Jsoup
import retrofit2.Response

object PkbData {

    private const val XPATH_VALUE = "//table[contains(@class,'table-bordered')]//td[normalize-space()='%s']/following-sibling::td"
    var pkbData: Pkb = Pkb()

    suspend fun getPkbData(nomer: String, kodeBelakang: String): Response<String> {
        val response = PkbBantenApi.retrofitService.getPkbData(nomer, kodeBelakang)

        if (response.isSuccessful && response.body() != null) {
            val htmlDoc = response.body().let { Jsoup.parse(it!!) }
            if (htmlDoc.toString().contains("Data Tidak ditemukan!")) {
                pkbData.isFound = false
            } else {
                pkbData.merek = htmlDoc.selectXpath(String.format(XPATH_VALUE, "Merk")).text()
                pkbData.model = htmlDoc.selectXpath(String.format(XPATH_VALUE, "Model")).text()
                pkbData.tahun = htmlDoc.selectXpath(String.format(XPATH_VALUE, "Tahun")).text()

                pkbData.pkbPokok = htmlDoc.selectXpath(String.format(XPATH_VALUE, "PKB POKOK")).text()
                pkbData.pkbDenda = htmlDoc.selectXpath(String.format(XPATH_VALUE, "PKB DENDA")).text()
                pkbData.pkb = htmlDoc.selectXpath(String.format(XPATH_VALUE, "PKB")).text()
                pkbData.swdklljPokok = htmlDoc.selectXpath(String.format(XPATH_VALUE, "SWDKLLJ POKOK")).text()
                pkbData.swdklljDenda = htmlDoc.selectXpath(String.format(XPATH_VALUE, "SWDKLLJ DENDA")).text()
                pkbData.swdkllj = htmlDoc.selectXpath(String.format(XPATH_VALUE, "SWDKLLJ")).text()
                pkbData.total = htmlDoc.selectXpath(String.format(XPATH_VALUE, "TOTAL PAJAK")).text()
                pkbData.tanggal = htmlDoc.selectXpath(String.format(XPATH_VALUE, "TGL AKHIR PKB")).text()
            }
        }

        return response
    }
}