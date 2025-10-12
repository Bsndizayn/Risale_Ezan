package com.example.risaleezanvakticompose.data.repository

import com.example.risaleezanvakticompose.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TesbihatRepository @Inject constructor() {

    fun getAllCategories(): List<TesbihatCategory> {
        return TesbihatCategory.values().toList()
    }

    fun getTesbihatContent(category: TesbihatCategory): TesbihatContent {
        return when (category) {
            TesbihatCategory.SABAH -> getSabahTesbihat()
            TesbihatCategory.OGLE -> getOgleTesbihat()
            TesbihatCategory.IKINDI -> getIkindiTesbihat()
            TesbihatCategory.AKSAM -> getAksamTesbihat()
            TesbihatCategory.YATSI -> getYatsiTesbihat()
            TesbihatCategory.GENEL -> getGenelTesbihat()
        }
    }


    /**
     * SABAH NAMAZI TESBİHATI
     */
    private fun getSabahTesbihat(): TesbihatContent {
        val items = mutableListOf<TesbihatItem>()

        // 1. Salâten Tüncînâ Duası (Eller açılarak okunur)
        items.add(
            TesbihatItem(
                id = "sabah_salaten_tuncina",
                title = "Salâten Tüncînâ Duası",
                arabicText = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, salâten tüncînâ bihâ min cemî'il-ehvâli vel-âfât ve takdî lenâ bihâ cemî'al-hâcât ve tutahhirunâ bihâ min cemî'is-seyyiât ve terfe'unâ bihâ indeke a'led-derecât ve tübelliğunâ bihâ aksâl-ğâyât min cemî'il-hayrât fil-hayâti ve ba'del-memât. Âmîne yâ mücîbed-de'avât vel-hamdü lillâhi rabbil-âlemîn",
                translation = "Allah'ım! Efendimiz Muhammed'e ve Efendimiz Muhammed'in âline öyle bir salât eyle ki, o salât vesilesiyle bizi bütün korku ve afetlerden kurtarasın, bütün hacetlerimizi onunla yerine getiresin, bütün kötülüklerden bizi onunla temizleyesin, katında bizi onunla en yüce derecelere yükseltsin ve dünya ve ahirette hayırların en yükseğine onunla ulaştırasın. Amin, ey duaları kabul eden! Hamd, âlemlerin Rabbi Allah'a mahsustur.",
                count = 1,
                type = TesbihatType.DUA,
                order = 1
            )
        )

        // 2. Giriş Duası (1 defa)
        items.add(
            TesbihatItem(
                id = "sabah_giris_duasi",
                title = "Giriş Duası",
                arabicText = "اَللّٰهُمَّ اِنَّا نُقَدِّمُ اِلَيْكَ بَيْنَ يَدَىْ كُلِّ نَفَسٍ وَ لَمْحَةٍ وَ لَحْظَةٍ وَ طَرْفَةٍ يَطْرِفُ بِهَٓا اَهْلُ السَّمٰوَاتِ وَ اَهْلُ اْلاَرَض۪ينَ شَهَادَةً اَشْهَدُ اَنْ",
                transcription = "Allahümme innâ nukaddimu ileyke beyne yedey külli nefesin ve lemhatin ve lahzatin ve tarfetin yatrıfu bihâ ehlüs-semâvâti ve ehlül-aradîne şehâdeten eşhedü en",
                translation = "Allah'ım! Göklerin ve yerlerin halkının her nefes, göz açıp kapama, an ve bakış arasında Sana şu şehâdeti takdim ediyoruz: Şehâdet ederim ki...",
                count = 1,
                type = TesbihatType.DUA,
                order = 2
            )
        )

        // 3. Kelime-i Tevhid (9 defa)
        items.add(
            TesbihatItem(
                id = "sabah_tevhid_9",
                title = "Kelime-i Tevhid (9 defa)",
                arabicText = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh, lehül-mülkü ve lehül-hamd, yuhyî ve yümît ve hüve hayyün lâ yemût, bi yedihil-hayr ve hüve alâ külli şey'in kadîr",
                translation = "Allah'tan başka ilah yoktur. O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. O diriltir ve öldürür. O diridir, ölmez. Hayır O'nun elindedir ve O her şeye kadirdir.",
                count = 9,
                type = TesbihatType.ZIKIR,
                order = 3
            )
        )

        // 4. Kelime-i Tevhid (10. - Tamamlayıcı)
        items.add(
            TesbihatItem(
                id = "sabah_tevhid_10",
                title = "Kelime-i Tevhid (10. Tamamlama)",
                arabicText = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
                transcription = "...ve ileyhil-masîr",
                translation = "...ve dönüş O'nadır.",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 4
            )
        )

        // 5. Allahümme Ecirnâ Minen-Nâr (3, 5 veya 7 defa)
        items.add(
            TesbihatItem(
                id = "sabah_ecirna_minen_nar",
                title = "Allahümme Ecirnâ Minen-Nâr",
                arabicText = "اَللّٰهُمَّ اَجِرْنَا مِنَ النَّارِ",
                transcription = "Allahümme ecirnâ minen-nâr",
                translation = "Allah'ım! Bizi cehennem ateşinden koru",
                count = 7,
                type = TesbihatType.DUA,
                order = 5
            )
        )

        // 6. Ecirnâ Duaları Uzun Versiyonu (1 defa - tamamı)
        items.add(
            TesbihatItem(
                id = "sabah_ecirna_uzun",
                title = "Ecirnâ Duaları",
                arabicText = "اَللّٰهُمَّ اَجِرْنَا مِنْ كُلِّ نَارٍ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ نَارِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ",
                transcription = "Allahümme ecirnâ min külli nâr. Allahümme ecirnâ min fitnetid-dîniyyeti ved-dünyeviyyeh. Allahümme ecirnâ min fitneti âhiriz-zemân. Allahümme ecirnâ min fitnetil-Mesîhid-Deccâli ves-Süfyân. Allahümme ecirnâ mined-dalâlâti vel-bid'iyyâti vel-beliyyât. Allahümme ecirnâ min şerrin-nefsil-emmâreh. Allahümme ecirnâ min şurûrin-nüfûsil-emmârâtil-fir'avniyyeh. Allahümme ecirnâ min şerrin-nisâ. Allahümme ecirnâ min belâin-nisâ. Allahümme ecirnâ min fitnetin-nisâ. Allahümme ecirnâ min azâbil-kabr. Allahümme ecirnâ min azâbi yevmil-kıyâmeh. Allahümme ecirnâ min azâbi Cehennem. Allahümme ecirnâ min azâbi kahrik. Allahümme ecirnâ min nâri kahrik. Allahümme ecirnâ min azâbil-kabri ven-nîrân. Allahümme ecirnâ miner-riyâi ves-süm'ati vel-ucubi vel-fahr. Allahümme ecirnâ min tecâvüzil-mülhidîn. Allahümme ecirnâ min şerril-münâfikîn. Allahümme ecirnâ min fitnetil-fâsikîn. Allahümme ecirnâ ve ecir vâlideynâ ve talebete Resâilin-Nûris-sâdikîne fî hidmetil-Kur'âni vel-îmân ve ahbâbenel-mü'minînel-muhlisîne ve akrıbâenâ ve ecdâdenâ minen-nâr",
                translation = "Allah'ım! Bizi her türlü ateşten koru. Allah'ım! Bizi din ve dünya fitnesinden koru. Allah'ım! Bizi ahir zaman fitnesinden koru. Allah'ım! Bizi Mesih Deccal ve Süfyan fitnesinden koru. Allah'ım! Bizi sapıklıklardan, bid'atlardan ve belalardan koru. Allah'ım! Bizi nefsi emmârenin şerrinden koru. Allah'ım! Bizi firavun gibi emir veren nefislerin şerrinden koru. Allah'ım! Bizi kadınların şerrinden koru. Allah'ım! Bizi kadınların belasından koru. Allah'ım! Bizi kadınların fitnesinden koru. Allah'ım! Bizi kabir azabından koru. Allah'ım! Bizi kıyamet günü azabından koru. Allah'ım! Bizi cehennem azabından koru. Allah'ım! Bizi kahrının azabından koru. Allah'ım! Bizi kahrının ateşinden koru. Allah'ım! Bizi kabir azabından ve ateşlerden koru. Allah'ım! Bizi riyadan, gösterişten, kibirden ve övünmeden koru. Allah'ım! Bizi dinsizlerin tecavüzünden koru. Allah'ım! Bizi münafıkların şerrinden koru. Allah'ım! Bizi fasıkların fitnesinden koru. Allah'ım! Bizi, anne babamızı, Kur'an ve iman hizmetinde sadık Risale-i Nur talebelerini, ihlâslı mümin dostlarımızı, akrabalarımızı ve dedelerimizi ateşten koru",
                count = 1,
                type = TesbihatType.DUA,
                order = 6
            )
        )

        // 7. Cennet Duası (Avuçlar yukarı)
        items.add(
            TesbihatItem(
                id = "sabah_cennet_duasi",
                title = "Cennet Duası",
                arabicText = "بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا وَ اَدْخِلْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ وَ اِخْوَانَنَا وَ اَخَوَاتَنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Bi afvike yâ mücîr, bi fadlike yâ ğaffâr. Allahümme edhılnel-cennete meal-ebrâr...",
                translation = "Affınla ey koruyan, fazlınla ey bağışlayan! Allah'ım! Bizi ve üstadımız Said Nursî'yi, anne babamızı, Risale-i Nur talebelerini iyilerle birlikte cennete koy...",
                count = 1,
                type = TesbihatType.DUA,
                order = 7
            )
        )

        // 8. Ara Dua
        items.add(
            TesbihatItem(
                id = "sabah_ara_dua",
                title = "Salâvat ve Dua",
                arabicText = "سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
                transcription = "Sübhanallahi vel-hamdülillahi ve lâ ilâhe illallahu vallahu ekber ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm",
                translation = "Allah'ı tesbih ederim, Allah'a hamd ederim, Allah'tan başka ilah yoktur, Allah en büyüktür ve yüce ve azîm olan Allah'tan başka güç ve kuvvet yoktur",
                count = 1,
                type = TesbihatType.DUA,
                order = 8
            )
        )

        // 9. Âyetü'l Kürsî
        items.add(
            TesbihatItem(
                id = "sabah_ayetelkursi",
                title = "Âyetü'l Kürsî",
                arabicText = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
                transcription = "Allahü lâ ilâhe illâ hüvel hayyül kayyûm. Lâ te'huzühü sinetün ve lâ nevm. Lehü mâ fis-semâvâti ve mâ fil-ard. Men zellezî yeşfe'u indehü illâ bi iznih. Ya'lemü mâ beyne eydîhim ve mâ halfehüm ve lâ yuhîtûne bi şey'in min ilmihî illâ bimâ şâe. Vesia kürsiyyühüs-semâvâti vel-ard. Ve lâ yeûdühü hıfzuhümâ ve hüvel aliyyül azîm",
                translation = "Allah, kendisinden başka ilah olmayandır. O Hayy'dır (diri, her zaman diri), Kayyûm'dur (her şeyi ayakta tutan). O'nu ne uyuklama tutar ne de uyku. Göklerde ve yerde ne varsa hepsi O'nundur. İzni olmadan O'nun katında kim şefaat edebilir? O, kullarının önlerindekini ve arkalarındakini bilir. Onlar, O'nun ilminden, kendisinin dilediği kadarından başka bir şeyi kavrayamazlar. O'nun Kürsüsü gökleri ve yeri kaplayıp kuşatmıştır. Gökleri ve yeri koruyup gözetmek O'na güç gelmez. O, yücedir, büyüktür.",
                count = 1,
                type = TesbihatType.AYET,
                order = 9
            )
        )

        // 10. Sübhanallah (33 defa)
        items.add(
            TesbihatItem(
                id = "sabah_subhanallah",
                title = "Sübhanallah",
                arabicText = "سُبْحَانَ اللّٰهِ",
                transcription = "Sübhanallah",
                translation = "Allah noksan sıfatlardan münezzehtir",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 10
            )
        )

        // 11. Elhamdülillah (33 defa)
        items.add(
            TesbihatItem(
                id = "sabah_elhamdulillah",
                title = "Elhamdülillah",
                arabicText = "اَلْحَمْدُ لِلّٰهِ",
                transcription = "Elhamdülillah",
                translation = "Hamd Allah'a mahsustur",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 11
            )
        )

        // 12. Allahu Ekber (33 defa)
        items.add(
            TesbihatItem(
                id = "sabah_allahuekber",
                title = "Allahu Ekber",
                arabicText = "اَللّٰهُ اَكْبَرُ",
                transcription = "Allahu Ekber",
                translation = "Allah en büyüktür",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 12
            )
        )

        // 13. Tahlil (Tesbih sonrası - 1 defa)
        items.add(
            TesbihatItem(
                id = "sabah_tahlil_tesbih_sonrasi",
                title = "Tahlil (Tesbih Sonrası)",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ وَهُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh, lehül-mülkü ve lehül-hamd, yuhyî ve yümît ve hüve hayyün lâ yemût, bi yedihil-hayr ve hüve alâ külli şey'in kadîr ve ileyhil-masîr",
                translation = "Allah'tan başka ilah yoktur. O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. O diriltir ve öldürür. O diridir, ölmez. Hayır O'nun elindedir ve O her şeye kadirdir ve dönüş O'nadır.",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 13
            )
        )

        // 14. Kelime-i Tevhid (33 defa)
        items.add(
            TesbihatItem(
                id = "sabah_tevhid_33",
                title = "Kelime-i Tevhid",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ",
                transcription = "Lâ ilâhe illallah",
                translation = "Allah'tan başka ilah yoktur",
                count = 33,
                type = TesbihatType.ZIKIR,
                order = 14
            )
        )

        // 15. SABAH'A MAHSUS: 10 defa Kelime-i Tevhid (Melikül-Hakkul-Mubîn)
        items.add(
            TesbihatItem(
                id = "sabah_tevhid_melik_10",
                title = "Sabah'a Mahsus Kelime-i Tevhid",
                arabicText = "لآَ اِلٰهَ اِلاَّ اللّٰهُ اَلْمَلِكُ الْحَقُّ الْمُب۪ينُ مُحَمَّدٌ رَسُولُ اللّٰهِ صَادِقُ الْوَعْدِ اْلاَم۪ينُ",
                transcription = "Lâ ilâhe illallahül-melikül-hakkul-mubîn, Muhammedün Rasûlullahi sâdikul-va'dil-emîn",
                translation = "Allah'tan başka ilah yoktur. O gerçek melik, apaçık haktır. Muhammed Allah'ın Resulüdür, sözünde sadık, güvenilir olandır.",
                count = 10,
                type = TesbihatType.ZIKIR,
                order = 15
            )
        )

        // 16. Salâvat-ı Şerife Bölümü (Uzun)
        items.add(
            TesbihatItem(
                id = "sabah_salavat_uzun",
                title = "Salâvat-ı Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا كَث۪يرًا\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا رَسُولَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَا حَب۪يبَ اللّٰهِ\nاَلْفُ اَلْفِ صَلاَةٍ وَ اَلْفُ اَلْفِ سَلاَمٍ عَلَيْكَ يَٓا اَم۪ينَ وَحْىِ اللّٰهِ\nاَللّٰهُمَّ صَلِّ وَ سَلِّمْ وَ بَارِكْ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِه۪ وَ أَصْحَابِهِ بِعَدَدِ اَوْرَاقِ اْلاَشْجَارِ وَ اَمْوَاجِ الْبِحَارِ وَ قَطَرَاتِ اْلاَمْطَارِ وَ اغْفِرْلَنَا وَ ارْحَمْنَا وَ الْطُفْ بِنَا وَ بِاُسْتَادِنَا سَع۪يدِ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ بِطَلَبَةِ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ يَٓا اِلٰهَنَا بِكُلِّ صَلاَةٍ مِنْهَٓا اَشْهَدُ اَنْ لآَ اِلٰهَ اِلاَّ اللّٰهُ وَ اَشْهَدُ اَنَّ مُحَمَّدًا رَسُولُ اللّٰهِ صَلَّى اللّٰهُ تَعَالٰى عَلَيْهِ وَ سَلَّمْ",
                transcription = "Bismillahir-rahmanir-rahîm. İnnallâhe ve melâiketehü yusallûne alen-nebiyy, yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîran kesîrâ. Salli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemî'il-enbiyâi vel-mürselîn ve alâ âli küllin ve sahbi küllin ecma'în. Âmîne vel-hamdü lillâhi rabbil-âlemîn. Elfu elfi salâtin ve elfu elfi selâmin aleyke yâ Resûlallah. Elfu elfi salâtin ve elfu elfi selâmin aleyke yâ Habîballah. Elfu elfi salâtin ve elfu elfi selâmin aleyke yâ Emîne vahyillah. Allahümme salli ve sellim ve bârik alâ seyyidinâ Muhammedin ve alâ âlihî ve ashâbihî bi-adedi evrâkil-eşcâri ve emvâcil-bihâri ve katarâtil-emtâr. Vağfirlena verhamna valtuf binâ ve bi-üstâdinâ Saîdin-Nursî radıyallahü anh ve vâlideynâ ve bi-talebete Resâilin-Nûris-sâdikîn. Yâ ilâhenâ bi-külli salâtin minhâ eşhedü en lâ ilâhe illallah ve eşhedü enne Muhammeden Resûlullah sallallahü teâlâ aleyhi ve sellem",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla. Şüphesiz Allah ve melekleri Peygamber'e salât ediyorlar. Ey iman edenler! Siz de O'na salât edin ve tam bir teslimiyetle selam verin. Buyur ya Rab! Allah'ım! Efendimiz Muhammed'e ve Efendimiz Muhammed'in âline, her hastalık ve her ilaç adedince salât eyle, bereketini ve selamını çok çok ihsan et. Allah'ım! Efendimiz Muhammed'e ve Efendimiz Muhammed'in âline, her hastalık ve her ilaç adedince salât eyle, bereketini ve selamını çok çok ihsan et. Allah'ım! Efendimiz Muhammed'e ve Efendimiz Muhammed'in âline, her hastalık ve her ilaç adedince salât eyle, bereketini ve selamını pek çok çok ihsan et. Ya Rabbi! Sevgilin Muhammed'e, bütün peygamberlere, bütün rasullere, hepsinin âline ve ashabına salât ve selam eyle. Amin ve hamd âlemlerin Rabbi Allah'a mahsustur. Milyonlarca salât ve milyonlarca selam üzerine olsun ey Allah'ın Resulü! Milyonlarca salât ve milyonlarca selam üzerine olsun ey Allah'ın Habibi! Milyonlarca salât ve milyonlarca selam üzerine olsun ey Allah'ın vahyinin emini! Allah'ım! Efendimiz Muhammed'e, âline ve ashabına, ağaçların yaprakları, denizlerin dalgaları ve yağmurun damlaları sayısınca salât, selam ve bereket ihsan et. Bizi, üstadımız Said Nursî'yi, anne babamızı ve sadık Risale-i Nur talebelerini bağışla, merhamet et ve lütfet. Ey Rabbimiz! Bunların her biri için şehadet ederim ki Allah'tan başka ilah yoktur ve şehadet ederim ki Muhammed Allah'ın Resulüdür. Allah Teâlâ ona salât ve selam eylesin",
                count = 1,
                type = TesbihatType.DUA,
                order = 16
            )
        )

        // 17. Tercüman-ı İsm-i A'zâm Duası (Kısaltılmış - İlk 3 isim)
        items.add(
            TesbihatItem(
                id = "sabah_ismi_azam_kisa",
                title = "Tercüman-ı İsm-i A'zâm (Kısa)",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nسُبْحَانَكَ يَا اَللّٰهُ تَعَالَيْتَ يَا رَحْمٰنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ\nسُبْحَانَكَ يَا رَح۪يمُ تَعَالَيْتَ يَا كَر۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا قُدُّوسُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ",
                transcription = "Bismillahir-rahmanir-rahîm. Sübhâneke yâ Allah, teâleyte yâ Rahmân, ecirnâ minen-nâri bi afvike yâ Rahmân. Sübhâneke yâ Rahîm, teâleyte yâ Kerîm, ecirnâ minen-nâri bi afvike yâ Rahmân. Sübhâneke yâ Ferd, teâleyte yâ Kuddûs, ecirnâ minen-nâri bi afvike yâ Rahmân",
                translation = "Allah'ın adıyla Rahmân ve Rahîm olan. Seni tesbih ederim ya Allah, yücesin ya Rahmân, affınla bizi ateşten koru ya Rahmân. Seni tesbih ederim ya Rahîm, yücesin ya Kerîm, affınla bizi ateşten koru ya Rahmân. Seni tesbih ederim ya Ferd, yücesin ya Kuddûs, affınla bizi ateşten koru ya Rahmân",
                count = 1,
                type = TesbihatType.DUA,
                order = 17
            )
        )

        // 18. İsm-i A'zâm Kapanış Duası
        items.add(
            TesbihatItem(
                id = "sabah_ismi_azam_kapanis",
                title = "İsm-i A'zâm Kapanış",
                arabicText = "سُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتَنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ وَ رُفَقَٓائَنَا وَ اَحْبَابَنَا الْمُخْلِص۪ينَ مِنَ النَّارِ وَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Sübhâneke âhiyyen şerâhiyyen teâleyte lâ ilâhe illâ ente ecirnâ ve ecir üstâdenâ Saîd en-Nursî radıyallahü anh ve vâlideynâ...",
                translation = "Seni tesbih ederim ey Âhî Şerâhî, yücesin, Senden başka ilah yoktur. Bizi ve üstadımız Said Nursî'yi, anne babamızı, Risale-i Nur talebelerini, kardeşlerimizi ateşten koru...",
                count = 1,
                type = TesbihatType.DUA,
                order = 18
            )
        )

        // 19. Haşr Sûresi 20-24. Ayetler
        items.add(
            TesbihatItem(
                id = "sabah_hasr_20_24",
                title = "Haşr Sûresi 20-24",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ\nلَوْ اَنْزَلْنَا هٰذَا الْقُرْاٰنَ عَلٰى جَبَلٍ لَرَاَيْتَهُ خَاشِعًا مُتَصَدِّعًا مِنْ خَشْيَةِ اللّٰهِۜ وَتِلْكَ اْلاَمْثَالُ نَضْرِبُهَا لِلنَّاسِ لَعَلَّهُمْ يَتَفَكَّرُونَ\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ عَالِمُ الْغَيْبِ وَالشَّهَادَةِۚ هُوَ الرَّحْمٰنُ الرَّح۪يمُ\nهُوَ اللّٰهُ الَّذ۪ى لآَ اِلٰهَ اِلاَّ هُوَۚ اَلْمَلِكُ الْقُدُّوسُ السَّلاَمُ الْمُؤْمِنُ الْمُهَيْمِنُ الْعَز۪يزُ الْجَبَّارُ الْمُتَكَبِّرُۜ سُبْحَانَ اللّٰهِ عَمَّا يُشْرِكُونَ\nهُوَ اللّٰهُ الْخَالِقُ الْبَارِىُٔ الْمُصَوِّرُ لَهُ اْلاَسْمَٓاءُ الْحُسْنٰىۜ يُسَبِّحُ لَهُ مَا فِى السَّمٰوَاتِ وَاْلاَرْضِۚ وَهُوَ الْعَز۪يزُ الْحَك۪يمُ",
                transcription = "Bismillahir-rahmanir-rahîm. Lâ yestevî as-hâbün-nâri ve as-hâbül-cenneh, as-hâbül-cenneti hümül-fâizûn. Lev enzelnâ hâzel-Kur'âne alâ cebelin le-raeytehü hâşian müteşaddian min haşyetillah, ve tilkel-emsâlü nadrıbühâ lin-nâsi leallehüm yetefekkerûn. Hüvallahüllezî lâ ilâhe illâ hüve, âlimül-ğaybi veş-şehâdeh, hüver-rahmânür-rahîm. Hüvallahüllezî lâ ilâhe illâ hüvel-melikül-kuddûsüs-selâmül-mü'minül-müheyminül-azîzül-cebbârül-mütekebbir, sübhanallahi ammâ yüşrikûn. Hüvallahül-hâlıkul-bâriül-musavvir, lehül-esmâül-hüsnâ, yüsebbihu lehü mâ fis-semâvâti vel-ard, ve hüvel-azîzül-hakîm",
                translation = "Allah'ın adıyla Rahmân ve Rahîm olan. Cehennem ehli ile cennet ehli eşit değildir. Kurtuluşa erenler cennet ehlidir. Eğer biz bu Kur'an'ı bir dağa indirseydik, onu Allah korkusundan baş eğmiş, parça parça olmuş görürdün. İşte biz bu misalleri insanlara düşünsünler diye veriyoruz. O Allah ki, O'ndan başka ilah yoktur. Gaybı da, görüleni de bilendir. O Rahmân'dır, Rahîm'dir. O Allah ki, O'ndan başka ilah yoktur. Melik'tir, Kuddûs'tür, Selâm'dır, Mü'min'dir, Müheymin'dir, Azîz'dir, Cebbâr'dır, Mütekebbir'dir. Allah onların şirk koştuklarından münezzehtir. O, yaratan, yoktan var eden, şekil veren Allah'tır. En güzel isimler O'nundur. Göklerde ve yerde ne varsa O'nu tesbih eder. O Azîz'dir, Hakîm'dir",
                count = 1,
                type = TesbihatType.AYET,
                order = 19
            )
        )

        // 20. Fatiha-i Şerife
        items.add(
            TesbihatItem(
                id = "sabah_fatiha",
                title = "Fatiha-i Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ\nاَلرَّحْمٰنِ الرَّح۪يمِۙ\nمَالِكِ يَوْمِ الدّ۪ينِۜ\nاِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ\nاِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ\nصِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Elhamdü lillahi rabbil-âlemîn. Er-rahmânir-rahîm. Mâliki yevmid-dîn. İyyâke na'büdü ve iyyâke neste'în. İhdinâs-sırâtal-müstekîm. Sırâtallezîne en'amte aleyhim ğayril-mağdûbi aleyhim ve leddâllîn",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla. Hamd, âlemlerin Rabbi Allah'a mahsustur. O, Rahmân'dır, Rahîm'dir. Din gününün sahibidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet. Kendilerine nimet verdiklerinin yoluna; gazaba uğramışların ve sapıkların yoluna değil.",
                count = 1,
                type = TesbihatType.SURE,
                order = 20
            )
        )

        return TesbihatContent(TesbihatCategory.SABAH, items)
    }


    /**
     * ÖĞLE NAMAZI TESBİHATI
     */
    private fun getOgleTesbihat(): TesbihatContent {
        val items = mutableListOf<TesbihatItem>()

        // 1. Salâten Tüncînâ Duası
        items.add(
            TesbihatItem(
                id = "ogle_salaten_tuncina",
                title = "Salâten Tüncînâ Duası",
                arabicText = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, salâten tüncînâ bihâ min cemî'il-ehvâli vel-âfât ve takdî lenâ bihâ cemî'al-hâcât ve tutahhirunâ bihâ min cemî'is-seyyiât ve terfe'unâ bihâ indeke a'led-derecât ve tübelliğunâ bihâ aksâl-ğâyât min cemî'il-hayrât fil-hayâti ve ba'del-memât. Âmîne yâ mücîbed-de'avât vel-hamdü lillâhi rabbil-âlemîn",
                translation = "Allah'ım! Efendimiz Muhammed'e ve Efendimiz Muhammed'in âline öyle bir salât eyle ki, o salât vesilesiyle bizi bütün korku ve afetlerden kurtarasın, bütün hacetlerimizi onunla yerine getiresin, bütün kötülüklerden bizi onunla temizleyesin, katında bizi onunla en yüce derecelere yükseltsin ve dünya ve ahirette hayırların en yükseğine onunla ulaştırasın. Amin, ey duaları kabul eden! Hamd, âlemlerin Rabbi Allah'a mahsustur",
                count = 1,
                type = TesbihatType.DUA,
                order = 1
            )
        )

        // 2. Ara Dua
        items.add(
            TesbihatItem(
                id = "ogle_ara_dua",
                title = "Salâvat ve Dua",
                arabicText = "سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
                transcription = "Sübhanallahi vel-hamdülillahi ve lâ ilâhe illallahu vallahu ekber ve lâ havle ve lâ kuvvete illâ billâhil-aliyyil-azîm",
                translation = "Allah'ı tesbih ederim, Allah'a hamd ederim, Allah'tan başka ilah yoktur, Allah en büyüktür ve yüce ve azîm olan Allah'tan başka güç ve kuvvet yoktur",
                count = 1,
                type = TesbihatType.DUA,
                order = 2
            )
        )

        // 3. Âyetü'l Kürsî
        items.add(
            TesbihatItem(
                id = "ogle_ayetelkursi",
                title = "Âyetü'l Kürsî",
                arabicText = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
                transcription = "Allahü lâ ilâhe illâ hüvel hayyül kayyûm. Lâ te'huzühü sinetün ve lâ nevm. Lehü mâ fis-semâvâti ve mâ fil-ard. Men zellezî yeşfe'u indehü illâ bi iznih. Ya'lemü mâ beyne eydîhim ve mâ halfehüm ve lâ yuhîtûne bi şey'in min ilmihî illâ bimâ şâe. Vesia kürsiyyühüs-semâvâti vel-ard. Ve lâ yeûdühü hıfzuhümâ ve hüvel aliyyül azîm",
                translation = "Allah, kendisinden başka ilah olmayandır. O Hayy'dır (diri, her zaman diri), Kayyûm'dur (her şeyi ayakta tutan). O'nu ne uyuklama tutar ne de uyku. Göklerde ve yerde ne varsa hepsi O'nundur. İzni olmadan O'nun katında kim şefaat edebilir? O, kullarının önlerindekini ve arkalarındakini bilir. Onlar, O'nun ilminden, kendisinin dilediği kadarından başka bir şeyi kavrayamazlar. O'nun Kürsüsü gökleri ve yeri kaplayıp kuşatmıştır. Gökleri ve yeri koruyup gözetmek O'na güç gelmez. O, yücedir, büyüktür",
                count = 1,
                type = TesbihatType.AYET,
                order = 3
            )
        )

        // 4-6. Tesbih (33-33-33)
        items.add(
            TesbihatItem(
                id = "ogle_subhanallah",
                title = "Sübhanallah",
                arabicText = "سُبْحَانَ اللّٰهِ",
                transcription = "Sübhanallah",
                translation = "Allah noksan sıfatlardan münezzehtir",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 4
            )
        )

        items.add(
            TesbihatItem(
                id = "ogle_elhamdulillah",
                title = "Elhamdülillah",
                arabicText = "اَلْحَمْدُ لِلّٰهِ",
                transcription = "Elhamdülillah",
                translation = "Hamd Allah'a mahsustur",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 5
            )
        )

        items.add(
            TesbihatItem(
                id = "ogle_allahuekber",
                title = "Allahu Ekber",
                arabicText = "اَللّٰهُ اَكْبَرُ",
                transcription = "Allahu Ekber",
                translation = "Allah en büyüktür",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 6
            )
        )

        // 7. Tahlil
        items.add(
            TesbihatItem(
                id = "ogle_tahlil",
                title = "Tahlil",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ وَهُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh, lehül-mülkü ve lehül-hamd, yuhyî ve yümît ve hüve hayyün lâ yemût, bi yedihil-hayr ve hüve alâ külli şey'in kadîr ve ileyhil-masîr",
                translation = "Allah'tan başka ilah yoktur. O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. O diriltir ve öldürür. O diridir, ölmez. Hayır O'nun elindedir ve O her şeye kadirdir ve dönüş O'nadır",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 7
            )
        )

        // 8. Kelime-i Tevhid (33 defa)
        items.add(
            TesbihatItem(
                id = "ogle_tevhid_33",
                title = "Kelime-i Tevhid",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ",
                transcription = "Lâ ilâhe illallah",
                translation = "Allah'tan başka ilah yoktur",
                count = 33,
                type = TesbihatType.ZIKIR,
                order = 8
            )
        )

        // 9. Salâvat-ı Şerife
        items.add(
            TesbihatItem(
                id = "ogle_salavat_uzun",
                title = "Salâvat-ı Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا كَث۪يرًا\nصَلِّ وَ سَلِّمْ يَا رَبِّ عَلٰى حَب۪يبِكَ مُحَمَّدٍ وَ عَلٰى جَم۪يعِ اْلاَنْبِيَٓاءِ وَ الْمُرْسَل۪ينَ وَ عَلَٓى اٰلِ كُلٍّ وَ صَحْبِ كُلٍّ اَجْمَع۪ينَ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. İnnallâhe ve melâiketehü yusallûne alen-nebiyy, yâ eyyühellezîne âmenû sallû aleyhi ve sellimû teslîmâ. Lebbeyk. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîrâ. Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed, bi-adedi külli dâin ve devâin ve bârik ve sellim aleyhi ve aleyhim kesîran kesîrâ. Salli ve sellim yâ Rabbi alâ habîbike Muhammedin ve alâ cemî'il-enbiyâi vel-mürselîn ve alâ âli küllin ve sahbi küllin ecma'în. Âmîne vel-hamdü lillâhi rabbil-âlemîn",
                translation = "Allah'ın adıyla Rahmân ve Rahîm olan. Şüphesiz Allah ve melekleri Peygamber'e salât ediyorlar. Ey iman edenler! Siz de O'na salât edin ve tam bir teslimiyetle selam verin. Buyur ya Rab! Allah'ım! Efendimiz Muhammed'e ve âline, her hastalık ve her ilaç adedince salât eyle, bereketini ve selamını çok ihsan et (3 defa). Ya Rabbi! Sevgilin Muhammed'e, bütün peygamberlere ve rasullere, hepsinin âline ve ashabına salât ve selam eyle. Amin ve hamd âlemlerin Rabbi Allah'a mahsustur",
                count = 1,
                type = TesbihatType.DUA,
                order = 9
            )
        )

        // 10. İsm-i A'zâm Duası (Öğle'ye mahsus kısa versiyon)
        items.add(
            TesbihatItem(
                id = "ogle_ismi_azam",
                title = "İsm-i A'zâm Duası",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nيَا جَم۪يلُ يَٓا اَللّٰهُ ٭ يَا قَر۪يبُ يَٓا اَللّٰهُ\nيَا مُج۪يبُ يَٓا اَللّٰهُ ٭ يَا حَب۪يبُ يَٓا اَللّٰهُ\nيَا رَؤُفُ يَٓا اَللّٰهُ ٭ يَا عَطُوفُ يَٓا اَللّٰهُ\nيَا رَبَّ السَّمٰوَاتِ وَ اْلاَرْضِ يَا ذَا الْجَلاَلِ وَ اْلاِكْرَامِ",
                transcription = "Bismillahir-rahmanir-rahîm. Yâ Cemîl yâ Allah, yâ Karîb yâ Allah. Yâ Mücîb yâ Allah, yâ Habîb yâ Allah. Yâ Raûf yâ Allah, yâ Atûf yâ Allah. Yâ Rabbes-semâvâti vel-ard, yâ Zel-celâli vel-ikrâm",
                translation = "Allah'ın adıyla Rahmân ve Rahîm olan. Ey Cemîl (güzel) olan Allah! Ey Karîb (yakın) olan Allah! Ey Mücîb (icabet eden) olan Allah! Ey Habîb (sevgili) olan Allah! Ey Raûf (şefkatli) olan Allah! Ey Atûf (lütufkar) olan Allah! Ey göklerin ve yerin Rabbi! Ey celal ve ikram sahibi!",
                count = 1,
                type = TesbihatType.DUA,
                order = 10
            )
        )

        // 11. Fetih Sûresi 27-29
        items.add(
            TesbihatItem(
                id = "ogle_fetih_27_29",
                title = "Fetih Sûresi 27-29",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلَقَدْ صَدَقَ اللّٰهُ رَسُولَهُ الرُّءْيَا بِالْحَقِّۚ لَتَدْخُلُنَّ الْمَسْجِدَ الْحَرَامَ اِنْ شَٓاءَ اللّٰهُ اٰمِن۪ينَۙ مُحَلِّق۪ينَ رُءُوسَكُمْ وَمُقَصِّر۪ينَۙ لاَ تَخَافُونَۜ فَعَلِمَ مَا لَمْ تَعْلَمُوا فَجَعَلَ مِنْ دُونِ ذٰلِكَ فَتْحًا قَر۪يبًا\nهُوَ الَّذ۪ٓى اَرْسَلَ رَسُولَهُ بِالْهُدٰى وَد۪ينِ الْحَقِّ لِيُظْهِرَهُ عَلَى الدّ۪ينِ كُلِّه۪ۜ وَكَفٰى بِاللّٰهِ شَه۪يدًا\nمُحَمَّدٌ رَسُولُ اللّٰهِۜ وَالَّذ۪ينَ مَعَهُٓ اَشِدَّٓاءُ عَلَى الْكُفَّارِ رُحَمَٓاءُ بَيْنَهُمْۖ تَرٰيهُمْ رُكَّعًا سُجَّدًا يَبْتَغُونَ فَضْلًا مِنَ اللّٰهِ وَرِضْوَانًاۖ س۪يمَاهُمْ ف۪ي وُجُوهِهِمْ مِنْ اَثَرِ السُّجُودِۜ ذٰلِكَ مَثَلُهُمْ فِى التَّوْرٰيةِۚ وَمَثَلُهُمْ فِى اْلاِنْج۪يلِۚ كَزَرْعٍ اَخْرَجَ شَطْـَٔهُ فَاٰزَرَهُ فَاسْتَغْلَظَ فَاسْتَوٰى عَلٰى سُوقِه۪ يُعْجِبُ الزُّرَّاعَۙ لِيَغ۪يظَ بِهِمُ الْكُفَّارَۜ وَعَدَ اللّٰهُ الَّذ۪ينَ اٰمَنُوا وَعَمِلُوا الصَّالِحَاتِ مِنْهُمْ مَغْفِرَةً وَاَجْرًا عَظ۪يمًا",
                transcription = "Bismillahir-rahmanir-rahîm. Lekad sadakallahü rasûlehür-rü'yâ bil-hakk, le-tedhulünnel-mescidel-harâme in şâallahü âminîne muhallıkîne ruûseküm ve mukassırîne lâ tehâfûn, fe-alime mâ lem ta'lemû fe-ceale min dûni zâlike fethan karîbâ. Hüvellezî ersele rasûlehü bil-hüdâ ve dînil-hakkı li-yuzhirahû aled-dîni küllih, ve kefâ billâhi şehîdâ. Muhammedür-resûlullahi vellezîne meahû eşiddâü alel-küffâri ruhamâü beynehüm, terâhüm rukkeân sücceden yebteğûne fadlen minallâhi ve rıdvânâ, sîmâhüm fî vücûhihim min eseris-sucûd. Zâlike meselühüm fit-tevrâti ve meselühüm fil-incîl, ke-zer'in ahrace şat'ehû fe-âzerehû festağleza festevâ alâ sûkıhî yu'cibüz-zürrâa li-yağîza bihimül-küffâr. Ve adallahüllezîne âmenû ve amilüs-sâlihâti minhüm mağfiraten ve ecran azîmâ",
                translation = "Allah'ın adıyla Rahmân ve Rahîm olan. Allah, Resulüne rüyayı gerçek olarak doğruladı. Allah dilerse, başlarınızı tıraş ettirerek veya kısaltarak, güven içinde, korkusuzca kesinlikle Mescid-i Haram'a gireceksiniz. Allah, sizin bilmediğinizi bildi ve bundan önce yakın bir fetih nasip etti. Resulünü hidayet ve hak din ile gönderen O'dur ki, onu bütün dinlere üstün kılsın. Şahit olarak Allah yeter. Muhammed Allah'ın Resulüdür. Onunla beraber olanlar kâfirlere karşı çetin, kendi aralarında merhametlidirler. Onları rükû ve secde eder, Allah'tan lütuf ve rıza diler görürsün. Secde izinden yüzlerinde nişanları vardır. İşte bu, onların Tevrat'taki ve İncil'deki vasıflarıdır. Tıpkı bir ekin gibi ki, filizini çıkarmış, onu kuvvetlendirmiş, kalınlaşmış, gövdesi üzerinde dimdik durmuş, ekincileri hoşnut etmektedir ki, onlarla kâfirleri öfkelendirsin. Allah, içlerinden iman edip salih amel işleyenlere mağfiret ve büyük bir ecir vaat etmiştir",
                count = 1,
                type = TesbihatType.AYET,
                order = 11
            )
        )

        // 12. Fatiha
        items.add(
            TesbihatItem(
                id = "ogle_fatiha",
                title = "Fatiha-i Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ\nاَلرَّحْمٰنِ الرَّح۪يمِۙ\nمَالِكِ يَوْمِ الدّ۪ينِۜ\nاِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ\nاِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ\nصِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Elhamdü lillahi rabbil-âlemîn. Er-rahmânir-rahîm. Mâliki yevmid-dîn. İyyâke na'büdü ve iyyâke neste'în. İhdinâs-sırâtal-müstekîm. Sırâtallezîne en'amte aleyhim ğayril-mağdûbi aleyhim ve leddâllîn",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla. Hamd, âlemlerin Rabbi Allah'a mahsustur. O, Rahmân'dır, Rahîm'dir. Din gününün sahibidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet. Kendilerine nimet verdiklerinin yoluna; gazaba uğramışların ve sapıkların yoluna değil",
                count = 1,
                type = TesbihatType.SURE,
                order = 12
            )
        )

        return TesbihatContent(TesbihatCategory.OGLE, items)
    }

    /**
     * İKİNDİ NAMAZI TESBİHATI
     * Öğle ile aynı ama Fetih Sûresi yerine Nebe Sûresi var
     */
    private fun getIkindiTesbihat(): TesbihatContent {
        val ogleContent = getOgleTesbihat()
        val items = ogleContent.items.map { item ->
            if (item.id == "ogle_fetih_27_29") {
                // Fetih Sûresi yerine Nebe Sûresi
                item.copy(
                    id = "ikindi_nebe",
                    title = "Nebe Sûresi",
                    arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nعَمَّ يَتَسَٓاءَلُونَۚ\nعَنِ النَّبَإِ الْعَظ۪يمِۙ\nاَلَّذ۪ى هُمْ ف۪يهِ مُخْتَلِفُونَۜ\nكَلاَّ سَيَعْلَمُونَۙ\nثُمَّ كَلاَّ سَيَعْلَمُونَ",
                    transcription = "Bismillahir-rahmanir-rahîm. Amme yetesâelûn. Anin-nebeil-azîm. Ellezî hüm fîhi muhtelifûn. Kellâ seya'lemûn. Sümme kellâ seya'lemûn",
                    translation = "Allah'ın adıyla. Neyi sorup duruyorlar? O büyük haberi mi? Hakkında görüş ayrılığına düştükleri (haberi mi). Hayır! Yakında bilecekler. Sonra yine hayır! Yakında bilecekler.",
                    count = 1,
                    type = TesbihatType.SURE
                )
            } else {
                item.copy(id = item.id.replace("ogle", "ikindi"))
            }
        }
        return TesbihatContent(TesbihatCategory.IKINDI, items)
    }

    /**
     * AKŞAM NAMAZI TESBİHATI
     * Sabah ile benzer yapıda (3x3x3 tesbih)
     */
    private fun getAksamTesbihat(): TesbihatContent {
        val items = mutableListOf<TesbihatItem>()

        // 1. Salâten Tüncînâ Duası
        items.add(
            TesbihatItem(
                id = "aksam_salaten_tuncina",
                title = "Salâten Tüncînâ Duası",
                arabicText = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed...",
                translation = "Allah'ım! Efendimiz Muhammed'e ve âline öyle bir salât eyle ki...",
                count = 1,
                type = TesbihatType.DUA,
                order = 1
            )
        )

        // 2. Kelime-i Tevhid (9+1 defa) - Giriş
        items.add(
            TesbihatItem(
                id = "aksam_giris_duasi",
                title = "Giriş Duası",
                arabicText = "اٰمَنَّا بِاَنَّهُ",
                transcription = "Âmennâ bi ennehü",
                translation = "İman ettik ki O...",
                count = 1,
                type = TesbihatType.DUA,
                order = 2
            )
        )

        // 3. Kelime-i Tevhid (9 defa)
        items.add(
            TesbihatItem(
                id = "aksam_tevhid_9",
                title = "Kelime-i Tevhid (9 defa)",
                arabicText = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh, lehül-mülkü ve lehül-hamd, yuhyî ve yümît, bi yedihil-hayr ve hüve alâ külli şey'in kadîr",
                translation = "Allah'tan başka ilah yoktur. O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. O diriltir ve öldürür. Hayır O'nun elindedir ve O her şeye kadirdir.",
                count = 9,
                type = TesbihatType.ZIKIR,
                order = 3
            )
        )

        // 4. Kelime-i Tevhid (10. - Tamamlayıcı)
        items.add(
            TesbihatItem(
                id = "aksam_tevhid_10",
                title = "Kelime-i Tevhid (10.)",
                arabicText = "لآَ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لآَ شَر۪يكَ لَهُ لَهُ الْمُلْكُ وَ لَهُ الْحَمْدُ يُحْي۪ى وَ يُم۪يتُ وَ هُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَ هُوَ عَلٰى كُلِّ شَىْءٍ قَد۪يرٌ وَ اِلَيْهِ الْمَص۪يرُ",
                transcription = "...ve hüve hayyün lâ yemût...ve ileyhil-masîr",
                translation = "...O diridir, ölmez...ve dönüş O'nadır.",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 4
            )
        )

        // 5. Allahümme Ecirnâ Minen-Nâr
        items.add(
            TesbihatItem(
                id = "aksam_ecirna_minen_nar",
                title = "Allahümme Ecirnâ Minen-Nâr",
                arabicText = "اَللّٰهُمَّ اَجِرْنَا مِنَ النَّارِ",
                transcription = "Allahümme ecirnâ minen-nâr",
                translation = "Allah'ım! Bizi cehennem ateşinden koru",
                count = 7,
                type = TesbihatType.DUA,
                order = 5
            )
        )

        // 6. Ecirnâ Duaları Uzun
        items.add(
            TesbihatItem(
                id = "aksam_ecirna_uzun",
                title = "Ecirnâ Duaları",
                arabicText = "اَللّٰهُمَّ اَجِرْنَا مِنْ كُلِّ نَارٍ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الدّ۪ينِيَّةِ وَ الدُّنْيَوِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ اٰخِرِ الزَّمَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْمَس۪يحِ الدَّجَّالِ وَ السُّفْيَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الضَّلاَلاَتِ وَ الْبِدْعِيَّاتِ وَ الْبَلِيَّاتِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النَّفْسِ اْلاَمَّارَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شُرُورِ النُّفُوسِ اْلاَمَّارَاتِ الْفِرْعَوْنِيَّةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ بَلآَءِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ النِّسَٓاءِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ يَوْمِ الْقِيٰمَةِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ جَهَنَّمَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ نَارِ قَهْرِكَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ عَذَابِ الْقَبْرِ وَ النِّرَانِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنَ الرِّيَاءِ وَ السُّمْعَةِ وَ الْعُجُبِ وَ الْفَخْرِ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ تَجَاوُزِ الْمُلْحِد۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ شَرِّ الْمُنَافِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا مِنْ فِتْنَةِ الْفَاسِق۪ينَ ٭ اَللّٰهُمَّ اَجِرْنَا وَ اَجِرْ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ ف۪ى خِدْمَةِ الْقُرْاٰنِ وَ اْلا۪يمَانِ وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا مِنَ النَّارِ",
                transcription = "Allahümme ecirnâ min külli nâr...",
                translation = "Allah'ım! Bizi her türlü ateşten koru...",
                count = 1,
                type = TesbihatType.DUA,
                order = 6
            )
        )

        // 7. Cennet Duası
        items.add(
            TesbihatItem(
                id = "aksam_cennet_duasi",
                title = "Cennet Duası",
                arabicText = "بِعَفْوِكَ يَا مُج۪يرُ بِفَضْلِكَ يَا غَفَّارُ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ ٭ اَللّٰهُمَّ اَدْخِلْنَا وَ اَدْخِلْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ الصَّدِق۪ينَ وَ اِخْوَانَنَا وَ اَخَوَاتَنَا وَ اَقْرِبَٓائَنَا وَ اَجْدَادَنَا وَ اَحْبَابَنَا الْمُؤْمِن۪ينَ الْمُخْلِص۪ينَ ف۪ى خِدْمَةِ اْلا۪يمَانِ وَ الْقُرْاٰنِ اَلْجَنَّةَ مَعَ اْلاَبْرَارِ صَلِّ عَلٰى نَبِيِّكَ الْمُخْتَارِ وَ اٰلِه۪ اْلاَطْهَارِ وَ اَصْحَابِهِ اْلاَخْيَارِ وَ سَلِّمْ مَادَامَ الَّيْلُ وَ النَّهَارُ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Bi afvike yâ mücîr, bi fadlike yâ ğaffâr. Allahümme edhılnel-cennete meal-ebrâr...",
                translation = "Affınla ey koruyan, fazlınla ey bağışlayan! Allah'ım! Bizi iyilerle birlikte cennete koy...",
                count = 1,
                type = TesbihatType.DUA,
                order = 7
            )
        )

        // 8. Ara Dua
        items.add(
            TesbihatItem(
                id = "aksam_ara_dua",
                title = "Salâvat ve Dua",
                arabicText = "سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
                transcription = "Sübhanallahi vel-hamdülillahi ve lâ ilâhe illallahu vallahu ekber...",
                translation = "Allah'ı tesbih ederim, Allah'a hamd ederim...",
                count = 1,
                type = TesbihatType.DUA,
                order = 8
            )
        )

        // 9. Âyetü'l Kürsî
        items.add(
            TesbihatItem(
                id = "aksam_ayetelkursi",
                title = "Âyetü'l Kürsî",
                arabicText = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
                transcription = "Allahü lâ ilâhe illâ hüvel hayyül kayyûm...",
                translation = "Allah, kendisinden başka ilah olmayandır...",
                count = 1,
                type = TesbihatType.AYET,
                order = 9
            )
        )

        // 10-12. Tesbih (33-33-33)
        items.add(
            TesbihatItem(
                id = "aksam_subhanallah",
                title = "Sübhanallah",
                arabicText = "سُبْحَانَ اللّٰهِ",
                transcription = "Sübhanallah",
                translation = "Allah noksan sıfatlardan münezzehtir",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 10
            )
        )

        items.add(
            TesbihatItem(
                id = "aksam_elhamdulillah",
                title = "Elhamdülillah",
                arabicText = "اَلْحَمْدُ لِلّٰهِ",
                transcription = "Elhamdülillah",
                translation = "Hamd Allah'a mahsustur",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 11
            )
        )

        items.add(
            TesbihatItem(
                id = "aksam_allahuekber",
                title = "Allahu Ekber",
                arabicText = "اَللّٰهُ اَكْبَرُ",
                transcription = "Allahu Ekber",
                translation = "Allah en büyüktür",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 12
            )
        )

        // 13. Tahlil
        items.add(
            TesbihatItem(
                id = "aksam_tahlil",
                title = "Tahlil",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ وَهُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh...",
                translation = "Allah'tan başka ilah yoktur...",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 13
            )
        )

        // 14. Kelime-i Tevhid (33 defa)
        items.add(
            TesbihatItem(
                id = "aksam_tevhid_33",
                title = "Kelime-i Tevhid",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ",
                transcription = "Lâ ilâhe illallah",
                translation = "Allah'tan başka ilah yoktur",
                count = 33,
                type = TesbihatType.ZIKIR,
                order = 14
            )
        )

        // 15. Salâvat-ı Şerife
        items.add(
            TesbihatItem(
                id = "aksam_salavat_uzun",
                title = "Salâvat-ı Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ",
                transcription = "Bismillahir-rahmanir-rahîm. İnnallâhe ve melâiketehü yusallûne alen-nebiyy...",
                translation = "Allah'ın adıyla. Şüphesiz Allah ve melekleri Peygamber'e salât ediyorlar...",
                count = 1,
                type = TesbihatType.DUA,
                order = 15
            )
        )

        // 16. İsm-i A'zâm Duası (Tam versiyon)
        items.add(
            TesbihatItem(
                id = "aksam_ismi_azam",
                title = "İsm-i A'zâm Duası",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nسُبْحَانَكَ يَا اَللّٰهُ تَعَالَيْتَ يَا رَحْمٰنُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ\nسُبْحَانَكَ يَا رَح۪يمُ تَعَالَيْتَ يَا كَر۪يمُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ\nسُبْحَانَكَ يَا فَرْدُ تَعَالَيْتَ يَا قُدُّوسُ اَجِرْنَا مِنَ النَّارِ بِعَفْوِكَ يَا رَحْمٰنُ",
                transcription = "Bismillahir-rahmanir-rahîm. Sübhâneke yâ Allah...",
                translation = "Allah'ın adıyla. Seni tesbih ederim ya Allah...",
                count = 1,
                type = TesbihatType.DUA,
                order = 16
            )
        )

        // 17. İsm-i A'zâm Kapanış
        items.add(
            TesbihatItem(
                id = "aksam_ismi_azam_kapanis",
                title = "İsm-i A'zâm Kapanış",
                arabicText = "سُبْحَانَكَ اٰهِيًّا شَرَاهِيًّا تَعَالَيْتَ لآَ اِلٰهَ اِلآَّ اَنْتَ اَجِرْنَا وَ اَجِرْ اُسْتَادَنَا سَع۪يدَ النُّورْس۪ى رَضِىَ اللّٰهُ عَنْهُ وَ وَالِدَيْنَا وَ اِخْوَانَنَا وَ اَخَوَاتَنَا وَ طَلَبَةَ رَسَٓائِلِ النُّورِ وَ رُفَقَٓائَنَا وَ اَحْبَابَنَا الْمُخْلِص۪ينَ مِنَ النَّارِ وَ مِنْ كُلِّ نَارٍ وَ احْفَظْنَا مِنْ شَرِّ النَّفْسِ وَ الشَّيْطَانِ وَ مِنْ شَرِّ الْجِنِّ وَ اْلاِنْسَانِ وَ مِنْ شَرِّ الْبِدْعَةِ وَ الضَّلاَلاَتِ وَ اْلاِلْحَادِ وَ الطُّغْيَانِ اَللّٰهُمَّ اَدْخِلْنَا الْجَنَّةَ مَعَ اْلاَبْرَارِ بِشَفَاعَةِ نَبِيِّكَ الْمُخْتَارِ اٰم۪ينَ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Sübhâneke âhiyyen şerâhiyyen...",
                translation = "Seni tesbih ederim ey Âhî Şerâhî...",
                count = 1,
                type = TesbihatType.DUA,
                order = 17
            )
        )

        // 18. Haşr Sûresi 20-24
        items.add(
            TesbihatItem(
                id = "aksam_hasr_20_24",
                title = "Haşr Sûresi 20-24",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nلاَ يَسْتَو۪ٓى اَصْحَابُ النَّارِ وَاَصْحَابُ الْجَنَّةِۜ اَصْحَابُ الْجَنَّةِ هُمُ الْفَٓائِزُونَ",
                transcription = "Bismillahir-rahmanir-rahîm. Lâ yestevî as-hâbün-nâri ve as-hâbül-cenneh...",
                translation = "Allah'ın adıyla. Cehennem ehli ile cennet ehli eşit değildir...",
                count = 1,
                type = TesbihatType.AYET,
                order = 18
            )
        )

        // 19. Fatiha
        items.add(
            TesbihatItem(
                id = "aksam_fatiha",
                title = "Fatiha-i Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ\nاَلرَّحْمٰنِ الرَّح۪يمِۙ\nمَالِكِ يَوْمِ الدّ۪ينِۜ\nاِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ\nاِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ\nصِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Elhamdü lillahi rabbil-âlemîn...",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla...",
                count = 1,
                type = TesbihatType.SURE,
                order = 19
            )
        )

        return TesbihatContent(TesbihatCategory.AKSAM, items)
    }

    /**
     * YATSI NAMAZI TESBİHATI
     * Öğle/İkindi ile benzer ama Âmene'r-Rasûl var
     */
    private fun getYatsiTesbihat(): TesbihatContent {
        val items = mutableListOf<TesbihatItem>()

        // 1. Salâten Tüncînâ Duası
        items.add(
            TesbihatItem(
                id = "yatsi_salaten_tuncina",
                title = "Salâten Tüncînâ Duası",
                arabicText = "اَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَ عَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ صَلاَةً تُنْج۪ينَا بِهَا مِنْ جَم۪يعِ اْلاَهْوَالِ وَ اْلاٰفَاتِ وَ تَقْض۪ى لَنَا بِهَا جَم۪يعَ الْحَاجَاتِ وَ تُطَهِّرُنَا بِهَا مِنْ جَم۪يعِ السَّيِّئَاتِ وَ تَرْفَعُنَا بِهَا عِنْدَكَ اَعْلَى الدَّرَجَاتِ وَ تُبَلِّغُنَا بِهَٓا اَقْصَى الْغَايَاتِ مِنْ جَم۪يعِ الْخَيْرَاتِ فِى الْحَيَاةِ وَ بَعْدَ الْمَمَاتِ اٰم۪ينَ يَا مُج۪يبَ الدَّعَوَاتِ وَالْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَ",
                transcription = "Allahümme salli alâ seyyidinâ Muhammedin ve alâ âli seyyidinâ Muhammed...",
                translation = "Allah'ım! Efendimiz Muhammed'e ve âline öyle bir salât eyle ki...",
                count = 1,
                type = TesbihatType.DUA,
                order = 1
            )
        )

        // 2. Ara Dua
        items.add(
            TesbihatItem(
                id = "yatsi_ara_dua",
                title = "Salâvat ve Dua",
                arabicText = "سُبْحَانَ اللَّهِ وَ الْحَمْدُ لِلَّهِ وَ لَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ وَلَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
                transcription = "Sübhanallahi vel-hamdülillahi ve lâ ilâhe illallahu vallahu ekber...",
                translation = "Allah'ı tesbih ederim, Allah'a hamd ederim...",
                count = 1,
                type = TesbihatType.DUA,
                order = 2
            )
        )

        // 3. Âyetü'l Kürsî
        items.add(
            TesbihatItem(
                id = "yatsi_ayetelkursi",
                title = "Âyetü'l Kürsî",
                arabicText = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
                transcription = "Allahü lâ ilâhe illâ hüvel hayyül kayyûm...",
                translation = "Allah, kendisinden başka ilah olmayandır...",
                count = 1,
                type = TesbihatType.AYET,
                order = 3
            )
        )

        // 4-6. Tesbih (33-33-33)
        items.add(
            TesbihatItem(
                id = "yatsi_subhanallah",
                title = "Sübhanallah",
                arabicText = "سُبْحَانَ اللّٰهِ",
                transcription = "Sübhanallah",
                translation = "Allah noksan sıfatlardan münezzehtir",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 4
            )
        )

        items.add(
            TesbihatItem(
                id = "yatsi_elhamdulillah",
                title = "Elhamdülillah",
                arabicText = "اَلْحَمْدُ لِلّٰهِ",
                transcription = "Elhamdülillah",
                translation = "Hamd Allah'a mahsustur",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 5
            )
        )

        items.add(
            TesbihatItem(
                id = "yatsi_allahuekber",
                title = "Allahu Ekber",
                arabicText = "اَللّٰهُ اَكْبَرُ",
                transcription = "Allahu Ekber",
                translation = "Allah en büyüktür",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 6
            )
        )

        // 7. Tahlil
        items.add(
            TesbihatItem(
                id = "yatsi_tahlil",
                title = "Tahlil",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ وَهُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh...",
                translation = "Allah'tan başka ilah yoktur...",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 7
            )
        )

        // 8. Kelime-i Tevhid (33 defa)
        items.add(
            TesbihatItem(
                id = "yatsi_tevhid_33",
                title = "Kelime-i Tevhid",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ",
                transcription = "Lâ ilâhe illallah",
                translation = "Allah'tan başka ilah yoktur",
                count = 33,
                type = TesbihatType.ZIKIR,
                order = 8
            )
        )

        // 9. Salâvat-ı Şerife
        items.add(
            TesbihatItem(
                id = "yatsi_salavat_uzun",
                title = "Salâvat-ı Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاِنَّ اللّٰهَ وَ مَلَٓئِكَتَهُ يُصَلُّونَ عَلَى النَّبِىِّ يَٓا اَيُّهَا الَّذ۪ينَ اٰمَنُوا صَلُّوا عَلَيْهِ وَ سَلِّمُوا تَسْل۪يمًا ٭ لَبَّيْكَ\nاَللّٰهُمَّ صَلِّ عَلٰى سَيِّدِنَا مُحَمَّدٍ وَعَلَٓى اٰلِ سَيِّدِنَا مُحَمَّدٍ بِعَدَدِ كُلِّ دَٓاءٍ وَدَوَٓاءٍ وَبَارِكْ وَسَلِّمْ عَلَيْهِ وَعَلَيْهِمْ كَث۪يرًا",
                transcription = "Bismillahir-rahmanir-rahîm. İnnallâhe ve melâiketehü yusallûne alen-nebiyy...",
                translation = "Allah'ın adıyla. Şüphesiz Allah ve melekleri Peygamber'e salât ediyorlar...",
                count = 1,
                type = TesbihatType.DUA,
                order = 9
            )
        )

        // 10. İsm-i A'zâm Duası (Kısa)
        items.add(
            TesbihatItem(
                id = "yatsi_ismi_azam",
                title = "İsm-i A'zâm Duası",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nيَا جَم۪يلُ يَٓا اَللّٰهُ ٭ يَا قَر۪يبُ يَٓا اَللّٰهُ\nيَا مُج۪يبُ يَٓا اَللّٰهُ ٭ يَا حَب۪يبُ يَٓا اَللّٰهُ\nيَا غَفَّارُ يَٓا اَللّٰهُ ٭ يَا فَتَّاحُ يَٓا اَللّٰهُ",
                transcription = "Bismillahir-rahmanir-rahîm. Yâ Cemîl yâ Allah, yâ Karîb yâ Allah...",
                translation = "Allah'ın adıyla. Ey Cemîl olan Allah! Ey Karîb olan Allah!...",
                count = 1,
                type = TesbihatType.DUA,
                order = 10
            )
        )

        // 11. YATSI'YA MAHSUS: Âmene'r-Rasûl (Bakara 285-286)
        items.add(
            TesbihatItem(
                id = "yatsi_amene",
                title = "Âmene'r-Rasûl (Bakara 285-286)",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاٰمَنَ الرَّسُولُ بِمَٓا اُنْزِلَ اِلَيْهِ مِنْ رَبِّه۪ وَالْمُؤْمِنُونَۜ كُلٌّ اٰمَنَ بِاللّٰهِ وَمَلٰٓئِكَتِه۪ وَكُتُبِه۪ وَرُسُلِه۪ۜ لَا نُفَرِّقُ بَيْنَ اَحَدٍ مِنْ رُسُلِه۪۠ وَقَالُوا سَمِعْنَا وَاَطَعْنَا غُفْرَانَكَ رَبَّنَا وَاِلَيْكَ الْمَص۪يرُ\nلَا يُكَلِّفُ اللّٰهُ نَفْسًا اِلَّا وُسْعَهَاۜ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْۜ رَبَّنَا لَا تُؤَاخِذْنَٓا اِنْ نَس۪ينَٓا اَوْ اَخْطَأْنَاۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَٓا اِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذ۪ينَ مِنْ قَبْلِنَاۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِه۪ۚ وَاعْفُ عَنَّا۠ وَاغْفِرْ لَنَا۠ وَارْحَمْنَا۠ اَنْتَ مَوْلٰينَا فَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِر۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Âmener-rasûlü bimâ ünzile ileyhi min rabbihî vel-mü'minûn. Küllün âmene billâhi ve melâiketihî ve kütübihî ve rusülih, lâ nüferriku beyne ehadin min rusülih. Ve kâlû semi'nâ ve eta'nâ ğufrâneke rabbenâ ve ileykel-masîr. Lâ yükellifullaahü nefsen illâ vüs'ahâ...",
                translation = "Allah'ın adıyla. Resul, Rabbinden kendisine indirilene iman etti, mü'minler de. Her biri Allah'a, meleklerine, kitaplarına ve peygamberlerine iman etti. (Dediler ki:) 'O'nun peygamberleri arasında ayırım yapmayız.' Ve dediler ki: 'İşittik ve itaat ettik. Bağışlamanı dileriz Rabbimiz! Dönüş Sanadır.' Allah, hiç kimseyi gücünün üstünde bir şeyle yükümlü tutmaz...",
                count = 1,
                type = TesbihatType.AYET,
                order = 11
            )
        )

        // 12. Fatiha
        items.add(
            TesbihatItem(
                id = "yatsi_fatiha",
                title = "Fatiha-i Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ\nاَلرَّحْمٰنِ الرَّح۪يمِۙ\nمَالِكِ يَوْمِ الدّ۪ينِۜ\nاِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ\nاِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ\nصِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Elhamdü lillahi rabbil-âlemîn...",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla...",
                count = 1,
                type = TesbihatType.SURE,
                order = 12
            )
        )

        return TesbihatContent(TesbihatCategory.YATSI, items)
    }


    /**
     * GENEL TESBİHAT
     * Tüm namazlarda ortak olan temel tesbihat
     */
    private fun getGenelTesbihat(): TesbihatContent {
        val items = mutableListOf<TesbihatItem>()

        // 1. Âyetü'l Kürsî
        items.add(
            TesbihatItem(
                id = "genel_ayetelkursi",
                title = "Âyetü'l Kürsî",
                arabicText = "اَللّٰهُ لَٓا اِلٰهَ اِلَّا هُوَۚ اَلْحَيُّ الْقَيُّومُۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌۜ لَهُ مَا فِي السَّمٰوَاتِ وَمَا فِي الْاَرْضِۜ مَنْ ذَا الَّذ۪ي يَشْفَعُ عِنْدَهُٓ اِلَّا بِاِذْنِه۪ۜ يَعْلَمُ مَا بَيْنَ اَيْد۪يهِمْ وَمَا خَلْفَهُمْۚ وَلَا يُح۪يطُونَ بِشَيْءٍ مِنْ عِلْمِه۪ٓ اِلَّا بِمَا شَٓاءَۚ وَسِعَ كُرْسِيُّهُ السَّمٰوَاتِ وَالْاَرْضَۚ وَلَا يَؤُ۫دُهُ حِفْظُهُمَاۚ وَهُوَ الْعَلِيُّ الْعَظ۪يمُ",
                transcription = "Allahü lâ ilâhe illâ hüvel hayyül kayyûm. Lâ te'huzühü sinetün ve lâ nevm. Lehü mâ fis-semâvâti ve mâ fil-ard. Men zellezî yeşfe'u indehü illâ bi iznih. Ya'lemü mâ beyne eydîhim ve mâ halfehüm ve lâ yuhîtûne bi şey'in min ilmihî illâ bimâ şâe. Vesia kürsiyyühüs-semâvâti vel-ard. Ve lâ yeûdühü hıfzuhümâ ve hüvel aliyyül azîm",
                translation = "Allah, kendisinden başka ilah olmayandır. O Hayy'dır (diri, her zaman diri), Kayyûm'dur (her şeyi ayakta tutan). O'nu ne uyuklama tutar ne de uyku. Göklerde ve yerde ne varsa hepsi O'nundur. İzni olmadan O'nun katında kim şefaat edebilir? O, kullarının önlerindekini ve arkalarındakini bilir. Onlar, O'nun ilminden, kendisinin dilediği kadarından başka bir şeyi kavrayamazlar. O'nun Kürsüsü gökleri ve yeri kaplayıp kuşatmıştır. Gökleri ve yeri koruyup gözetmek O'na güç gelmez. O, yücedir, büyüktür.",
                count = 1,
                type = TesbihatType.AYET,
                order = 1
            )
        )

        // 2. Sübhanallah (33 defa)
        items.add(
            TesbihatItem(
                id = "genel_subhanallah",
                title = "Sübhanallah",
                arabicText = "سُبْحَانَ اللّٰهِ",
                transcription = "Sübhanallah",
                translation = "Allah noksan sıfatlardan münezzehtir",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 2
            )
        )

        // 3. Elhamdülillah (33 defa)
        items.add(
            TesbihatItem(
                id = "genel_elhamdulillah",
                title = "Elhamdülillah",
                arabicText = "اَلْحَمْدُ لِلّٰهِ",
                transcription = "Elhamdülillah",
                translation = "Hamd Allah'a mahsustur",
                count = 33,
                type = TesbihatType.TESBIH,
                order = 3
            )
        )

        // 4. Allahu Ekber (34 defa - Genel'de 34)
        items.add(
            TesbihatItem(
                id = "genel_allahuekber",
                title = "Allahu Ekber",
                arabicText = "اَللّٰهُ اَكْبَرُ",
                transcription = "Allahu Ekber",
                translation = "Allah en büyüktür",
                count = 34,
                type = TesbihatType.TESBIH,
                order = 4
            )
        )

        // 5. Tahlil
        items.add(
            TesbihatItem(
                id = "genel_tahlil",
                title = "Tahlil",
                arabicText = "لاۤ اِلٰهَ اِلاَّ اللّٰهُ وَحْدَهُ لاَ شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ يُحْيِى وَيُمِيتُ وَهُوَ حَىٌّ لاَ يَمُوتُ بِيَدِهِ الْخَيْرُ وَهُوَ عَلٰى كُلِّ شَىْءٍ قَدِيرٌ وَاِلَيْهِ الْمَص۪يرُ",
                transcription = "Lâ ilâhe illallahü vahdehü lâ şerîke leh, lehül-mülkü ve lehül-hamd, yuhyî ve yümît ve hüve hayyün lâ yemût, bi yedihil-hayr ve hüve alâ külli şey'in kadîr ve ileyhil-masîr",
                translation = "Allah'tan başka ilah yoktur. O birdir, ortağı yoktur. Mülk O'nundur, hamd O'na mahsustur. O diriltir ve öldürür. O diridir, ölmez. Hayır O'nun elindedir ve O her şeye kadirdir ve dönüş O'nadır.",
                count = 1,
                type = TesbihatType.ZIKIR,
                order = 5
            )
        )

        // 6. Fatiha-i Şerife
        items.add(
            TesbihatItem(
                id = "genel_fatiha",
                title = "Fatiha-i Şerife",
                arabicText = "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّح۪يمِ\nاَلْحَمْدُ لِلّٰهِ رَبِّ الْعَالَم۪ينَۙ\nاَلرَّحْمٰنِ الرَّح۪يمِۙ\nمَالِكِ يَوْمِ الدّ۪ينِۜ\nاِيَّاكَ نَعْبُدُ وَاِيَّاكَ نَسْتَع۪ينُۜ\nاِهْدِنَا الصِّرَاطَ الْمُسْتَق۪يمَۙ\nصِرَاطَ الَّذ۪ينَ اَنْعَمْتَ عَلَيْهِمْۙ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّٓالّ۪ينَ",
                transcription = "Bismillahir-rahmanir-rahîm. Elhamdü lillahi rabbil-âlemîn. Er-rahmânir-rahîm. Mâliki yevmid-dîn. İyyâke na'büdü ve iyyâke neste'în. İhdinâs-sırâtal-müstekîm. Sırâtallezîne en'amte aleyhim ğayril-mağdûbi aleyhim ve leddâllîn",
                translation = "Rahmân ve Rahîm olan Allah'ın adıyla. Hamd, âlemlerin Rabbi Allah'a mahsustur. O, Rahmân'dır, Rahîm'dir. Din gününün sahibidir. Yalnız Sana ibadet eder ve yalnız Senden yardım dileriz. Bizi doğru yola ilet. Kendilerine nimet verdiklerinin yoluna; gazaba uğramışların ve sapıkların yoluna değil.",
                count = 1,
                type = TesbihatType.SURE,
                order = 6
            )
        )

        return TesbihatContent(TesbihatCategory.GENEL, items)
    }
}