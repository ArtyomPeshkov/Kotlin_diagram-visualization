import java.io.File
/**
@brief
Функция перезаписи файла
 */
fun rewriteFile(newFile: Collection<String>,destinationFile: File)
{
    destinationFile.writeText("")
    destinationFile.appendText("Name\n")
    newFile.forEach {
        destinationFile.appendText(it + "\n")
    }
}
/**
@brief
Функция генерации рандомных значений для файла
 */
fun getRandomString(): Pair<String,String> {
    val nums = ('1' until '9')
    val strField=(1..3).map { nums.random() }.joinToString("")
    val strVal=(1..3).map { nums.random() }.joinToString("")
    return Pair(strField,strVal)
}

/**
@brief
Генератор рандомного inp.txt
 */
fun randomChanger(strNumber:Int) {

    val newFile :MutableList<String> = mutableListOf()
    repeat(strNumber)
    {
        val newStr=getRandomString()
        newFile.add(newStr.first)
        newFile.add(newStr.second)
    }
    File("inp.txt").createNewFile()
    rewriteFile(newFile, File("inp.txt"))
}

fun main(args: Array<String>) {
    println("Добро пожаловать в построитель диаграмм.")
    println("Для вас будет сгенерирован тестовый файл inp.txt, чтобы вы могли проверить работоспособность программы")

    randomChanger((2..100).random())
    val userInput = inputData()
    val app = DiagramBuilder(userInput)
    app.show()
}
