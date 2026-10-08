/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Based on Chat Heads by dzwdz and Fourmisain: https://github.com/dzwdz/chat_heads
 */
package com.alikdb.chatplus.client.mixininterface;

public interface VisibleInLog {
	void chatplus_setVisibleInLog(boolean visibleInLog);

	boolean chatplus_isVisibleInLog();
}
