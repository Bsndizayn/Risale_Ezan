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
        return inflater.inflate(R.layout.fragment_risale, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = view.findViewById<WebView>(R.id.htmlWebView)
        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(0x00000000)
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
                    .content{padding:16px 20px;background-color:rgba(0,0,0,0.2);border-top:1px solid rgba(255,255,255,0.1);}
                    .link-button{display:block;padding:10px;margin:5px 0;background-color:#8B4513;border-radius:8px;text-align:center;color:white;text-decoration:none;}
                </style>
            </head>
            <body>
                <details>
                    <summary>SABAH NAMAZI</summary>
                    <div class="content">
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">TAMAMI</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">Farzdan Sonra</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">Duadan Sonra</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">Ecirnalar</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">Ecirna Duası</a>
                        <a href="#" class="link-button" onclick="Android.openPrayerTime('SABAH')">Lâ Yestevi</a>
                    </div>
                </details>
                <details>
                    <summary>ÖĞLE NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE')">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE')">Esma Duasının Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('OGLE')">Lekad Sadakallah</a>
                    </div>
                </details>
                <details>
                    <summary>İKİNDİ NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI')">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI')">Ecirnalar</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI')">Ecirna Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('IKINDI')">Amme</a>
                    </div>
                </details>
                <details>
                    <summary>AKŞAM NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">Farzdan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">Esma Duasının Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('AKSAM')">Lâ Yestevi</a>
                    </div>
                </details>
                <details>
                    <summary>YATSI NAMAZI</summary>
                    <div class="content">
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI')">TAMAMI</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI')">Duadan Sonra</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI')">Esma Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI')">Esma Duasının Duası</a>
                         <a href="#" class="link-button" onclick="Android.openPrayerTime('YATSI')">AmenerResulü</a>
                    </div>
                </details>

                <script>
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
        fun openPrayerTime(prayerTime: String) {
            val actionId = when (prayerTime) {
                "SABAH" -> R.id.action_risaleFragment_to_sabahNamaziFragment
                "OGLE" -> R.id.action_risaleFragment_to_ogleNamaziFragment
                "IKINDI" -> R.id.action_risaleFragment_to_ikindiNamaziFragment
                "AKSAM" -> R.id.action_risaleFragment_to_aksamNamaziFragment
                "YATSI" -> R.id.action_risaleFragment_to_yatsiNamaziFragment
                else -> return
            }
            activity?.runOnUiThread {
                findNavController().navigate(actionId)
            }
        }
    }
}