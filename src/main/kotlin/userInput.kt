import java.io.File

fun readFile(): File {
    println("Введите имя файла:")
    var file= readLine()
    while (file == null || !File(file).exists()) {
        println("Такого файла не существует! Попробуйте снова.")
        file = readLine()
    }
    return File(file)
}

fun fieldChecker(): String {
    println("Введите поле:")
    var field = readLine()
    while (field.isNullOrBlank()) {
        println("Введите корректное поле")
        field = readLine()
    }
    return field
}

fun valueChecker(): String {
    println("Введите значение:")
    var value = readLine()
    while (value == null || value.toDoubleOrNull() == null || value.toDouble()<.0 || value.toDouble()>99999999) {
        println("Введите корректное значение (не более 99999999)")
        value = readLine()
    }
    return value
}

fun userInputData(): Pair<String, Map<String, Double>> {
    val res: MutableMap<String, Double> = mutableMapOf()
    println("Введите название диаграммы")
    var name = readLine()
    while (name.isNullOrBlank()) {
        println("Название не должно быть пустым")
        name = readLine()
    }

    println("Введите количество полей, по которым Вы хотите построить диаграмму (не более 20)")
    var number = readLine()
    while (number == null || number.toIntOrNull() == null || number.toInt() < 0 || number.toInt() > 20) {
        println("Введите корректное число")
        number = readLine()
    }

    println("Введите непустые поля и числовые значения для них")
    repeat(number.toInt())
    {
        val field = fieldChecker()
        val value = valueChecker()
        res[field] = value.toDouble()
    }
    return Pair(name, res)
}

fun processFileString(input: String,stringNumber:Int): Pair<String, Double> {
    var userInput: String? = input
    while (userInput == null || userInput.split(';').size != 2) {
        println("Что-то пошло не так, строка $stringNumber не соответствует формату: 'поле';'число'")
        println("Введите правильную строку вручную или завершите выполнение программы и исправьте файл")
        userInput = readLine()
    }
    var field: String? = userInput.split(';')[0]
    if (field.isNullOrBlank()) {
        println("Обнаружено пустое имя поля в $stringNumber строке, введите новое имя или закройте программу и проверьте файл")
        field = fieldChecker()
    }

    var value: String? = userInput.split(';')[1]

    if (value == null || value.toDoubleOrNull() == null ||  value.toDouble()<.0 || value.toDouble()>99999999) {
        println("Ошибка в значении поля($field) ($stringNumber строка), введите новое значение или закройте программу и проверьте файл")
        value = valueChecker()
    }
    return Pair(field, value.toDouble())

}


fun processFile(): Pair<String, Map<String, Double>> {
    var userInputFile = readFile()
    var probablyBadInput  = userInputFile.readLines()
    var fileStrings:MutableList<String> = mutableListOf()
    probablyBadInput.forEachIndexed { index, s ->
        if (index==0 || s.isNotBlank())
            fileStrings.add(probablyBadInput[index])
    }
    while (fileStrings.size>21)
    {
        println("В вашем файле слишком много строк, строк должно быть не более 21 (название + 20 полей), попробуйте другой файл или исправьте ошибку")
        userInputFile = readFile()
        probablyBadInput  = userInputFile.readLines()
        fileStrings = mutableListOf()
        probablyBadInput.forEachIndexed { index, s ->
            if (index==0 || s.isNotBlank())
                fileStrings.add(probablyBadInput[index])
        }

    }
    var name: String? = fileStrings[0]
    while (name.isNullOrBlank()) {
        println("Что-то пошло не так, название пустое")
        println("Введите правильное название или завершите выполнение программы и исправьте файл")
        name = readLine()
    }
    val res:MutableMap<String,Double> = mutableMapOf()
    fileStrings.drop(1).forEachIndexed{index,it ->
        val stringOfFile = processFileString(it,index+1)
        res[stringOfFile.first]=stringOfFile.second
    }
    return Pair(name,res)
}

fun inputData(): Pair<String, Map<String, Double>> {
    println("Диаграмма строится по списку параметров и значений для них.")
    println("Если Вы хотите считывать данные из файла, напишите 'file', если же Вы хотите прописать их вручную - 'myInp'.")
    while (true) {
        when (readLine()?.trim() ?: "") {
            "file", "File", "FILE" -> {
                println("Файл должен иметь следующий вид:")
                println("Первая строка - название(любая непустая строка)")
                println("Каждая следующая строка должна иметь вид: название поля(непустая строка) и значение для этого поля(число), записанные без пробелов через точку с запятой(;).")
                return processFile()
            }
            "myInp", "MyInp", "MYINP" -> {
                return userInputData()
            }
            else -> println("Неизвестная команда")
        }
    }

}