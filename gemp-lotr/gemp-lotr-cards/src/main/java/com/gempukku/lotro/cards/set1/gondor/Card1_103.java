package com.gempukku.lotro.cards.set1.gondor;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.AddUntilStartOfPhaseModifierEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Maneuver: Make a [GONDOR] companion defender +1 until the regroup phase.
 */
public class Card1_103 extends AbstractOldEvent {
    public Card1_103() {
        super(Side.FREE_PEOPLE, Culture.GONDOR, "Elendil's Valor", Phase.MANEUVER);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseActiveCardEffect(self, playerId, "Choose a GONDOR companion", Culture.GONDOR, CardType.COMPANION) {
                    @Override
                    protected void cardSelected(LotroGame game, PhysicalCard gondorCompanion) {
                        action.appendEffect(
                                new AddUntilStartOfPhaseModifierEffect(
                                        new KeywordModifier(self, Filters.sameCard(gondorCompanion), Keyword.DEFENDER), Phase.REGROUP));
                    }
                }
        );
        return action;
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }
}
