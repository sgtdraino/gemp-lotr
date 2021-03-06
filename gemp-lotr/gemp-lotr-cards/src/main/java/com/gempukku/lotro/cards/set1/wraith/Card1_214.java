package com.gempukku.lotro.cards.set1.wraith;

import com.gempukku.lotro.cards.AbstractResponseOldEvent;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddBurdenEffect;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Response: If a Nazgul wins a skirmish, the Free Peoples player chooses to either exert the Ring-bearer or
 * add a burden.
 */
public class Card1_214 extends AbstractResponseOldEvent {
    public Card1_214() {
        super(Side.SHADOW, Culture.WRAITH, "In the Ringwraith's Wake");
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<PlayEventAction> getOptionalAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.winsSkirmish(game, effectResult, Race.NAZGUL)
                && checkPlayRequirements(playerId, game, self, 0, 0, false, false)) {
            PlayEventAction action = new PlayEventAction(self);
            List<Effect> possibleEffects = new LinkedList<Effect>();
            possibleEffects.add(
                    new ExertCharactersEffect(action, self, game.getGameState().getRingBearer(game.getGameState().getCurrentPlayerId())) {
                        @Override
                        public String getText(LotroGame game) {
                            return "Exert the Ring-bearer";
                        }
                    });
            possibleEffects.add(
                    new AddBurdenEffect(game.getGameState().getCurrentPlayerId(), self, 1));
            action.appendEffect(
                    new ChoiceEffect(action, game.getGameState().getCurrentPlayerId(), possibleEffects));
            return Collections.singletonList(action);
        }
        return null;
    }
}
