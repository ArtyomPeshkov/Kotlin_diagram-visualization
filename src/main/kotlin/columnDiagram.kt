import java.awt.*
import java.awt.geom.Rectangle2D
import javax.swing.JPanel
import kotlin.math.pow
import kotlin.math.sign

class Diagram(private val data: Map<String,List<Double>>) : JPanel() {
    private var extraSpaceX = 20
    private var extraSpaceY = 20
    private var extraSpace = 20
    private val notchLen = 5
    private val numberOfNotchesX = data.size
    private var numberOfNotchesY = 10

    private fun maxOfData(): Double {
        return data.values.maxOf{ it.sum() }
    }

    /**
    @brief
    Функция считает высоты столбцов диаграммы
     */
    private fun colHeightGenerator(): List<Double> {
        val columns: MutableList<Double> = mutableListOf()
        columns.addAll(List(data.values.size){
            data.values.elementAt(it).sum()
        })
        return columns
    }

    /**
    @brief
    Функция вывзывает все методы необходимые для рисования
     */
    override fun paint(g: Graphics) {
        super.paint(g)
        extraSpace = 20
        extraSpaceX = (maxOfData().toString().length-1)*g.font.size
        extraSpaceY = height/extraSpace
        drawAxis(g as Graphics2D)
        drawNotches(g)
        drawColumns(g)
    }

    /**
    @brief
    Функция отрисовывает оси
     */
    private fun drawAxis(g: Graphics2D) {
        g.stroke = BasicStroke(3F)
        g.color = Color.BLACK
        g.drawLine(extraSpaceX, 0, extraSpaceX, height - extraSpaceY)
        g.drawLine(extraSpaceX, height - extraSpaceY, width, height - extraSpaceY)
    }

    /**
    @brief
    Функция рисует засечки на осях в зависимости от размера диапазона и количества полей
     */
    private fun drawNotches(g: Graphics2D) {
        val maxUserValue = maxOfData()
        val tail = 10.0.pow((maxUserValue.toInt().toString().length - 2).toDouble()).toInt()
        val maxNotchValue: Int
        if (maxUserValue >= 100)
            maxNotchValue = (maxUserValue.toInt() / tail + 1) * tail
        else {
            maxNotchValue = maxUserValue.toInt() + 5 - maxUserValue.toInt() % 5
            numberOfNotchesY = 5
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
        repeat(numberOfNotchesX) {
            val xx = extraSpaceX + (it + 1) * (width - extraSpaceX) / (numberOfNotchesX)
            g.stroke = BasicStroke(2F)
            g.drawLine(
                xx,
                height - extraSpaceY + notchLen,
                xx,
                height - extraSpaceY - notchLen
            )
        }
    }

    /**
    @brief
    Функция рисует столбики диаграммы и подписывает их под осью
     */
    private fun drawColumns(g: Graphics2D) {
        var i = 0
        val maxNotchValue: Int
        val maxUserValue = maxOfData()
        val tail = 10.0.pow((maxUserValue.toInt().toString().length - 2).toDouble()).toInt()
        maxNotchValue = if (maxUserValue >= 100)
            (maxUserValue.toInt() / tail + 1) * tail
        else
            maxUserValue.toInt() + 5 - maxUserValue.toInt() % 5

        colHeightGenerator().forEach {
            val oneNotchLen = (width - extraSpaceX).toFloat() / numberOfNotchesX
            val columnHeight = (height - extraSpaceY).toFloat() * it.toFloat() / maxNotchValue
            val xx = extraSpaceX + i * oneNotchLen
            val col = Rectangle2D.Float(
                xx + oneNotchLen/3,
                height.toFloat() - extraSpaceY - columnHeight,
                oneNotchLen/3,
                columnHeight
            )
            g.stroke = BasicStroke(4F)
            g.draw(col)
            g.stroke = BasicStroke(2F)
            g.color = Color.RED
            g.fill(col)
            g.color = Color.BLACK
            g.font = Font("Mistral", Font.PLAIN, width/(50+(numberOfNotchesX+1)/2))
            g.drawString(
                (++i).toString(),
                xx + oneNotchLen/3,
                height.toFloat()-extraSpaceY.toFloat()+extraSpaceY.toFloat()/2
            )
        }
    }
}
