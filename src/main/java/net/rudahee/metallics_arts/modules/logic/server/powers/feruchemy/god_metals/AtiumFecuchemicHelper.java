package net.rudahee.metallics_arts.modules.logic.server.powers.feruchemy.god_metals;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.rudahee.metallics_arts.modules.logic.server.powers.feruchemy.AbstractFechuchemicHelper;
import net.rudahee.metallics_arts.modules.logic.server.server_events.OnDamageEvent;

import java.util.function.Supplier;

/**
 * Helper class containing the methods and implementations for using feruchemical Atium.
 *
 * @author SteelCode Team
 * @since 1.5.1
 */
public class AtiumFecuchemicHelper extends AbstractFechuchemicHelper {

    /**
     * Implementation of the abstract method of the AbstractFechuchemicHelper class.
     * In this specific case, for the power of the Atium: Invisibility will be applied to the target player.
     *
     * @param player to whom the effect will be applied.
     *
     * @see AbstractFechuchemicHelper#tappingPower(Player)
     */
    @Override
    public void tappingPower(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 1, true, false));
    }

    /**
     * Implementation of the abstract method of the AbstractFechuchemicHelper class.
     * In this specific case, for the power of the Atium: Glowing will be applied to the target player.
     *
     * @param player to whom the effect will be applied.
     *
     * @see AbstractFechuchemicHelper#storagePower(Player)
     */
    @Override
    public void storagePower(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 5, 1, true, true));
    }

    //todo
    public static Supplier<? extends AtiumFecuchemicHelper> getInstance() {
        return AtiumFecuchemicHelper::new;
    }
}
