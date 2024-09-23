import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream

class LoadDataSetClient {

    private companion object {
        private const val TAG = "BertAppDemo"
        private const val JSON_DIR = "qa.json"
    }

    // Load json file into a data object.
    fun loadJson(): DataSet? {
        var dataSet: DataSet? = null
        try {
//            val inputStream: InputStream = context.assets.open(JSON_DIR)
            val inputStream: InputStream = javaClass.classLoader.getResourceAsStream(JSON_DIR)
            /*val x:String= javaClass.classLoader?.getResourceAsStream("fileName").use { stream ->
                stream?.let {
                    InputStreamReader(stream).use { reader ->
                        reader.readText()
                    }
                }!!
            }*/
            val bufferReader = inputStream.bufferedReader()
            val stringJson: String = bufferReader.use { it.readText() }
            val datasetType = object : TypeToken<DataSet>() {}.type
            dataSet = Gson().fromJson(stringJson, datasetType)
        } catch (e: IOException) {
            println(e.message.toString())
        }
        return dataSet
    }
}
