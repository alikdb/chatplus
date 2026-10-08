package com.alikdb.chatplus

import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object HypixelChatPLUS {
	const val MOD_ID: String = "hypixel-chatplus"

	@JvmField
	val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

	fun id(path: String): Identifier
		= Identifier.fromNamespaceAndPath(MOD_ID, path)
}
