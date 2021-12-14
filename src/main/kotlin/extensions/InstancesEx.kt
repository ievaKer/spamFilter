import weka.core.Instance
import weka.core.Instances
import weka.filters.Filter
import weka.filters.unsupervised.attribute.NominalToBinary
import weka.filters.unsupervised.attribute.NumericToNominal
import weka.filters.unsupervised.attribute.Remove
import weka.filters.unsupervised.attribute.ReplaceMissingValues

fun Instances.attributes(attributes: List<Int>): Instances {
    val filter = Remove()
    filter.options = arrayOf("-V", "-R", (attributes).joinToString(","))
    filter.setInputFormat(this)

    return Filter.useFilter(this, filter)
}

fun Instances.lastNominal(): Instances {
    val nominal = this.numAttributes()

    val filter = NumericToNominal()
    filter.options = arrayOf("-R", "$nominal")
    filter.setInputFormat(this)

    val data = Filter.useFilter(this, filter).apply {
        setClassIndex(nominal - 1)
    }

    return data
}

fun Instances.removeMissingValues(): Instances {
    val data = Instances(this)
    data.removeIf(Instance::hasMissingValue)

    return data
}

fun Instances.imputeMissingValues(): Instances {
    val filter = ReplaceMissingValues()
    filter.setInputFormat(this)

    return Filter.useFilter(this, filter)
}

fun Instances.discretizeUnsupervised(bins: Int): Instances {
    val attributes = (1 until this.numAttributes()).toList()

    val filter = weka.filters.unsupervised.attribute.Discretize()
    filter.options = arrayOf("-B", "$bins", "-R", attributes.joinToString(","))
    filter.setInputFormat(this)

    return Filter.useFilter(this, filter)
}

fun Instances.discretizeSupervised(): Instances {
    val attributes = (1 until this.numAttributes()).toList()

    val filter = weka.filters.supervised.attribute.Discretize()
    filter.options = arrayOf("-R", attributes.joinToString(","))
    filter.setInputFormat(this)

    return Filter.useFilter(this, filter)
}