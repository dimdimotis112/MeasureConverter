package converter

enum class Measurements(val abbrv: String, val singular: String, val plural: String, val conversionToBase: Double, val type: String, val extra1: String = "", val extra2: String = "") {
    // Base is meters.
    M("m", "meter", "meters", 1.0, "Length"),
    KM("km", "kilometer", "kilometers", 1000.0, "Length"),
    CM("cm", "centimeter", "centimeters", 0.01, "Length"),
    MM("mm", "millimeter", "millimeters", 0.001, "Length"),
    MI("mi", "mile", "miles", 1609.35, "Length"),
    YD("yd", "yard", "yards", 0.9144, "Length"),
    FT("ft", "foot", "feet", 0.3048, "Length"),
    IN("in", "inch", "inches", 0.0254, "Length"),

    // Base is grams
    G("g", "gram", "grams", 1.0, "Weight"),
    KG("kg", "kilogram", "kilograms", 1000.0, "Weight"),
    MG("mg", "milligram", "milligrams", 0.001, "Weight"),
    LB("lb", "pound", "pounds", 453.592, "Weight"),
    OZ("oz", "ounce", "ounces", 28.3495, "Weight"),

    // Base is ignored or set to 1, because converting each of them requires different numbers and formulas, so it won't be done here.
    C("c", "degree Celsius", "degrees Celsius", 1.0, "Temperature", "celsius", "dc"),
    F("f", "degree Fahrenheit", "degrees Fahrenheit", 1.0, "Temperature", "fahrenheit", "df"),
    K("k", "kelvin", "kelvins", 1.0, "Temperature"),

    UNKNOWN("???", "???", "???", 0.0, "Unknown");

    companion object {
        fun find(measurement: String): Measurements {
            for (unit in Measurements.values()) {
                if (measurement == unit.abbrv || measurement == unit.singular.lowercase() || measurement == unit.plural.lowercase() || measurement == unit.extra1 || measurement == unit.extra2) {
                    return unit
                }
            }
            return Measurements.UNKNOWN
        }
    }
}

fun main() {
    parseInput()
}

fun parseInput() {
    while (true) {
        // Input from the user.
        print("Enter what you want to convert (or exit): ")
        val input = readln().split(' ')

        if (input[0] == "exit") break

        for (chars in input[0]) {
            if (chars.isLetter()){
                println("Parse error")
                parseInput()
                return
            }
        }

        // Splits the users input into different sections.
        val number = input[0].toDouble()
        val measureFrom = if (input[1].lowercase().contains("degree") || input[1].contains("degrees")) "${input[1]} ${input[2]}" else input[1]
        val measureTo = if (input.size == 6) {
            "${input[4]} ${input[5]}"
        } else if ((input[3].lowercase() == "to" || input[3].lowercase() == "in") && input.size == 5) {
            "${input[4]}"
        } else if ((input[2].lowercase() == "to" || input[2].lowercase() == "in") && input.size == 5) {
            "${input[3]} ${input[4]}"
        } else {
            input[3]
        }

        convert(number, measureFrom, measureTo)
        println()
    }

}

fun convert(number: Double, firstMeasure: String, secondMeasure: String) {
    val measure1 = Measurements.find(firstMeasure.lowercase())
    val measure2 = Measurements.find(secondMeasure.lowercase())

    if (measure1.type == "Unknown" || measure2.type == "Unknown") {
        println("Conversion from ${measure1.plural} to ${measure2.plural} is impossible")
    } else if (measure1.type != measure2.type) {
        println("Conversion from ${measure1.plural} to ${measure2.plural} is impossible")
    } else if ((measure1.type == "Length" || measure2.type == "Weight") && number < 0) {
        println("${measure1.type} shouldn't be negative.")
    } else {
        var result = 0.0
        var intermediateValue = 0.0

        intermediateValue = number * measure1.conversionToBase
        result = intermediateValue / measure2.conversionToBase

        // Calculates the conversion for temperature measurements.
        if (measure1.type == "Temperature") {
            when(measure1.singular) {
                "degree Celsius" -> result = when (measure2.singular) {
                    "degree Fahrenheit" -> number * (9.0/5.0) + 32
                    "kelvin" -> number + 273.15
                    else -> number * measure1.conversionToBase
                }
                "degree Fahrenheit" -> result = when (measure2.singular) {
                    "degree Celsius" -> (number - 32) * (5.0/9.0)
                    "kelvin" -> (number + 459.67) * (5.0/9.0)
                    else -> number * measure1.conversionToBase
                }
                "kelvin" -> result = when (measure2.singular) {
                    "degree Celsius" -> number - 273.15
                    "degree Fahrenheit" -> number * (9.0/5.0) - 459.67
                    else -> number * measure1.conversionToBase
                }
            }
        }

        when (number) {
            1.0 -> when (result) {
                1.0 -> println("$number ${measure1.singular} is $result ${measure2.singular}")
                else -> println("$number ${measure1.singular} is $result ${measure2.plural}")
            }
            else -> when (result) {
                1.0 -> println("$number ${measure1.plural} is $result ${measure2.singular}")
                else -> println("$number ${measure1.plural} is $result ${measure2.plural}")
            }
        }
    }
}