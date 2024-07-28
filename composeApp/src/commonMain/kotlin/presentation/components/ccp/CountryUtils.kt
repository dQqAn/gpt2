import kotlinx.serialization.json.Json
import java.io.InputStreamReader

class SharedFileReader() {
    suspend fun loadJsonFile(fileName: String?): String? {
        return javaClass.classLoader?.getResourceAsStream(fileName).use { stream ->
            stream?.let {
                InputStreamReader(stream).use { reader ->
                    reader.readText()
                }
            }
        }
    }
}

private fun parseJsonFile(jsonString: String): MutableList<Country?>? {
    return Json.decodeFromString<MutableList<Country?>?>(jsonString)
}

suspend fun loadAndParseJsonFile(): MutableList<Country?>? {
    val jsonString = SharedFileReader().loadJsonFile("Countries.json") ?: return null
    return try {
        parseJsonFile(jsonString)
    } catch (e: Exception) {
        println("Error: " + e.message.toString())
        null
    }
}

internal fun MutableList<Country?>?.searchCountryList(countryName: String): MutableList<Country?> {
    val countryList = mutableListOf<Country?>()
    this?.forEach {
        it?.let {
            if (it.name.lowercase().contains(countryName.lowercase()) ||
                it.areaCode.contains(countryName.lowercase())
            ) {
                countryList.add(it)
            }
        }
    }
    return countryList
}
