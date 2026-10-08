# Translating ChatPlus - Hypixel

ChatPlus - Hypixel uses Minecraft's normal language files, so anyone can add a language with a pull request.
You don't need to know any programming, and you can do everything on the GitHub website.

## Supported languages

| Language | File | Status |
|---|---|---|
| English | `en_us.json` | complete (source language) |
| Türkçe | `tr_tr.json` | complete |

Missing your language? Add it, see below. If a translation above is outdated or wrong, you can fix it the same way.

## Adding a language

1. **Fork** this repository (button in the top right on GitHub).
2. Open [`src/main/resources/assets/chatplus-hypixel/lang/en_us.json`](../src/main/resources/assets/chatplus-hypixel/lang/en_us.json)
   and copy its whole content.
3. In the same folder, create a new file named after your **Minecraft language code**, in lower case:
   `de_de.json` for German, `es_es.json` for Spanish, `pt_br.json` for Brazilian Portuguese, and so on.
   The full list is on the [Minecraft Wiki](https://minecraft.wiki/w/Language) ("In-game" column).
4. Paste the English content and translate the text on the **right** side of each line:

   ```json
   "chatplus-hypixel.settings.channels.sync": "Mit Server synchronisieren",
   ```

5. Commit the file and open a **pull request** with a title like `Add German translation`.
   Automatic checks run on your pull request and report mistakes (see below).

## Rules

- **Translate only the values.** The key on the left (`chatplus-hypixel.settings...`) must stay exactly the same.
- **Keep placeholders.** `%s` is replaced by something, for example the key name in `Peek Key: %s`.
  Keep it in your text, wherever it fits best in your language.
- **Keep `\n`.** It is a line break inside a tooltip.
- **Keep symbols, commands and Hypixel terms** like `⚙`, `/chat a`, `/p warp`, `ALL`, `PARTY` and channel names.
  Hypixel itself uses these words.
- **Keep it short.** Button labels have limited space; look at the English and Turkish texts for the length.
  If a label is cut off in game, shorten it.
- **Partial translations are fine.** Leave out lines you can't translate; the game shows English for them.
- Use the same words as the official Minecraft translation of your language, for example for "Controls" or "Key Binds".

## Checks on your pull request

The build checks every language file. It fails if:

- the file is not valid JSON (often a missing `"` or `,`),
- a key does not exist in `en_us.json` (typo, or the key was changed),
- a `%s` placeholder is missing or added,
- the file name is not a lower case language code like `de_de.json`.

Click **Details** next to the failed check to see the exact line.

## Testing in game (optional)

Build the mod with `./gradlew build` (JDK 25 needed) and copy `build/libs/chatplus-hypixel-<version>.jar` into your
`mods` folder, or start a test client with `./gradlew runClient`. Choose your language in Options → Language and
open the settings with `/chatplus`.

## Hypixel channel names in your language (optional)

Hypixel translates its own messages into your `/language`, including the message the channel buttons listen to:

```
You are now in the PARTY channel
```

The highlighted button only follows this message if the channel name is known. English and Turkish names are
included. If you play Hypixel in your language, please add the exact messages for each channel
(`/chat a`, `/chat p`, `/chat g`, `/chat o`, `/chat coop`) to your pull request description, and they will be added.
