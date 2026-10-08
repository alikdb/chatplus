package com.alikdb.chatplus.client

import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/** Checks translation files, so a community pull request with a broken file fails CI instead of the game. */
class LangFilesTest {
	private val langDir = Path.of("src/main/resources/assets/chatplus-hypixel/lang")
	private val placeholder = Regex("""%(?:\d+\$)?[sd]""")

	private fun read(file: Path): Map<String, String> {
		val json = try {
			JsonParser.parseString(file.readText()).asJsonObject
		} catch (e: Exception) {
			fail("${file.name} is not valid JSON: ${e.message}")
		}
		return json.entrySet().associate { (key, value) ->
			assertTrue(value.isJsonPrimitive && value.asJsonPrimitive.isString, "${file.name}: \"$key\" must be a string")
			key to value.asString
		}
	}

	private fun translations(): List<Path> = Files.list(langDir).use { files ->
		files.filter { it.name != "en_us.json" }.sorted().toList()
	}

	@Test
	fun fileNamesAreMinecraftLocaleCodes() {
		for (file in translations()) {
			assertTrue(Regex("""[a-z]{2,3}_[a-z]{2,3}\.json""").matches(file.name), "${file.name}: use a lower case Minecraft locale code like de_de.json")
		}
	}

	@Test
	fun translationsOnlyUseExistingKeys() {
		val english = read(langDir.resolve("en_us.json"))
		for (file in translations()) {
			val unknown = read(file).keys - english.keys
			assertTrue(unknown.isEmpty(), "${file.name} has keys that are not in en_us.json (typo or removed key): $unknown")
		}
	}

	@Test
	fun placeholdersAreKept() {
		val english = read(langDir.resolve("en_us.json"))
		for (file in translations()) {
			for ((key, value) in read(file)) {
				val expected = placeholder.findAll(english[key] ?: continue).map { it.value }.sorted().toList()
				val actual = placeholder.findAll(value).map { it.value }.sorted().toList()
				assertEquals(expected, actual, "${file.name}: \"$key\" must keep the placeholders of the English text")
			}
		}
	}
}
