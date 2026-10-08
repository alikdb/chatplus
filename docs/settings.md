# Settings guide

[Türkçe](settings.tr.md)

This page explains every option of Hypixel ChatPLUS, what it is for, and its default value.

- [Opening the settings](#opening-the-settings)
- [Channel Buttons tab](#channel-buttons-tab)
- [Chat Heads tab](#chat-heads-tab)
- [Chat & Peek tab](#chat--peek-tab)
- [Key bindings and commands](#key-bindings-and-commands)
- [Config file](#config-file)
- [FAQ](#faq)

## Opening the settings

Any of these works:

- The **⚙** button at the end of the channel buttons, while the chat is open on Hypixel.
- The `/chatplus` command.
- **Mod Menu** → Hypixel ChatPLUS → config button, if Mod Menu is installed.
- The **Open Settings** key. It has no key by default; bind it in Controls → Key Binds → Hypixel ChatPLUS.

The screen works on a copy of your settings. **Done** saves the changes, **Cancel** throws them away.

## Channel Buttons tab

![Channel button settings](images/settings-channels.png)

| Option | Default | What it does |
|---|---|---|
| Channel Buttons | ON | Shows the button row above the chat input. |
| Only on Hypixel | ON | Hides the buttons on other servers and in singleplayer. |
| Sync With Server | ON | Highlights the channel Hypixel says you are in (see below). With this off, only your clicks change the highlight. |
| Settings Button (⚙) | ON | Shows a small button after the channel buttons that opens the settings. |
| Alignment | Left | Puts the row on the left or on the right side of the screen. |
| Button Width | Fit Label | **Fit Label** sizes each button to its text. Any other value (2–120) gives every button the same width, in GUI pixels. |

### Buttons

Each row is one button:

| Field | Meaning |
|---|---|
| **Label** | Text on the button. Use `&` color codes, for example `&aG` for a green "G". Labels can be as short as one letter. |
| **Command** | What the button runs. Starts with `/` for a command, otherwise it is sent as a chat message. |
| ✔ / ✖ | Shows or hides the button without deleting it. |
| ▲ / ▼ | Moves the button left or right in the row. |
| ✕ | Deletes the button. |

**+ Add Button** adds a new row. **Reset Buttons** restores the default buttons.

Default buttons:

| Label | Command | Visible |
|---|---|---|
| Normal | `/chat a` | yes |
| Party | `/chat p` | yes |
| Guild | `/chat g` | yes |
| Co-op | `/chat coop` | yes |
| Officer | `/chat o` | no |

#### Channel buttons and action buttons

A button whose command is a Hypixel channel switch becomes a **channel button**. It is highlighted while you
are in that channel. These commands are recognized (upper or lower case):

| Channel | Commands |
|---|---|
| ALL | `/chat a`, `/chat all` |
| PARTY | `/chat p`, `/chat party` |
| GUILD | `/chat g`, `/chat guild` |
| OFFICER | `/chat o`, `/chat officer` |
| SKYBLOCK CO-OP | `/chat coop`, `/chat co-op`, `/chat skyblock-coop` |

Any other command makes an **action button**. It runs the command and is never highlighted. Some ideas:

| Label | Command |
|---|---|
| Warp | `/p warp` |
| Island | `/is` |
| Hub | `/hub` |
| Dungeon | `/warp dungeon_hub` |
| PL | `/pl` |

#### Syncing with the server

With **Sync With Server** on, the highlight follows these Hypixel messages:

- "You are now in the PARTY channel", including when you switch by typing `/chat`.
- "You are not in a party and were moved to the ALL channel." and other "moved back to ALL" messages.
- "Opened a chat conversation with ..." (`/chat <player>`). None of the buttons is highlighted then.

Hypixel translates these messages into your `/language`. English and Turkish channel names are recognized.

The buttons are hidden while you type a command (the input starts with `/`), because Minecraft shows command
suggestions in the same place.

## Chat Heads tab

![Chat head settings](images/settings-heads.png)

| Option | Default | What it does |
|---|---|---|
| Chat Heads | ON | Turns player heads in chat on or off. |
| Position | Before Name | **Before Name** puts the head right before the player, in front of the rank: `Guild > 🙂[MVP+] Name: hi`. **Before Line** puts it at the very start of the line: `🙂Guild > [MVP+] Name: hi`. |
| Name Lookup | ON | Looks up the skin by name for players who are not in your lobby: guild, party, co-op, private messages, SkyBlock chat. With this off, those messages get no head. |
| System Messages | ON | Adds heads to messages the server sends as system messages. **Hypixel sends all chat this way, so keep this on.** |
| Detection | UUID + Text | How the sender is found. **UUID** means the server tells the client who sent the message (vanilla chat). **Text** means the sender is read from the message itself (Hypixel). *UUID Only* and *Text Only* limit it to one method. |
| Smart Heuristics | ON | On servers that do report senders, stop guessing from text. It has no effect on Hypixel. |
| Align Text | ON | Only for **Before Line**: moves lines without a head to the right so all text starts at the same place. |
| Head Shadow | ON | Draws a shadow under heads, like the text shadow. |
| Gap After Head | 1px | Space between the head and the text. 0 = touching, 1 = same as the text shadow. |
| 3D Hat | 0% | Draws the hat (second skin layer) slightly bigger than the face so heads look less flat. |
| Heads in Tab Completion | ON | Shows heads next to player names in command suggestions (Tab). |

### How it finds the player

1. **Hypixel formats.** On Hypixel, the start of each line is matched against known formats: guild, officer,
   party, co-op, private messages, Party Finder, SkyBlock and lobby chat with levels, ranks and emblems,
   join/leave lines. Translated prefixes like `Parti >` work too.
2. **Tab list.** If the player is in your lobby, the head uses the exact skin the server sent (nicked players included).
3. **Name lookup.** Otherwise the skin is looked up by name. The game shows a default Steve/Alex head for a
   moment until the skin has loaded. Results are cached, also across restarts (`usercache.json`).
4. **Fallback.** For other messages, like "Name was killed by Name2", the first name from the tab list found in
   the line gets the head. This is how Chat Heads works.

Lines from NPCs and bosses (`[NPC]`, `[BOSS]`, `[STATUE]`, ...) never get a head.

## Chat & Peek tab

| Option | Default | What it does |
|---|---|---|
| Kept Messages | 1000 | How many chat lines you can scroll back through (100–10000). Vanilla keeps 100. |
| Sent History | 500 | How many of your own messages and commands the ↑ key remembers (100–10000). Vanilla keeps 100. |
| Keep Chat on Disconnect | OFF | Keeps the chat when you leave a server, so you can still read it on the next server you join. |
| Chat Peek | ON | Turns the peek key on or off. |
| Key Mode | Hold | **Hold**: chat is visible while the key is held. **Toggle**: press once to show, again to hide. |
| Full Height | ON | Uses the taller chat height of the open chat while peeking. |
| Scroll With Wheel | ON | While peeking, the mouse wheel scrolls the chat instead of changing the hotbar slot. Hold Shift to scroll one line at a time. |
| Peek Key | Left Alt | Opens Minecraft's Controls screen to change the key. |

### What chat peek does

When you play, old chat messages fade out after a few seconds. Opening the chat shows them again, but it also
opens the input box, frees the mouse and stops your movement.

Chat peek shows the chat the way it looks when open, with every line at full opacity, **without** the input box.
You can keep walking, fighting and looking around. Let go of the key (or press it again in toggle mode)
and the chat goes back to normal, scrolled to the newest message.

## Key bindings and commands

Find them under **Controls → Key Binds → Hypixel ChatPLUS**:

| Key binding | Default |
|---|---|
| Peek Chat | Left Alt |
| Open Settings | not bound |

| Command | |
|---|---|
| `/chatplus` | opens the settings |

## Config file

Settings are stored in `.minecraft/config/hypixel-chatplus.json`. You can edit or share this file. Delete it to
go back to the defaults. Out-of-range values are clamped when the file is loaded.

```json
{
  "channelButtons": {
    "enabled": true,
    "onlyOnHypixel": true,
    "syncWithServer": true,
    "showSettingsButton": true,
    "alignment": "LEFT",
    "buttonWidth": 0,
    "buttons": [
      { "label": "Normal", "command": "/chat a", "enabled": true },
      { "label": "Party", "command": "/chat p", "enabled": true },
      { "label": "Guild", "command": "/chat g", "enabled": true },
      { "label": "Co-op", "command": "/chat coop", "enabled": true },
      { "label": "Officer", "command": "/chat o", "enabled": false }
    ]
  },
  "chatHeads": {
    "enabled": true,
    "position": "BEFORE_NAME",
    "offsetNonPlayerText": true,
    "resolveOfflinePlayers": true,
    "handleSystemMessages": true,
    "senderDetection": "UUID_AND_HEURISTIC",
    "smartHeuristics": true,
    "drawShadow": true,
    "rightPadding": 1,
    "threeDeeNess": 0.0,
    "commandSuggestionHeads": true
  },
  "chatHistory": {
    "maxMessages": 1000,
    "maxSentHistory": 500,
    "keepOnDisconnect": false
  },
  "chatPeek": {
    "enabled": true,
    "toggleMode": false,
    "fullHeight": true,
    "scrollWithMouseWheel": true
  }
}
```

## FAQ

**A head shows Steve or Alex for a second, then the real skin.**
The skin is being downloaded. After that it is cached.

**A player keeps the default head.**
The name in chat is not a real Minecraft account (for example a nick in a game), or the skin servers could not
be reached. Players in your lobby always use the skin from the tab list.

**I don't see the channel buttons.**
They only show on Hypixel by default (**Only on Hypixel**). They are also hidden while you type a command
starting with `/`.

**The wrong button is highlighted.**
Turn on **Sync With Server**. If you use Hypixel in a language whose channel names are not recognized, the
highlight only follows your clicks. Please open an issue with the exact message so it can be added.

**Can I use it with Chat Heads?**
No. Both add heads, so remove Chat Heads. This mod includes its features.

**Does it work on other servers?**
Chat heads, chat history and chat peek work everywhere. The channel buttons are made for Hypixel's `/chat`
command, but you can turn off **Only on Hypixel** and use them as command buttons anywhere.
