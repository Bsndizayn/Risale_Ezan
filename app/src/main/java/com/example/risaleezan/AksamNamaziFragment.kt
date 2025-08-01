package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AksamNamaziFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_aksam_namazi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.aksam_namazi_webview)
        val zoomInButton = view.findViewById<FloatingActionButton>(R.id.zoom_in_button)
        val zoomOutButton = view.findViewById<FloatingActionButton>(R.id.zoom_out_button)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        webView.setBackgroundColor(0x00000000)

        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        settings.textZoom = currentTextZoom

        zoomInButton.setOnClickListener {
            if (currentTextZoom < 200) {
                currentTextZoom += 10
                settings.textZoom = currentTextZoom
            }
        }

        zoomOutButton.setOnClickListener {
            if (currentTextZoom > 50) {
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
              <title>Akşam Namazı Tesbihatı</title>
              
              <style>
                @font-face {
                  font-family: 'ScheherazadeLocal';
                  src: url('file:///android_asset/fonts/ScheherazadeNew-Regular.ttf') format('truetype');
                  font-weight: 400;
                }
                @font-face {
                  font-family: 'ScheherazadeLocal';
                  src: url('file:///android_asset/fonts/ScheherazadeNew-Bold.ttf') format('truetype');
                  font-weight: 500;
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
                h1, h2 {
                  text-align: center;
                  font-size: 28px;
                  color: #7c3a03;
                  margin-top: 20px;
                  border-bottom: 2px solid #7c3a03;
                  padding-bottom: 10px;
                }
                h2 {
                  font-size: 24px;
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
                p > strong { color: #5e4635; font-style: italic; }
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
                h1, h2, .arabic, p {
                   max-width: 100%;
                   word-wrap: break-word;
                }
              </style>
            </head>
            <body>
              <div class="page">
                <h1 id="nav-aksam-1">AKŞAM NAMAZI TESBİHATI</h1>
                <p><strong>Akşam Namazının farzını kılıp, selam verdikten sonra</strong></p>
                <p class="arabic red2-text"><span class="blue-text">اَللّٰهُمَّ</span> اَنْتَ السَّلاَمُ وَ مِنْكَ السَّلاَمُ&nbsp;تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ</p>
                <p><strong>Denilir, eller yukarı kaldırılıp açılarak Salâten Tüncînâ duası okunur:</strong></p>
                <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ&nbsp;صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ&nbsp;اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                
                <p id="nav-aksam-2"><strong>Sonrasında Akşam Namazının son sünneti kılınıp; bir defa <span class="arabic red2-text" style="font-size:1.1em; vertical-align:middle;">اٰمَنَّا&nbsp;بِاَنَّهُ</span> deyip, 9 defa</strong></p>
                <p class="arabic red2-text">لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ&nbsp;يُحْي۪ى وَ يُم۪يتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ</p>
                <p><strong>Okunur. Onuncuda ise;</strong></p>
                <p class="arabic red2-text">لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ&nbsp;بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ</p>
                
                <p id="nav-aksam-3"><strong>Sonra avuç içleri aşağıya doğru çevrilip üç, beş veya yedi defa <span class="red-text inline-arabic-large">اَللّٰهُمَّ اَجِرْنَا مِنَ النَّارِ</span> denilir. Müteakiben;</strong></p>
                <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ كُلِّ نَارٍ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شَرِّ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ نَارِ قَهْرِكَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ</p>
                <p><strong>Avuç içleri yukarı çevrilerek devam edilir:</strong></p>
                <p class="arabic red2-text">بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا وَ اَدْخِلْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ&nbsp;اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                
                <p><strong>Akabinde Salâvat-ı Şerife getirilip, bu dua okunur:</strong></p>
                <p class="arabic">سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ&nbsp;وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ</p>
                
                <h2>Âyetü’l Kürsî</h2>
                <p><strong>Âyetü’l Kürsî (Bakara Sûresi 255) okunur:</strong></p>
                <p class="arabic">اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُhُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ</p>
                
                <p><strong>Daha sonra tesbihleri çekilir.</strong></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">سُبْحَانَ اللّٰهِ</span></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">اَلْحَمْدُ لِلّٰهِ</span></p>
                <p class="tesbih-line">33 defa <span class="red-text tesbih-arabic">اَللّٰهُ اَكْبَرُ</span></p>
                <p class="arabic red2-text">لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ&nbsp;لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ&nbsp;وَهُوَ حَىٌّ لاَ يَمُوتُ&nbsp;بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ</p>
                
                <p><strong>Denildikten sonra dua edilir. Duadan sonra bir defa<span class="red-text inline-arabic-large"> فَاعْلَمْ اَنَّهُ </span>diyerek kelime-i tevhide geçilir.</strong></p>
                <p><strong>33 defa<span class="red-text inline-arabic-large"> لاۤ اِلٰهَ اِلاَّ&nbsp;اللّٰهُ&nbsp;</span>okunduktan sonra<span class="red-text inline-arabic-large">&nbsp;مُحَمَّدٌ رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ&nbsp;سَلَّمَ </span>diyerek tamamlanır. Ve tesbihata şöylece devam edilir:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    اِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا&nbsp;٭&nbsp;لَبَّيْكَ<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا كَث۪يرًا<br>
                    صَلِّ وَ سَلِّمْ يَا رَبِّ&nbsp;عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَا رَسُولَ اللّٰهِ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَا حَب۪يبَ اللّٰهِ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ&nbsp;بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ&nbsp;وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِاُسْتَادِنَا سَع۪يدِ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ بِطَلَبَةِ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ&nbsp;يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا&nbsp;اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ
                </p>
                <h2 id="nav-aksam-4">İsm-i A’zâm Duası</h2>
                <p><strong>İsm-i A’zâm duası okunur:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    سُبْحَانَكَ يَا <span class="red-text">اَللّٰهُ</span> تَعَالَيْتَ يَا <span class="red-text">رَحْمٰنُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">رَح۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">كَر۪يمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">حَم۪يدُ</span> تَعَالَيْتَ يَا <span class="red-text">حَك۪يمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مَج۪يدُ</span> تَعَالَيْتَ يَا <span class="red-text">مَلِكُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">قُدُّوسُ</span> تَعَالَيْتَ يَا <span class="red-text">سَلاَمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مُؤْمِنُ</span> تَعَالَيْتَ يَا <span class="red-text">مُهَيْمِنُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">عَز۪يزُ</span> تَعَالَيْتَ يَا <span class="red-text">جَبَّارُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مُتَكَبِّرُ</span> تَعَالَيْتَ يَا <span class="red-text">خَالِقُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">اَوَّلُ</span> تَعَالَيْتَ يَا <span class="red-text">اٰخِرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">ظَاهِرُ</span> تَعَالَيْتَ يَا <span class="red-text">بَاطِنُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">بَارِىءُ</span> تَعَالَيْتَ يَا <span class="red-text">مُصَوِّرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">تَوَّابُ</span> تَعَالَيْتَ يَا <span class="red-text">وَهَّابُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">بَاعِثُ</span> تَعَالَيْتَ يَا <span class="red-text">وَارِثُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">قَد۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">مُق۪يمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">فَرْدُ</span> تَعَالَيْتَ يَا <span class="red-text">وِتْرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">نُورُ</span> تَعَالَيْتَ يَا <span class="red-text">سَتَّارُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">جَل۪يلُ</span> تَعَالَيْتَ يَا <span class="red-text">جَم۪يلُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">قَاهِرُ</span> تَعَالَيْتَ يَا <span class="red-text">قَادِرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مَل۪يكُ</span> تَعَالَيْتَ يَا <span class="red-text">مُقْتَدِرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">عَل۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">عَلاَّمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">عَظ۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">غَفُورُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">حَل۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">وَدُودُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">شَه۪يدُ</span> تَعَالَيْتَ يَا <span class="red-text">شَاهِدُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">كَب۪يرُ</span> تَعَالَيْتَ يَا <span class="red-text">مُتَعَالُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">نُورُ</span> تَعَالَيْتَ يَا <span class="red-text">لَط۪يفُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">سَم۪يعُ</span> تَعَالَيْتَ يَا <span class="red-text">كَف۪يلُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">قَر۪يبُ</span> تَعَالَيْتَ يَا <span class="red-text">بَص۪يرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">حَقُّ</span> تَعَالَيْتَ يَا <span class="red-text">مُب۪ينُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">رَؤُفُ</span> تَعَالَيْتَ يَا <span class="red-text">رَح۪يمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">طَاهِرُ</span> تَعَالَيْتَ يَا <span class="red-text">مُطَهِّرُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مُجَمِّلُ</span> تَعَالَيْتَ يَا <span class="red-text">مُفَضِّلُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">مُظْهِرُ</span> تَعَالَيْتَ يَا <span class="red-text">مُنْعِمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">دَيَّانُ</span> تَعَالَيْتَ يَا <span class="red-text">سُلْطَانُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">حَنَّانُ</span> تَعَالَيْتَ يَا <span class="red-text">مَنَّانُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">اَحَدُ</span> تَعَالَيْتَ يَا <span class="red-text">صَمَدُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">حَىُّ</span> تَعَالَيْتَ يَا <span class="red-text">قَيُّومُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">عَدْلُ</span> تَعَالَيْتَ يَا <span class="red-text">حَكَمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">فَرْدُ</span> تَعَالَيْتَ يَا <span class="red-text">قُدُّوسُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ
                </p>
                <p><strong>Avuçlar yukarı gelecek şekilde eller açılır:</strong></p>
                <p class="arabic">سُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ&nbsp;وَ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ وَ رُفَقَٓائَنَا وَ اَحْبَابَنَا الْمُخْلِص۪ينَ مِنَ النَّارِ</p>
                <p><strong>Avuç içleri aşağıya çevrilerek devam edilir:</strong></p>
                <p class="arabic">وَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ</p>
                <p><strong>Avuçlar tekrar yukarı gelecek şekilde dua şöylece tamamlanır:</strong></p>
                <p class="arabic red"><span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ&nbsp;مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                <h2 id="nav-aksam-6">Haşr Sûresi’nin 20-24.Âyetleri</h2>
                <p><strong>Haşr Sûresi’nin 20-24.Âyetleri okunur:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    لاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ&nbsp;اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ<br>
                    لَوْ اَنْزَلْنَا هٰذَا الْقُرْاٰنَ عَلٰى جَبَلٍ لَرَاَيْتَهُ خَاشِعًا مُتَصَدِّعًا مِنْ&nbsp;خَشْyَةِ اللّٰهِۜ وَتِلْكَ اْلاَمْثَالُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ<br>
                    هُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ عَالِمُ الْغَيْبِ وَالشَّهَادَةِۚ هُوَ الرَّحْمٰنُ الرَّح۪يمُ<br>
                    هُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ اَلْمَلِكُ الْقُدُّوسُ السَّلاَمُ الْمُؤْمِنُ&nbsp;الْمُهَيْمِنُ الْعَز۪يزُ الْجَبَّارُ الْمُتَكَبِّرُۜ سُبْحَانَ اللّٰهِ عَمَّا يُشْرِكُونَ<br>
                    هُوَ اللّٰهُ الْخَالِقُ الْبَارِىُٔ الْمُصَوِّرُ لَهُ اْلاَسْمَٓاءُ الْحُsْنٰىۜ يُسَبِّحُ لَهُ مَا فِى السَّمٰوَاتِ وَاْلاَرْضِۚ وَهُوَ الْعَز۪يزُ الْحَك۪يمُ
                </p>
                <h2>Fatiha-i Şerife</h2>
                <p><strong>Fatiha-i Şerife ile tesbihat tamamlanır:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ<br>
                    اَلرَّחْمٰنِ الرَّח۪يمِۙ<br>
                    מָלִךِ يَوْمِ الدّ۪ينِۜ<br>
                    אِيَّךָ نَعْبُدُ وَאِيَّךָ نَسْتَع۪ينُۜ<br>
                    אִהְדִּנَا الصِّרָاطَ الْمُসְתַּק۪יםَۙ<br>
                    صِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ
                </p>
              </div>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null)
    }
}