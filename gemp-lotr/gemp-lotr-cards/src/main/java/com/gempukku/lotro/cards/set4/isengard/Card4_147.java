package com.gempukku.lotro.cards.set4.isengard;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 0
 * Type: Condition
 * Game Text: To play, spot 2 [ISENGARD] archers. Plays to your support area. The Free Peoples player may not play
 * archery events or use archery special abilities. Discard this condition during the regroup phase.
 */
public class Card4_147 extends AbstractPermanent {
    public Card4_147() {
        super(Side.SHADOW, 0, CardType.CONDITION, Culture.ISENGARD, Zone.SUPPORT, "Covering Fire");
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, 2, Culture.ISENGARD, Keyword.ARCHER);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (game.getGameState().getCurrentPhase() == Phase.REGROUP) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new SelfDiscardEffect(self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new AbstractModifier(self, null, null, ModifierEffect.ACTION_MODIFIER) {
                    @Override
                    public boolean canPlayAction(GameState gameState, ModifiersQuerying modifiersQuerying, String performingPlayer, Action action) {
                        if (!performingPlayer.equals(gameState.getCurrentPlayerId()))
                            return true;
                        if (action.getActionTimeword() == Phase.ARCHERY)
                            return false;

                        return true;
                    }
                });
    }
}
