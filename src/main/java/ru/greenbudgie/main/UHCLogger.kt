package ru.greenbudgie.main

import org.bukkit.Bukkit
import ru.greenbudgie.util.*
import java.util.logging.Level
import java.util.logging.Logger

object UHCLogger {

    lateinit var log: Logger

    /**
     * Logs and sends an error message to every online OP player
     */
    @JvmStatic
    fun sendError(message: String) {
        log.log(Level.SEVERE, message)
        sendToOps("$GRAY[$DARK_RED${BOLD}ERROR$RESET$GRAY]$WHITE$message")
    }

    /**
     * Logs and sends a warning message to every online OP player
     */
    @JvmStatic
    fun sendWarning(message: String) {
        log.log(Level.WARNING, message)
        sendToOps("$GRAY[$GOLD${BOLD}WARNING$RESET$GRAY]$WHITE$message")
    }

    /**
     * Logs and sends an informative message to every online OP player
     */
    @JvmStatic
    fun sendInfo(message: String) {
        log.log(Level.INFO, message)
        sendToOps("$GRAY[$WHITE${BOLD}INFO$RESET$GRAY]$WHITE$message")
    }

    private fun sendToOps(message: String) {
        Bukkit.getOnlinePlayers()
            .filter { it.isOp }
            .forEach { it.sendMessage(message) }
    }

}