import weka.classifiers.Classifier
import weka.classifiers.rules.JRip
import weka.classifiers.rules.OneR
import weka.classifiers.rules.ZeroR

sealed class Tree {
    class J48: WekaTree {
        override val name = "J48"
        override val tree = j48Tree()

        private fun j48Tree(): weka.classifiers.trees.J48 {
            return weka.classifiers.trees.J48().apply {
                options = arrayOf("-C", "0.25", "-M", "4", "-B")
            }
        }
    }

    class RandomTree: WekaTree {
        override val name = "RandomTree"
        override val tree = randomTree()

        private fun randomTree(): weka.classifiers.trees.RandomTree {
            return weka.classifiers.trees.RandomTree().apply {
                options = arrayOf("-K", "0", "-M", "40", "-V", "0.001", "-S", "42", "-depth", "4")
            }
        }
    }

    class REPTree: WekaTree {
        override val name = "REPTree"
        override val tree = repTree()

        private fun repTree(): weka.classifiers.trees.REPTree {
            return weka.classifiers.trees.REPTree().apply {
                options = arrayOf("-M", "4", "-V", "0.001", "-N", "3", "-S", "42", "-L", "4", "-I", "0.0")
            }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        val j48: J48 by lazy {
            J48()
        }

        val randomTree: RandomTree by lazy {
            RandomTree()
        }

        val repTree: REPTree by lazy {
            REPTree()
        }

        fun all(): List<WekaTree> = listOf(j48, randomTree, repTree)
    }
}

interface WekaTree {
    val name: String
    val tree: Classifier
}