import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.pow
import kotlin.math.sign

class ScatterPlot(private val data: Map<String, List<Double>>) : JPanel() {
    private var extraSpaceX = 20
    private var extraSpaceY = 20
    private var extraSpace = 20
    private var dataForScatter = mutableMapOf<Double, List<Double>>()

    private fun maxOfKeys(): Double {
        return dataForScatter.keys.maxOrNull() ?: -.0
    }

    private fun maxOfValues(): Double {
        return dataForScatter.values.maxOf { it.maxOrNull() ?: -.0 }
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        data.forEach {
            dataForScatter[it.key.toDouble()] = it.value
        }
        extraSpace = 20
        extraSpaceX = (maxOfValues().toString().length-1)*g.font.size
        extraSpaceY = height / extraSpace
        drawAxis(g as Graphics2D)
        drawNotches(g)
        drawPoints(g)
    }


    private fun drawAxis(g: Graphics2D) {
        g.stroke = BasicStroke(3F)
        g.color = Color.BLACK
        g.drawLine(extraSpaceX, 0, extraSpaceX, height - extraSpaceY)
        g.drawLine(extraSpaceX, height - extraSpaceY, width, height - extraSpaceY)
    }

    private fun maxNotch(maxUV:Double, curTail: Int):Int{
        return if (maxUV >= 100)
            (maxUV.toInt() / curTail + 1) * curTail
        else {
            maxUV.toInt() + 5 - maxUV.toInt() % 5
        }
    }

    private fun drawNotches(g: Graphics2D) {
        val notchLen = 5
        var numberOfNotchesX = 10
        var numberOfNotchesY = 10
        val maxUserKey = maxOfKeys()
        val maxUserValue = maxOfValues()
        if (maxUserKey<100)
            numberOfNotchesX=5
        if (maxUserValue<100)
            numberOfNotchesY=5


        var tail = 10.0.pow((maxUserValue.toInt().toString().length - 2).toDouble()).toInt()
        val maxNotchValue: Int=maxNotch(maxUserValue,tail)
        tail = 10.0.pow((maxUserKey.toInt().toString().length - 2).toDouble()).toInt()
        val maxNotchKey: Int=maxNotch(maxUserKey,tail)

        g.color = Color.BLACK
        repeat(numberOfNotchesY)
        {
            val hh = height - extraSpaceY - (it + 1) * (height - extraSpaceY) / numberOfNotchesY
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
            val fontSize = width.toFloat() / (50 + (numberOfNotchesX.toFloat() + 1) / 2)
            g.stroke = BasicStroke(fontSize)
            val number: Long = maxNotchKey.toLong() * (it + 1) / (numberOfNotchesX)
            g.drawString(
                number.toString(),
                xx - ((number.toString().length + 1) * font.size) / 2,
                height - (extraSpaceY - notchLen) / 2
            )
            g.stroke = BasicStroke(2F)
        }
    }

    private fun coordinateGenerator(): List<Pair<Double, Double>> {
        val columns: MutableList<Pair<Double, Double>> = mutableListOf()
        dataForScatter.forEach { elem ->
            elem.value.forEach {
                columns.add(Pair(elem.key, it))
            }
        }
        return columns
    }

    private fun drawPoints(g: Graphics2D) {
        val maxNotchValue: Int
        val maxNotchKey: Int
        val maxUserValue = maxOfValues()
        val maxUserKey = maxOfKeys()
        var tail = 10.0.pow((maxUserValue.toInt().toString().length - 2).toDouble()).toInt()
        maxNotchValue = maxNotch(maxUserValue,tail)
        tail = 10.0.pow((maxUserKey.toInt().toString().length - 2).toDouble()).toInt()
        maxNotchKey =maxNotch(maxUserKey,tail)


        coordinateGenerator().forEach {
            val xCoordinate = (width - extraSpaceX).toFloat() * it.first.toFloat() / maxNotchKey
            val yCoordinate = (height - extraSpaceY).toFloat() * it.second.toFloat() / maxNotchValue
            val point = Rectangle2D.Float(
                xCoordinate + extraSpaceX,
                height.toFloat() - extraSpaceY - yCoordinate,
                4F, 4F
            )
            g.stroke = BasicStroke(2F)
            g.draw(point)
            g.color = Color.RED
            g.fill(point)
            g.color = Color.BLACK
        }
    }

}
