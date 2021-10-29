import java.awt.*
import java.awt.geom.Arc2D
import javax.swing.JPanel
import kotlin.math.cos
import kotlin.math.sin

class CircleDiagram(private val data: Map<String,List<Double>>) : JPanel() {
    private var extraSpace = 5f

    private fun sumOfData(): Double {
        return data.values.sumOf{it.sum()}
    }

    /**
    @brief
    Функция генерирует список углов, для построения секторов в круговой диаграмме
     */
    private fun angleListGenerator(): List<Double> {
        val angles: MutableList<Double> = mutableListOf()
        data.forEach {
            angles.add(it.value.sum() * 360 / sumOfData())
        }
        return angles
    }

    /**
    @brief
    Функция вывзывает все методы необходимые для рисования, передавая в них нужные параметры: цвета, углы поворота.
     */
    override fun paint(g: Graphics) {
        super.paint(g)
        g as Graphics2D
        val colors = listOf(
            Color(0, 255, 255),
            Color(255, 255, 0),
            Color(255, 0, 0),
            Color(255, 0, 255),
            Color(0, 0, 255),
            Color(255, 140, 0),
            Color(255, 99, 71)
        )
        var currentStart = 270.0
        val angleList=angleListGenerator()
        angleList.forEachIndexed { index, angle ->
            drawOneArc(g, currentStart, angle, colors[index % colors.size])
            if (index==angleList.size-1)
                drawOneArc(g, currentStart, angle, Color(138,43,226))
            currentStart += angle
        }
        currentStart = 270.0
        angleListGenerator().forEachIndexed { index, angle ->
            drawFields(g, currentStart, angle, index + 1)
            currentStart += angle
        }

    }
    /**
    @brief
    Функция рисует 1 сектор круговой диаграммы по стартовому углу и углу поворота, заполняя его нужным цветом
     */
    private fun drawOneArc(g: Graphics2D, currentStart: Double, angle: Double, color: Color) {
        g.color = Color.BLACK
        g.stroke = BasicStroke(3f)
        val arc = Arc2D.Double(
            0.0 + extraSpace,
            0.0 + extraSpace,
            width.toDouble() - 2 * extraSpace,
            height.toDouble() - 2 * extraSpace,
            -currentStart,
            -angle,
            Arc2D.PIE
        )
        g.draw(arc)
        g.color = color
        g.fill(arc)
    }

    /**
    @brief
    Функция рисует подпись для одного из секторов круговой диаграммы по стартовому углу и углу поворота, заполняя его нужным цветом
     */
    private fun drawFields(g: Graphics2D, currentStart: Double, angle: Double, fieldNum: Int = 1){
        g.color = Color.BLACK
        g.font = Font("Mistral", Font.PLAIN, width/(50+(angleListGenerator().size+1)/2))
        val angleForFieldShower = (((currentStart - 270 + angle / 2) / 180) * Math.PI).toFloat()
        g.drawString(
            fieldNum.toString(),
            width / 2 - extraSpace / 2 + (width / 2 - 5 * extraSpace) * sin(angleForFieldShower),
            height / 2 + extraSpace / 2 - (height / 2 - 5 * extraSpace) * cos(angleForFieldShower)
        )
    }
}
