package com.gempukku.lotro.cards.set7.gollum;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.RevealHandEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseOpponentEffect;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.common.Side;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseAndDiscardCardsFromHandEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Set: The Return of the King
 * Side: Free
 * Culture: Gollum
 * Twilight Cost: 2
 * Type: Event • Regroup
 * Game Text: Exert Smeagol to reveal an opponent's hand. That opponent must discard a card from hand for each culture
 * revealed.
 */
public class Card7_066 extends AbstractEvent {
    public Card7_066() {
        super(Side.FREE_PEOPLE, 2, Culture.GOLLUM, "No Safe Places", Phase.REGROUP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Filters.smeagol);
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, final LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.smeagol));
        action.appendEffect(
                new ChooseOpponentEffect(playerId) {
                    @Override
                    protected void opponentChosen(final String opponentId) {
                        action.insertEffect(
                                new RevealHandEffect(self, playerId, opponentId) {
                                    @Override
                                    protected void cardsRevealed(Collection<? extends PhysicalCard> cards) {
                                        Set<Culture> cultures = new HashSet<Culture>();
                                        for (PhysicalCard physicalCard : cards)
                                            cultures.add(physicalCard.getBlueprint().getCulture());

                                        int cultureCount = cultures.size();
                                        action.appendEffect(
                                                new ChooseAndDiscardCardsFromHandEffect(action, opponentId, true, cultureCount));
                                    }
                                });
                    }
                });
        return action;
    }
}
