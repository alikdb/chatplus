package com.alikdb.chatplus.client.hypixel

/**
 * @property name the Minecraft username found in the line
 * @property headIndex UTF-16 index in the parsed text where the head should go (before the rank, if any)
 * @property trusted whether the line is certainly from/about a player, so the name may be looked up online
 *           even if that player is not in the tab list (guild, party, co-op, DMs, SkyBlock chat...)
 */
data class ParsedSender(val name: String, val headIndex: Int, val trusted: Boolean)

/**
 * Finds the player a Hypixel chat line is from. Works on plain text (no § codes).
 *
 * Covered formats, e.g.:
 *  - `Guild > [MVP+] Name [Officer]: hi`, `Party > ...`, `Officer > ...`, `Co-op > ...`
 *  - `From [VIP] Name: hi`, `To Name: hi`
 *  - `Guild > Name joined.`, `Friend > Name left.`
 *  - `[245] ⚔ [MVP+] Name: hi` (SkyBlock), `[123✫] [MVP++] Name: hi` (BedWars), `[RED] Name: hi`
 *  - `Party Finder > Name joined the dungeon group! (Mage Level 30)`
 *  - `[MVP+] Name joined the party.`, `You have joined [VIP] Name's party!`
 */
object HypixelSenderParser {
	private const val TAG = """\[[^\[\]]{1,32}]"""
	private const val SYMBOL = """[^\w\s\[\]:>]{1,4}"""
	// [ዞ] is the current staff rank; the older staff ranks still show up in old logs and replays
	private const val RANK = """\[(?:VIP\+?|MVP\+{0,2}|YOUTUBE|YT|ዞ|PIG\+{0,3}|INNIT|MOJANG|EVENTS|MCP|ADMIN|OWNER|GM|MOD|HELPER|JR HELPER|BUILD TEAM|STAFF)]"""
	private const val NAME = """(?<name>\w{1,16})"""
	private const val PRE = """(?<pre>(?:(?:$TAG|$SYMBOL) )*?)"""
	private const val SUFFIX = """(?: (?:$TAG|$SYMBOL))*"""
	private const val PLAYER = """$PRE(?<head>)(?:(?<rank>$RANK) )?$NAME$SUFFIX"""

	// Hypixel translates system text into the player's /language, so channel prefixes are matched as
	// "any single word followed by >" (Guild, Party, Officer, Co-op, Friend, and their translations)
	private const val CHANNEL = """[\p{L}-]{1,16} > """

	private val TRUSTED = listOf(
		Regex("""^\s*$CHANNEL$PLAYER: """),
		// "Guild > Name joined.", "Friend > Name left." and translated variants
		Regex("""^\s*$CHANNEL(?<head>)$NAME \p{Ll}+\.\s*$"""),
		Regex("""^\s*(?:From|To|Voicemail) $PLAYER: """),
		Regex("""^\s*Party Finder > (?<head>)$NAME joined the (?:dungeon )?group!"""),
		Regex("""^\s*You have joined $PLAYER's party!"""),
		Regex("""^\s*(?:The party was transferred to )?$PLAYER (?:joined the party|has left the party|has been removed from the party|was removed from your party|has disbanded the party|has invited you to join their party|has disconnected|has promoted)"""),
		// ">>> [MVP++] Name joined the lobby! <<<", "[MVP+] Name joined the guild!" ...
		Regex("""^\s*(?:>>> )?$PRE(?<head>)(?<rank>$RANK) $NAME$SUFFIX (?:joined|left|has|was|is|found|sent|obtained|unlocked)\b"""),
	)

	/** Plain all-chat: `[level] [RANK] Name: msg`, `[RED] Name: msg`, `Name: msg`. */
	private val CHAT = Regex("""^\s*$PLAYER: """)
	private val LEVEL_TAG = Regex("""\[\d{1,4}\D{0,2}]""")

	/** NPC and boss dialogue look just like player chat. */
	private val NON_PLAYER_TAGS = Regex("""\[(?:NPC|BOSS|STATUE|CROWD|SKULL|SECRET|DUNGEON|SkyBlock)]""", RegexOption.IGNORE_CASE)

	// "From stash: ..." is a SkyBlock system line
	private val NOT_NAMES = setOf("You", "Your", "The", "Party", "Guild", "stash")

	fun parse(text: String): ParsedSender? {
		for (regex in TRUSTED) {
			regex.find(text)?.let { return it.toSender(trusted = true) }
		}

		val match = CHAT.find(text) ?: return null
		val pre = match.groups["pre"]?.value.orEmpty()
		if (NON_PLAYER_TAGS.containsMatchIn(pre)) return null

		val trusted = match.groups["rank"] != null || LEVEL_TAG.containsMatchIn(pre)
		return match.toSender(trusted)
	}

	private fun MatchResult.toSender(trusted: Boolean): ParsedSender? {
		val name = groups["name"]?.value ?: return null
		if (name in NOT_NAMES) return null

		val head = groups["head"]?.range?.first ?: groups["name"]!!.range.first
		return ParsedSender(name, head, trusted)
	}
}
