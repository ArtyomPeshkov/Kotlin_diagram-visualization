import java.io.File

data class UserInput(val name: String, val data: Map<String, List<Double>>, val scatterPlot: Boolean)

/**
@brief
Функция проверки существования файла
@return
Возвращает файл с входными данными
 */
fun readFile(): File {
    println("Введите имя файла:")
    var file = readLine()
    while (file == null || !File(file).exists()) {
        println("Такого файла не существует! Попробуйте снова.")
        file = readLine()
    }
    return File(file)
}

/**
@brief
Проверка на то, что поле не пустое
@return
Непустое поле
 */
fun fieldChecker(): String {
    println("Введите поле:")
    var field = readLine()
    while (field.isNullOrBlank()) {
        println("Введите корректное поле")
        field = readLine()
    }
    return field
}

/**
@brief
Проверка на то, что значение не пустое
@return
Возвращает непустое значение
 */
fun valueChecker(): String {
    println("Введите значение:")
    var value = readLine()
    while (value == null || value.toDoubleOrNull() == null || value.toDouble() < .0 || value.toDouble() > 99999999) {
        println("Введите корректное значение (не более 99999999)")
        value = readLine()
    }
    return value
}

/**
@brief
Функция ручного ввода данных
@detailed
Функция запрашивает название диаграмм, поля и значения для них и, чтобы потом передать их для построения диаграмм
@return
Функция возвращает имя диаграммы, данные для построения и информацию о том, можно ли построить диаграмму рассеяния
 */
fun userInputData(): UserInput {
    val res: MutableMap<String, MutableList<Double>> = mutableMapOf()
    var scatterPlot = true
    println("Введите название диаграммы")
    var name = readLine()
    while (name.isNullOrBlank()) {
        println("Название не должно быть пустым")
        name = readLine()
    }

    println("Введите количество полей, по которым Вы хотите построить диаграмму (не более 100)")
    var number = readLine()
    while (number == null || number.toIntOrNull() == null || number.toInt() < 0 || number.toInt() > 100) {
        println("Введите корректное число")
        number = readLine()
    }

    println("Введите непустые поля и числовые значения для них")
    repeat(number.toInt())
    {
        val field = fieldChecker()
        if (field.toDoubleOrNull() == null || field.toDouble().toLong() > 99999999)
            scatterPlot = false
        if (!res.containsKey(field))
            res[field] = mutableListOf()
        val value = valueChecker()
        res[field]?.add("%.2f".format(value.toFloat()).replace(',','.').toDouble())
    }
    return UserInput(name, res, scatterPlot)
}

data class OneFileString(val field: String, val value: Double)

/**
@brief
Функция обработки строк файла
@detailed
Функция получает 2 соседние строки (поле и значение) и проверяет их корректность, после чего возвращает их или выдаёт ошибку
с указанием строки, где произошёл
@param
Пара соседних строк файла и их номера
@return
Функция возвращает проверенные поле и значение
 */
fun processFileString(input: Pair<String, String>, stringField: Int, stringValue: Int): OneFileString {
    var userInput: Pair<String, String>? = input
    while (userInput == null) {
        println("Что-то пошло не так, в строках $stringField и $stringValue не соответствует формату")
        println("Введите правильные строки вручную или завершите выполнение программы и исправьте файл")
        val field = fieldChecker()
        val value = valueChecker()
        userInput = Pair(field, value)
    }
    var field: String? = userInput.first
    if (field.isNullOrBlank()) {
        println("Обнаружено пустое имя поля в $stringField строке, введите новое имя или закройте программу и проверьте файл")
        field = fieldChecker()
    }

    var value: String? = userInput.second
    if (value == null || value.toDoubleOrNull() == null || value.toDouble() < .0 || value.toDouble() > 99999999) {
        println("Ошибка в значении поля $field ($stringValue строка), введите новое значение или закройте программу и проверьте файл")
        value = valueChecker()
    }
    return OneFileString(field, "%.2f".format(value.toFloat()).replace(',','.').toDouble())

}

/**
@brief
Функция обработки файла с входными данными
@detailed
Функция работает с файлом пользователя, проверяя, что в нём не слишком много строк, считывает имя диаграмм и проверяет
возможность построения диаграммы рассеяния
@return
Функция возвращает имя файла, значения и информацию о возможности построения диаграммы рассеяния
 */
fun processFile(): UserInput {
    var userInputFile = readFile()
    var scatterPlot = true
    var probablyBadInput = userInputFile.readLines()
    var fileStrings: MutableList<String> = mutableListOf()
    probablyBadInput.forEachIndexed { index, s ->
        if (index == 0 || s.isNotBlank())
            fileStrings.add(probablyBadInput[index])
    }
    while (fileStrings.size > 201 || fileStrings.size%2==0) {
        println("В вашем файле слишком много строк или не для всех полей есть значение, количество строк должно быть нечётным(с учётом названия) числом не более 201 (название + 100 полей и 100 значений), попробуйте другой файл")
        userInputFile = readFile()
        probablyBadInput = userInputFile.readLines()
        fileStrings = mutableListOf()
        probablyBadInput.forEachIndexed { index, s ->
            if (index == 0 || s.isNotBlank())
                fileStrings.add(probablyBadInput[index])
        }

    }
    var name: String? = fileStrings[0]
    while (name.isNullOrBlank()) {
        println("Что-то пошло не так, название пустое")
        println("Введите правильное название или завершите выполнение программы и исправьте файл")
        name = readLine()
    }
    val res: MutableMap<String, MutableList<Double>> = mutableMapOf()
    repeat(fileStrings.drop(1).size) { index ->
        if (index % 2 == 0) {
            val stringOfFile = processFileString(Pair(fileStrings[index+1], fileStrings[index + 2]), index + 1, index + 2)
            if (stringOfFile.field.toDoubleOrNull() == null || stringOfFile.field.toDouble().toLong() > 99999999)
                scatterPlot = false
            if (!res.containsKey(stringOfFile.field))
                res[stringOfFile.field] = mutableListOf()
            res[stringOfFile.field]?.add(stringOfFile.value)
        }
    }
    return UserInput(name, res, scatterPlot)
}

/**
@brief
Функция обработки пользовательских команд
@return
Функция возвращает имя файла, значения и информацию о возможности построения диаграммы рассеяния
 */
fun inputData(): UserInput {
    println("Диаграмма строится по списку параметров и значений для них.")
    println("Если Вы хотите считывать данные из файла, напишите 'file', если же Вы хотите прописать их вручную - 'myInp'.")
    while (true) {
        when (readLine()?.trim() ?: "") {
            "file", "File", "FILE" -> {
                println("Файл должен иметь следующий вид:")
                println("Первая строка - название(любая непустая строка)")
                println("Каждая следующая строка должна иметь вид: название категории(непустая строка) и значение для этого категории(число), записанные без пробелов через точку с запятой(;).")
                return processFile()
            }
            "myInp", "MyInp", "MYINP" -> {
                return userInputData()
            }
            else -> println("Неизвестная команда")
        }
    }

}