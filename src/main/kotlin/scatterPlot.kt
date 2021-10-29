import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.pow
import kotlin.math.sign

class ScatterPlot(private val data: Map<String,Double>) : JPanel() {
    private var extraSpaceX = 20
    private var extraSpaceY = 20
    private var extraSpace = 20
    private var dataForScatter = mutableMapOf<Double,Double>()

    private fun maxOfKeys(): Double {
        return dataForScatter.keys.maxOrNull() ?: -.0
    }

    private fun maxOfValues(): Double {
        return dataForScatter.values.maxOrNull() ?: -.0
    }
    private fun colHeightGenerator(): List<Double> {
        val columns: MutableList<Double> = mutableListOf()
        columns.addAll(data.values)
        return columns
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        data.forEach{
            dataForScatter[it.key.toDouble()] = it.value
        }
        extraSpace = 20
        extraSpaceX = maxOfValues().toInt().toString().length*g.font.size
        extraSpaceY = height/extraSpace
        drawAxis(g as Graphics2D)
        drawNotches(g)
        //drawColumns(g)
    }


    private fun drawAxis(g: Graphics2D) {
        g.stroke = BasicStroke(3F)
        g.color = Color.BLACK
        g.drawLine(extraSpaceX, 0, extraSpaceX, height - extraSpaceY)
        g.drawLine(extraSpaceX, height - extraSpaceY, width, height - extraSpaceY)
    }

    private fun drawNotches(g: Graphics2D) {
        val notchLen = 5
        var numberOfNotchesX = 10
        var numberOfNotchesY = 10
        val maxUserKey = maxOfKeys()
        val maxUserValue = maxOfValues()

        var tail = 10.0.pow((maxUserValue.toString().length - 4).toDouble()).toInt()
        val maxNotchValue: Int
        if (maxUserValue >= 100)
            maxNotchValue = (maxUserValue.toInt() / tail + 1) * tail
        else {
            maxNotchValue = maxUserValue.toInt() + 5 - maxUserValue.toInt() % 5
            numberOfNotchesY = 5
        }
        tail = 10.0.pow((maxUserKey.toString().length - 4).toDouble()).toInt()
        val maxNotchKey: Int
        if (maxUserKey >= 100)
            maxNotchKey = (maxUserKey.toInt() / tail + 1) * tail
        else {
            maxNotchKey = maxUserKey.toInt() + 5 - maxUserKey.toInt() % 5
            numberOfNotchesX = 5
        }

        g.color = Color.BLACK
        repeat(numberOfNotchesY)
        {
            val hh=height - extraSpaceY - (it + 1) * (height - extraSpaceY) / numberOfNotchesY
            g.drawLine(
                extraSpaceX - notchLen,
                hh,
                extraSpaceX + notchLen,
                hh
            )
            val fontSize = width.toFloat() / 200
            g.stroke = BasicStroke(fontSize)
            val number = maxNotchValue * (it + 1) / (numberOfNotchesY)
            g.drawString(
                number.toString(),
                1,
                hh + (height - extraSpaceY) / numberOfNotchesY / 4
            )
            g.stroke = BasicStroke(2F)
        }
        g.color = Color.BLACK
        repeat(numberOfNotchesX) {
            val xx = extraSpaceX + (it + 1) * (width - extraSpaceX) / (numberOfNotchesX)
            g.stroke = BasicStroke(2F)
            g.drawLine(
                xx,
                height - extraSpaceY + notchLen,
                xx,
                height - extraSpaceY - notchLen
            )
            val fontSize = width.toFloat() / 200
            g.stroke = BasicStroke(fontSize)
            val number = maxNotchKey * (it + 1) / (numberOfNotchesX)
            g.drawString(
                number.toString(),
                xx + (width - extraSpaceX) / numberOfNotchesX / 4,
                height-extraSpaceY+ notchLen
            )
            g.stroke = BasicStroke(2F)
        }
    }


}
