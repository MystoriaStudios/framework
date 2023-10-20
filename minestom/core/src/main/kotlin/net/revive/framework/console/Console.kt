package net.revive.framework.console

import net.minestom.server.MinecraftServer
import org.jline.reader.*
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.tinylog.Logger
import java.io.IOException
import kotlin.system.exitProcess

object Console {

    private const val APP_NAME = "FrameworkConsole"

    fun processInput(input: String) {
        val command = input.trim()
        if (command.isNotEmpty()) {
            MinecraftServer.getCommandManager().execute(MinecraftServer.getCommandManager().consoleSender, command)
        }
    }

    fun buildReader(builder: LineReaderBuilder): LineReader {
        return builder.appName(APP_NAME)
            .completer(commandCompleter())
            .build().apply {
                setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION)
                unsetOpt(LineReader.Option.INSERT_TAB)
            }
    }

    private fun commandCompleter(): Completer {
        return Completer { _, parsedLine, list ->
            val input = parsedLine.line().trim()
            val commands = MinecraftServer.getCommandManager().dispatcher.commands

            val candidates = commands.flatMap { command ->
                // Start with the command's name
                val names = mutableListOf(command.name)
                // Add all the aliases
                command.aliases?.filterNotNull()?.let { names.addAll(it) }
                // Only include names or aliases that start with the current input
                names.filter { it.startsWith(input) }
            }

            list.addAll(candidates.map { Candidate(it) })
        }
    }

    fun start() {
        try {
            readCommands(TerminalBuilder.builder().system(true).build())
        } catch (e: IOException) {
            Logger.error("Failed to read console input", e)
        }
    }

    private fun readCommands(terminal: Terminal) {
        val reader = buildReader(LineReaderBuilder.builder().terminal(terminal))
        try {
            while (!MinecraftServer.isStopping()) {
                val line = reader.readLine("") ?: break
                processInput(line)
            }
        } catch (e: UserInterruptException) {
            exitProcess(0)
        } finally {
            terminal.close()
        }
    }
}