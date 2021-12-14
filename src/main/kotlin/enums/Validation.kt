import weka.classifiers.AbstractClassifier
import weka.classifiers.Classifier
import weka.classifiers.evaluation.Evaluation
import weka.core.Instances
import weka.filters.Filter
import weka.filters.supervised.instance.Resample
import java.util.*
import kotlin.math.roundToInt

sealed class Validation {

    class CrossValidation : WekaValidation {
        override val name = "Cross"

        override fun evaluate(data: Instances, classifier: Classifier): Double {
            val eval = Evaluation(data)
            eval.crossValidateModel(classifier, data, 10, Random(42))

            println(name)
            println(eval.toSummaryString(true))

            return eval.pctCorrect()
        }
    }

    class DatasetValidation : WekaValidation {
        override val name = "Dataset"

        override fun evaluate(data: Instances, classifier: Classifier): Double {
            val modelCopy = AbstractClassifier.makeCopy(classifier)
            val resampleTraining = Resample().apply {
                options = arrayOf("-B", "0.0", "-S", "1", "-Z", "70.0", "-no-replacement")
                setInputFormat(data)
            }

            val resampleTest = Resample().apply {
                options = arrayOf("-B", "0.0", "-S", "1", "-Z", "70.0", "-no-replacement", "-V")
                setInputFormat(data)
            }

            val training = Filter.useFilter(data, resampleTraining)
            val test = Filter.useFilter(data, resampleTest)

            modelCopy.buildClassifier(training)

            val eval = Evaluation(training)
            eval.evaluateModel(modelCopy, test)

            println(name)
            println(eval.toSummaryString(true))

            return eval.pctCorrect()
        }
    }

    class SuppliedTest: WekaValidation {
        override val name = "SuppliedTest"

        override fun evaluate(data: Instances, classifier: Classifier): Double {
            val modelCopy = AbstractClassifier.makeCopy(classifier)
            val resampleTest = Resample().apply {
                options = arrayOf("-B", "0.0", "-S", "1", "-Z", "30.0", "-no-replacement")
                setInputFormat(data)
            }

            val resampleTraining = Resample().apply {
                options = arrayOf("-B", "0.0", "-S", "1", "-Z", "70.0", "-no-replacement")
                setInputFormat(data)
            }

            val test = Filter.useFilter(data, resampleTest)
            val training = Filter.useFilter(data, resampleTraining)

            modelCopy.buildClassifier(training)

            val eval = Evaluation(training)
            eval.evaluateModel(modelCopy, test)

            println(name)
            println(eval.toSummaryString(true))

            return eval.pctCorrect()
        }
    }

    class PercentageSplit: WekaValidation {
        override val name = "PercentageSplit"

        override fun evaluate(data: Instances, classifier: Classifier): Double {
            val modelCopy = AbstractClassifier.makeCopy(classifier)
            data.randomize(Random(0))

            val trainSize = (data.numInstances() * 0.8).roundToInt()
            val testSize = data.numInstances() - trainSize
            val train = Instances(data, 0, trainSize)
            val test = Instances(data, trainSize, testSize)

            modelCopy.buildClassifier(train)

            val eval = Evaluation(train)
            eval.evaluateModel(modelCopy, test)

            println(name)
            println(eval.toSummaryString(true))

            return eval.pctCorrect()
        }
    }

    class TrainingSet: WekaValidation {
        override val name = "TrainingSet"

        override fun evaluate(data: Instances, classifier: Classifier): Double {
            val modelCopy = AbstractClassifier.makeCopy(classifier)

            modelCopy.buildClassifier(data)

            val eval = Evaluation(data)
            eval.evaluateModel(modelCopy, data)

            println(name)
            println(eval.toSummaryString(true))

            return eval.pctCorrect()
        }

    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val crossValidation: CrossValidation by lazy {
            CrossValidation()
        }

        val trainingSet: TrainingSet by lazy {
            TrainingSet()
        }

        val percentageSplit: PercentageSplit by lazy {
            PercentageSplit()
        }
        
        val suppliedTest: SuppliedTest by lazy {
            SuppliedTest()
        }

        fun all(): List<WekaValidation> = listOf(trainingSet, crossValidation, suppliedTest, percentageSplit)
    }
}

interface WekaValidation {
    val name: String

    fun evaluate(data: Instances, classifier: Classifier): Double
}
