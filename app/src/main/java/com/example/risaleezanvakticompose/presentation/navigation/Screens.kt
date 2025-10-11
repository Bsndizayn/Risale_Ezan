package com.example.risaleezanvakticompose.presentation.navigation

import androidx.navigation.NavBackStackEntry
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * ════════════════════════════════════════════════════════════════════════════════
 * NAVIGATION SCREEN HIERARCHY
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * Bu sealed class yapısı uygulamanın tüm ekranlarını tip güvenli bir şekilde temsil ediyor.
 *
 * ┌─ SEALED CLASS NEDİR VE NEDEN KULLANIYORUZ? ─────────────────────────────────┐
 * │                                                                              │
 * │ Sealed class, sınırlı sayıda alt sınıfa sahip olabilen özel bir class türü. │
 * │ Normal class'tan farkı: tüm alt sınıflar compile-time'da bilinir.          │
 * │                                                                              │
 * │ Avantajları:                                                                 │
 * │ 1. TİP GÜVENLİĞİ: Yanlış route kullanımı compile-time'da yakalanır         │
 * │    Örnek: "book_detial" (typo) yazmaya çalışsan kod compile olmaz          │
 * │                                                                              │
 * │ 2. EXHAUSTIVE WHEN: When expression'da tüm case'leri yazmaya zorluyor      │
 * │    Yeni ekran eklediğinde when kullanan her yerde uyarı alırsın            │
 * │                                                                              │
 * │ 3. IDE DESTEĞİ: Autocomplete çalışır, refactoring güvenli olur             │
 * │    Screen.Detail yazınca IDE tüm detail ekranlarını öneriyor                │
 * │                                                                              │
 * │ 4. HİYERARŞİK YAPI: Ekranlar arasındaki ilişki kodda görünür               │
 * │    Auth ekranları vs Main ekranları ayrımı net                              │
 * │                                                                              │
 * └──────────────────────────────────────────────────────────────────────────────┘
 *
 * NEDEN STRING ROUTE KULLANIYORUZ?
 * Navigation Compose kütüphanesi String route'lar üzerine kurulu. Alternatif olarak
 * resource ID kullanılabilirdi (XML navigation gibi) ama Compose dünyasında String
 * daha esnek ve deep link desteği daha kolay. Sealed class sayesinde String'lerin
 * dezavantajları (typo, magic string) ortadan kalkıyor.
 */
sealed class Screen {
    /**
     * Her ekranın bir route string'i var. Bu abstract olarak tanımlanıyor çünkü
     * her alt sınıf kendi route'unu belirleyecek. Abstract property kullanmamızın
     * nedeni: sealed class'ın kendisi instantiate edilemez, sadece alt sınıfları
     * oluşturulabilir. Bu da mantıklı çünkü "Screen" soyut bir kavram, gerçek
     * ekranlar onun alt sınıfları.
     */
    abstract val route: String

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * AUTH - KİMLİK DOĞRULAMA VE ONBOARDING AKIŞI
     * ════════════════════════════════════════════════════════════════════════════
     *
     * Bu katman uygulamanın "dış kapısı". Kullanıcı henüz uygulamaya tam olarak
     * girmeden önce gördüğü ekranlar burada. İki ana senaryo var:
     *
     * 1. İLK KULLANIM: Onboarding ekranları gösteriliyor
     * 2. GİRİŞ YAPILMIŞ: Direkt Main ekranına yönlendiriliyor
     *
     * Bu ekranlar farklı bir NavController ile yönetiliyor (RootNavigationGraph).
     * Böylece kullanıcı ana uygulamaya girdikten sonra geri tuşuna basınca
     * onboarding'e dönmüyor, çünkü farklı bir navigation context'i.
     */
    sealed class Auth : Screen() {
        data object OnBoarding : Auth() {
            override val route = "onboarding"
            const val ROUTE = "onboarding"
        }

        data object Permissions : Auth() {
            override val route = "permissions"
            const val ROUTE = "permissions"
        }

        data object Main : Auth() {
            override val route = "main"
            const val ROUTE = "main"
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * MAIN - ANA UYGULAMA EKRANLARı (BOTTOM NAVIGATION SEVIYESI)
     * ════════════════════════════════════════════════════════════════════════════
     *
     * Bu ekranlar kullanıcının en sık etkileşimde bulunduğu, uygulamanın core
     * functionality'sini barındıran ekranlar. Hepsi aynı hiyerarşi seviyesinde
     * ve bottom navigation bar ile erişilebilir durumda.
     *
     * Bu ekranların ortak özellikleri:
     * • Parametre almıyorlar (stateless navigation)
     * • Bottom navigation bar'da görünüyorlar
     * • Aralarında geçiş yaparken back stack temizleniyor
     * • Her biri kendi state'ini koruyor (saveState/restoreState ile)
     */
    sealed class Main : Screen() {

        /**
         * HOME SCREEN - Kitap Arama ve Keşfet
         *
         * Bu ekran uygulamanın "landing page"i. Kullanıcı ana uygulamaya
         * girdiğinde ilk gördüğü yer. StartDestination olarak kullanılıyor
         * MainScreenNavHost'ta.
         *
         * Burada kitap arama, filtreleme, öneriler gibi discovery özellikleri var.
         * Kullanıcı bir kitaba tıklayınca BookDetail ekranına navigate ediliyor.
         */
        data object Home : Main() {
            override val route = "home"
            const val ROUTE = "home"

            /**
             * Route string'ler generic tutuyoruz. "search_book_screen" yerine
             * sadece "home" yazıyoruz çünkü:
             *
             * 1. KISALIK: Route'lar URL gibi, kısa olması iyi
             * 2. ESNEKLİK: İleride ekranın implementasyonu değişse route aynı kalıyor
             * 3. ANLAMLILIK: "home" kullanıcı için anlamlı, "search_book_screen" teknik detay
             *
             * Deep link'lerde de "booknest://home" daha temiz görünüyor.
             */
        }

        /**
         * PROFILE SCREEN - Kullanıcı Bilgileri ve Ayarlar
         *
         * Kullanıcının profilini görüntülediği, ayarlarını değiştirdiği ekran.
         * Genelde şunları içerir:
         * • Kullanıcı bilgileri (ad, email, avatar)
         * • Uygulama ayarları (tema, dil, bildirimler)
         * • Hesap yönetimi (şifre değiştirme, çıkış yapma)
         * • İstatistikler (okunan kitap sayısı, geçirilen süre)
         */
        data object Profile : Main() {
            override val route = "profile"
            const val ROUTE = "profile"
        }

        /**
         * MY LIBRARY SCREEN - Kullanıcının Kütüphanesi
         *
         * Kullanıcının kaydettiği, okuduğu ve okumak istediği kitapların
         * listelendiği ekran. Bu ekran "hub" görevi görüyor, birçok farklı
         * ekrana navigation başlatılıyor buradan:
         *
         * • Kitaba tıklayınca → BookDetail
         * • Edit ikonuna tıklayınca → BookEdit
         * • Note ikonuna tıklayınca → Note
         * • Quiz butonuna tıklayınca → Quiz
         *
         * Bu yüzden bu ekran NavController yerine callback'ler alıyor.
         * Navigation kararları navigation graph'ta veriliyor.
         */
        data object MyLibrary : Main() {
            override val route = "my_library"
            const val ROUTE = "my_library"
        }

        data object LocationSelection : Main() {
            override val route = "location_selection"
            const val ROUTE = "location_selection"
        }
    }

    /**
     * ════════════════════════════════════════════════════════════════════════════
     * DETAIL - PARAMETRE ALAN DERİN SEVİYE EKRANLAR
     * ════════════════════════════════════════════════════════════════════════════
     *
     * Bu ekranlar bir üst seviye ekranlardan açılıyor ve mutlaka parametre alıyor.
     * Parametresiz detail ekranı olmaz çünkü neyin detayını göstereceğini bilmek
     * gerekiyor.
     *
     * ┌─ DATA CLASS KULLANIMI ───────────────────────────────────────────────────┐
     * │                                                                           │
     * │ Detail ekranlar için DATA CLASS kullanıyoruz çünkü:                      │
     * │                                                                           │
     * │ • Her detay ekranı farklı bir parametre seti ile instantiate edilecek   │
     * │ • Equals/hashCode otomatik gelecek, comparison kolaylaşacak             │
     * │ • Copy fonksiyonu otomatik, parametre değiştirme kolay                  │
     * │ • ComponentN fonksiyonları gelecek, destructuring kullanılabilir        │
     * │                                                                           │
     * │ Örnek:                                                                    │
     * │ val detail1 = Screen.Detail.BookDetail(bookID = "123")                   │
     * │ val detail2 = Screen.Detail.BookDetail(bookID = "456")                   │
     * │ // İki farklı instance, farklı parametreler                             │
     * │                                                                           │
     * └───────────────────────────────────────────────────────────────────────────┘
     *
     * ROUTE ŞABLONLARI (TEMPLATE ROUTES):
     *
     * Route string'lerinde süslü parantez {} placeholder olarak kullanılıyor.
     * Örnek: "book_detail/{bookID}"
     *
     * Navigation kütüphanesi bunu gördüğünde şunu anlıyor:
     * "Bu route'un bir değişken parametresi var, ismini parse et ve argument
     * olarak ekrana geçir"
     *
     * Actual navigation yaparken placeholder yerine gerçek değer koyuyoruz:
     * "book_detail/123" → bookID = "123" olarak parse ediliyor
     */
    sealed class Detail : Screen() {

        /**
         * ────────────────────────────────────────────────────────────────────────
         * BOOK DETAIL SCREEN - Kitap Detay Sayfası
         * ────────────────────────────────────────────────────────────────────────
         *
         * Belirli bir kitabın tüm bilgilerinin gösterildiği ekran. Kitap hakkında
         * CRUD işlemleri (okuma, güncelleme, silme) için entry point.
         *
         * Burada şunları görürüz:
         * • Kitap kapağı, başlık, yazar, açıklama
         * • Okuma durumu, progress bar
         * • Edit, delete, share butonları
         * • Notes, quiz, chat gibi alt ekranlara linkler
         */
        data class BookDetail(
            /**
             * Constructor'daki bookID parametresi default değer alıyor.
             *
             * ┌─ NEDEN DEFAULT DEĞER? ─────────────────────────────────────────┐
             * │                                                                 │
             * │ Bu class aslında iki farklı amaçla kullanılıyor:               │
             * │                                                                 │
             * │ 1. TYPE DEFINITION: Route şablonunu tanımlamak için            │
             * │    Screen.Detail.BookDetail("").route → "book_detail/{bookID}" │
             * │    Burada gerçek ID önemsiz, sadece tip bilgisi lazım         │
             * │                                                                 │
             * │ 2. INSTANCE CREATION: Companion object fonksiyonları ile       │
             * │    Gerçek navigation yaparken bookID geçilecek                 │
             * │                                                                 │
             * │ Default değer sayesinde type definition'da parametre vermemize │
             * │ gerek kalmıyor. Companion object fonksiyonları zaten her zaman │
             * │ parametre istiyor, kullanıcı hata yapamıyor.                  │
             * │                                                                 │
             * └─────────────────────────────────────────────────────────────────┘
             */
            val bookID: String = ""
        ) : Detail() {
            /**
             * Route şablonu - Placeholder içeriyor
             *
             * Bu String Navigation'a şunu söylüyor:
             * "book_detail/" sabit kısmı, sonrası değişken, değişkenin adı bookID"
             *
             * Navigation kütüphanesi bu route'u parse ederken regex kullanıyor:
             * Pattern: "book_detail/([^/]+)"
             * Group 1: bookID değeri
             *
             * Örnek parse işlemi:
             * Input: "book_detail/123"
             * Output: Map("bookID" -> "123")
             */
            override val route = "book_detail/{bookID}"

            companion object {
                /**
                 * ════════════════════════════════════════════════════════════════
                 * COMPANION OBJECT - Static Fonksiyonlar ve Sabitler
                 * ════════════════════════════════════════════════════════════════
                 *
                 * Companion object Java'daki static member'lara benziyor ama daha
                 * güçlü. Kotlin'de class seviyesinde (instance olmadan erişilebilen)
                 * fonksiyonlar ve değişkenler companion object içinde tanımlanıyor.
                 *
                 * Faydaları:
                 * • Instance olmadan çağrılabiliyor: BookDetail.createRoute()
                 * • Interface implement edebiliyor (static'ten farkı)
                 * • Extension fonksiyon yazılabiliyor üzerine
                 * • Inheritance destekliyor
                 *
                 * Burada üç tip member var:
                 * 1. ROUTE sabiti: Composable tanımlarken kullanılıyor
                 * 2. ARG sabiti: Argument key'i, typo önleniyor
                 * 3. Helper fonksiyonlar: Route oluşturma ve parse etme
                 */

                /**
                 * Route sabiti - Composable tanımı için
                 *
                 * NavHost'ta composable fonksiyonuna bu sabiti geçiyoruz:
                 * composable(route = Screen.Detail.BookDetail.ROUTE)
                 *
                 * Neden hem route property hem de ROUTE const var?
                 * • route property: Instance üzerinden erişim için
                 * • ROUTE const: Static erişim için, daha performanslı
                 *
                 * Const olması sayesinde compile-time'da inline ediliyor,
                 * runtime'da object lookup yapmıyor.
                 */
                const val ROUTE = "book_detail/{bookID}"

                /**
                 * Argument key sabiti
                 *
                 * Bu sabit üç yerde kullanılıyor:
                 * 1. Route şablonunda: "book_detail/{bookID}"
                 * 2. navArgument tanımında: navArgument(ARG_BOOK_ID)
                 * 3. Argument çıkarmada: arguments?.getString(ARG_BOOK_ID)
                 *
                 * Sabit kullanmanın faydaları:
                 * • Typo impossible: "bookID" yerine "bookId" yazsan compile hata
                 * • Refactoring safe: Key'i değiştirince her yer güncelleniyor
                 * • Autocomplete: IDE önerirken hatırlamana gerek kalmıyor
                 *
                 * Naming convention: ARG_ prefix argument olduğunu gösteriyor
                 */
                const val ARG_BOOK_ID = "bookID"

                /**
                 * Route oluşturma fonksiyonu
                 *
                 * Bu fonksiyon gerçek navigation yaparken kullanılıyor:
                 * navController.navigate(BookDetail.createRoute("123"))
                 *
                 * ┌─ FONKSİYON AKIŞI ──────────────────────────────────────────┐
                 * │                                                            │
                 * │ Input: bookID = "123"                                      │
                 * │   ↓                                                        │
                 * │ Validation: require(bookID.isNotEmpty())                  │
                 * │   ↓                                                        │
                 * │ String building: "book_detail/$bookID"                    │
                 * │   ↓                                                        │
                 * │ Output: "book_detail/123"                                 │
                 * │                                                            │
                 * └────────────────────────────────────────────────────────────┘
                 *
                 * @param bookID Kitabın unique identifier'ı, boş olamaz
                 * @return Navigate edilebilir route string
                 * @throws IllegalArgumentException eğer bookID boşsa
                 */
                fun createRoute(bookID: String): String {
                    /**
                     * REQUIRE KULLANIMI:
                     *
                     * Require Kotlin'in precondition check fonksiyonu.
                     * IllegalArgumentException fırlatıyor false durumunda.
                     *
                     * Alternatifler ve ne zaman hangisi:
                     * • require: Parametre validasyonu için (public API'larda)
                     * • check: State validasyonu için (internal logic'te)
                     * • assert: Development time check'ler için (release'te disabled)
                     *
                     * Burada require kullanıyoruz çünkü:
                     * • Bu public bir fonksiyon, dışardan çağrılıyor
                     * • BookID zorunlu bir parametre, boş olamaz
                     * • Erken hata almak istiyoruz, navigation'a geçmeden
                     *
                     * Error message açıklayıcı yazıyoruz, debug kolaylaşıyor.
                     * Production'da bile bu mesajı görebilmek önemli.
                     */
                    require(bookID.isNotEmpty()) {
                        "Book ID cannot be empty for BookDetail navigation"
                    }

                    /**
                     * String interpolation ile route oluşturuyoruz.
                     *
                     * "$bookID" syntax Kotlin'in string template özelliği.
                     * Alternatif: "book_detail/" + bookID (daha verbose)
                     *
                     * String template avantajları:
                     * • Daha okunabilir
                     * • Performans açısından aynı (compile-time'da concat'e çevriliyor)
                     * • Complex expression destekliyor: "${book.id}"
                     */
                    return "book_detail/$bookID"
                }

                /**
                 * Argument çıkarma yardımcı fonksiyonu
                 *
                 * Bu fonksiyon composable lambda içinde kullanılıyor:
                 *
                 * composable(...) { backStackEntry ->
                 *     val bookId = BookDetail.getBookID(backStackEntry)
                 *     BookDetailScreen(bookID = bookId)
                 * }
                 *
                 * ┌─ NEDEN HELPER FONKSIYON? ──────────────────────────────────┐
                 * │                                                             │
                 * │ Bu fonksiyon olmasaydı her yerde şunu yazmak zorunda:      │
                 * │                                                             │
                 * │ val bookId = backStackEntry.arguments                       │
                 * │     ?.getString("bookID")                                   │
                 * │     ?: throw IllegalStateException("...")                   │
                 * │                                                             │
                 * │ Problem:                                                    │
                 * │ • Code duplication: Her composable'da aynı kod             │
                 * │ • Typo riski: "bookID" vs "bookId" karışabilir            │
                 * │ • Maintainability: Key değişince her yeri güncellemek      │
                 * │                                                             │
                 * │ Helper fonksiyon çözüyor:                                   │
                 * │ • Tek satır: getBookID(entry)                              │
                 * │ • Type safe: ARG_BOOK_ID sabiti kullanılıyor              │
                 * │ • Consistent error handling: Hep aynı exception            │
                 * │                                                             │
                 * └─────────────────────────────────────────────────────────────┘
                 *
                 * @param entry Navigation back stack'inden gelen entry
                 * @return Parse edilmiş book ID
                 * @throws IllegalStateException eğer ID bulunamazsa
                 */
                fun getBookID(entry: NavBackStackEntry): String {
                    /**
                     * NavBackStackEntry nedir?
                     *
                     * Navigation stack'indeki bir "durak noktası". İçinde şunlar var:
                     * • destination: Hangi ekrandayız
                     * • arguments: Route'tan parse edilen parametreler
                     * • savedStateHandle: State'i kaydetme/geri yükleme için
                     * • lifecycle: Bu entry'nin yaşam döngüsü
                     *
                     * Arguments bir Bundle, Android'in key-value store'u.
                     * Bundle primitive tipleri ve Parcelable'ları tutabiliyor.
                     */
                    return entry.arguments?.getString(ARG_BOOK_ID)
                    /**
                     * Elvis operator (?:) - Null handling
                     *
                     * Sol taraf null ise sağ taraf çalışıyor.
                     * Burada sağ taraf exception fırlatıyor.
                     *
                     * Neden !! yerine elvis + throw kullanıyoruz?
                     *
                     * !! kullanırsak:
                     * • NullPointerException fırlatır (generic, anlamsız)
                     * • Error message yok, neyin null olduğu belli değil
                     * • Debugging zorlaşır
                     *
                     * Elvis + throw kullanınca:
                     * • IllegalStateException fırlatıyor (daha spesifik)
                     * • Açıklayıcı error message var
                     * • Ne olduğu, neden olduğu açık
                     *
                     * Bu parametre zorunlu, null gelmemeli. Eğer gelirse
                     * bu ciddi bir bug, crash olması daha iyi ki
                     * development'ta hemen fark edilsin.
                     */
                        ?: throw IllegalStateException(
                            "Book ID is required for BookDetail screen but was not found in arguments"
                        )
                }
            }
        }

        /**
         * ────────────────────────────────────────────────────────────────────────
         * BOOK EDIT SCREEN - Kitap Düzenleme
         * ────────────────────────────────────────────────────────────────────────
         *
         * Mevcut bir kitabın bilgilerini düzenleme ekranı. BookDetail'den farklı
         * çünkü burada form elemanları, save/cancel butonları var.
         *
         * BookDetail ile aynı parametreyi alıyor ama farklı UI. Bu pattern'e
         * "Edit mode" deniyor ve birçok uygulamada kullanılıyor:
         * • View mode: Read-only, detay gösterme
         * • Edit mode: Editable fields, kaydetme işlemi
         */
        data class BookEdit(val bookID: String = "") : Detail() {
            override val route = "book_edit/{bookID}"

            companion object {
                const val ROUTE = "book_edit/{bookID}"
                const val ARG_BOOK_ID = "bookID"

                fun createRoute(bookID: String): String {
                    require(bookID.isNotEmpty()) {
                        "Book ID cannot be empty for BookEdit navigation"
                    }
                    return "book_edit/$bookID"
                }

                fun getBookID(entry: NavBackStackEntry): String {
                    return entry.arguments?.getString(ARG_BOOK_ID)
                        ?: throw IllegalStateException(
                            "Book ID is required for BookEdit screen"
                        )
                }
            }
        }

        /**
         * ────────────────────────────────────────────────────────────────────────
         * QUIZ SCREEN - Kitap Hakkında Quiz
         * ────────────────────────────────────────────────────────────────────────
         *
         * Belirli bir kitap hakkında interaktif quiz soruları. Muhtemelen AI
         * tarafından oluşturulan sorular ve kullanıcının kitabı ne kadar
         * anladığını test ediyor.
         *
         * Bu ekran bookID yerine bookName alıyor.
         *
         * ┌─ BOOKID VS BOOKNAME TERCİHİ ──────────────────────────────────────────┐
         * │                                                                        │
         * │ Ne zaman ID, ne zaman Name kullanmalı?                                 │
         * │                                                                        │
         * │ ID KULLAN:                                                             │
         * │ • Database işlemi gerekiyorsa (CRUD)                                  │
         * │ • İlişkisel veri çekeceksen (foreign key)                             │
         * │ • Unique identifier olarak gerekiyorsa                                │
         * │                                                                        │
         * │ NAME KULLAN:                                                           │
         * │ • Sadece UI'da gösterilecekse                                         │
         * │ • Database sorgusu gereksizse                                         │
         * │ • Değişken bir veri değilse (immutable)                              │
         * │                                                                        │
         * │ Quiz ekranında bookName kullanmamızın nedenleri:                      │
         * │ 1. Quiz sorularında kitap ismini göstermemiz lazım                   │
         * │ 2. ID ile database'den ismi çekmek gereksiz overhead                 │
         * │ 3. Quiz verisi muhtemelen cache'li veya static, ID'ye ihtiyaç yok   │
         * │                                                                        │
         * │ Dezavantajı: Kitap ismi değişirse sorun olabilir                     │
         * │ Alternatif: Hem ID hem Name geçebilirdik, daha güvenli olurdu       │
         * │                                                                        │
         * └────────────────────────────────────────────────────────────────────────┘
         */
        data class Quiz(val bookName: String = "") : Detail() {
            override val route = "quiz/{bookName}"

            companion object {
                const val ROUTE = "quiz/{bookName}"
                const val ARG_BOOK_NAME = "bookName"

                /**
                 * Book name için özel handling gerekiyor çünkü isimler
                 * özel karakterler içerebilir.
                 *
                 * ┌─ URL ENCODING NEDİR VE NEDEN GEREKLİ? ────────────────────┐
                 * │                                                            │
                 * │ URL'lerde bazı karakterler özel anlam taşıyor:            │
                 * │ • / → Path separator                                       │
                 * │ • ? → Query string başlangıcı                             │
                 * │ • & → Query parameter separator                           │
                 * │ • # → Fragment identifier                                 │
                 * │ • Boşluk → Geçersiz karakter                              │
                 * │                                                            │
                 * │ Kitap isimleri şunları içerebilir:                        │
                 * │ • "Harry Potter & The Philosopher's Stone"                │
                 * │ • "1984: A Novel"                                         │
                 * │ • "Brave New World/Yeni Dünya"                           │
                 * │ • Türkçe karakterler: "İçimizdeki Şeytan"                │
                 * │                                                            │
                 * │ Bu karakterler encode edilmezse:                          │
                 * │ • Route parse hata verir                                  │
                 * │ • Navigation çalışmaz                                     │
                 * │ • Crash veya yanlış ekran açılır                         │
                 * │                                                            │
                 * │ URL Encoding çözümü:                                       │
                 * │ Özel karakterler % ve hex code'a çevriliyor:             │
                 * │ • Boşluk → %20                                            │
                 * │ • & → %26                                                 │
                 * │ • ş → %C5%9F                                             │
                 * │                                                            │
                 * └────────────────────────────────────────────────────────────┘
                 */
                fun createRoute(bookName: String): String {
                    require(bookName.isNotEmpty()) {
                        "Book name cannot be empty for Quiz navigation"
                    }

                    /**
                     * URLEncoder.encode() kullanımı:
                     *
                     * Java'nın standard utility'si, URL-safe string'e çeviriyor.
                     * İkinci parametre charset, UTF-8 kullanıyoruz çünkü:
                     * • Unicode karakterleri destekliyor (Türkçe, emoji vb.)
                     * • Web standard, her yerde destekleniyor
                     * • Backward compatible
                     *
                     * Alternatif: Uri.encode() Android API'si ama benzer iş yapıyor
                     */
                    val encodedName = URLEncoder.encode(bookName, "UTF-8")
                    return "quiz/$encodedName"
                }

                /**
                 * Decode işlemi - Encode'un tersi
                 *
                 * Navigation kütüphanesi route'u parse ederken otomatik decode
                 * etmiyor, biz manuel yapıyoruz.
                 */
                fun getBookName(entry: NavBackStackEntry): String {
                    val encodedName = entry.arguments?.getString(ARG_BOOK_NAME)
                        ?: throw IllegalStateException(
                            "Book name is required for Quiz screen"
                        )

                    /**
                     * URLDecoder.decode() ile orijinal string'e dönüyoruz.
                     *
                     * Örnek dönüşüm:
                     * Input: "Harry%20Potter%20%26%20The%20Philosopher%27s%20Stone"
                     * Output: "Harry Potter & The Philosopher's Stone"
                     *
                     * Charset yine UTF-8, encode ile aynı olmalı.
                     * Farklı charset kullanırsan karakterler bozulur.
                     */
                    return URLDecoder.decode(encodedName, "UTF-8")
                }
            }
        }

        /**
         * ────────────────────────────────────────────────────────────────────────
         * GEMINI CHAT SCREEN - AI Sohbet
         * ────────────────────────────────────────────────────────────────────────
         *
         * Google Gemini AI ile kitap hakkında sohbet ekranı. Kullanıcı kitap
         * hakkında sorular sorabiliyor, AI cevaplıyor.
         *
         * Bu ekran da bookName kullanıyor Quiz gibi, aynı mantıkla.
         * AI'a prompt gönderirken kitap ismini kullanacağız muhtemelen.
         */
        data class GeminiChat(val bookName: String = "") : Detail() {
            override val route = "gemini_chat/{bookName}"

            companion object {
                const val ROUTE = "gemini_chat/{bookName}"
                const val ARG_BOOK_NAME = "bookName"

                fun createRoute(bookName: String): String {
                    require(bookName.isNotEmpty()) {
                        "Book name cannot be empty for GeminiChat navigation"
                    }
                    val encodedName = URLEncoder.encode(bookName, "UTF-8")
                    return "gemini_chat/$encodedName"
                }

                fun getBookName(entry: NavBackStackEntry): String {
                    val encodedName = entry.arguments?.getString(ARG_BOOK_NAME)
                        ?: throw IllegalStateException(
                            "Book name is required for GeminiChat screen"
                        )
                    return URLDecoder.decode(encodedName, "UTF-8")
                }
            }
        }

        /**
         * ────────────────────────────────────────────────────────────────────────
         * NOTE SCREEN - Kitap Notları
         * ────────────────────────────────────────────────────────────────────────
         *
         * Kullanıcının bir kitap için aldığı notları görüntülediği ve
         * düzenlediği ekran. CRUD işlemleri yapılıyor: oluşturma, okuma,
         * güncelleme, silme.
         *
         * Bu ekran bookID kullanıyor çünkü:
         * • Database'de notlar bookID ile ilişkili (foreign key)
         * • Note'ları çekmek için bookID ile sorgu yapıyoruz
         * • Yeni note kaydederken bookID gerekiyor
         */
        data class Note(val bookID: String = "") : Detail() {
            override val route = "note/{bookID}"

            companion object {
                const val ROUTE = "note/{bookID}"
                const val ARG_BOOK_ID = "bookID"

                fun createRoute(bookID: String): String {
                    require(bookID.isNotEmpty()) {
                        "Book ID cannot be empty for Note navigation"
                    }
                    return "note/$bookID"
                }

                fun getBookID(entry: NavBackStackEntry): String {
                    return entry.arguments?.getString(ARG_BOOK_ID)
                        ?: throw IllegalStateException(
                            "Book ID is required for Note screen"
                        )
                }
            }
        }
    }



}