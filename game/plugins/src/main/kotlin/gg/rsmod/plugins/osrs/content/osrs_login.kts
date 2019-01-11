import gg.rsmod.game.model.NEW_ACCOUNT_ATTR
import gg.rsmod.game.model.interf.DisplayMode
import gg.rsmod.plugins.*
import gg.rsmod.plugins.osrs.InterfacePane
import gg.rsmod.plugins.osrs.OSRSGameframe
import gg.rsmod.plugins.osrs.api.ChatMessageType
import gg.rsmod.plugins.osrs.api.Skills
import gg.rsmod.plugins.osrs.content.mechanics.special.SpecialEnergy

/**
 * @author Tom <rspsmods@gmail.com>
 */

r.bindLogin {
    val p = it.player()

    /**
     * First log-in logic (when accounts have just been made).
     */
    if (p.attr.getOrDefault(NEW_ACCOUNT_ATTR, false)) {
        SpecialEnergy.setEnergy(p, 100)
    }

    /**
     * Skill-related logic.
     */
    if (p.getSkills().getMaxLevel(Skills.HITPOINTS) < 10) {
        p.getSkills().setBaseLevel(Skills.HITPOINTS, 10)
    }
    p.calculateAndSetCombatLevel()
    p.sendWeaponInterfaceInformation()
    p.sendCombatLevelText()

    /**
     * Interface-related logic.
     */
    p.sendDisplayInterface(DisplayMode.FIXED)
    InterfacePane.values().filter { pane -> pane.interfaceId != -1 }.forEach { pane ->
        if (pane == InterfacePane.XP_COUNTER && p.getVarbit(OSRSGameframe.XP_DROPS_VISIBLE_VARBIT) == 0) {
            return@forEach
        } else if (pane == InterfacePane.MINI_MAP && p.getVarbit(OSRSGameframe.DATA_ORBS_HIDDEN_VARBIT) == 1) {
            return@forEach
        }
        p.openInterface(pane.interfaceId, pane)
    }

    /**
     * Inform the client whether or not we have a display name.
     */
    val displayName = p.username.isNotEmpty()
    p.invokeScript(1105, if (displayName) 1 else 0) // Has display name
    p.invokeScript(423, p.username)
    if (p.getVarp(1055) == 0 && displayName) {
        p.syncVarp(1055)
    }

    /**
     * Game-related logic.
     */
    p.sendRunEnergy()
    p.message("Welcome to ${p.world.gameContext.name}.", ChatMessageType.SERVER)
}