package com.gempukku.lotro.cards.set2.dwarven;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.AddUntilEndOfPhaseModifierEffect;
import com.gempukku.lotro.cards.effects.CheckPhaseLimitEffect;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 1
 * Type: Condition
 * Game Text: Plays to your support area. Skirmish: Discard the top 3 cards from your draw deck to make a Dwarf
 * strength +1 (limit +3) and, if underground, damage +1 (limit +3).
 */
public class Card2_012 extends AbstractPermanent {
    public Card2_012() {
        super(Side.FREE_PEOPLE, 1, CardType.CONDITION, Culture.DWARVEN, Zone.SUPPORT, "Realm of Dwarrowdelf", null, true);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && game.getGameState().getDeck(playerId).size() >= 3) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new DiscardTopCardFromDeckEffect(self, playerId, 3, false));
            action.appendEffect(
                    new ChooseActiveCardEffect(self, playerId, "Choose a Dwarf", Race.DWARF) {
                        @Override
                        protected void cardSelected(LotroGame game, PhysicalCard dwarf) {
                            action.appendEffect(
                                    new CheckPhaseLimitEffect(action, self, "str", 3, Phase.SKIRMISH,
                                            new AddUntilEndOfPhaseModifierEffect(
                                                    new StrengthModifier(self, Filters.sameCard(dwarf), 1))));
                            if (PlayConditions.location(game, Keyword.UNDERGROUND))
                                action.appendEffect(
                                        new CheckPhaseLimitEffect(action, self, "dmg", 3, Phase.SKIRMISH,
                                                new AddUntilEndOfPhaseModifierEffect(
                                                        new KeywordModifier(self, Filters.sameCard(dwarf), Keyword.DAMAGE))));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
