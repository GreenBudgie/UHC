package ru.greenbudgie.main

import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import ru.greenbudgie.UHC.PlayerOptionHolder
import ru.greenbudgie.UHC.RecipeHandler
import ru.greenbudgie.UHC.UHC
import ru.greenbudgie.artifact.ArtifactManager
import ru.greenbudgie.classes.ClassManager
import ru.greenbudgie.commands.*
import ru.greenbudgie.items.CustomItems
import ru.greenbudgie.items.CustomItemsListener
import ru.greenbudgie.lobby.sign.SignManager
import ru.greenbudgie.mutator.InventoryBuilderMutator
import ru.greenbudgie.rating.InventoryBuilderRating
import ru.greenbudgie.rating.Rating
import ru.greenbudgie.requester.ItemRequester
import ru.greenbudgie.util.TaskManager

class UHCPlugin : JavaPlugin() {
    override fun onEnable() {
        instance = this
        UHCLogger.log = logger

        registerCommand("test", CommandTest())
        registerCommand("gm", CommandGM())
        registerCommand("start", CommandStart())
        registerCommand("end", CommandEnd())
        registerCommand("lobby", CommandLobby())
        registerCommand("arena", CommandArena())
        registerCommand("skip", CommandSkip())
        registerCommand("map", CommandMap())
        registerCommand("rating", CommandRating())
        registerCommand("drop", CommandDrop())
        registerCommand("customitem", CommandCustomItem())
        registerCommand("timer", CommandTimer())
        registerCommand("mutator", CommandMutator())
        registerCommand("optmutator", CommandOptMutator())
        registerCommand("inv", CommandInv())
        registerCommand("class", CommandClass())
        registerCommand("teammate", CommandTeammate())
        registerCommand("worldtime", CommandWorldTime())
        registerCommand("editarena", CommandEditArena())
        registerCommand("requests", CommandRequests())
        registerCommand("artifacts", CommandArtifacts())

        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(UHC(), this)
        pluginManager.registerEvents(SignManager(), this)
        pluginManager.registerEvents(RecipeHandler(), this)
        pluginManager.registerEvents(CustomItemsListener(), this)
        pluginManager.registerEvents(ItemRequester(), this)
        pluginManager.registerEvents(ArtifactManager(), this)

        InventoryBuilderMutator.registerListener()
        InventoryBuilderRating.registerListener()
        UHC.init()
        CustomItems.init()
        ItemRequester.init()
        ArtifactManager.init()
        ClassManager.init()
        Rating.loadFromConfig()
        TaskManager.init()
    }

    private fun registerCommand(commandName: String, executor: CommandExecutor) {
        val command = getCommand(commandName)
        command?.setExecutor(executor)
    }

    override fun onDisable() {
        PlayerOptionHolder.saveOptions()
    }

    companion object {

		lateinit var instance: UHCPlugin

    }
}
