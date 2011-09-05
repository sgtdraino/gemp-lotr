package com.gempukku.lotro.cards.set1.dwarven;

import com.gempukku.lotro.cards.AbstractLotroCardBlueprint;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayPermanentAction;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.ExertCharacterEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.CostToEffectAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 0
 * Type: Condition
 * Game Text: Tale. Exert a Dwarf to play this condition. Plays to your support area. Each time your opponent plays an
 * Orc, that player discards the top card of his or her draw deck.
 */
public class Card1_016 extends AbstractLotroCardBlueprint {
    public Card1_016() {
        super(Side.FREE_PEOPLE, CardType.CONDITION, Culture.DWARVEN, "Greatest Kingdom of My People", true);
        addKeyword(Keyword.TALE);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public List<? extends Action> getPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canPlayFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.DWARF), Filters.canExert())) {
            final PlayPermanentAction action = new PlayPermanentAction(self, Zone.FREE_SUPPORT);
            action.addCost(
                    new ChooseActiveCardEffect(playerId, "Choose Dwarf to exert", Filters.keyword(Keyword.DWARF), Filters.canExert()) {
                        @Override
                        protected void cardSelected(PhysicalCard dwarf) {
                            action.addCost(new ExertCharacterEffect(dwarf));
                        }
                    });

            return Collections.<Action>singletonList(action);
        }
        return null;
    }

    @Override
    public List<? extends Action> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (PlayConditions.played(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.keyword(Keyword.ORC))) {
            String playerId = ((PlayCardResult) effectResult).getPlayedCard().getOwner();
            CostToEffectAction action = new CostToEffectAction(self, null, "Discard the top card of deck");
            action.addEffect(new DiscardTopCardFromDeckEffect(playerId));
            return Collections.<Action>singletonList(action);
        }
        return null;
    }
}
