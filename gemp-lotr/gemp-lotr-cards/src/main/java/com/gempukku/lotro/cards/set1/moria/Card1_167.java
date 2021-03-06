package com.gempukku.lotro.cards.set1.moria;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Race;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

/**
 * Set: The Fellowship of the Ring
 * Side: Shadow
 * Culture: Moria
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Skirmish: Make a [MORIA] Orc strength +1 for each other [MORIA] Orc you spot (limit +4).
 */
public class Card1_167 extends AbstractOldEvent {
    public Card1_167() {
        super(Side.SHADOW, Culture.MORIA, "Denizens Enraged", Phase.SKIRMISH);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, final LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose MORIA Orc", Culture.MORIA, Race.ORC) {
                    @Override
                    protected void cardSelected(LotroGame game, PhysicalCard orc) {
                        int bonus = Math.min(4, Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Culture.MORIA, Race.ORC, Filters.not(Filters.sameCard(orc))));
                        action.appendEffect(
                                new AddUntilEndOfPhaseModifierEffect(
                                        new StrengthModifier(self, Filters.sameCard(orc), bonus)));
                    }
                }
        );

        return action;
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }
}
