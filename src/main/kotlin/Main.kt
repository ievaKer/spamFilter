import enums.DiscretizationType
import weka.attributeSelection.AttributeSelection
import weka.attributeSelection.InfoGainAttributeEval
import weka.attributeSelection.Ranker
import weka.classifiers.rules.JRip
import weka.classifiers.rules.OneR
import weka.classifiers.rules.ZeroR
import weka.core.Instances
import guru.nidi.graphviz.parse.Parser

//                Medzio sudetingumas
//                1. Data preprocessing + discretization
//                2. Algorithm + params -> choose best params
//                3. Evaluate model (4 ways) + tree
//                - training set (vertinimas pagal mokymo aibe)
//                - supplied test set (pagaminti taip, kad klases skirstinys atitiktu klases skirstini visoje aibeje)
//                - cross validation
//                - percentage split

fun main() {
    val filename = "emails.csv"

    val loader = Loader()
    loader.load(filename)
    println()

    val data = loader.data
    val supervized = data.discretizeSupervised()
    val unsupervized = data.discretizeUnsupervised(2)

//    dataSetInformation(data)
//    wekaInfoGain(data)

//    val scores = mutableListOf<Score>()
//    val tree = Tree.repTree
//    Validation.all().forEach {
//        scores.add(Score(tree.tree, "repTree", it, DiscretizationType.NONE, it.evaluate(data, tree.tree), data))
//        scores.add(Score(tree.tree, "repTree", it, DiscretizationType.SUPERVISED, it.evaluate(data, tree.tree), supervized))
//        scores.add(Score(tree.tree, "repTree", it, DiscretizationType.UNSUPERVISED, it.evaluate(data, tree.tree), unsupervized))
//    }
//
//    scores.sortBy { it.disc }
//    println(scores.joinToString("\n"))
//
//    scores.forEach {
//        it.saveGraph()
//    }

//    val scores = mutableListOf<Score>()
//    val tree = Tree.randomTree
//    Validation.all().forEach {
//        scores.add(Score(tree.tree, "randomTree", it, DiscretizationType.NONE, it.evaluate(data, tree.tree), data))
//        scores.add(Score(tree.tree, "randomTree", it, DiscretizationType.SUPERVISED, it.evaluate(data, tree.tree), supervized))
//        scores.add(Score(tree.tree, "randomTree", it, DiscretizationType.UNSUPERVISED, it.evaluate(data, tree.tree), unsupervized))
//    }
//
//    scores.sortBy { it.disc }
//    println(scores.joinToString("\n"))
//
//    scores.forEach {
//        it.saveGraph()
//    }

//    val scores = mutableListOf<Score>()
//    val tree = Tree.j48
//    Validation.all().forEach {
//        scores.add(Score(tree.tree, "j48", it, DiscretizationType.NONE, it.evaluate(data, tree.tree), data))
//        scores.add(Score(tree.tree, "j48", it, DiscretizationType.SUPERVISED, it.evaluate(data, tree.tree), supervized))
//        scores.add(Score(tree.tree, "j48", it, DiscretizationType.UNSUPERVISED, it.evaluate(data, tree.tree), unsupervized))
//    }
//
//    scores.sortBy { it.disc }
//    println(scores.joinToString("\n"))
//
//    scores.forEach {
//        it.saveGraph()
//    }

//    val scores = mutableListOf<Score>()
//    val classifier = jRip()
//
//    Validation.all().forEach { eval ->
//        println("NONE")
//        scores.add(Score(classifier, "jRip", eval, DiscretizationType.NONE, eval.evaluate(data, classifier), data))
//        println("SUPERVISED")
//        scores.add(Score(classifier, "jRip", eval, DiscretizationType.SUPERVISED, eval.evaluate(data, classifier), supervized))
//        println("UNSUPERVISED")
//        scores.add(Score(classifier, "jRip", eval, DiscretizationType.UNSUPERVISED, eval.evaluate(data, classifier), unsupervized))
//    }
//
//    println("NONE")
//    println(classifier.apply { buildClassifier(data) })
//    println("SUPERVISED")
//    println(classifier.apply { buildClassifier(supervized) })
//    println("UNSUPERVISED")
//    println(classifier.apply { buildClassifier(unsupervized) })
//
//    scores.sortBy { it.disc }
//    println(scores.joinToString("\n"))


    val scores = mutableListOf<Score>()
    val classifier = zeroRule()

    Validation.all().forEach { eval ->
        println("NONE")
        scores.add(Score(classifier, "zeroR", eval, DiscretizationType.NONE, eval.evaluate(data, classifier), data))
        println("SUPERVISED")
        scores.add(Score(classifier, "zeroR", eval, DiscretizationType.SUPERVISED, eval.evaluate(data, classifier), supervized))
        println("UNSUPERVISED")
        scores.add(Score(classifier, "zeroR", eval, DiscretizationType.UNSUPERVISED, eval.evaluate(data, classifier), unsupervized))
    }

    println("NONE")
    println(classifier.apply { buildClassifier(data) })
    println("SUPERVISED")
    println(classifier.apply { buildClassifier(supervized) })
    println("UNSUPERVISED")
    println(classifier.apply { buildClassifier(unsupervized) })

    scores.sortBy { it.disc }
    println(scores.joinToString("\n"))
}



private fun dataSetInformation(data: Instances){
    println("\n####### Attribute information ########")
    for(i in 0 until data.numAttributes()){
        println(String.format("%-20s %s", data.attribute(i).name(), if (data.attribute(i).isNumeric) "numeric" else "nominal"))
    }
}

private fun wekaInfoGain(data: Instances) {
    val evaluator = InfoGainAttributeEval()
    val ranker = Ranker().apply {
        numToSelect = -1
        threshold = -1.7976931348623157E308
    }

    val selector = AttributeSelection().apply {
        setEvaluator(evaluator)
        setSearch(ranker)
        SelectAttributes(data)
    }

    val att = selector.rankedAttributes()

    println("\n######## Info gain ########")
    for(i in att){
        println(String.format("%-20s %-5s %s", data.attribute(i[0].toInt()).name(), data.attribute(i[0].toInt()).index(), i[1]))
    }
}

private fun zeroRule(): ZeroR {
    return ZeroR()
}

private fun oneRule(): OneR {
    return OneR().apply {
        options = arrayOf("-B", "6")
    }
}

private fun jRip(): JRip {
    return JRip().apply {
        options = arrayOf("-F", "3", "-N", "13.0", "-O", "2", "-S", "42")
    }
}
