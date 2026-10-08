package com.alikdb.chatplus.client.hypixel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HypixelSenderParserTest {
	/** Asserts the sender name, the text right after the head position and whether it is trusted. */
	private fun assertSender(line: String, name: String, headBefore: String, trusted: Boolean = true) {
		val parsed = HypixelSenderParser.parse(line) ?: throw AssertionError("no sender found in: $line")
		assertEquals(name, parsed.name, line)
		assertEquals(true, line.substring(parsed.headIndex).startsWith(headBefore), "head goes before \"${line.substring(parsed.headIndex)}\" in: $line")
		assertEquals(trusted, parsed.trusted, "trusted: $line")
	}

	@Test
	fun guildAndOfficer() {
		assertSender("Guild > [MVP+] lrg89 [Iron]: h", "lrg89", "[MVP+] lrg89")
		assertSender("Guild > ⚔ [MVP++] RealBacklight: !warp", "RealBacklight", "[MVP++] RealBacklight")
		assertSender("Guild > SomeNon: hi", "SomeNon", "SomeNon")
		assertSender("Officer > [VIP+] Off_icer [GM]: secret", "Off_icer", "[VIP+] Off_icer")
		assertSender("Guild > Steve123 joined.", "Steve123", "Steve123")
		assertSender("Friend > Alex left.", "Alex", "Alex")
	}

	@Test
	fun partyAndCoop() {
		assertSender("Party > [MVP+] lrg89: peee", "lrg89", "[MVP+] lrg89")
		assertSender("Party > [MVP+] lrg89 ቾ: hello", "lrg89", "[MVP+] lrg89")
		assertSender("Co-op > nea89o: hallooooo", "nea89o", "nea89o")
		assertSender("Co-op > [VIP] nea89o ⚒: x", "nea89o", "[VIP] nea89o")
		assertSender("[MVP+] Leader joined the party.", "Leader", "[MVP+] Leader")
		assertSender("[VIP] Someone has left the party.", "Someone", "[VIP] Someone")
		assertSender("You have joined [MVP++] Host's party!", "Host", "[MVP++] Host")
		assertSender("Party Finder > Mage_Main joined the dungeon group! (Mage Level 30)", "Mage_Main", "Mage_Main")
	}

	@Test
	fun translatedChannelPrefix() {
		assertSender("Parti > [MVP+] Ali: selam", "Ali", "[MVP+] Ali")
		assertSender("Lonca > Veli katıldı.", "Veli", "Veli")
	}

	@Test
	fun privateMessages() {
		assertSender("From [MVP++] qtLuna: this is a test", "qtLuna", "[MVP++] qtLuna")
		assertSender("To [ዞ] Staffer: hi", "Staffer", "[ዞ] Staffer")
		assertNull(HypixelSenderParser.parse("From stash: You have 3 items in your stash!"))
	}

	@Test
	fun skyblockAllChat() {
		assertSender("[323] [MVP+] xatarna: wts hyperion", "xatarna", "[MVP+] xatarna")
		assertSender("[266] ♫ [MVP+] lrg89: a", "lrg89", "[MVP+] lrg89")
		assertSender("[58] nea89o: haiiiii", "nea89o", "nea89o")
		assertSender("[58] nea89o ♲: ironman", "nea89o", "nea89o")
		assertSender("[✌] [123] [VIP] Guest: hi", "Guest", "[VIP] Guest")
	}

	@Test
	fun lobbyAndGames() {
		assertSender("[123✫] [MVP+] BedWarsGuy: gg", "BedWarsGuy", "[MVP+] BedWarsGuy")
		assertSender("[MVP++] Rich: hello lobby", "Rich", "[MVP++] Rich")
		assertSender(" >>> [MVP++] Rich joined the lobby! <<<", "Rich", "[MVP++] Rich")
		assertSender("[SHOUT] [RED] [VIP] Loud: help", "Loud", "[VIP] Loud")
		// no rank and no level: only accepted if the player is in the tab list
		assertSender("Default_Player: hi", "Default_Player", "Default_Player", trusted = false)
		assertSender("[SPECTATOR] Watcher: hi", "Watcher", "Watcher", trusted = false)
	}

	@Test
	fun notPlayers() {
		assertNull(HypixelSenderParser.parse("[NPC] Jacob: Welcome to the contest!"))
		assertNull(HypixelSenderParser.parse("[BOSS] Maxor: WELL WELL WELL"))
		assertNull(HypixelSenderParser.parse("[STATUE] Oruo the Omniscient: I am Oruo"))
		assertNull(HypixelSenderParser.parse("You are now in the PARTY channel"))
		assertNull(HypixelSenderParser.parse("RARE DROP! Enchanted Diamond (+250% Magic Find)"))
		assertNull(HypixelSenderParser.parse("Profile ID: 1234-5678"))
	}
}
