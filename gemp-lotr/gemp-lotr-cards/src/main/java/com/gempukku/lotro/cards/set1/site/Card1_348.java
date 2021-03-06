package com.gempukku.lotro.cards.set1.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.CheckTurnLimitEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromDeckEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Twilight Cost: 2
 * Type: Site
 * Site: 4
 * Game Text: Mountain. Shadow: Spot a [ISENGARD] minion to play a weather card from your draw deck (limit one per
 * turn).
 */
public class Card1_348 extends AbstractSite {
    public Card1_348() {
        super("Pass of Caradhras", Block.FELLOWSHIP, 4, 2, Direction.RIGHT);
        addKeyword(Keyword.MOUNTAIN);
    }

    @Override
    public List<? extends Action> getPhaseActions(final String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseSiteDuringPhase(game, Phase.SHADOW, self)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Culture.ISENGARD, CardType.MINION)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendEffect(
                    new CheckTurnLimitEffect(action, self, 1,
                            new ChooseAndPlayCardFromDeckEffect(playerId, Keyword.WEATHER)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
