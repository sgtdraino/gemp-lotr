package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.AddTwilightEffect;
import com.gempukku.lotro.logic.effects.ChooseAndHealCharactersEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Maneuver: Add (1) and exert a Dwarf to heal an Elf, or add (1) and exert an
 * Elf to heal a Dwarf.
 */
public class Card1_059 extends AbstractPermanent {
    public Card1_059() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.ELVEN, Zone.SUPPORT, "Shoulder to Shoulder");
    }

    @Override
    public List<? extends Action> getExtraPhaseActions(final String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.MANEUVER, self)
                && PlayConditions.canExert(self, game,
                Filters.or(
                        Race.ELF,
                        Race.DWARF))) {
            final ActivateCardAction action = new ActivateCardAction(self);

            action.appendCost(new AddTwilightEffect(self, 1));
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.or(Race.DWARF, Race.ELF)) {
                        @Override
                        protected void forEachCardExertedCallback(PhysicalCard character) {
                            Filterable filter;
                            if (character.getBlueprint().getRace() == Race.ELF)
                                filter = Race.DWARF;
                            else
                                filter = Race.ELF;
                            action.appendEffect(
                                    new ChooseAndHealCharactersEffect(action, playerId, filter));
                        }
                    }
            );

            return Collections.singletonList(action);
        }

        return null;
    }
}
