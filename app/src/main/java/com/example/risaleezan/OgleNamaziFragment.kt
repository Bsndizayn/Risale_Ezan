package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton // YENİ IMPORT

class OgleNamaziFragment : Fragment() {
    private var scrollToId: String? = null
    private var currentTextZoom = 100 // Başlangıç yazı boyutu yüzdesi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scrollToId = it.getString("scrollToId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ogle_namazi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.ogle_namazi_webview)
        val zoomInButton = view.findViewById<FloatingActionButton>(R.id.zoom_in_button)
        val zoomOutButton = view.findViewById<FloatingActionButton>(R.id.zoom_out_button)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.setBackgroundColor(0x00000000)

        // ESKİ VE SORUNLU YAKINLAŞTIRMA AYARLARI TAMAMEN KALDIRILDI
        // settings.setSupportZoom(true)
        // settings.builtInZoomControls = true
        // settings.displayZoomControls = false
        // settings.loadWithOverviewMode = true
        // settings.useWideViewPort = true
        // settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

        // YAZILARIN KAYBOLMASINI ÖNLEYEN AYAR (BU KALSIN)
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // Başlangıç metin boyutunu ayarla
        settings.textZoom = currentTextZoom

        // BUTON TIKLAMA OLAYLARI
        zoomInButton.setOnClickListener {
            if (currentTextZoom < 200) { // Maksimum %200 büyüsün
                currentTextZoom += 10
                settings.textZoom = currentTextZoom
            }
        }

        zoomOutButton.setOnClickListener {
            if (currentTextZoom > 50) { // Minimum %50 küçülsün
                currentTextZoom -= 10
                settings.textZoom = currentTextZoom
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                scrollToId?.let { id ->
                    val script = "document.getElementById('$id').scrollIntoView({ behavior: 'smooth' });"
                    webView.evaluateJavascript(script, null)
                }
            }
        }


        val htmlContent = """
           <!DOCTYPE html>
            <html lang="tr">
            <head>
              <meta charset="UTF-8" />
              <title>Öğle Namazı Tesbihatı</title>
              
              <style>
                html { scroll-behavior: smooth; }
                
                .side-nav {
                  position: fixed;
                  top: 20px;
                  left: 20px;
                  background-color: rgba(94, 70, 53, 0.9);
                  border: 1px solid #d6b97c;
                  padding: 10px 15px;
                  border-radius: 8px;
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.25);
                  z-index: 1000;
                  font-family: sans-serif;
                  visibility: hidden;
                  opacity: 0;
                  transition: opacity 0.3s ease-in-out;
                }
                .side-nav.visible {
                  visibility: visible;
                  opacity: 1;
                }
                .side-nav ul { list-style-type: none; margin: 0; padding: 0; }
                .side-nav li { margin-bottom: 8px; }
                .side-nav a { text-decoration: none; color: #fdf7e0; font-weight: bold; font-size: 14px; }
                .side-nav a:hover { text-decoration: underline; }
                
                @font-face {
                  font-family: 'ScheherazadeLocal';
                  src: url('file:///android_asset/fonts/ScheherazadeNew-Regular.ttf') format('truetype');
                  font-weight: 400; /* Regular için */
                }
                @font-face {
                  font-family: 'ScheherazadeLocal';
                  src: url('file:///android_asset/fonts/ScheherazadeNew-Bold.ttf') format('truetype');
                  font-weight: 500; /* Bold için */
                }

                body {
                  background-color: #fdf7e0;
                  color: #333;
                  font-family: 'Georgia', serif;
                  margin: 0;
                  padding: 0;
                }

                .page {
                  max-width: 750px;
                  margin: auto;
                  padding: 30px 20px 60px;
                  line-height: 2;
                }
                h1 {
                  text-align: center;
                  font-size: 28px;
                  color: #7c3a03;
                  margin-top: 20px;
                  border-bottom: 2px solid #7c3a03;
                  padding-bottom: 10px;
                }
                h2 {
                  text-align: center;
                  font-size: 24px;
                  color: #7c3a03;
                  margin-top: 40px;
                  border-bottom: 1px solid #d6b97c;
                  padding-bottom: 8px;
                }
                .arabic {
                  font-size: 2.1em;
                  text-align: center;
                  direction: rtl;
                  font-family: 'ScheherazadeLocal', serif; 
                  font-weight: 300;
                  margin: 25px 0 15px;
                  color: #111;
                  line-height: 1.9;
                }
                  
                .red-text { color: #b30000; font-weight: 500; }
                .red2-text { color: #b30000; font-weight: 300; }
                .blue-text { color: #0056b3; font-weight: 500; }
                
                p { font-size: 1em; color: #5e4635; }
                
                .inline-arabic-large {
                  font-family: 'ScheherazadeLocal', serif;
                  font-size: 1.8em;
                  vertical-align: middle;
                }
                  
                .tesbih-line {
                  text-align: center;
                  font-size: 1.1em;
                  color: #5e4635;
                  margin: 10px 0;
                }

                .tesbih-arabic {
                  font-family: 'ScheherazadeLocal', serif;
                  font-size: 2em;
                  vertical-align: middle;

                  margin-left: 15px;
                }
              </style>
            </head>
            <body>
              <nav class="side-nav" id="sideNavMenu">
                <ul>
                  <li><a href="#nav-ogle-2" onclick="toggleNavMenu()">Duadan Sonra</a></li>
                  <li><a href="#nav-ogle-3" onclick="toggleNavMenu()">Esma Duası</a></li>
                  <li><a href="#nav-ogle-4" onclick="toggleNavMenu()">Esma Duasının Duası</a></li>
                  <li><a href="#nav-ogle-5" onclick="toggleNavMenu()">Lekad Sadakallah</a></li>
                </ul>
              </nav>

              <div class="page">
                <h1 id="nav-ogle-1">ÖĞLE NAMAZI TESBİHATI</h1>
                <p><strong>Öğle Namazının farzını kılıp, selam verdikten sonra</strong></p>
                <p class="arabic red2-text"><span class="blue-text">اَللّٰهُمَّ</span> اَنْتَ السَّلاَمُ وَ مِنْكَ السَّلاَمُ&nbsp;تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ</p>
                <p><strong>Denilir, eller yukarı kaldırılıp açılarak <span class="red-text"><b>SALÂTEN TÜNCÎNÂ</b></span> duası okunur:</strong></p>
                <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ&nbsp;صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ&nbsp;اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                <p id="nav-ogle-2"><strong>Akabinde Öğle Namazının son sünneti kılınıp; Salâvat-ı Şerife getirilip, bu dua okunur:</strong></p>
                <p class="arabic">سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ&nbsp;وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ</p>
                <h2>Âyetü’l Kürsî</h2>
                <p><strong>Âyetü’l Kürsî (Bakara Sûresi 255) okunur:</strong></p>
                <p class="arabic"><span class="red-text">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br></span> اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُhُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ</p>
                <p><strong>Daha sonra tesbihleri çekilir.</strong></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">سُبْحَانَ اللّٰهِ</span></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">اَلْحَمْدُ لِلّٰهِ</span></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">اَللّٰهُ اَكْبَرُ</span></p>
                <p><strong>Tesbih çekildikten sonra</strong></p>
                <p class="arabic red2-text">لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ&nbsp;لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ&nbsp;وَهُوَ حَىٌّ لاَ يَمُوتُ&nbsp;بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ</p>
                <p><strong>Denilir ve sonra dua edilir. Duadan sonra bir defa<span class="red-text inline-arabic-large"> فَاعْلَمْ اَنَّهُ </span>diyerek kelime-i tevhide geçilir.</strong></p>
                <p  id="nav-ogle-3"><strong>33 defa<span class="red-text inline-arabic-large"> لاۤ اِلٰهَ اِلاَّ&nbsp;اللّٰهُ&nbsp;</span>okunduktan sonra<span class="red-text inline-arabic-large">&nbsp;مُحَمَّدٌ رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ&nbsp;سَلَّمَ </span>diyerek tamamlanır.</strong></p>
                <p class="arabic"><span class="red-text">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br></span>اِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا&nbsp;٭&nbsp;لَبَّيْكَ<br><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّdٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا كَث۪يرًا<br>صَلِّ وَ سَلِّمْ يَا رَبِّ&nbsp;عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ<br><span class="red2-text">اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍعَلَيْكَ يَا رَسُولَ اللّٰهِ<br>اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍعَلَيْكَ يَا حَب۪يبَ اللّٰهِ<br>اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَمٍعَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ<br></span><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ&nbsp;بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ&nbsp;وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِاُسْتَادِنَا سَع۪يدِ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ بِطَلَبَةِ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ&nbsp;يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا&nbsp;اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ</p>
                <h2 id="nav-ogle-4">İsm-i A’zâm Duası</h2>
                <p><strong>İsm-i A’zâm duası okunur:</strong></p>
                <p class="arabic"><span class="red-text">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br></span><span class="red-text">يَا جَم۪يلُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا قَر۪يبُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مُج۪يبُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا حَب۪يبُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا رَؤُفُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا عَطُوفُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مَعْرُوفُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا لَط۪يفُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا عَظ۪يمُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا حَنَّانُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مَنَّانُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا دَيَّانُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا سُبْحَانُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَٓا اَمَانُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا بُرْهَانُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا سُلْطَانُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مُسْتَعَانُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا مُحْسِنُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مُتَعَالُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا رَحْمٰنُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا رَح۪يمُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا كَر۪يمُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا مَج۪يدُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا فَرْدُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا وِتْرُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَٓا اَحَدُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا صَمَدُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا مَحْمُودُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا صَادِقَ الْوَعْدِ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا عَلِىُّ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا غَنِىُّ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا شَاف۪ى</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا كَاف۪ى</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا مُعَاف۪ى</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا بَاق۪ى</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا هَاد۪ى</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا قَادِرُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا سَاتِرُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا قَهَّارُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا جَبَّارُ</span> يَٓا اَللّٰهُ<br><span class="red-text">يَا غَفَّارُ</span> يَٓا اَللّٰهُ ٭ <span class="red-text">يَا فَتَّاحُ</span> يَٓا اَللّٰهُ</p>
                <p><strong>Avuçlar yukarı gelecek şekilde eller açılır:</strong></p>
                <p class="arabic">يَا رَبَّ السَّمٰوَاتِ وَ اْلاَرْضِ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ<br>اَسْئَلُكَ بِحَقِّ هٰذِهِ اْلاَسْمَٓاءِ كُلِّهَٓا اَنْ تُصَلِّىَ عَلٰى sَيِّدِنَا مُحَمَّدٍ وَ عَلٰٓى اٰلِ مُحَمَّدٍ وَ ارْحَمْ مُحَمَّدًا&nbsp;كَمَا صَلَّيْتَ وَ sَلَّمْتَ وَ بَارَكْتَ وَ رَحِمْتَ وَ تَرَحَّمْتَ عَلٰٓى اِبرَاه۪يمَ وَ عَلٰٓى اٰلِ اِبْرَاه۪يمَ فِى الْعَالَم۪ينَ&nbsp;رَبَّنَٓا اِنَّكَ حَم۪يدٌ مَج۪يدٌ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ وَ الْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                <h2 id="nav-ogle-5">Fetih Sûresi’nin 27-29. Âyetleri</h2>
                <p><strong>Fetih Sûresi’nin 27-29. Âyetleri okunur:</strong></p>
                <p class="arabic"><span class="red-text">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br></span>لَقَدْ صَدَقَ اللّٰهُ رَسُولَهُ الرُّءْيَا بِالْحَقِّۚ لَتَدْخُلُنَّ الْمَسْجِدَ الْحَرَامَ اِنْ شَٓاءَ اللّٰهُ اٰمِن۪ينَۙ مُحَلِّق۪ينَ رُوُ۫ٔسَكُمْ وَمُقَصِّر۪ينَۙ لاَ تَخَافُونَۜ فَعَلِمَ مَا لَمْ تَعْلَمُوا فَجَعَلَ مِنْ دُونِ ذٰلِكَ فَتْحًا قَر۪يبًا<br>هُوَ الَّذ۪ٓى اَرْسَلَ رَسُولَهُ بِالْهُدٰى وَد۪ينِ الْحَقِّ&nbsp;لِيُظْهِرَهُ عَلَى الدّ۪ينِ كُلِّه۪ۜ وَكَفٰى بِاللّٰهِ شَه۪يدًۜا<br>مُحَمَّدٌ رَسُولُ اللّٰهِۜ وَالَّذ۪ينَ مَعَهُٓ اَشِدَّٓاءُ عَلَى الْكُفَّارِ رُحَمَٓاءُ بَيْنَهُمْ تَرٰيهُمْ رُكَّعًا سُجَّدًا يَبْتَغُونَ فَضْلاً مِنَ اللّٰهِ وَرِضْوَانًۘا s۪يمَاهُمْ ف۪ى وُجُوهِهِمْ مِنْ اَثَرِ السُّجُودِۜ ذٰلِكَ مَثَلُهُمْ فِى التَّوْرٰيةِۚۛ وَمَثَلُهُمْ فِى اْلاِنْج۪يلِ۠ۛ كَزَرْعٍ اَخْرَجَ שַׁטְאֵהוּ فَاٰזَرَهُ فَاسْتَغْلَظَ فَاسْتَوٰى عَلٰى sُوقِه۪ يُعْجِبُ الزُّرَّاعَ لِيَغ۪يظَ بِهِمُ الْكُفَّارَۜ&nbsp;وَعَدَ اللّٰهُ الَّذ۪ينَ اٰمَنُوا وَعَمِلُوا الصَّالِحَاتِ مِنْهُمْ مَغْfِرَةً وَاَجْرًا عَظ۪يمًا</p>
                <h2>Fatiha-i Şerife</h2>
                <p><strong>Fatiha-i Şerife ile tesbihat tamamlanır:</strong></p>
                <p class="arabic"><span class="red-text">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br></span>اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ<br>اَلرَّחْمٰنِ الرَّח۪يمِۙ<br>מָלִךِ يَوْمِ الدّ۪ينِۜ<br>אِيَّךָ نَعْبُدُ وَאِيَّךָ نَسْتَع۪ينُۜ<br>אִהְדִּנَا الصِّרָاطَ الْمُসְתַּק۪יםَۙ<br>صِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ</p>
              </div>

              <script>
                function toggleNavMenu() {
                  var menu = document.getElementById('sideNavMenu');
                  menu.classList.toggle('visible');
                }
              </script>
            </body>
            </html>
        """.trimIndent() // SİZİN HTML İÇERİĞİNİZ BURADA DEVAM EDİYOR

        // base-url'i doğru ayarlamak, yerel fontların çalışması için kritiktir.
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null)
    }
}