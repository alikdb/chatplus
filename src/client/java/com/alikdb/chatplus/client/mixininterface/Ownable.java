/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixininterface;

import net.minecraft.client.multiplayer.PlayerInfo;
import org.jspecify.annotations.Nullable;

/** Carries the sender's tab list entry along with a signed chat message. */
public interface Ownable {
	@Nullable PlayerInfo chatplus_getOwner();

	void chatplus_setOwner(@Nullable PlayerInfo owner);
}
