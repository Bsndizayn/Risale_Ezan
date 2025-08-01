package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class IkindiNamaziFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_ikindi_namazi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.ikindi_namazi_webview)
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
              <title>İkindi Namazı Tesbihatı</title>
              
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
                <h1 id="nav-ikindi-1">İKİNDİ NAMAZI TESBİHATI</h1>
                <p><strong>İkindi Namazının farzını kılıp, selam verdikten sonra</strong></p>
                <p class="arabic red2-text"><span class="blue-text">اَللّٰهُمَّ</span> اَنْتَ السَّلاَمُ وَ مِنْكَ السَّلاَمُ&nbsp;تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ</p>
                <p><strong>Denilir, eller yukarı kaldırılıp açılarak Salâten Tüncînâ duası okunur:</strong></p>
                <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ&nbsp;صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ&nbsp;اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                <p id="nav-ikindi-2"><strong>Salâvat-ı Şerife getirilip, bu dua okunur:</strong></p>
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
                <h2 id="nav-ikindi-3">Tercüman-ı İsm-i A’zâm Duası</h2>
                <p><strong>Tercüman-ı İsm-i A’zâm duası okunur:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    سُبْحَانَكَ يَا <span class="red-text">اَللّٰهُ</span> تَعَالَيْتَ يَا <span class="red-text">رَحْمٰنُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">رَح۪يمُ</span> تَعَالَيْتَ يَا <span class="red-text">كَر۪يمُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    سُبْحَانَكَ يَا <span class="red-text">فَرْدُ</span> تَعَالَيْتَ يَا <span class="red-text">قُدُّوسُ</span> اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ
                </p>
                <p id="nav-ikindi-4"><strong>Avuçlar yukarı gelecek şekilde eller açılır:</strong></p>
                <p class="arabic">سُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ&nbsp;وَ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتَنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ وَ رُفَقَٓائَنَا وَ اَحْبَابَنَا الْمُخْلِص۪ينَ مِنَ النَّارِ</p>
                <p><strong>Avuç içleri aşağıya çevrilerek devam edilir:</strong></p>
                <p class="arabic">وَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ</p>
                <p><strong>Avuçlar tekrar yukarı gelecek şekilde dua şöylece tamamlanır:</strong></p>
                <p class="arabic red">بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ <span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ&nbsp;مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                <h2 id="nav-ikindi-5">Nebe Sûresi</h2>
                <p><strong>Nebe Sûresi okunur:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    عَمَّ يَتَسَٓاءَلُونَۚ<br>
                    عَنِ النَّبَإِالْعَظ۪يمِۙ<br>
                    اَلَّذ۪ى هُمْ ف۪يهِ مُخْتَلِفُونَۜ<br>
                    كَلاَّ سَيَعْلَمُونَۙ<br>
                    ثُمَّ كَلاَّ سَيَعْلَمُونَ<br>
                    اَلَمْ نَجْعَلِ اْلاَرْضَ مِهَادًۙا<br>
                    وَالْجِبَالَ اَوْتَادًۖا<br>
                    وَخَلَقْنَاكُمْ اَزْوَاجًۙا<br>
                    وَجَعَلْنَا نَوْمَكُمْ سُبَاتًۙا<br>
                    وَجَعَلْنَا الَّيْلَ لِبَاسًۙا<br>
                    وَجَعَلْنَا النَّهَارَ مَعَاشًۖا<br>
                    وَبَنَيْنَا فَوْقَكُمْ سَبْعًا شِدَادًۙا<br>
                    وَجَعَلْنَا سِرَاجًا وَهَّاجًۖا<br>
                    وَاَنْزَلْنَا مِنَ الْمُعْصِرَاتِ مَٓاءً ثَجَّاجًۙا<br>
                    لِنُخْرِجَ بِه۪ حَبًّا وَنَبَاتًۙا<br>
                    وَجَنَّاتٍ اَلْفَافًۜا<br>
                    اِنَّ يَوْمَ الْفَصْلِ كَانَ م۪يقَاتًۙا<br>
                    يَوْمَ يُنْفَخُ فِى الصُّورِ فَتَاْتُونَ اَفْوَاجًۙا<br>
                    وَفُتِحَتِ السَّمَٓاءُ فَكَانَتْ اَبْوَابًۙا<br>
                    وَسُيِّرَتِ الْجِبَالُ فَكَانَتْ سَرَابًۜا<br>
                    اِنَّ جَهَنَّمَ كَانَتْ مِرْصَادًۙا<br>
                    لِلطَّاغ۪ينَ مَاٰبًۙا<br>
                    لاَبِث۪ينَ ف۪يهَٓا اَحْقَابًۚا<br>
                    لاَيَذُوقُونَ ف۪يهَا بَرْدًا وَلاَ شَرَابًۙا<br>
                    اِلاَّ حَم۪يمًا وَغَسَّاقًۙا<br>
                    جَزَٓاءً وِفَاقًا<br>
                    اِنَّهُمْ كَانُوا لاَيَرْجُونَ حِسَابًۙا<br>
                    وَكَذَّبُوا بِاٰيَاتِنَا كِذَّابًۜا<br>
                    وَكُلَّ شَىْءٍ اَحْصَيْنَاهُ كِتَابًا<br>
                    فَذُوقُوا فَلَنْ نَز۪يدَكُمْ اِلاَّ عَذَابً۟ا<br>
                    اِنَّ لِلْمُتَّق۪ينَ مَفَازًۙا<br>
                    حَدَٓائِقَ وَاَعْنَابًۙا<br>
                    وَكَوَاعِبَ اَتْرَابًۙا<br>
                    وَكَاْسًا دِهَاقًۜا<br>
                    لاَيَسْمَعُونَ ف۪يهَالَغْوًا وَلاَ كِذَّابًۚا<br>
                    جَزَٓاءً مِنْ رَبِّكَ عَطَٓاءً حِسَابًۙا<br>
                    رَبِّ السَّمٰوَاتِ وَاْلاَرْضِ وَمَا بَيْنَهُمَاۙ الرَّحْمٰنِ لاَيَمْلِكُونَ مِنْهُ خِطَابًۙا<br>
                    يَوْمَ يَقُومُ الرُّوحُ وَالْمَلٰٓئِكَةُ صَفًّاۜ لاَيَتَكَلَّمُونَ اِلاَّ مَنْ اَذِنَ لَهُ الرَّحْمٰنُ وَقَالَ صَوَابًا<br>
                    ذٰلِكَ الْيَوْمُ الْحَقُّ ۚ فَمَنْ شَٓاءَ اتَّخَذَ اِلٰى رَبِّه۪ مَاٰبًا<br>
                    اِنَّٓا اَنْذَرْنَاكُمْ عَذَابًا قَر۪يبًۚا يَوْمَ يَنْظُرُ الْمَرْءُ مَا قَدَّمَتْ يَدَاهُ وَيَقُولُ الْكَافِرُ يَالَيْتَن۪ى كُنْتُ تُرَابًا
                </p>
                <h2>Fatiha-i Şerife</h2>
                <p><strong>Fatiha-i Şerife ile tesbihat tamamlanır:</strong></p>
                <p class="arabic">
                    بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ<br>
                    اَلرَّحْمٰنِ الرَّح۪يمِۙ<br>
                    مَالِكِ يَوْمِ الدّ۪ينِۜ<br>
                    اِيَّאكَ نَعْبُدُ وَاِيَّאكَ نَسْتَع۪ينُۜ<br>
                    اِهْدِنَا الصِّרَاطَ الْمُসْتَق۪يمَۙ<br>
                    صِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ
                </p>
              </div>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null)
    }
}