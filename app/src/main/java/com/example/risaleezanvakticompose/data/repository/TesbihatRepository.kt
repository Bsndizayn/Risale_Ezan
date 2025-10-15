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
                description = "Sabah namazı tesbihatının tamamını göster",
                scrollId = "nav-1"
            ),
            TesbihatSection(
                id = "sabah_farz_sonrasi",
                title = "Farz Sonrası Tesbihat",
                description = "Sabah namazı farzından sonra okunur",
                scrollId = "nav-1"
            ),
            TesbihatSection(
                id = "sabah_kelime_tevhid",
                title = "Kelime-i Tevhid ve Dualar",
                description = "La ilahe illallah zikri ve dualar",
                scrollId = "nav-2"
            ),
            TesbihatSection(
                id = "sabah_ecirna",
                title = "Ecirna Minennar Duası",
                description = "Ateşten korunma duası",
                scrollId = "nav-3"
            ),
            TesbihatSection(
                id = "sabah_ayetelkursi",
                title = "Âyetü'l Kürsî ve Tesbihat",
                description = "Âyetü'l Kürsî, 33’lü tesbihler ve dua",
                scrollId = "nav-4"
            )
        )
    }


    private fun getOgleSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "ogle_tamami",
                title = "Tamamı",
                description = "Öğle namazı tesbihatının tamamını göster",
                scrollId = "nav-ogle-1"
            ),
            TesbihatSection(
                id = "ogle_farz_sonrasi",
                title = "Farz Sonrası Tesbihat",
                description = "Öğle namazı farzından sonra okunur",
                scrollId = "nav-ogle-1"
            ),
            TesbihatSection(
                id = "ogle_sunnet_sonrasi",
                title = "Son Sünnet Sonrası Tesbihat",
                description = "Son sünnet namazından sonra okunur",
                scrollId = "nav-ogle-2"
            ),
            TesbihatSection(
                id = "ogle_ismiazam",
                title = "İsm-i Âzam Duası",
                description = "Allah’ın isimleriyle yapılan dua",
                scrollId = "nav-ogle-3"
            ),
            TesbihatSection(
                id = "ogle_esma_duasinin_duasi",
                title = "Esma Duasının Duası",
                description = "Esma duasından sonra okunur",
                scrollId = "nav-ogle-4"
            ),
            TesbihatSection(
                id = "ogle_fetih",
                title = "Fetih Sûresi 27-29. Âyetler",
                description = "Lekad Sadakallah ayetleri okunur",
                scrollId = "nav-ogle-5"
            )
        )
    }


    private fun getIkindiSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "ikindi_tamami",
                title = "Tamamı",
                description = "İkindi namazı tesbihatının tamamını göster",
                scrollId = "nav-ikindi-1"
            ),
            TesbihatSection(
                id = "ikindi_farz_sonrasi",
                title = "Farz Sonrası Tesbihat",
                description = "İkindi namazı farzından sonra okunur",
                scrollId = "nav-ikindi-1"
            ),
            TesbihatSection(
                id = "ikindi_tesbih_salavat",
                title = "Tesbihat ve Salavatlar",
                description = "Tesbih, salavat ve dualar bölümü",
                scrollId = "nav-ikindi-2"
            ),
            TesbihatSection(
                id = "ikindi_ismiazam",
                title = "Tercüman-ı İsm-i Âzam Duası",
                description = "Allah’ın en büyük ismiyle yapılan dua",
                scrollId = "nav-ikindi-3"
            ),
            TesbihatSection(
                id = "ikindi_ecirna",
                title = "Ecirna Minennar Duası",
                description = "Ateşten korunma duası",
                scrollId = "nav-ikindi-4"
            ),
            TesbihatSection(
                id = "ikindi_nebe",
                title = "Nebe Sûresi",
                description = "Nebe Sûresi okunur",
                scrollId = "nav-ikindi-5"
            )
        )
    }

    private fun getAksamSections(): List<TesbihatSection> {
        return listOf(
            TesbihatSection(
                id = "aksam_tamami",
                title = "Tamamı",
                description = "Akşam namazı tesbihatının tamamını göster",
                scrollId = "nav-aksam-1"
            ),
            TesbihatSection(
                id = "aksam_farz_sonrasi",
                title = "Farz Sonrası Tesbihat",
                description = "Akşam namazı farzından sonra okunur",
                scrollId = "nav-aksam-1"
            ),
            TesbihatSection(
                id = "aksam_sunnet_sonrasi",
                title = "Sünnet Sonrası Tesbihat",
                description = "Son sünnet namazından sonra okunur",
                scrollId = "nav-aksam-2"
            ),
            TesbihatSection(
                id = "aksam_ecirna",
                title = "Ecirna Minennar Duası",
                description = "Ateşten korunma duası",
                scrollId = "nav-aksam-3"
            ),
            TesbihatSection(
                id = "aksam_ismiazam",
                title = "İsm-i A’zâm Duası",
                description = "Allah'ın en büyük isimleriyle yapılan dua",
                scrollId = "nav-ogle-3" // HTML'de böyle geçiyor
            ),
            TesbihatSection(
                id = "aksam_ismiazam_sonrasi",
                title = "İsm-i A’zâm Sonrası Dua",
                description = "Eller açılarak yapılan dua",
                scrollId = "nav-ogle-4" // HTML'de bu kısımda dua geçiyor
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
                description = "Yatsı namazı tesbihatının tamamını göster",
                scrollId = "nav-yatsi-1"
            ),
            TesbihatSection(
                id = "yatsi_farz_sonrasi",
                title = "Farz Sonrası Tesbihat",
                description = "Yatsı namazı farzından sonra okunur",
                scrollId = "nav-yatsi-1"
            ),
            TesbihatSection(
                id = "yatsi_duadan_sonra",
                title = "Duadan Sonra",
                description = "Son sünnet ve vitr namazından sonra okunur",
                scrollId = "nav-yatsi-2"
            ),
            TesbihatSection(
                id = "yatsi_ismiazam",
                title = "İsm-i Âzam Duası",
                description = "Allah’ın isimleriyle yapılan dua",
                scrollId = "nav-yatsi-3"
            ),
            TesbihatSection(
                id = "yatsi_ismiazam_duasi",
                title = "İsm-i Âzam Duasının Duası",
                description = "İsm-i Âzam duasından sonra eller açılarak okunur",
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
                            font-family: Arial, sans-serif;
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