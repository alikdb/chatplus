package com.alikdb.chatplus.client.hypixel

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener

object Hypixel {
	/** Cached per connection; the brand and address do not change while connected. */
	@JvmStatic
	var isConnected = false
		private set

	fun onJoin(connection: ClientPacketListener, client: Minecraft) {
		val address = client.currentServer?.ip?.lowercase().orEmpty()
		val brand = connection.serverBrand()?.lowercase().orEmpty()
		isConnected = "hypixel" in address || "hypixel" in brand
	}

	fun onDisconnect() {
		isConnected = false
	}
}
