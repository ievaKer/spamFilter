import weka.core.Instances
import weka.core.converters.CSVLoader

class Loader {
    private val csvLoader = CSVLoader()
    lateinit var data: Instances

    fun load(filename: String) {
        csvLoader.setSource(object {}.javaClass.getResourceAsStream(filename))
        data = csvLoader.dataSet.lastNominal()
    }
}

