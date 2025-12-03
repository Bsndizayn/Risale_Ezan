package com.example.risaleezanvakticompose.data.repository

import android.content.Context
import com.example.risaleezanvakticompose.domain.model.TesbihatCategory
import com.example.risaleezanvakticompose.domain.model.TesbihatSection
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TesbihatRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getAllCategories(): List<TesbihatCategory> {
        return listOf(
            TesbihatCategory.SABAH,
            TesbihatCategory.OGLE,
            TesbihatCategory.IKINDI,
            TesbihatCategory.AKSAM,
            TesbihatCategory.YATSI
        )
    }

    fun getSections(category: TesbihatCategory): List<TesbihatSection> {
        return when (category) {
            TesbihatCategory.SABAH -> getSabahSections()
            TesbihatCategory.OGLE -> getOgleSections()
            TesbihatCategory.IKINDI -> getIkindiSections()
            TesbihatCategory.AKSAM -> getAksamSections()
            TesbihatCategory.YATSI -> getYatsiSections()
        }
    }

    private fun getSabahSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "sabah_tamami",
                title = "Tamamı",
                description = "Sabah namazı tesbihatının baştan sona",
                scrollId = "nav-1"
            ),
            TesbihatSection(
                id = "sabah_farz_sonrasi",
                title = "Ecirna Minennar Duası",
                description = " Allahümme ecirnâ mine’n-nârٍ, Allahümme ecirnâ mine’n-nâr",
                scrollId = "nav-2"
            ),
            TesbihatSection(
                id = "sabah_kelime_tevhid",
                title = "Ecirnalardan Sonrası",
                description = "Subhânallah, Elhamdülillah, Allahu Ekber",
                scrollId = "nav-3"
            ),
            TesbihatSection(
                id = "sabah_ecirna",
                title = "Tercüman-ı İsm-i A’zâm ",
                description = "Sübḥâneke yâ Allah teʿâleyte yâ Raḥmân..",
                scrollId = "nav-4"
            ),
            TesbihatSection(
                id = "sabah_ayetelkursi",
                title = "La Yestevi",
                description = "Lâ yestevî ašḥâbü’n-nâri ve ašḥâbü’l-Cenneh...",
                scrollId = "nav-5"
            )
        )
    }


    private fun getOgleSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "ogle_tamami",
                title = "Tamamı",
                description = "Öğle namazı tesbihatı baştan sona",
                scrollId = "nav-ogle-1"
            ),
            TesbihatSection(
                id = "ogle_farz_sonrasi",
                title = "Kısa Tesbihat Başlangıç",
                description = "Subhânallah, Elhamdülillah, Allahu Ekber",
                scrollId = "nav-ogle-2"
            ),
            TesbihatSection(
                id = "ogle_sunnet_sonrasi",
                title = "Uzun Tesbihat Başlangıç",
                description = "İnnallâhe ve melâiketehû yüšallûne ʿale’nnebiy.",
                scrollId = "nav-ogle-3"
            ),
            TesbihatSection(
                id = "ogle_sunnet_sonrasi",
                title = "İsm-i Âzam Duası",
                description = "Yâ Cemîlu yâ Allah, Yâ Ḳarîbu yâ Allah",
                scrollId = "nav-ogle-4"
            ),
            TesbihatSection(
                id = "ogle_ismiazam",
                title = "Fetih Sûresi 27-29. Âyetler",
                description = "Leḳad šadaḳallâhü resûlehü...",
                scrollId = "nav-ogle-5"
            ),

        )
    }


    private fun getIkindiSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "ikindi_tamami",
                title = "Tamamı",
                description = "İkindi namazı tesbihatı baştan sona",
                scrollId = "nav-ikindi-1"
            ),
            TesbihatSection(
                id = "ikindi_farz_sonrasi",
                title = "Kısa Tesbihat Başlangıç",
                description = "Subhânallah, Elhamdülillah, Allahu Ekber",
                scrollId = "nav-ikindi-2"
            ),
            TesbihatSection(
                id = "ikindi_tesbih_salavat",
                title = "Uzun Tesbihat Başlangıç",
                description = "İnnallâhe ve melâiketehû yüšallûne ʿale’nnebiy.",
                scrollId = "nav-ikindi-3"
            ),
            TesbihatSection(
                id = "ikindi_ismiazam",
                title = "Tercüman-ı İsm-i A’zâm ",
                description = "Sübḥâneke yâ Allah teʿâleyte yâ Raḥmân..",
                scrollId = "nav-ikindi-4"
            ),

            TesbihatSection(
                id = "ikindi_nebe",
                title = "Nebe Sûresi",
                description = "ʿAmme yetesêelûn .ʿAni’n-nebei’l-ʿaẓîm ",
                scrollId = "nav-ikindi-5"
            )
        )
    }

    private fun getAksamSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "aksam_tamami",
                title = "Tamamı",
                description = "Akşam namazı tesbihatı baştan sona",
                scrollId = "nav-aksam-1"
            ),
            TesbihatSection(
                id = "aksam_farz_sonrasi",
                title = "Ecirna Minennar Duası",
                description = " Allahümme ecirnâ mine’n-nârٍ, Allahümme ecirnâ mine’n-nâr",
                scrollId = "nav-aksam-2"
            ),
            TesbihatSection(
                id = "aksam_sunnet_sonrasi",
                title = "Kısa Tesbihat Başlangıç",
                description = "Subhânallah, Elhamdülillah, Allahu Ekber",
                scrollId = "nav-aksam-3"
            ),
            TesbihatSection(
                id = "aksam_ecirna",
                title = "Uzun Tesbihat Başlangıç",
                description = "İnnallâhe ve melâiketehû yüšallûne ʿale’nnebiy.",
                scrollId = "nav-aksam-4"
            ),
            TesbihatSection(
                id = "aksam_ismiazam",
                title = "İsm-i Âzam Duası",
                description = "Yâ Cemîlu yâ Allah, Yâ Ḳarîbu yâ Allah",
                scrollId = "nav-aksam-5"
            ),

            TesbihatSection(
                id = "aksam_hasr",
                title = "Haşr Sûresi 20-24. Âyetler",
                description = "Haşr Sûresi'nin son âyetleri okunur",
                scrollId = "nav-aksam-6"
            )
        )
    }

    private fun getYatsiSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "yatsi_tamami",
                title = "Tamamı",
                description = "Yatsı namazı tesbihatı baştan sona",
                scrollId = "nav-yatsi-1"
            ),
            TesbihatSection(
                id = "yatsi_farz_sonrasi",
                title = "Kısa Tesbihat Başlangıç",
                description = "Subhânallah, Elhamdülillah, Allahu Ekber",
                scrollId = "nav-yatsi-2"
            ),
            TesbihatSection(
                id = "yatsi_duadan_sonra",
                title = "Uzun Tesbihat Başlangıç",
                description = "İnnallâhe ve melâiketehû yüšallûne ʿale’nnebiy.",
                scrollId = "nav-yatsi-3"
            ),
            TesbihatSection(
                id = "yatsi_ismiazam",
                 title = "İsm-i Âzam Duası",
                description = "Yâ Cemîlu yâ Allah, Yâ Ḳarîbu yâ Allah",
                scrollId = "nav-yatsi-4"
            ),
            TesbihatSection(
                id = "yatsi_amener_resulu",
                title = "Âmene’r-Resulü",
                description = "Bakara Sûresi 285–286. âyetleri okunur",
                scrollId = "nav-yatsi-5"
            )
        )
    }


    fun getHtmlContent(category: TesbihatCategory): String {
        val fileName = when (category) {
            TesbihatCategory.SABAH -> "tesbihat/sabah.html"
            TesbihatCategory.OGLE -> "tesbihat/ogle.html"
            TesbihatCategory.IKINDI -> "tesbihat/ikindi.html"
            TesbihatCategory.AKSAM -> "tesbihat/aksam.html"
            TesbihatCategory.YATSI -> "tesbihat/yatsi.html"
        }

        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            """
                <!DOCTYPE html>
                <html lang="tr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: RisaleSans;
                            padding: 20px;
                            text-align: center;
                            background-color: #fdf7e0;
                        }
                        h1 { color: #7c3a03; }
                        p { color: #5e4635; }
                    </style>
                </head>
                <body>
                    <h1>İçerik Yüklenemedi</h1>
                    <p>${category.displayName} tesbihatı yüklenirken bir hata oluştu.</p>
                    <p>Hata: ${e.message}</p>
                </body>
                </html>
            """.trimIndent()
        }
    }
}