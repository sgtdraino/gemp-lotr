package com.gempukku.lotro.cards.set2.wraith;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.PutOnTheOneRingEffect;
import com.gempukku.lotro.cards.effects.TakeOffTheOneRingEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.AbstractActionProxy;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.RequiredTriggerAction;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.UnrespondableEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Mines of Moria
 * Side: Shadow
 * Culture: Wraith
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Maneuver: Exert a twilight Nazgul to exert the Ring-bearer. If the Ring-bearer is then exhausted, he puts
 * on The One Ring until the regroup phase.
 */
public class Card2_079 extends AbstractOldEvent {
    public Card2_079() {
        super(Side.SHADOW, Culture.WRAITH, "Resistance Becomes Unbearable", Phase.MANEUVER);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Race.NAZGUL, Keyword.TWILIGHT);
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Race.NAZGUL, Keyword.TWILIGHT));
        action.appendEffect(
                new ExertCharactersEffect(action, self, Filters.ringBearer));
        action.appendEffect(
                new UnrespondableEffect() {
                    @Override
                    protected void doPlayEffect(LotroGame game) {
                        if (Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.ringBearer, Filters.exhausted)) {
                            action.insertEffect(
                                    new PutOnTheOneRingEffect());
                            game.getActionsEnvironment().addUntilEndOfPhaseActionProxy(
                                    new AbstractActionProxy() {
                                        @Override
                                        public List<? extends RequiredTriggerAction> getRequiredAfterTriggers(LotroGame lotroGame, EffectResult effectResult) {
                                            if (TriggerConditions.startOfPhase(lotroGame, effectResult, Phase.REGROUP)) {
                                                RequiredTriggerAction action = new RequiredTriggerAction(self);
                                                action.appendEffect(
                                                        new TakeOffTheOneRingEffect());
                                                return Collections.singletonList(action);
                                            }
                                            return null;
                                        }
                                    }, Phase.REGROUP);
                        }
                    }
                });
        return action;
    }
}
