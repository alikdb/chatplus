A chat mod for **Hypixel and SkyBlock**: one-click chat channels, player heads in every chat, a longer chat history and a chat peek key.

Fabric · Minecraft 26.2 and 26.3 · client-side · [Türkçe rehber](https://github.com/alikdb/chatplus/blob/main/docs/settings.tr.md)

![Channel buttons and chat heads](https://raw.githubusercontent.com/alikdb/chatplus/main/docs/images/chat.png)

## Features

### Channel buttons
Open the chat and a row of buttons sits right above the input box: **Normal**, **Party**, **Guild** and **Co-op**.
Click one to switch the Hypixel chat channel (`/chat a`, `/chat p`, `/chat g`, `/chat coop`); the channel you are in is highlighted.

- The highlight follows the server. If you switch with a command, open a private conversation, or get moved back to ALL ("You are not in a party..."), the buttons follow. This also works with a non-English Hypixel `/language`.
- Every button can be renamed (`G`, `&aGuild`, ...), reordered, hidden or removed. You can add your own.
- A button can run any command, not just channel switches, for example `/p warp`, `/is` or `/warp dungeon_hub`.

### Player heads in every chat
Shows the sender's head next to chat messages, like the [Chat Heads](https://modrinth.com/mod/chat-heads) mod, but it also works where Chat Heads can't:

| Chat | Example |
|---|---|
| Guild / Officer | `Guild > [MVP+] Name [Officer]: hi`, `Guild > Name joined.` |
| Party | `Party > [MVP++] Name: warping`, `[VIP] Name joined the party.` |
| SkyBlock co-op | `Co-op > Name: minions are full` |
| SkyBlock all chat | `[312] ♫ [MVP+] Name: hi`, ironman `♲` |
| Private messages | `From [VIP+] Name: hi`, `To Name: hi` |
| Party Finder | `Party Finder > Name joined the dungeon group!` |
| Lobbies and games | `[123✫] [MVP+] Name: gg`, `>>> [MVP++] Name joined the lobby! <<<` |

Guild members, party members and people messaging you are usually not in your lobby, so they are not in the tab list. Chat Heads relies on the tab list, which is why it shows no head for them. ChatPlus reads the sender from the message and looks their skin up by name. Lookups go through Minecraft's own profile cache and run in the background, so chat never waits for them.

NPC and boss dialogue (`[NPC] Jacob: ...`) gets no head.

### Longer chat history
Vanilla forgets everything older than 100 messages. ChatPlus keeps **1000** by default, and you can set anything from 100 to 10000. The up-arrow history of your own messages is longer too. Optionally, the chat survives disconnecting.

### Chat peek
Hold **Left Alt** to see the whole chat, the way it looks when chat is open, without opening the input box. You keep moving and fighting. While peeking, the mouse wheel scrolls the chat instead of your hotbar. You can switch to toggle mode or change the key.

### Settings screen
Open it from the ⚙ button next to the channel buttons, with `/chatplus`, from Mod Menu, or with a key you bind. All settings are explained in the **[settings guide](https://github.com/alikdb/chatplus/blob/main/docs/settings.md)**.

![Channel button settings](https://raw.githubusercontent.com/alikdb/chatplus/main/docs/images/settings-channels.png)

![Chat head settings](https://raw.githubusercontent.com/alikdb/chatplus/main/docs/images/settings-heads.png)

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft **26.2** or **26.3**.
2. Put these in your `mods` folder:

| Mod | |
|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | required |
| [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) | required |
| [Mod Menu](https://modrinth.com/mod/modmenu) | optional, adds a config button to the mod list |
| ChatPlus - Hypixel | this mod, the file for your Minecraft version |

Don't use it together with Chat Heads, since both would add heads to the chat.

## Quick reference

| | |
|---|---|
| Peek chat | hold **Left Alt** (Controls → ChatPlus - Hypixel) |
| Open settings | ⚙ button in chat, `/chatplus`, Mod Menu, or bind "Open Settings" |
| Config file | `.minecraft/config/chatplus-hypixel.json` |

## Languages

English and Türkçe are included. Want the mod in your language? Translations are added with a pull request, no programming needed; see the **[translation guide](https://github.com/alikdb/chatplus/blob/main/docs/translations.md)**.

## Source code and bugs

The source code is on [GitHub](https://github.com/alikdb/chatplus). Found a bug or a chat line without a head? Open an [issue](https://github.com/alikdb/chatplus/issues) and include the exact chat message.

## Credits and license

The chat head rendering is based on [Chat Heads](https://github.com/dzwdz/chat_heads) by dzwdz and Fourmisain. Like Chat Heads, this mod is licensed under the [Mozilla Public License 2.0](https://github.com/alikdb/chatplus/blob/main/LICENSE).
