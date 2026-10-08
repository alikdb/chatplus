package com.alikdb.chatplus.client.config

import com.alikdb.chatplus.client.peek.ChatPeek
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen
import net.minecraft.locale.Language
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import kotlin.math.roundToInt

/** Edits a copy of the config; "Done" saves it, "Cancel" throws it away. */
class ChatPlusConfigScreen(private val parent: Screen?) : Screen(Component.translatable("$KEY.title")) {
	enum class Tab { CHANNELS, HEADS, CHAT }

	private val config = ChatPlusConfig.get().copy()
	private var layout = HeaderAndFooterLayout(this, HEADER_HEIGHT, FOOTER_HEIGHT)
	private var list: ConfigList? = null
	private var rebuildListNextTick = false

	override fun init() {
		layout = HeaderAndFooterLayout(this, HEADER_HEIGHT, FOOTER_HEIGHT)

		val header = layout.addToHeader(LinearLayout.vertical().spacing(6))
		header.defaultCellSetting().alignHorizontallyCenter()
		header.addChild(StringWidget(title, font))
		val tabs = header.addChild(LinearLayout.horizontal().spacing(4))
		for (tab in Tab.entries) {
			val button = Button.builder(Component.translatable("$KEY.tab.${tab.name.lowercase()}")) { selectTab(tab) }.width(100).build()
			button.active = tab != selectedTab
			tabs.addChild(button)
		}

		list = layout.addToContents(ConfigList(minecraft, width, layout.contentHeight, layout.headerHeight))
		fillList()

		val footer = layout.addToFooter(LinearLayout.horizontal().spacing(8))
		footer.addChild(Button.builder(CommonComponents.GUI_CANCEL) { onClose() }.width(150).build())
		footer.addChild(Button.builder(CommonComponents.GUI_DONE) { saveAndClose() }.width(150).build())

		layout.visitWidgets { addRenderableWidget(it) }
		repositionElements()
	}

	override fun repositionElements() {
		layout.arrangeElements()
		list?.updateSize(width, layout)
	}

	override fun tick() {
		// rows are rebuilt outside of their own click handlers
		if (rebuildListNextTick) {
			rebuildListNextTick = false
			val scroll = list?.scrollAmount() ?: 0.0
			fillList()
			list?.setScrollAmount(scroll)
		}
	}

	override fun onClose() {
		minecraft.gui.setScreen(parent)
	}

	private fun saveAndClose() {
		ChatPlusConfig.replace(config)
		// re-wrap the chat: head offsets and history size may have changed
		minecraft.gui.hud.chat.rescaleChat()
		onClose()
	}

	private fun selectTab(tab: Tab) {
		selectedTab = tab
		rebuildWidgets()
	}

	private fun fillList() {
		val list = list ?: return
		list.clear()
		when (selectedTab) {
			Tab.CHANNELS -> channelsTab(list)
			Tab.HEADS -> headsTab(list)
			Tab.CHAT -> chatTab(list)
		}
	}

	private fun channelsTab(list: ConfigList) {
		val settings = config.channelButtons

		list.row(
			toggle("channels.enabled", settings.enabled) { settings.enabled = it },
			toggle("channels.only_on_hypixel", settings.onlyOnHypixel) { settings.onlyOnHypixel = it },
		)
		list.row(
			toggle("channels.sync", settings.syncWithServer) { settings.syncWithServer = it },
			toggle("channels.settings_button", settings.showSettingsButton) { settings.showSettingsButton = it },
		)
		list.row(
			choice("channels.alignment", settings.alignment, ButtonAlignment.entries) { settings.alignment = it },
			IntSlider(ConfigList.HALF, text("channels.width"), 0, 120, 2, settings.buttonWidth, { if (it == 0) text("channels.width.auto") else Component.literal("$it") }) {
				settings.buttonWidth = it
			},
		)

		list.header(text("channels.buttons").withStyle(ChatFormatting.BOLD), font)
		list.rowAt(
			StringWidget(76, 20, text("channels.label").withStyle(ChatFormatting.GRAY), font) to 0,
			StringWidget(136, 20, text("channels.command").withStyle(ChatFormatting.GRAY), font) to 80,
		)

		settings.buttons.forEachIndexed { index, button -> buttonRow(list, settings.buttons, index, button) }

		list.row(
			Button.builder(text("channels.add")) {
				settings.buttons += ChannelButton("New", "/chat a")
				rebuildListNextTick = true
			}.width(ConfigList.HALF).build(),
			Button.builder(text("channels.reset")) {
				settings.buttons = ChannelButtonsSettings.defaultButtons()
				rebuildListNextTick = true
			}.width(ConfigList.HALF).build(),
		)
	}

	private fun buttonRow(list: ConfigList, buttons: MutableList<ChannelButton>, index: Int, button: ChannelButton) {
		val label = EditBox(font, 76, 20, text("channels.label"))
		label.setMaxLength(32)
		label.value = button.label
		label.setResponder { button.label = it }

		val command = EditBox(font, 136, 20, text("channels.command"))
		command.setMaxLength(256)
		command.value = button.command
		command.setHint(Component.literal("/chat g").withStyle(ChatFormatting.DARK_GRAY))
		command.setResponder { button.command = it }
		command.setTooltip(Tooltip.create(text("channels.command.tooltip")))

		val enabled = Button.builder(enabledMark(button.enabled)) {
			button.enabled = !button.enabled
			it.message = enabledMark(button.enabled)
		}.width(20).tooltip(Tooltip.create(text("channels.visible"))).build()

		val up = smallButton("▲", index > 0) { move(buttons, index, -1) }
		val down = smallButton("▼", index < buttons.size - 1) { move(buttons, index, 1) }
		val remove = smallButton("✕", true) {
			buttons.removeAt(index)
			rebuildListNextTick = true
		}
		remove.setTooltip(Tooltip.create(text("channels.remove")))

		list.rowAt(label to 0, command to 80, enabled to 220, up to 242, down to 264, remove to 290)
	}

	private fun headsTab(list: ConfigList) {
		val settings = config.chatHeads

		list.row(
			toggle("heads.enabled", settings.enabled) { settings.enabled = it },
			choice("heads.position", settings.position, HeadPosition.entries) { settings.position = it },
		)
		list.row(
			toggle("heads.resolve_offline", settings.resolveOfflinePlayers) { settings.resolveOfflinePlayers = it },
			toggle("heads.system_messages", settings.handleSystemMessages) { settings.handleSystemMessages = it },
		)
		list.row(
			choice("heads.sender_detection", settings.senderDetection, SenderDetection.entries) { settings.senderDetection = it },
			toggle("heads.smart_heuristics", settings.smartHeuristics) { settings.smartHeuristics = it },
		)
		list.row(
			toggle("heads.offset_text", settings.offsetNonPlayerText) { settings.offsetNonPlayerText = it },
			toggle("heads.shadow", settings.drawShadow) { settings.drawShadow = it },
		)
		list.row(
			IntSlider(ConfigList.HALF, text("heads.padding"), 0, 4, 1, settings.rightPadding, { Component.literal("${it}px") }) {
				settings.rightPadding = it
			}.withTooltip("heads.padding"),
			IntSlider(ConfigList.HALF, text("heads.3d"), 0, 100, 5, (settings.threeDeeNess * 100).roundToInt(), { Component.literal("$it%") }) {
				settings.threeDeeNess = it / 100f
			}.withTooltip("heads.3d"),
		)
		list.row(toggle("heads.suggestions", settings.commandSuggestionHeads) { settings.commandSuggestionHeads = it })
	}

	private fun chatTab(list: ConfigList) {
		val history = config.chatHistory
		val peek = config.chatPeek

		list.header(text("chat.history").withStyle(ChatFormatting.BOLD), font)
		list.row(
			IntSlider(ConfigList.HALF, text("chat.max_messages"), ChatPlusConfig.MIN_HISTORY, ChatPlusConfig.MAX_HISTORY, 100, history.maxMessages, { Component.literal("$it") }) {
				history.maxMessages = it
			}.withTooltip("chat.max_messages"),
			IntSlider(ConfigList.HALF, text("chat.max_sent"), ChatPlusConfig.MIN_HISTORY, ChatPlusConfig.MAX_HISTORY, 100, history.maxSentHistory, { Component.literal("$it") }) {
				history.maxSentHistory = it
			}.withTooltip("chat.max_sent"),
		)
		list.row(toggle("chat.keep_on_disconnect", history.keepOnDisconnect) { history.keepOnDisconnect = it })

		list.header(text("peek").withStyle(ChatFormatting.BOLD), font)
		list.row(
			toggle("peek.enabled", peek.enabled) { peek.enabled = it },
			CycleButton.booleanBuilder(text("peek.mode.toggle"), text("peek.mode.hold"), peek.toggleMode)
				.create(text("peek.mode")) { _, value -> peek.toggleMode = value },
		)
		list.row(
			toggle("peek.full_height", peek.fullHeight) { peek.fullHeight = it },
			toggle("peek.scroll", peek.scrollWithMouseWheel) { peek.scrollWithMouseWheel = it },
		)
		list.row(
			Button.builder(Component.translatable("$KEY.peek.key", ChatPeek.keyName())) {
				minecraft.gui.setScreen(KeyBindsScreen(this, minecraft.options))
			}.width(ConfigList.ROW_WIDTH).build()
		)
	}

	private fun move(buttons: MutableList<ChannelButton>, index: Int, by: Int) {
		val target = index + by
		if (target !in buttons.indices) return
		buttons.add(target, buttons.removeAt(index))
		rebuildListNextTick = true
	}

	private fun smallButton(symbol: String, active: Boolean, onPress: () -> Unit): Button {
		val button = Button.builder(Component.literal(symbol)) { onPress() }.width(20).build()
		button.active = active
		return button
	}

	private fun enabledMark(enabled: Boolean): Component =
		if (enabled) Component.literal("✔").withStyle(ChatFormatting.GREEN) else Component.literal("✖").withStyle(ChatFormatting.RED)

	private fun toggle(key: String, value: Boolean, set: (Boolean) -> Unit): CycleButton<Boolean> =
		CycleButton.onOffBuilder(value)
			.withTooltip { tooltip(key) }
			.create(text(key)) { _, newValue -> set(newValue) }

	private fun <T : Enum<T>> choice(key: String, value: T, values: List<T>, set: (T) -> Unit): CycleButton<T> =
		CycleButton.builder({ it: T -> text("$key.${it.name.lowercase()}") }, value)
			.withValues(values)
			.withTooltip { tooltip(key) }
			.create(text(key)) { _, newValue -> set(newValue) }

	private fun <W : AbstractWidget> W.withTooltip(key: String): W = apply { tooltip(key)?.let(::setTooltip) }

	private fun tooltip(key: String): Tooltip? {
		val tooltipKey = "$KEY.$key.tooltip"
		return if (Language.getInstance().has(tooltipKey)) Tooltip.create(Component.translatable(tooltipKey)) else null
	}

	private fun text(key: String) = Component.translatable("$KEY.$key")

	companion object {
		private const val KEY = "hypixel-chatplus.settings"
		private const val HEADER_HEIGHT = 56
		private const val FOOTER_HEIGHT = 33

		/** Reopening the screen shows the tab used last. */
		private var selectedTab = Tab.CHANNELS
	}
}
