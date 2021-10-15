import java.awt.*
import java.awt.geom.Arc2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.lang.Double.max
import java.lang.Math.PI
import javax.imageio.ImageIO
import javax.swing.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

fun main(args: Array<String>) {
    val userInput = inputData()
    val app = DiagramBuilder(userInput)
    app.show()
}



class CircleDiagram(private val data: Pair<String, Map<String, Double>>) : JPanel() {
    private var extraSpace = 5f

    private fun sumOfData(): Double {
        var sum = .0
        data.second.forEach {
            sum += it.value
        }
        return sum
    }

    private fun angleListGenerator(): List<Double> {
        val angles: MutableList<Double> = mutableListOf()
        data.second.forEach {
            angles.add(it.value * 360 / sumOfData())
        }
        return angles
    }

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
        angleListGenerator().forEachIndexed { index, angle ->
            drawOneArc(g, currentStart, angle, colors[index % colors.size])
            currentStart += angle
        }
        currentStart = 270.0
        angleListGenerator().forEachIndexed { index, angle ->
            drawFields(g, currentStart, angle, index + 1)
            currentStart += angle
        }

    }

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

    private fun drawFields(g: Graphics2D, currentStart: Double, angle: Double,fieldNum: Int = 1){
        g.color = Color.BLACK
        g.font = Font("Mistral", Font.PLAIN, 30)
        val angleForFieldShower = (((currentStart - 270 + angle / 2) / 180) * PI).toFloat()
        g.drawString(
            fieldNum.toString(),
            width / 2 - extraSpace / 2 + (width / 2 - 5 * extraSpace) * sin(angleForFieldShower),
            height / 2 + extraSpace / 2 - (height / 2 - 5 * extraSpace) * cos(angleForFieldShower)
        )
    }
}

class DiagramBuilder(private val data: Pair<String, Map<String, Double>>) {
    private var mainWindow: JFrame? = null
    private var nameLabel: JLabel? = null
    private var circleDiagram: CircleDiagram? = null
    private var backgrColorRed: Int = 255
    private var backgrColorGreen: Int = 255
    private var backgrColorBlue: Int = 255

    init {
        createWindow()
        initialiseLayout()
    }

    private fun createWindow() {
        mainWindow = JFrame("Diagram builder")
        mainWindow?.setSize(1300, 650)
        mainWindow?.minimumSize = Dimension(1300, 650)
        mainWindow?.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    }

    fun show() {
        mainWindow?.isVisible = true
    }

    private fun initialiseLayout() {
        val mainContainer = mainWindow!!.contentPane
        mainContainer.layout = BorderLayout()

        val namePanel = JPanel()
        namePanel.background = Color.GRAY
        nameLabel = JLabel(data.first)
        nameLabel?.font = Font("TimesNewRoman", Font.PLAIN, 40)
        nameLabel?.foreground = Color.WHITE
        namePanel.add(nameLabel)
        mainContainer.add(namePanel, BorderLayout.NORTH)

        val rightPanel = createRightPanel()
        rightPanel.background = Color.lightGray
        mainContainer.add(rightPanel, BorderLayout.EAST)

        val leftPanel = createLeftPanel()
        mainContainer.add(leftPanel, BorderLayout.WEST)

        circleDiagram = CircleDiagram(data)
        circleDiagram?.background = Color(backgrColorRed, backgrColorGreen, backgrColorBlue)
        mainContainer.add(circleDiagram)
    }

    private fun createLeftPanel(): Box {
        val verticalLayout = Box.createVerticalBox()
        var title = JLabel("Тип диаграммы:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(mainWindow!!.height / 40))


        val buttonCircleDiagram = JButton("Круговая диаграмма")
        verticalLayout.add(buttonCircleDiagram)
        buttonCircleDiagram.addActionListener { drawCircleDiagram() }

        val buttonSave = JButton("Сохранить картинку")
        verticalLayout.add(buttonSave)
        buttonSave.addActionListener { saveImage() }
        verticalLayout.add(Box.createVerticalStrut(mainWindow!!.height / 20))

        title = JLabel("Цвет фона:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(15))

        val horizontalLayoutRed = Box.createHorizontalBox()
        horizontalLayoutRed.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val sliderRed = JSlider(0, 255)
        sliderRed.value = 255
        sliderRed.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        horizontalLayoutRed.add(sliderRed)

        val textRed = JLabel()
        textRed.text = "255"
        textRed.maximumSize = Dimension(75, 30)
        textRed.border = BorderFactory.createLineBorder(Color.BLACK)
        horizontalLayoutRed.add(textRed)

        sliderRed.addChangeListener {
            backgrColorRed = sliderRed.value
            textRed.text = sliderRed.value.toString()
            if (mainWindow!!.contentPane.components.size == 4)
                mainWindow!!.contentPane.components.last().background =
                    Color(backgrColorRed, backgrColorGreen, backgrColorBlue)
        }
        verticalLayout.add(horizontalLayoutRed)

        val horizontalLayoutGreen = Box.createHorizontalBox()
        horizontalLayoutGreen.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val sliderGreen = JSlider(0, 255)
        sliderGreen.value = 255
        sliderGreen.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        horizontalLayoutGreen.add(sliderGreen)

        val textGreen = JLabel()
        textGreen.text = "255"
        textGreen.maximumSize = Dimension(75, 30)
        textGreen.border = BorderFactory.createLineBorder(Color.BLACK)
        horizontalLayoutGreen.add(textGreen)
        sliderGreen.addChangeListener {
            backgrColorGreen = sliderGreen.value
            textGreen.text = sliderGreen.value.toString()
            if (mainWindow!!.contentPane.components.size == 4)
                mainWindow!!.contentPane.components.last().background =
                    Color(backgrColorRed, backgrColorGreen, backgrColorBlue)
        }
        verticalLayout.add(horizontalLayoutGreen)

        val horizontalLayoutBlue = Box.createHorizontalBox()
        horizontalLayoutBlue.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val sliderBlue = JSlider(0, 255)
        sliderBlue.value = 255
        sliderBlue.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        horizontalLayoutBlue.add(sliderBlue)

        val textBlue = JLabel()
        textBlue.text = "255"
        textBlue.maximumSize = Dimension(75, 30)
        textBlue.border = BorderFactory.createLineBorder(Color.BLACK)
        horizontalLayoutBlue.add(textBlue)

        sliderBlue.addChangeListener {
            backgrColorBlue = sliderBlue.value
            textBlue.text = sliderBlue.value.toString()
            if (mainWindow!!.contentPane.components.size == 4)
                mainWindow!!.contentPane.components.last().background =
                    Color(backgrColorRed, backgrColorGreen, backgrColorBlue)
        }
        verticalLayout.add(horizontalLayoutBlue)
        verticalLayout.alignmentX = Component.CENTER_ALIGNMENT
        return verticalLayout
    }

    private fun createRightPanel(): Box {
        val verticalLayout = Box.createVerticalBox()
        val title = JLabel("Поля:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(15))
        var num = 1
        data.second.forEach {
            val horizontalLayout = Box.createHorizontalBox()
            val fields = JLabel("Поле №1:")
            fields.font = Font("TimesNewRoman", Font.BOLD, 25)
            horizontalLayout.add(JLabel("Поле №${num++}:"))
            val fieldTextLabel = JLabel()
            val fieldString = if (it.key.length > 30)
                it.key.substring(0, 19) + "..."
            else
                it.key
            fieldTextLabel.font = Font("PTMono", Font.BOLD, 15)
            fieldTextLabel.text = " $fieldString"
            fieldTextLabel.border = BorderFactory.createLineBorder(Color.BLACK)
            fieldTextLabel.maximumSize = Dimension(300, 30)
            horizontalLayout.border = BorderFactory.createEmptyBorder(1, 10, 1, 10)
            horizontalLayout.alignmentX = Component.LEFT_ALIGNMENT
            horizontalLayout.add(fieldTextLabel)
            verticalLayout.add(horizontalLayout)
            verticalLayout.add(Box.createVerticalStrut(1))
        }
        return verticalLayout
    }

    private fun drawCircleDiagram() {
        if (mainWindow!!.contentPane.components.size == 4)
            mainWindow!!.contentPane.remove(mainWindow!!.contentPane.components.last())
        circleDiagram = CircleDiagram(data)
        circleDiagram?.background = Color(backgrColorRed, backgrColorGreen, backgrColorBlue)
        mainWindow!!.contentPane.add(circleDiagram)
        mainWindow?.isVisible = true
    }


    private fun getScreenShot(component: Component): BufferedImage? {
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gd = ge.defaultScreenDevice
        val robot = Robot(gd)
        val bounds = Rectangle(component.locationOnScreen, component.size)
        return robot.createScreenCapture(bounds)
    }

    private fun saveImage(){
        try {
            val bi = getScreenShot(mainWindow!!.contentPane)
            File("saves/saved.png").mkdirs()
            File("saves/saved.png").createNewFile()
            val outfile = File("saves/saved.png")
            ImageIO.write(bi, "png", outfile)
        } catch (ie: IOException) {
            ie.printStackTrace()
        }
    }

}