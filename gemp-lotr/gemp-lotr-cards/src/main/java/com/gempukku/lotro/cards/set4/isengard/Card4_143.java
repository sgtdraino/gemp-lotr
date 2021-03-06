package com.gempukku.lotro.cards.set4.isengard;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.PreventableEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.SubAction;
import com.gempukku.lotro.logic.effects.AssignmentEffect;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.timing.Effect;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 0
 * Type: Event
 * Game Text: Search. Assignment: Exert an [ISENGARD] tracker to assign it to an unbound companion. That companion may
 * exert to prevent this (unless that companion is a Hobbit).
 */
public class Card4_143 extends AbstractOldEvent {
    public Card4_143() {
        super(Side.SHADOW, Culture.ISENGARD, "Brought Back Alive", Phase.ASSIGNMENT);
        addKeyword(Keyword.SEARCH);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Culture.ISENGARD, Keyword.TRACKER);
    }

    @Override
    public int getTwilightCost() {
        return 0;
    }

    @Override
    public PlayEventAction getPlayCardAction(final String playerId, final LotroGame game, final PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        final PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Culture.ISENGARD, Keyword.TRACKER) {
                    @Override
                    protected void forEachCardExertedCallback(final PhysicalCard minion) {
                        action.appendEffect(
                                new ChooseActiveCardEffect(self, playerId, "Choose an unbound companion", Filters.unboundCompanion, Filters.assignableToSkirmishAgainst(Side.SHADOW, minion)) {
                                    @Override
                                    protected void cardSelected(LotroGame game, final PhysicalCard companion) {
                                        Race race = companion.getBlueprint().getRace();
                                        AssignmentEffect assignmentEffect = new AssignmentEffect(playerId, companion, minion);
                                        if (race == Race.HOBBIT) {
                                            action.insertEffect(
                                                    assignmentEffect);
                                        } else {
                                            action.insertEffect(
                                                    new PreventableEffect(action,
                                                            assignmentEffect, game.getGameState().getCurrentPlayerId(),
                                                            new PreventableEffect.PreventionCost() {
                                                                @Override
                                                                public Effect createPreventionCostForPlayer(SubAction subAction, String playerId) {
                                                                    return new ExertCharactersEffect(action, self, companion);
                                                                }
                                                            }
                                                    ));
                                        }
                                    }
                                });
                    }
                });
        return action;
    }
}
