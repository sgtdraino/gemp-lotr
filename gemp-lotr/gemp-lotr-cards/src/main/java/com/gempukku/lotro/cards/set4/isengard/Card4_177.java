package com.gempukku.lotro.cards.set4.isengard;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndPlayCardFromHandEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseAndDiscardCardsFromHandEffect;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Two Towers
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 1
 * Type: Possession • Hand Weapon
 * Strength: +3
 * Game Text: Bearer must be an Uruk-hai. Shadow: If bearer is Ugluk, exert it and discard a minion from hand to play
 * a minion. Its twilight cost is -3.
 */
public class Card4_177 extends AbstractAttachable {
    public Card4_177() {
        super(Side.SHADOW, CardType.POSSESSION, 1, Culture.ISENGARD, PossessionClass.HAND_WEAPON, "Ugluk's Sword", null, true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.URUK_HAI;
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        return Collections.singletonList(
                new StrengthModifier(self, Filters.hasAttached(self), 3));
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SHADOW, self, 0)
                && self.getAttachedTo().getBlueprint().getName().equals("Ugluk")
                && PlayConditions.canExert(self, game, Filters.name("Ugluk"))
                && PlayConditions.canPlayFromHand(playerId, game, -3, CardType.MINION)) {
            ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.name("Ugluk")));
            action.appendCost(
                    new ChooseAndDiscardCardsFromHandEffect(action, playerId, false, 1, CardType.MINION));
            action.appendEffect(
                    new ChooseAndPlayCardFromHandEffect(playerId, game, -3, CardType.MINION));
            return Collections.singletonList(action);
        }
        return null;
    }
}
