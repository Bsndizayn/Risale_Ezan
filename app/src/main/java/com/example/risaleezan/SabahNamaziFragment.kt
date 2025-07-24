package com.example.risaleezan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment

class SabahNamaziFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sabah_namazi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.sabah_namazi_webview)
        webView.settings.javaScriptEnabled = true
        webView.setBackgroundColor(0x00000000)

        // Sizin verdiğiniz, temizlenmiş ve düzeltilmiş tam HTML içeriği
        val htmlContent = """
            <!DOCTYPE html>
            <html lang="tr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Sabah Namazı Tesbihatı</title>
                <style>
                    /* Sayfa Genel Stilleri - Resimdeki gibi sarımsı bir arka plan */
                    body {
                        font-family: 'Times New Roman', Times, serif; /* Daha klasik bir görünüm için */
                        line-height: 1.8;
                        color: #000000;
                        background-color: #fdf5e6; /* Kitap sayfası rengi */
                    }
                    /* Ana İçerik Alanı */
                    .container {
                        max-width: 800px;
                        margin: 30px auto;
                        padding: 30px;
                        background-color: #fefcf5; /* İçeriğin arka planı biraz daha açık */
                        border: 2px solid #d4b996;
                        border-radius: 5px;
                        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                    }
                    /* Ana Başlık */
                    h1 {
                        text-align: center;
                        color: #8B4513; /* Koyu kahverengi */
                        border-bottom: 2px solid #8B4513;
                        padding-bottom: 10px;
                        margin-bottom: 25px;
                        font-weight: bold;
                    }
                    /* Alt Başlıklar */
                    h2 {
                        color: #8B4513;
                        border-bottom: 1px solid #d4b996;
                        padding-bottom: 5px;
                        margin-top: 40px;
                        font-weight: bold;
                    }
                    /* Talimat ve Açıklama Paragrafları */
                    .instruction {
                        color: #333;
                        font-style: italic;
                        background-color: #f5f5f5;
                        padding: 12px;
                        border-left: 4px solid #d4b996;
                        border-radius: 4px;
                        margin: 15px 0;
                    }
                    /* Arapça Dua Metinleri */
                    .arabic {
                        direction: rtl;
                        text-align: right;
                        font-size: 1em; /* Daha büyük ve okunaklı font */
                        line-height: 2.2;
                        color: #000000;
                        background-color: transparent; /* Arka plan rengi olmasın */
                        padding: 15px 0;
                        border: none;
                        margin-top: 15px;
                        margin-bottom: 15px;
                    }
                    /* Kırmızı vurgulanan kelimeler için stil */
                    .red-text {
                        color: #d92d2d;
                        font-weight: bold;
                    }
                    /* Mavi vurgulanan kelimeler için stil */
                    .blue-text {
                        color: #0056b3; /* Canlı bir mavi renk */
                        font-weight: bold;
                    }
                    /* Tesbih Listesi */
                    ul.tesbih-list {
                        list-style-type: none;
                        padding-left: 0;
                        font-size: 1.2em;
                    }
                    ul.tesbih-list li {
                        margin-bottom: 10px;
                    }
                    ul.tesbih-list li .count {
                        font-weight: bold;
                        color: #c0392b;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>SABAH NAMAZI TESBİHATI</h1>
                    <p class="instruction">Sabah Namazının farzını kılıp, selam verdikten sonra denilir:</p>
                    <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> اَنْتَ السَّلاَمُ وَ مِنْكَ السَّلاَمُ&nbsp;تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ</p>
                    <p class="instruction">Denilir, eller yukarı kaldırılıp açılarak Salâten Tüncînâ duası okunur:</p>
                    <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ&nbsp;صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ&nbsp;اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                    <p class="instruction">Akabinde bir defa okunur:</p>
                    <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> اِنَّا نُقَدِّمُ اِلَيْكَ بَيْنَ يَدَىْ كُلِّ نَفَسٍ وَ لَمْحَةٍ وَ لَحْظَةٍ وَ طَرْفَةٍ يَطْرِفُ بِهَٓا اَهْلُ السَّمٰوَاتِ وَ اَهْلُ اْلاَرَض۪ينَ شَهَادَةً اَشْهَدُ اَنْ</p>
                    <p class="instruction">Dokuz defa tekrar edilir:</p>
                    <p class="arabic">لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ&nbsp;وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ</p>
                    <p class="instruction">Onuncuda şöylece tamamlanır:</p>
                    <p class="arabic">لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ&nbsp;لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ</p>
                    <p class="instruction">Sonra avuç içleri aşağıya doğru çevrilip <strong>üç, beş</strong> veya <strong>yedi</strong> defa <span class="blue-text">اَللّٰهُمَّ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ denilir. Müteakiben şöyle devam edilir:</p>
                    <p class="arabic"><span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ كُلِّ نَارٍ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَjِرْنَا مِنْ شَرِّ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ نَارِ قَهْرِكَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ</p>
                    <p class="instruction">Avuç içleri yukarı çevrilerek devam edilir:</p>
                    <p class="arabic">بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;٭&nbsp;<span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا وَ اَدْخِلْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ&nbsp;صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ&nbsp;اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                    <p class="instruction">Akabinde Salâvat-ı Şerife getirilip, bu dua okunur:</p>
                    <p class="arabic">سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ&nbsp;وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ</p>
                    <h2>Âyetü’l Kürsî (Bakara Sûresi 255)</h2>
                    <p class="instruction">Âyetü’l Kürsî okunur:</p>
                    <p class="arabic">اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ</p>
                    <p class="instruction">Daha sonra tesbihler çekilir:</p>
                    <ul class="tesbih-list">
                        <li><span class="count">33 defa</span>&nbsp; سُبْحَانَ اللّٰهِ</li>
                        <li><span class="count">33 defa</span>&nbsp; اَلْحَمْدُ لِلّٰهِ</li>
                        <li><span class="count">33 defa</span>&nbsp; اَللّٰهُ اَكْبَرُ</li>
                    </ul>
                    <p class="arabic">لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ&nbsp;لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ&nbsp;وَهُوَ حَىٌّ لاَ يَمُوتُ&nbsp;بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَصِيرُ</p>
                    <p class="instruction">Denildikten sonra dua edilir. Duadan sonra bir defa <strong>فَاعْلَمْ اَنَّهُ</strong> diyerek kelime-i tevhide geçilir.</p>
                    <p class="instruction"><strong>33 defa</strong> لاۤ اِلٰهَ اِلاَّ اللّٰهُ okunduktan sonra <strong>مُحَمَّدٌ رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمَ</strong> diyerek tamamlanır.</p>
                    <p class="instruction">Sabah Namazı Tesbihatına mahsus 10 defa okunur:</p>
                    <p class="arabic">لآَ اِلٰهَ اِلاَّ اللّٰهُ اَلْمَلِكُ الْحَقُّ الْمُب۪ينُ&nbsp;مُحَمَّدٌ رَسُولُ اللّٰهِ صَادِقُ الْوَعْدِ اْلاَم۪ينُ</p>
                    <p class="instruction">Ve tesbihata şöylece devam edilir:</p>
                    <p class="arabic">بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                    اِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا&nbsp;٭&nbsp;لَبَّيْكَ<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ&nbsp;وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا كَث۪يرًا<br>
                    صَلِّ وَ سَلِّمْ يَا رَبِّ&nbsp;عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَا رَسُولَ اللّٰهِ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَا حَب۪يبَ اللّٰهِ<br>
                    اَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ&nbsp;عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ<br>
                    <span class="blue-text">اَللّٰهُمَّ</span> صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ&nbsp;بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ&nbsp;وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِاُسْتَادِنَا سَع۪يدِ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ بِطَلَبَةِ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ&nbsp;يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا&nbsp;اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ</p>
                    <h2>Tercüman-ı İsm-i A’zâm Duası</h2>
                    <p class="instruction">Tercüman-ı İsm-i A’zâm duası okunur:</p>
                    <p class="arabic">
                        بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                        <span style="font-size: 2em; font-weight: bold;">يسۤ</span><br><br>
                        سُبْحَانَكَ يَٓا&nbsp;<span class="red-text">اَللّٰهُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">رَحْمٰنُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">رَح۪يمُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">كَر۪يمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">حَم۪يدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">حَك۪يمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مَج۪يدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مَلِكُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰن<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">قُدُّوسُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">سَلاَمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مُؤْمِنُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُهَيْمِنُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">عَز۪يزُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">جَبَّارُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مُتَكَبِّرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">خَالِقُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَٓا&nbsp;<span class="red-text">اَوَّلُ</span>&nbsp;تَعَالَيْتَ يَٓا&nbsp;<span class="red-text">اٰخِرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">ظَاهِرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">بَاطِنُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">بَارِىءُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُصَوِّرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">تَوَّابُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">وَهَّابُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">بَاعِثُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">وَارِثُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">قَد۪يمُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُق۪يمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">فَرْدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">وِتْرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">نُورُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">سَتَّارُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">جَل۪يلُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">جَم۪يلُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">قَاهِرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">قَادِرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مَل۪يكُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُقْتَدِرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">عَل۪يمُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">عَلاَّمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">عَظ۪يمُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">غَفُورُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">حَل۪يمُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">وَدُودُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">شَه۪يدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">شَاهِدُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">كَب۪يرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُتَعَالُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">نُورُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">لَط۪يفُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">سَم۪يعُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">كَف۪يلُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">قَر۪يبُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">بَص۪يرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">حَقُّ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُب۪ينُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">رَؤُفُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">رَح۪يمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">طَاهِرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُطَهِّرُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مُجَمِّلُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُفَضِّلُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">مُظْهِرُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مُنْعِمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">دَيَّانُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">سُلْطَانُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْHَانَكَ يَا&nbsp;<span class="red-text">حَنَّانُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">مَنَّانُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَٓا&nbsp;<span class="red-text">اَحَدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">صَمَدُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">حَىُّ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">قَيُّومُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">عَدْلُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">حَكَمُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                        سُبْحَانَكَ يَا&nbsp;<span class="red-text">فَرْدُ</span>&nbsp;تَعَالَيْتَ يَا&nbsp;<span class="red-text">قُدُّوسُ</span>&nbsp;اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ<br>
                    </p>
                    <p class="instruction">Avuçlar yukarı gelecek şekilde eller açılır:</p>
                    <p class="arabic">سُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ&nbsp;وَ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتِنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ وَ رُفَقَٓائَنَا وَ اَحْبَابَنَا الْمُخْلِص۪ينَ مِنَ النَّارِ</p>
                    <p class="instruction">Avuç içleri aşağıya çevrilerek devam edilir:</p>
                    <p class="arabic">وَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ</p>
                    <p class="instruction">Avuçlar tekrar yukarı gelecek şekilde dua şöylece tamamlanır:</p>
                    <p class="arabic">بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ بِرَحْمَتِكَ يَٓا اَرْحَمَ الرَّاحِم۪ينَ <span class="blue-text">اَللّٰهُمَّ</span> اَدْخِلْنَا الْجَنَّةَ&nbsp;مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ</p>
                    <h2>Haşr Sûresi’nin 20-24. Âyetleri</h2>
                    <p class="instruction">Haşr Sûresi’nin 20-24. Âyetleri okunur:</p>
                    <p class="arabic">
                        بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                        لاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ&nbsp;اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ<br>
                        لَوْ اَنْزَلْنَا هٰذَا الْقُرْاٰنَ عَلٰى جَبَلٍ لَرَاَيْتَهُ خَاشِعًا مُتَصَدِّعًا مِنْ&nbsp;خَشْيَةِ اللّٰهِۜ وَتِلْكَ اْلاَمْثَالُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ<br>
                        هُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ عَالِمُ الْغَيْبِ وَالشَّهَادَةِۚ هُوَ الرَّحْمٰنُ الرَّح۪يمُ<br>
                        هُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ اَلْمَلِكُ الْقُدُّوسُ السَّلاَمُ الْمُؤْمِنُ&nbsp;الْمُهَيْمِنُ الْعَز۪يزُ الْجَبَّارُ الْمُتَكَبِّرُۜ سُبْحَانَ اللّٰهِ عَمَّا يُشْرِكُونَ<br>
                        هُوَ اللّٰهُ الْخَالِقُ الْبَارِىُٔ الْمُصَوِّرُ لَهُ اْلاَسْمَٓاءُ الْحُسْنٰىۜ يُسَبِّحُ لَهُ مَا فِى السَّمٰوَاتِ وَاْلاَرْضِۚ وَهُوَ الْعَز۪يزُ الْحَك۪يمُ
                    </p>
                    <h2>Fatiha-i Şerife</h2>
                    <p class="instruction">Fatiha-i Şerife ile tesbihat tamamlanır:</p>
                    <p class="arabic">
                        بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ<br>
                        اَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ<br>
                        اَلرَّحْمٰنِ الرَّح۪يمِۙ<br>
                        مَالِكِ يَوْمِ الدّ۪ينِۜ<br>
                        اِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ<br>
                        اِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ<br>
                        صِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}