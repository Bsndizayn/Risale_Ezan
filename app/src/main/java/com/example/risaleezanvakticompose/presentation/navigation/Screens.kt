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
        data object Home : Main() {
            override val route = "home"
            const val ROUTE = "home"
        }

        data object Profile : Main() {
            override val route = "profile"
            const val ROUTE = "profile"
        }

        data object MyLibrary : Main() {
            override val route = "my_library"
            const val ROUTE = "my_library"
        }

        data object Qibla : Main() {
            override val route = "qibla"
            const val ROUTE = "qibla"
        }

        data object LocationSelection : Main() {
            override val route = "location_selection"
            const val ROUTE = "location_selection"
        }

        data object Settings : Main() {
            override val route = "settings"
            const val ROUTE = "settings"
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

        data class TesbihatDetail(val categoryName: String = "") : Detail() {
            override val route = "tesbihat_detail/{categoryName}"

            companion object {
                const val ROUTE = "tesbihat_detail/{categoryName}"
                const val ARG_CATEGORY_NAME = "categoryName"

                fun createRoute(categoryName: String): String {
                    return "tesbihat_detail/$categoryName"
                }

                fun getCategoryName(entry: androidx.navigation.NavBackStackEntry): String {
                    return entry.arguments?.getString(ARG_CATEGORY_NAME)
                        ?: throw IllegalStateException("Category name required")
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