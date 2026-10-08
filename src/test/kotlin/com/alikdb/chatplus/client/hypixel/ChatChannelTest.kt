package com.alikdb.chatplus.client.hypixel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChatChannelTest {
	@Test
	fun commands() {
		assertEquals(ChatChannel.ALL, ChatChannels.channelFor("/chat a"))
		assertEquals(ChatChannel.PARTY, ChatChannels.channelFor("/chat party"))
		assertEquals(ChatChannel.GUILD, ChatChannels.channelFor("/CHAT G"))
		assertEquals(ChatChannel.COOP, ChatChannels.channelFor("/chat skyblock-coop"))
		assertNull(ChatChannels.channelFor("/p warp"))
		assertNull(ChatChannels.channelFor("/chat"))
	}

	@Test
	fun serverNames() {
		assertEquals(ChatChannel.COOP, ChatChannel.byServerName("SKYBLOCK CO-OP"))
		assertEquals(ChatChannel.PARTY, ChatChannel.byServerName("PARTİ"))
		assertEquals(ChatChannel.ALL, ChatChannel.byServerName("GENEL"))
		assertNull(ChatChannel.byServerName("DISCORD"))
	}
}
