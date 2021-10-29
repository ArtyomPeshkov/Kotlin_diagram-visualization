import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.*


class DiagramBuilder(private val userInp: UserInput) {
    private var mainWindow: JFrame? = null
    private var nameLabel: JLabel? = null
    private var circleDiagram: CircleDiagram? = null
    private var diagram: Diagram? = null
    private var scatter: ScatterPlot? = null
    private var backgroundColors: MutableList<Int> = mutableListOf(255,255,255)

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
        nameLabel = JLabel(userInp.name)
        nameLabel?.font = Font("TimesNewRoman", Font.PLAIN, 40)
        nameLabel?.foreground = Color.WHITE
        namePanel.add(nameLabel)
        mainContainer.add(namePanel, BorderLayout.NORTH)

        val rightPanel = createRightPanel()
        rightPanel.background = Color.lightGray
        mainContainer.add(rightPanel, BorderLayout.EAST)

        val leftPanel = createLeftPanel()
        mainContainer.add(leftPanel, BorderLayout.WEST)

        circleDiagram = CircleDiagram(userInp.data)
        circleDiagram?.background = Color(backgroundColors[0], backgroundColors[1], backgroundColors[2])
        mainContainer.add(circleDiagram)
    }

    private fun createButton(bName:String, currentBox: Box, action: () -> Unit)
    {
        val button = JButton(bName)
        currentBox.add(button)
        button.addActionListener { action() }
    }

    private fun createSlider(slider: JSlider, label: JLabel, color:Int): Box
    {
        val horizontalLayout = Box.createHorizontalBox()
        horizontalLayout.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        slider.value = 255
        slider.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
        horizontalLayout.add(slider)

        label.text = "255"
        label.maximumSize = Dimension(75, 30)
        label.border = BorderFactory.createLineBorder(Color.BLACK)
        horizontalLayout.add(label)

        slider.addChangeListener {
            backgroundColors[color] = slider.value
            label.text = slider.value.toString()
            if (mainWindow!!.contentPane.components.size == 4)
                mainWindow!!.contentPane.components.last().background =
                    Color(backgroundColors[0], backgroundColors[1], backgroundColors[2])
        }
        return horizontalLayout
    }

    private fun createLeftPanel(): Box {
        val verticalLayout = Box.createVerticalBox()
        var title = JLabel("Тип диаграммы:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(mainWindow!!.height / 40))

        createButton("Круговая диаграмма",verticalLayout,this::drawCircleDiagram)
        createButton("Диаграмма",verticalLayout,this::drawDiagram)
        if (userInp.scatterPlot)
            createButton("Диаграмма рассеяния",verticalLayout,this::drawScatter)
        verticalLayout.add(Box.createVerticalStrut(mainWindow!!.height / 20))
        createButton("Сохранить картинку",verticalLayout,this::saveImage)
        verticalLayout.add(Box.createVerticalStrut(mainWindow!!.height / 20))

        title = JLabel("Цвет фона:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(15))

        val sliderRed = JSlider(0, 255)
        val textRed = JLabel()
        verticalLayout.add(createSlider(sliderRed,textRed,0))

        val sliderGreen = JSlider(0, 255)
        val textGreen = JLabel()
        verticalLayout.add(createSlider(sliderGreen,textGreen,1))

        val sliderBlue = JSlider(0, 255)
        val textBlue = JLabel()
        verticalLayout.add(createSlider(sliderBlue,textBlue,2))

        verticalLayout.alignmentX = Component.CENTER_ALIGNMENT
        return verticalLayout
    }

    private fun createRightPanel(): JScrollPane {
        val verticalLayout = Box.createVerticalBox()
        val title = JLabel("Поля:")
        title.font = Font("Mistral", Font.BOLD, 30)
        verticalLayout.add(title)
        verticalLayout.add(Box.createVerticalStrut(15))
        var num = 1
        userInp.data.forEach {
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
        return JScrollPane(verticalLayout)
    }

    private fun drawCircleDiagram() {
        if (mainWindow!!.contentPane.components.size == 4)
            mainWindow!!.contentPane.remove(mainWindow!!.contentPane.components.last())
        circleDiagram = CircleDiagram(userInp.data)
        circleDiagram?.background = Color(backgroundColors[0], backgroundColors[1], backgroundColors[2])
        mainWindow!!.contentPane.add(circleDiagram)
        mainWindow?.isVisible = true
    }

    private fun drawDiagram() {
        if (mainWindow!!.contentPane.components.size == 4)
            mainWindow!!.contentPane.remove(mainWindow!!.contentPane.components.last())
        diagram = Diagram(userInp.data)
        diagram?.background = Color(backgroundColors[0], backgroundColors[1], backgroundColors[2])
        mainWindow!!.contentPane.add(diagram)
        mainWindow?.isVisible = true
    }

    private fun drawScatter() {
        if (mainWindow!!.contentPane.components.size == 4)
            mainWindow!!.contentPane.remove(mainWindow!!.contentPane.components.last())
        scatter = ScatterPlot(userInp.data)
        scatter?.background = Color(backgroundColors[0], backgroundColors[1], backgroundColors[2])
        mainWindow!!.contentPane.add(scatter)
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