package ru.greenbudgie.artifact

import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import ru.greenbudgie.UHC.PlayerManager
import ru.greenbudgie.util.DARK_GRAY
import ru.greenbudgie.util.MathUtils
import ru.greenbudgie.util.ParticleUtils

class ArtifactDamage : Artifact() {

    override fun getName() = "${DARK_GRAY}Злодеяние"

    override fun getDescription(): String {
        return "Дамажит всех игроков на 1.5 сердца. Использовавшего игрока дамажит на 2 сердца. Умереть нельзя."
    }

    override fun getStartingPrice() = 16

    override fun getPriceIncreaseAmount() = 1F

    override fun onUse(player: Player?): Boolean {
        PlayerManager.getAlivePlayers().forEach {
            if (it.isOnline) {
                val currentPlayer = it.player
                val doMaxDamage = player == null || player === currentPlayer
                val initialDamage = if (doMaxDamage) 4 else 3
                val damage = MathUtils.clamp(initialDamage.toDouble(), 0.0, currentPlayer.health - 1)
                currentPlayer.damage(damage)
                currentPlayer.playSound(currentPlayer.location, Sound.ENTITY_VILLAGER_NO, 1f, 0.5f)
                ParticleUtils.createParticlesInRange(currentPlayer.location, 3.0, Particle.SMOKE_LARGE, null, 15)
            } else {
                val damage = MathUtils.clamp(3.0, 0.0, it.offlineHealth - 1)
                it.addOfflineHealth(-damage)
            }
        }
        return true
    }

    override fun getType() = Material.FERMENTED_SPIDER_EYE
}
