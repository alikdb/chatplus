# Yayınlama notları

Modrinth ve GitHub'daki her alana ne yazılacağı burada. Kod bloklarının içindekileri olduğu gibi kopyala.
Metinler İngilizce, çünkü Modrinth'teki oyuncuların çoğu İngilizce okuyor.

---

## 1. Jar dosyaları

Mod her Minecraft sürümü için ayrı jar olarak çıkar, çünkü Minecraft'ın iç yapısı sürümden sürüme değişiyor
(26.3'te örneğin tuş kodları değişti). Şu an desteklenenler `versions/` klasöründeki dosyalar: **26.2** ve **26.3**.

Jar'ları elle derlemene gerek yok: GitHub'a `v1.0.0` gibi bir sürüm etiketi gönderildiğinde GitHub Actions hepsini
derleyip repo sayfasının sağındaki **Releases** bölümünde yayınlar (bkz. [5. Sonraki sürümler](#5-sonraki-sürümler)).
Modrinth'e yükleyeceğin dosyaları oradan indir:

- `chatplus-hypixel-1.0.0+26.2.jar`
- `chatplus-hypixel-1.0.0+26.3.jar`

Elle derlemek istersen: `./gradlew build -Pmc=26.2` ve `./gradlew build -Pmc=26.3`, dosyalar `build/libs/` altında.
`-sources.jar` ile biten dosyaları **yükleme**.

---

## 2. GitHub repo "About" kısmı

Repo sayfasında sağdaki **About** yanındaki dişli simgesine tıkla.

**Description**
```
Chat mod for Hypixel and SkyBlock: channel buttons, player heads in every chat, longer chat history and a chat peek key. Fabric 26.2 and 26.3.
```

**Website** (Modrinth sayfası açılınca)
```
https://modrinth.com/mod/chatplus-hypixel
```

**Topics** (tek tek ekle)
```
minecraft
minecraft-mod
fabric
fabricmc
hypixel
hypixel-skyblock
skyblock
chat
```

---

## 3. Modrinth projesini oluştur

modrinth.com → sağ üstte **+** → **Create a project**.

**Name**
```
ChatPlus - Hypixel
```

**URL** (slug)
```
chatplus-hypixel
```

**Visibility**: Public

**Summary** (kısa açıklama, en fazla 256 karakter)
```
Chat mod for Hypixel and SkyBlock: one-click Party, Guild and Co-op channel buttons, player heads in guild, party and DM chat, a 1000-message chat history and a hold-to-peek chat key.
```

### Description
Projeyi oluşturduktan sonra **Description** sekmesi. [`docs/modrinth.md`](modrinth.md) dosyasının **tamamını**
kopyalayıp yapıştır. İçindeki görseller GitHub'daki dosyalara bağlı, repo public olduğu sürece görünür.

### Icon
**Settings → General → Icon**: [`docs/images/icon.png`](images/icon.png) (blksx kafası, 512×512).

### Tags (Settings → Tags)
- **Primary category**: `Social`
- **Additional categories**: `Utility`

### Environments (Settings → Environment)
```
Client-side only
```
Sunucuya kurulmaz, sadece oyuncunun bilgisayarında çalışır.

### License (Settings → License)
- **License**: `Mozilla Public License 2.0` (MPL-2.0)
- **License URL** sorulursa:
```
https://github.com/alikdb/chatplus/blob/main/LICENSE
```

### Links (Settings → Links)
**Issue tracker**
```
https://github.com/alikdb/chatplus/issues
```
**Source code**
```
https://github.com/alikdb/chatplus
```
**Wiki page**
```
https://github.com/alikdb/chatplus/blob/main/docs/settings.md
```
Discord sunucun yoksa o alanı boş bırak.

### Gallery
Her görsel için başlık ve açıklama:

| Dosya | Featured | Title | Description |
|---|---|---|---|
| `docs/images/chat.png` | ✔ | `Channel buttons and chat heads` | `Switch between All, Party, Guild and Co-op chat with one click. Heads show up in every chat, even for players who are not in your lobby.` |
| `docs/images/settings-channels.png` | | `Channel button settings` | `Rename, reorder, hide or add buttons. Any command works, for example /p warp.` |
| `docs/images/settings-heads.png` | | `Chat head settings` | `Head position, name lookup, shadow, gap and 3D hat.` |
| `docs/images/settings-chat.png` | | `Chat history and chat peek` | `Keep up to 10000 messages and peek at the chat with Left Alt.` |

---

## 4. İlk sürümü yükle

Her Minecraft sürümü Modrinth'te **ayrı bir version** olarak yüklenir. Oyuncular kendi sürümlerine uyanı otomatik görür.
Proje sayfasında **Versions → Create a version**, iki kere:

| Alan | 26.2 için | 26.3 için |
|---|---|---|
| File | `chatplus-hypixel-1.0.0+26.2.jar` | `chatplus-hypixel-1.0.0+26.3.jar` |
| Version title | `1.0.0 for 26.2` | `1.0.0 for 26.3` |
| Version number | `1.0.0+26.2` | `1.0.0+26.3` |
| Release channel | `Release` | `Release` |
| Loaders | `Fabric` | `Fabric` |
| Game versions | `26.2` | `26.3` |
| Environment (sorulursa) | Client-side only | Client-side only |

Bağımlılıklar ve changelog ikisinde de aynı:

**Dependencies** (proje adıyla ara, ilişki türünü seç):

| Proje | Tür |
|---|---|
| Fabric API | Required |
| Fabric Language Kotlin | Required |
| Mod Menu | Optional |
| Chat Heads | Incompatible |

**Changelog**
```
First release, for Minecraft 26.2 and 26.3 (Fabric).

- Channel buttons above the chat input: Normal, Party, Guild and Co-op, plus your own command buttons. The active channel is highlighted and follows Hypixel's channel messages.
- Player heads in guild, party, co-op, private message, Party Finder and SkyBlock chat, also for players who are not in your lobby.
- Chat history of 1000 messages (100–10000), longer sent message history, optional "keep chat on disconnect".
- Chat peek: hold Left Alt to see the whole chat without opening it; the mouse wheel scrolls it.
- Settings screen with /chatplus, a ⚙ button in chat and Mod Menu support.
- English and Turkish translations.
```

Sonra **Submit for review**. Modrinth ekibi yeni projeleri elle onaylar, bu genelde bir iki gün sürer.

---

## 5. Sonraki sürümler

Yayın tamamen otomatik: GitHub sürümü ve Modrinth yüklemesi tek bir etiketle olur.

1. `gradle.properties` içinde `version=` değerini artır (`1.0.2`, `1.1.0`...).
2. [`CHANGELOG.md`](../CHANGELOG.md) dosyasının en üstüne `## 1.0.2` başlığıyla sürüm notlarını yaz.
   Bu metin hem GitHub'da hem Modrinth'te changelog olarak görünür. `### Added`, `### Changed`, `### Fixed`
   alt başlıkları ve her madde tek satır olursa iki yerde de düzgün görünür.
3. Commit'le ve gönder, sonra sürüm etiketini gönder:
   ```sh
   git tag v1.0.2
   git push origin v1.0.2
   ```
4. GitHub Actions birkaç dakikada:
   - her Minecraft sürümü (`versions/`) için jar derler,
   - **Releases** altında `v1.0.2` sürümünü açar,
   - her jar'ı Modrinth'e ayrı version olarak yükler: `1.0.2+26.2`, `1.0.2+26.3`... Oyun sürümü, Fabric,
     bağımlılıklar (Fabric API, Fabric Language Kotlin zorunlu; Mod Menu isteğe bağlı; Chat Heads uyumsuz)
     ve changelog otomatik doldurulur.

   Etiket `gradle.properties`'teki sürümle uyuşmazsa hiçbir şey yayınlanmaz.

### Modrinth token'ı

Yükleme için Modrinth token'ı GitHub'da **Settings → Secrets and variables → Actions** altında
`MODRINTH_TOKEN` adıyla saklanıyor. Repoda hiçbir dosyada yazmıyor, loglarda da gizleniyor.

Token'ı yenilersen (Modrinth → Settings → Personal access tokens) yenisini şöyle kaydet:

```sh
gh secret set MODRINTH_TOKEN --repo alikdb/chatplus
```

Token'da en az **Create versions** ve **Read projects** yetkileri olmalı.

Bir sürümü elle yüklemen gerekirse (örneğin iş akışı yarıda kaldıysa), jar'ları bir klasöre koyup:

```sh
MODRINTH_TOKEN=... .github/scripts/publish-modrinth.sh 1.0.2 notlar.md jar-klasörü
```

## 6. Yeni Minecraft sürümü desteği

Yeni bir Minecraft sürümü çıkınca (örneğin 26.4):

1. `versions/26.3.properties` dosyasını `versions/26.4.properties` olarak kopyala. İçindeki sürümleri
   [fabricmc.net/develop](https://fabricmc.net/develop) (Fabric API) ve Modrinth'teki Mod Menu sayfasından güncelle.
2. `./gradlew build -Pmc=26.4` ile derle, sonra `./gradlew runClientGameTest -Pmc=26.4` ile oyun içi testi çalıştır.
3. İkisi de geçerse yeni sürüm hazır: bir sonraki etikette 26.4 jar'ı da otomatik çıkar.
   Derleme hatası olursa Minecraft o sürümde bir şeyi değiştirmiştir, kodun uyarlanması gerekir.

Eski bir sürümü bırakmak için `versions/` altındaki dosyasını silmek yeterli.

---

## 7. Paylaşım metinleri

Discord, Reddit veya Hypixel forumu için:

**English**
```
ChatPlus - Hypixel is out! A Fabric chat mod for Hypixel & SkyBlock:
• One-click Party / Guild / Co-op channel buttons
• Player heads in guild, party, co-op and DM chat (even for players not in your lobby)
• 1000-message chat history
• Hold Left Alt to peek at the chat without opening it
https://modrinth.com/mod/chatplus-hypixel
```

**Türkçe**
```
ChatPlus - Hypixel yayında! Hypixel ve SkyBlock için Fabric chat modu:
• Tek tıkla Party / Guild / Co-op kanal butonları
• Guild, parti, co-op ve özel mesajlarda oyuncu kafaları (lobide olmayanlar dahil)
• 1000 mesajlık chat geçmişi
• Sol Alt'a basılı tutup chat'i açmadan görme
https://modrinth.com/mod/chatplus-hypixel
```
