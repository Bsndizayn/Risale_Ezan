package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class RisaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML layout dosyasını inflate et
        return inflater.inflate(R.layout.fragment_risale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.htmlWebView)
        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(0x00000000) // Şeffaf arka plan
        // JavaScript arayüzünü WebView'a ekle
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        val menuHtml = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body{background-color:transparent;font-family:'Segoe UI',sans-serif;color:white;padding:16px;}
                    details{border-radius:12px;margin-bottom:12px;overflow:hidden;background:linear-gradient(145deg, #5a0908, #6f1c1c);}
                    summary{padding:18px 20px;font-size:17px;font-weight:600;cursor:pointer;list-style:none;}
                    summary::-webkit-details-marker { display: none; } /* Safari için ok işaretini kaldır */
                    .content{padding:16px 20px;background-color:rgba(0,0,0,0.2);border-top:1px solid rgba(255,255,255,0.1);}
                    .link-button{display:block;padding:10px;margin:5px 0;background-color:#8B4513;border-radius:8px;text-align:center;color:white;text-decoration:none;}
                </style>
            </head>
            <body>
                <details> <summary>SABAH NAMAZI</summary>
                    <div class="content">
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', null)">Baştan Sona<</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', 'nav-2')">Allahümme İnne Nükaddimü</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', 'nav-3')">Ecirnalar</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', 'nav-4')">Kısa Tesbihat Başlangıç</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', 'nav-5')">Sübhaneke Ya Allah</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH', 'nav-6')">Lâ Yestevi</a>
                    </div>
                </details>
                <details>
                    <summary>ÖĞLE NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE', null)">Baştan Sona</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE', 'nav-ogle-2')">Kısa Tesbihat Başlangıç</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE', 'nav-ogle-3')">Uzun Tesbihat Başlangıç</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE', 'nav-ogle-4')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE', 'nav-ogle-5')">Lekad Sadakallah</a>
                    </div>
                </details>
                <details>
                    <summary>İKİNDİ NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI', null)">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI', 'nav-ikindi-2')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI', 'nav-ikindi-3')">Ecirnalar</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI', 'nav-ikindi-4')">Ecirna Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI', 'nav-ikindi-5')">Amme</a>
                    </div>
                </details>
                <details>
                    <summary>AKŞAM NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', null)">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', 'nav-aksam-2')">Farzdan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', 'nav-aksam-3')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', 'nav-aksam-4')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', 'nav-aksam-5')">Esma Duasının Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM', 'nav-aksam-6')">Lâ Yestevi</a>
                    </div>
                </details>
                <details>
                    <summary>YATSI NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI', null)">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI', 'nav-yatsi-2')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI', 'nav-yatsi-3')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI', 'nav-yatsi-4')">Esma Duasının Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI', 'nav-yatsi-5')">AmenerResulü</a>
                    </div>
                </details>

                <script>
                    // Bu script, menülerden sadece birinin aynı anda açık kalmasını sağlar
                    document.querySelectorAll('details').forEach((el) => {
                        el.addEventListener('toggle', (e) => {
                            if (el.open) {
                                document.querySelectorAll('details').forEach((otherEl) => {
                                    if (otherEl !== el) {
                                        otherEl.open = false;
                                    }
                                });
                            }
                        });
                    });
                </script>

            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, menuHtml, "text/html", "UTF-8", null)
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun openPrayerTime(prayerTime: String, scrollToId: String?) {
            val actionId = when (prayerTime) {
                "SABAH" -> R.id.action_risaleFragment_to_sabahNamaziFragment
                "OGLE" -> R.id.action_risaleFragment_to_ogleNamaziFragment
                "IKINDI" -> R.id.action_risaleFragment_to_ikindiNamaziFragment
                "AKSAM" -> R.id.action_risaleFragment_to_aksamNamaziFragment
                "YATSI" -> R.id.action_risaleFragment_to_yatsiNamaziFragment
                else -> return
            }

            val bundle = Bundle().apply {
                if (scrollToId != null) {
                    putString("scrollToId", scrollToId)
                }
            }

            // UI thread'inde çalıştığından emin ol
            activity?.runOnUiThread {
                try {
                    findNavController().navigate(actionId, bundle)
                } catch (e: Exception) {
                    // Navigasyon hatasını yakala (örn: zaten o ekrandayken tekrar tıklama)
                    e.printStackTrace()
                }
            }
        }
    }
}