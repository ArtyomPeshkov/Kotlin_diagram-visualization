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


fun rewriteFile(newFile: Collection<String>,destinationFile: File)
{
    destinationFile.writeText("")
    destinationFile.appendText("Name\n")
    newFile.forEach {
        destinationFile.appendText(it + "\n")
    }
}

fun getRandomString(difChars: Int, length: Int): String {
    val allowedChars = ('a' until 'a' + difChars)
    val nums = ('1' until '9')
    var str = (1..length).map { allowedChars.random() }.joinToString("")
    str+=';'
    str+=(1..3).map { nums.random() }.joinToString("")
    return str
}


fun randomChanger() {
    val strNumber = 100
    val strLength = 6
    val difChars = 26

    val newFile :MutableList<String> = mutableListOf()
    repeat(strNumber)
    {
        newFile.add(getRandomString(difChars, strLength))
    }
    rewriteFile(newFile, File("inp.txt"))
}

fun main(args: Array<String>) {
    randomChanger()
    val userInput = inputData()
    val app = DiagramBuilder(userInput)
    app.show()
}
