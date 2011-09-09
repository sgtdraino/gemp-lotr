package com.gempukku.lotro.cards.set1.shire;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.decisions.ForEachYouSpotDecision;
import com.gempukku.lotro.cards.effects.ChooseArbitraryCardsEffect;
import com.gempukku.lotro.cards.effects.PutCardFromDiscardOnBottomOfDeckEffect;
import com.gempukku.lotro.cards.effects.ShuffleDeckEffect;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Culture;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.DefaultCostToEffectAction;
import com.gempukku.lotro.logic.decisions.DecisionResultInvalidException;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.effects.DiscardCardFromPlayEffect;
import com.gempukku.lotro.logic.effects.PlayoutDecisionEffect;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Shire
 * Twilight Cost: 1
 * Type: Possession • Pipe
 * Game Text: Bearer must be a Hobbit. Fellowship: Discard a pipeweed possession and spot X pipes to shuffle X tales
 * from your discard pile into your draw deck.
 */
public class Card1_285 extends AbstractAttachableFPPossession {
    public Card1_285() {
        super(1, Culture.SHIRE, Keyword.PIPE, "Bilbo's Pipe", true);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.keyword(Keyword.HOBBIT);
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(final String playerId, final LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game.getGameState(), Phase.FELLOWSHIP, self)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.PIPEWEED), Filters.type(CardType.POSSESSION))) {
            final DefaultCostToEffectAction action = new DefaultCostToEffectAction(self, Keyword.FELLOWSHIP, "Discard a pipeweed possession and spot X pipes to shuffle X tales from your discard pile into your draw deck.");
            action.addCost(
                    new ChooseActiveCardEffect(playerId, "Choose pipeweed", Filters.keyword(Keyword.PIPEWEED), Filters.type(CardType.POSSESSION)) {
                        @Override
                        protected void cardSelected(PhysicalCard pipeweed) {
                            action.addCost(new DiscardCardFromPlayEffect(self, pipeweed));
                        }
                    });
            action.addEffect(
                    new PlayoutDecisionEffect(game.getUserFeedback(), playerId,
                            new ForEachYouSpotDecision(1, "Choose number of pipes you wish to spot", game, Filters.keyword(Keyword.PIPE), Integer.MAX_VALUE) {
                                @Override
                                public void decisionMade(String result) throws DecisionResultInvalidException {
                                    int spotCount = getValidatedResult(result);
                                    List<PhysicalCard> talesInDiscard = Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.keyword(Keyword.TALE));

                                    int shufflableTales = Math.min(spotCount, talesInDiscard.size());
                                    action.addEffect(
                                            new ChooseArbitraryCardsEffect(playerId, "Choose tales", talesInDiscard, shufflableTales, shufflableTales) {
                                                @Override
                                                protected void cardsSelected(List<PhysicalCard> selectedCards) {
                                                    for (PhysicalCard selectedCard : selectedCards)
                                                        action.addEffect(new PutCardFromDiscardOnBottomOfDeckEffect(selectedCard));
                                                    action.addEffect(new ShuffleDeckEffect(playerId));
                                                }
                                            });
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }
}
