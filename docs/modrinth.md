A chat mod for **Hypixel and SkyBlock**: one-click chat channels, player heads in every chat, a longer chat history and a chat peek key.

![Channel buttons and chat heads](https://raw.githubusercontent.com/alikdb/chatplus/main/docs/images/chat.png)

## Channel buttons
Open the chat and a row of buttons sits right above the input box: **Normal**, **Party**, **Guild** and **Co-op**. Click one to switch the Hypixel chat channel. The channel you are in is highlighted.

- The highlight follows the server: switching with `/chat`, opening a private conversation or getting moved back to ALL updates the buttons. This also works with a non-English Hypixel `/language`.
- Rename (`G`, `&aGuild`), reorder, hide or add buttons.
- A button can run any command, for example `/p warp`, `/is` or `/warp dungeon_hub`.

## Player heads in every chat
Shows the sender's head next to chat messages, like Chat Heads, but also for players who are **not in your lobby**:

- Guild and officer chat, including join/leave lines
- Party chat and party messages
- SkyBlock co-op chat and SkyBlock all chat with levels, emblems and ironman ♲
- Private messages (`From` / `To`)
- Party Finder
- Lobby and game chat with ranks and Bed Wars stars

Chat Heads only knows the players in the tab list, so guild, party and DM senders get no head. ChatPLUS reads the sender from the message and looks their skin up by name, in the background and cached. NPC and boss dialogue gets no head.

Choose between *before name* and *before line* placement. Shadow, gap, 3D hat and heads in tab completion are all configurable.

## Longer chat history
Keep **1000** messages instead of vanilla's 100, adjustable from 100 to 10000. The ↑ history of your own messages is longer too. Optionally, the chat survives disconnecting.

## Chat peek
Hold **Left Alt** to see the whole chat without opening the input box, so you can keep moving and fighting. The mouse wheel scrolls the chat while peeking. Hold or toggle mode, and the key can be changed.

## Settings
Open them from the ⚙ button in chat, with `/chatplus`, or from Mod Menu. Every option is explained in the [settings guide](https://github.com/alikdb/chatplus/blob/main/docs/settings.md) ([Türkçe](https://github.com/alikdb/chatplus/blob/main/docs/settings.tr.md)).

![Settings](https://raw.githubusercontent.com/alikdb/chatplus/main/docs/images/settings-channels.png)

## Requirements
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional)

Don't use it together with Chat Heads, since this mod includes its features.

---
Chat head rendering is based on [Chat Heads](https://github.com/dzwdz/chat_heads) by dzwdz and Fourmisain. Licensed under MPL-2.0. [Source code](https://github.com/alikdb/chatplus)
