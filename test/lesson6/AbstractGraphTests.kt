package lesson6

import lesson6.impl.GraphBuilder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class AbstractGraphTests {

    private fun Graph.Edge.isNeighbour(other: Graph.Edge): Boolean {
        return begin == other.begin || end == other.end || begin == other.end || end == other.begin
    }

    private fun List<Graph.Edge>.assert(shouldExist: Boolean, graph: Graph) {
        val edges = graph.edges
        if (shouldExist) {
            assertEquals(edges.size, size, "Euler loop should traverse all edges")
        } else {
            assertTrue(isEmpty(), "Euler loop should not exist")
        }
        for (edge in this) {
            assertTrue(edge in edges, "Edge $edge is not inside graph")
        }
        for (i in 0 until size - 1) {
            assertTrue(this[i].isNeighbour(this[i + 1]), "Edges ${this[i]} & ${this[i + 1]} are not incident")
        }
        if (size > 1) {
            assertTrue(this[0].isNeighbour(this[size - 1]), "Edges ${this[0]} & ${this[size - 1]} are not incident")
        }
    }

    fun findEulerLoop(findEulerLoop: Graph.() -> List<Graph.Edge>) {
        val emptyGraph = GraphBuilder().build()
        val emptyLoop = emptyGraph.findEulerLoop()
        assertTrue(emptyLoop.isEmpty(), "Euler loop should be empty for the empty graph")
        val noEdgeGraph = GraphBuilder().apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
        }.build()
        val noEdgeLoop = noEdgeGraph.findEulerLoop()
        noEdgeLoop.assert(shouldExist = false, graph = noEdgeGraph)
        val simpleGraph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            addConnection(a, b)
        }.build()
        val simpleLoop = simpleGraph.findEulerLoop()
        simpleLoop.assert(shouldExist = false, graph = simpleGraph)
        val unconnected = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            addConnection(a, b)
            addConnection(c, d)
        }.build()
        val unconnectedLoop = unconnected.findEulerLoop()
        unconnectedLoop.assert(shouldExist = false, graph = unconnected)
        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(a, c)
        }.build()
        val loop = graph.findEulerLoop()
        loop.assert(shouldExist = true, graph = graph)
        val graph2 = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            val g = addVertex("G")
            val h = addVertex("H")
            val i = addVertex("I")
            val j = addVertex("J")
            val k = addVertex("K")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(c, d)
            addConnection(a, e)
            addConnection(d, k)
            addConnection(e, j)
            addConnection(j, k)
            addConnection(b, f)
            addConnection(c, i)
            addConnection(f, i)
            addConnection(b, g)
            addConnection(g, h)
            addConnection(h, c)
        }.build()
        val loop2 = graph2.findEulerLoop()
        loop2.assert(shouldExist = true, graph = graph2)
        // Seven bridges of Koenigsberg
        //    A1 -- A2 ---
        //    |      |    |
        //    B1 -- B2 -- C
        //    |     |     |
        //    D1 -- D2 ---
        val graph3 = GraphBuilder().apply {
            val a1 = addVertex("A1")
            val a2 = addVertex("A2")
            val b1 = addVertex("B1")
            val b2 = addVertex("B2")
            val c = addVertex("C")
            val d1 = addVertex("D1")
            val d2 = addVertex("D2")
            addConnection(a1, a2)
            addConnection(b1, b2)
            addConnection(d1, d2)
            addConnection(a1, b1)
            addConnection(b1, d1)
            addConnection(a2, b2)
            addConnection(b2, d2)
            addConnection(a2, c)
            addConnection(b2, c)
            addConnection(d2, c)
        }.build()
        val loop3 = graph3.findEulerLoop()
        loop3.assert(shouldExist = false, graph = graph3)
    }

    fun minimumSpanningTree(minimumSpanningTree: Graph.() -> Graph) {
        val emptyGraph = GraphBuilder().build()
        assertTrue(emptyGraph.minimumSpanningTree().edges.isEmpty())
        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(a, c)
        }.build()
        val tree = graph.minimumSpanningTree()
        assertEquals(2, tree.edges.size)
        assertEquals(2, tree.findBridges().size)
        val graph2 = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            val g = addVertex("G")
            val h = addVertex("H")
            val i = addVertex("I")
            val j = addVertex("J")
            val k = addVertex("K")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(c, d)
            addConnection(a, e)
            addConnection(d, k)
            addConnection(e, j)
            addConnection(j, k)
            addConnection(b, f)
            addConnection(c, i)
            addConnection(f, i)
            addConnection(b, g)
            addConnection(g, h)
            addConnection(h, c)
        }.build()
        val tree2 = graph2.minimumSpanningTree()
        assertEquals(10, tree2.edges.size)
        assertEquals(10, tree2.findBridges().size)
        // Cross
        val graph3 = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            addConnection(a, e)
            addConnection(b, e)
            addConnection(c, e)
            addConnection(d, e)
        }.build()
        val tree3 = graph3.minimumSpanningTree()
        assertEquals(4, tree3.edges.size)
        assertEquals(4, tree3.findBridges().size)
    }

    fun someMinimumSpanningTreeTest(minimumSpanningTree: Graph.() -> Graph) {
        //before: a -- b        after:  a -- b
        //        |    |                     |
        //        d -- c                d -- c
        val graph1 = GraphBuilder().apply {
            val a = addVertex("a")
            val b = addVertex("b")
            val c = addVertex("c")
            val d = addVertex("d")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(c, d)
            addConnection(d, a)
        }.build()
        assertEquals(4, graph1.edges.size)
        val tree1 = graph1.minimumSpanningTree()
        assertEquals(3, tree1.edges.size)


        //         6 -- 7    8 -- 9
        //         |    |    |    |
        //before:  5 -- 1 -- 2 -- 10
        //              |    |
        //        16 -- 4 -- 3 -- 11
        //        |     |    |     |
        //        15 -- 14   13 -- 12

        //        6 -- 7    8 -- 9
        //        |         |
        //after:  5 -- 1 -- 2 -- 10
        //                  |
        //       16 -- 4 -- 3 -- 11
        //             |          |
        //       15 -- 14   13 -- 12

        val graph2 = GraphBuilder().apply {
            val v1 = addVertex("1")
            val v2 = addVertex("2")
            val v3 = addVertex("3")
            val v4 = addVertex("4")
            val v5 = addVertex("5")
            val v6 = addVertex("6")
            val v7 = addVertex("7")
            val v8 = addVertex("8")
            val v9 = addVertex("9")
            val v10 = addVertex("10")
            val v11 = addVertex("11")
            val v12 = addVertex("12")
            val v13 = addVertex("13")
            val v14 = addVertex("14")
            val v15 = addVertex("15")
            val v16 = addVertex("16")
            addConnection(v1, v2)
            addConnection(v1, v5)
            addConnection(v5, v6)
            addConnection(v6, v7)
            addConnection(v7, v1)
            addConnection(v2, v3)
            addConnection(v2, v8)
            addConnection(v8, v9)
            addConnection(v9, v10)
            addConnection(v10, v2)
            addConnection(v3, v4)
            addConnection(v3, v11)
            addConnection(v11, v12)
            addConnection(v12, v13)
            addConnection(v13, v3)
            addConnection(v4, v1)
            addConnection(v4, v14)
            addConnection(v14, v15)
            addConnection(v15, v16)
            addConnection(v16, v4)
        }.build()
        assertEquals(20, graph2.edges.size)
        assertEquals(16, graph2.vertices.size)
        val tree2 = graph2.minimumSpanningTree()
        assertEquals(15, tree2.edges.size)
    }

    fun largestIndependentVertexSet(largestIndependentVertexSet: Graph.() -> Set<Graph.Vertex>) {
        val emptyGraph = GraphBuilder().build()
        assertTrue(emptyGraph.largestIndependentVertexSet().isEmpty())
        val simpleGraph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            addConnection(a, b)
        }.build()
        assertEquals(
            setOf(simpleGraph["A"]),
            simpleGraph.largestIndependentVertexSet()
        )
        val noEdgeGraph = GraphBuilder().apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
        }.build()
        assertEquals(
            setOf(noEdgeGraph["A"], noEdgeGraph["B"], noEdgeGraph["C"]),
            noEdgeGraph.largestIndependentVertexSet()
        )
        val unconnected = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            addConnection(a, b)
            addConnection(c, d)
            addConnection(d, e)
        }.build()
        assertEquals(
            setOf(unconnected["A"], unconnected["C"], unconnected["E"]),
            unconnected.largestIndependentVertexSet()
        )
        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            val g = addVertex("G")
            val h = addVertex("H")
            val i = addVertex("I")
            val j = addVertex("J")
            addConnection(a, b)
            addConnection(a, c)
            addConnection(b, d)
            addConnection(c, e)
            addConnection(c, f)
            addConnection(b, g)
            addConnection(d, i)
            addConnection(g, h)
            addConnection(h, j)
        }.build()
        assertEquals(
            setOf(graph["A"], graph["D"], graph["E"], graph["F"], graph["G"], graph["J"]),
            graph.largestIndependentVertexSet()
        )
        val cross = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            addConnection(a, e)
            addConnection(b, e)
            addConnection(c, e)
            addConnection(d, e)
        }.build()
        assertEquals(
            setOf(cross["A"], cross["B"], cross["C"], cross["D"]),
            cross.largestIndependentVertexSet()
        )
    }

    fun longestSimplePath(longestSimplePath: Graph.() -> Path) {
        val emptyGraph = GraphBuilder().build()
        assertEquals(0, emptyGraph.longestSimplePath().length)

        val noEdgeGraph = GraphBuilder().apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
        }.build()
        val longestNoEdgePath = noEdgeGraph.longestSimplePath()
        assertEquals(0, longestNoEdgePath.length)

        val unconnected = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            addConnection(a, b)
            addConnection(c, d)
            addConnection(d, e)
        }.build()
        val longestUnconnectedPath = unconnected.longestSimplePath()
        assertEquals(2, longestUnconnectedPath.length)

        val graph = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(a, c)
        }.build()
        val longestPath = graph.longestSimplePath()
        assertEquals(2, longestPath.length)

        val graph2 = GraphBuilder().apply {
            val a = addVertex("A")
            val b = addVertex("B")
            val c = addVertex("C")
            val d = addVertex("D")
            val e = addVertex("E")
            val f = addVertex("F")
            val g = addVertex("G")
            val h = addVertex("H")
            val i = addVertex("I")
            val j = addVertex("J")
            val k = addVertex("K")
            addConnection(a, b)
            addConnection(b, c)
            addConnection(c, d)
            addConnection(a, e)
            addConnection(d, k)
            addConnection(e, j)
            addConnection(j, k)
            addConnection(b, f)
            addConnection(c, i)
            addConnection(f, i)
            addConnection(b, g)
            addConnection(g, h)
            addConnection(h, c)
        }.build()
        val longestPath2 = graph2.longestSimplePath()
        assertEquals(10, longestPath2.length)
        // Seven bridges of Koenigsberg
        //    A1 -- A2 ---
        //    |      |    |
        //    B1 -- B2 -- C
        //    |     |     |
        //    D1 -- D2 ---
        val graph3 = GraphBuilder().apply {
            val a1 = addVertex("A1")
            val a2 = addVertex("A2")
            val b1 = addVertex("B1")
            val b2 = addVertex("B2")
            val c = addVertex("C")
            val d1 = addVertex("D1")
            val d2 = addVertex("D2")
            addConnection(a1, a2)
            addConnection(b1, b2)
            addConnection(d1, d2)
            addConnection(a1, b1)
            addConnection(b1, d1)
            addConnection(a2, b2)
            addConnection(b2, d2)
            addConnection(a2, c)
            addConnection(b2, c)
            addConnection(d2, c)
        }.build()
        val longestPath3 = graph3.longestSimplePath()
        assertEquals(6, longestPath3.length)
    }

    fun someLargestPathTest(longestSimplePath: Graph.() -> Path) {
        val graph1 = GraphBuilder().build()
        assertEquals(0, graph1.longestSimplePath().length)

        val graph2 = GraphBuilder().apply {
            addVertex("a")
        }.build()
        assertEquals(0, graph2.longestSimplePath().length)

        val graph3 = GraphBuilder().apply {
            val v1 = addVertex("1")
            val v2 = addVertex("2")
            val v3 = addVertex("3")
            val v4 = addVertex("4")
            val v5 = addVertex("5")
            val v6 = addVertex("6")
            val v7 = addVertex("7")
            val v8 = addVertex("8")
            val v9 = addVertex("9")
            val v10 = addVertex("10")
            val v11 = addVertex("11")
            val v12 = addVertex("12")
            val v13 = addVertex("13")
            val v14 = addVertex("14")
            val v15 = addVertex("15")
            val v16 = addVertex("16")
            val v17 = addVertex("17")
            val v18 = addVertex("18")
            val v19 = addVertex("19")
            val v20 = addVertex("20")
            val v21 = addVertex("21")
            val v22 = addVertex("22")
            val v23 = addVertex("23")
            val v24 = addVertex("24")
            val v25 = addVertex("25")
            val v26 = addVertex("26")
            val v27 = addVertex("27")
            val v28 = addVertex("28")
            val v29 = addVertex("29")
            val v30 = addVertex("30")
            val v31 = addVertex("31")
            val v32 = addVertex("32")
            val v33 = addVertex("33")
            val v34 = addVertex("34")
            val v35 = addVertex("35")
            val v36 = addVertex("36")
            val v37 = addVertex("37")
            val v38 = addVertex("38")
            val v39 = addVertex("39")
            val v40 = addVertex("40")
            val v41 = addVertex("41")
            val v42 = addVertex("42")
            val v43 = addVertex("43")
            val v44 = addVertex("44")
            val v45 = addVertex("45")
            val v46 = addVertex("46")
            val v47 = addVertex("47")
            val v48 = addVertex("48")
            val v49 = addVertex("49")
            val v50 = addVertex("50")

            addConnection(v1, v2)
            addConnection(v1, v3)
            addConnection(v1, v4)
            addConnection(v1, v5)
            addConnection(v1, v6)
            addConnection(v4, v7)
            addConnection(v4, v8)
            addConnection(v4, v9)
            addConnection(v4, v10)
            addConnection(v4, v11)
            addConnection(v11, v12)
            addConnection(v11, v13)
            addConnection(v11, v14)
            addConnection(v11, v15)
            addConnection(v11, v16)
            addConnection(v15, v17)
            addConnection(v15, v18)
            addConnection(v15, v19)
            addConnection(v15, v20)
            addConnection(v15, v21)
            addConnection(v17, v22)
            addConnection(v17, v23)
            addConnection(v17, v24)
            addConnection(v17, v25)
            addConnection(v17, v26)
            addConnection(v23, v27)
            addConnection(v23, v28)
            addConnection(v23, v29)
            addConnection(v23, v30)
            addConnection(v23, v31)
            addConnection(v30, v32)
            addConnection(v30, v33)
            addConnection(v30, v34)
            addConnection(v30, v35)
            addConnection(v30, v36)
            addConnection(v33, v37)
            addConnection(v33, v38)
            addConnection(v33, v39)
            addConnection(v33, v40)
            addConnection(v33, v41)
            addConnection(v41, v42)
            addConnection(v41, v43)
            addConnection(v41, v44)
            addConnection(v41, v45)
            addConnection(v41, v46)
            addConnection(v45, v47)
            addConnection(v45, v48)
            addConnection(v45, v49)
            addConnection(v45, v50)
            addConnection(v45, v1)
            addConnection(v15, v7)
            addConnection(v15, v8)
            addConnection(v15, v9)
            addConnection(v15, v10)
            addConnection(v7, v32)
            addConnection(v7, v33)
            addConnection(v7, v34)
            addConnection(v5, v14)
            addConnection(v6, v15)
            addConnection(v10, v16)
            addConnection(v48, v17)
            addConnection(v50, v18)
            addConnection(v22, v19)
            addConnection(v13, v10)
            addConnection(v28, v21)
            addConnection(v37, v22)
            addConnection(v32, v23)
            addConnection(v44, v24)
        }.build()
        assertEquals(11, graph3.longestSimplePath().length)

        val graph4 = GraphBuilder().apply {
            val v1 = addVertex("1")
            val v2 = addVertex("2")
            addConnection(v1, v2)
        }.build()
        assertEquals(1, graph4.longestSimplePath().length)

        val graph5 = GraphBuilder().apply {
            val v1 = addVertex("1")
            val v2 = addVertex("2")
            val v3 = addVertex("3")
            val v4 = addVertex("4")
            val v5 = addVertex("5")
            val v6 = addVertex("6")
            addConnection(v1, v2)
            addConnection(v2, v3)
            addConnection(v3, v4)
            addConnection(v4, v5)
            addConnection(v5, v6)
            addConnection(v6, v1)
        }.build()
        assertEquals(5, graph5.longestSimplePath().length)
    }

    fun baldaSearcher(baldaSearcher: (String, Set<String>) -> Set<String>) {
        assertEquals(
            setOf("ТРАВА", "КРАН", "АКВА", "НАРТЫ"),
            baldaSearcher("input/balda_in1.txt", setOf("ТРАВА", "КРАН", "АКВА", "НАРТЫ", "РАК"))
        )
        assertEquals(
            setOf("БАЛДА"),
            baldaSearcher("input/balda_in2.txt", setOf("БАЛАБОЛ", "БАЛДА", "БАЛДАЗАВР"))
        )
        assertEquals(
            setOf(
                "АПЕЛЬСИН", "МАРОККО", "ПЕРЕМЕНЫ", "ГРАВИТАЦИЯ",
                "РАССУДИТЕЛЬНОСТЬ", "КОНСТАНТИНОПОЛЬ", "ПРОГРАММИРОВАНИЕ", "ПОМЕХОУСТОЙЧИВОСТЬ", "АППРОКСИМАЦИЯ",
                "ЭЙНШТЕЙН"
            ),
            baldaSearcher(
                "input/balda_in3.txt", setOf(
                    "АПЕЛЬСИН", "МАРОККО", "ЭФИОПИЯ", "ПЕРЕМЕНЫ", "ГРАВИТАЦИЯ",
                    "РАССУДИТЕЛЬНОСТЬ", "БЕЗРАССУДНОСТЬ", "КОНСТАНТИНОПОЛЬ", "СТАМБУЛ", "ПРОГРАММИРОВАНИЕ",
                    "ПРОСТРАНСТВО", "ДИАЛЕКТИКА", "КВАЛИФИКАЦИЯ", "ПОМЕХОУСТОЙЧИВОСТЬ", "КОГЕРЕНТНОСТЬ",
                    "АППРОКСИМАЦИЯ", "ИНТЕРПОЛЯЦИЯ", "МАЙЕВТИКА", "ШРЕДИНГЕР", "ЭЙНШТЕЙН"
                )
            )
        )
    }
}