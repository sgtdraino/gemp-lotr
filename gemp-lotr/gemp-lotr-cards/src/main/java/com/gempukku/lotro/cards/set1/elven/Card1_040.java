package com.gempukku.lotro.cards.set1.elven;

import com.gempukku.lotro.cards.AbstractAlly;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.SelfExertEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.effects.DrawCardsEffect;
import com.gempukku.lotro.logic.effects.HealCharactersEffect;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 4
 * Type: Ally � Home 3 � Elf
 * Strength: 8
 * Vitality: 4
 * Site: 3
 * Game Text: To play, spot Gandalf or an Elf. At the start of each of your turns, heal every ally whose home is site 3.
 * Fellowship: Exert Elrond to draw a card.
 */
public class Card1_040 extends AbstractAlly {
    public Card1_040() {
        super(4, Block.FELLOWSHIP, 3, 8, 4, Race.ELF, Culture.ELVEN, "Elrond", "Lord of Rivendell", true);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.or(Race.ELF, Filters.gandalf));
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.FELLOWSHIP, self)
                && PlayConditions.canExert(self, game, self)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(new SelfExertEffect(action, self));
            action.appendEffect(new DrawCardsEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    public List<RequiredTriggerAction> getRequiredAfterTriggers(LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.startOfTurn(game, effectResult)) {
            RequiredTriggerAction action = new RequiredTriggerAction(self);
            action.appendEffect(
                    new HealCharactersEffect(self, Filters.and(CardType.ALLY, Filters.isAllyHome(3, Block.FELLOWSHIP))));

            return Collections.singletonList(action);
        }
        return null;
    }
}
