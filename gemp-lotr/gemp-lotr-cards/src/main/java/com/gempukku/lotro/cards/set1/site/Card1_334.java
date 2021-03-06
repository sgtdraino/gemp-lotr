package com.gempukku.lotro.cards.set1.site;

import com.gempukku.lotro.cards.AbstractSite;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.Block;
import com.gempukku.lotro.common.CardType;
import com.gempukku.lotro.common.Keyword;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.DrawCardsEffect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Twilight Cost: 1
 * Type: Site
 * Site: 2
 * Game Text: Forest. Each time you play a possession or artifact on your companion, draw a card.
 */
public class Card1_334 extends AbstractSite {
    public Card1_334() {
        super("Trollshaw Forest", Block.FELLOWSHIP, 2, 1, Direction.LEFT);
        addKeyword(Keyword.FOREST);
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.played(game, effectResult, Filters.or(CardType.POSSESSION, CardType.ARTIFACT))) {
            PlayCardResult playCardResult = (PlayCardResult) effectResult;
            PhysicalCard attachedTo = playCardResult.getAttachedTo();
            if (attachedTo != null && attachedTo.getBlueprint().getCardType() == CardType.COMPANION) {
                RequiredTriggerAction action = new RequiredTriggerAction(self);
                action.appendEffect(new DrawCardsEffect(action, game.getGameState().getCurrentPlayerId(), 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
