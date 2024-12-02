# Sahibinden.comClone
Sahibinden.comClone with Kotlin
- Sahibinden.com mobil uygulaması klonlanmıştır.
- Kotlin,Firebase authtication, Firebase Firestore , Firebase Storage ve Picasso kullanılmıştır.
- Uygulamanın içerisinde;
  - Kullanıcılar sisteme e-mail adresleri ile kayıt olabilir ve giriş yapablirler.
  - Kullanıcılar kendi profilleri üzerinde de CRUD işlemleri yapabilirler.
  - Kendilerine ait urunler üzerinde CRUD işlemleri yapaiblirler.
  - Urunleri katagorilere göre filtreleyerek listeleyebilir ve detaylarını görebilirler.
  - Profiller için premium özelliği eklenmiştir. Premium kişilerin ekledikleri ürünler anasayfada populer ürünler olarak gözükür.
- Uygulamada Image için uygun permission yazılarak her android seviyesindeki cihazlar için kullanılabilir hale getirilmiştir.
- Görseller proje içerisine kayıt edilmez. Firebase Storage üzerine kayıt edilir. Görsellerin link adresleri Firestore'da bulunan collectionlar içerisinde gönderi bilgileri ile tutulur.
- Picasso ile kayıt edilen görsellerin linkleri ile görseller indirilir ve gönderi içeriği ile gösterilir.
