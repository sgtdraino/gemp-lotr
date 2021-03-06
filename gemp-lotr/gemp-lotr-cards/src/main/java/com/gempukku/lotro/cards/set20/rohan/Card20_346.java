package com.gempukku.lotro.cards.set20.rohan;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDiscardEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.PossessionClass;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

/**
 * 0
 * To the Smithy!
 * Event • Skirmish
 * Play a [Rohan] weapon or [Rohan] mount from your discard pile.
 * http://lotrtcg.org/coreset/rohan/tothesmithy(r1).png
 */
public class Card20_346 extends AbstractEvent {
    public Card20_346() {
        super(Side.FREE_PEOPLE, 0, Culture.ROHAN, "To the Smithy!", Phase.SKIRMISH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canPlayFromDiscard(playerId, game, Culture.ROHAN, Filters.or(Filters.weapon, PossessionClass.MOUNT));
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseAndPlayCardFromDiscardEffect(playerId, game, Culture.ROHAN, Filters.or(Filters.weapon, PossessionClass.MOUNT)));
        return action;
    }
}
