import enums.DiscretizationType
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import weka.classifiers.AbstractClassifier
import weka.classifiers.Classifier
import weka.core.Drawable
import weka.core.Instances
import guru.nidi.graphviz.parse.Parser
import java.io.File

class Score(private val tree: Classifier, val name: String, private val validation: WekaValidation, val disc: DiscretizationType, val eval: Double, val data: Instances) {
    override fun toString(): String {
        return "${name},${validation.name},${disc.name},$eval"
    }

    fun saveGraph() {
        val filename = "${name}_${disc.name}.png"
        val graph = Parser().read((buildClassifier(tree, data) as Drawable).graph())
        Graphviz.fromGraph(graph).width(1024).render(Format.PNG).toFile(File(filename))
    }

    private fun buildClassifier(model: Classifier, data: Instances): Classifier {
        val modelCopy = AbstractClassifier.makeCopy(model)
        modelCopy.buildClassifier(data)
        return modelCopy
    }
}