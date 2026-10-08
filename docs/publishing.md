# Yayınlama notları

Modrinth ve GitHub'daki her alana ne yazılacağı burada. Kod bloklarının içindekileri olduğu gibi kopyala.
Metinler İngilizce, çünkü Modrinth'teki oyuncuların çoğu İngilizce okuyor.

---

## 1. Jar dosyasını hazırla

```sh
./gradlew build
```

Yükleyeceğin dosya: `build/libs/chatplus-hypixel-1.0.0.jar`.
Aynı klasördeki `-sources.jar` dosyasını **yükleme**.

Yüklemeden önce bir kere gerçek Hypixel'de dene: guild/party chatinde kafalar çıkıyor mu, butonlara basınca kanal
değişiyor mu, Sol Alt çalışıyor mu.

---

## 2. GitHub repo "About" kısmı

Repo sayfasında sağdaki **About** yanındaki dişli simgesine tıkla.

**Description**
```
Chat mod for Hypixel and SkyBlock: channel buttons, player heads in every chat, longer chat history and a chat peek key. Fabric 26.2.
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

Proje sayfasında **Versions → Create a version**.

| Alan | Değer |
|---|---|
| File | `chatplus-hypixel-1.0.0.jar` |
| Version title | `1.0.0` |
| Version number | `1.0.0` |
| Release channel | `Release` |
| Loaders | `Fabric` |
| Game versions | `26.2` |
| Environment (sorulursa) | Client-side only |

**Dependencies** (proje adıyla ara, ilişki türünü seç):

| Proje | Tür |
|---|---|
| Fabric API | Required |
| Fabric Language Kotlin | Required |
| Mod Menu | Optional |
| Chat Heads | Incompatible |

**Changelog**
```
First release for Minecraft 26.2 (Fabric).

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

1. `gradle.properties` içinde `version=` değerini artır (`1.0.1`, `1.1.0`...).
2. [`CHANGELOG.md`](../CHANGELOG.md) dosyasının en üstüne yeni sürümün maddelerini ekle.
3. `./gradlew build`, sonra yeni jar'ı Modrinth'te **Create a version** ile yükle. Changelog alanına CHANGELOG'daki maddeleri yapıştır.

---

## 6. Paylaşım metinleri

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
