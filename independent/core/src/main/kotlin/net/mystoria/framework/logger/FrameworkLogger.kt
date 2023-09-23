package net.mystoria.framework.logger

import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.Logger

class FrameworkLogger : Logger("Framework", null) {
    init {
        val consoleHandler = ConsoleHandler()
        consoleHandler.level = Level.ALL // Set the log level
        consoleHandler.formatter = CustomFormatter() // Use a custom formatter
        this.addHandler(consoleHandler)
    }
}

class CustomFormatter : Formatter() {
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    override fun format(record: java.util.logging.LogRecord): String {
        val timestamp = dateFormat.format(Date(record.millis))
        return "[$timestamp] [${record.level}] ${record.message}\n"
    }
}