package com.gempukku.lotro.cards.set5.isengard;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.cards.effects.CancelActivatedEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.cards.modifiers.VitalityModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.ActivateCardResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Battle of Helm's Deep
 * Side: Shadow
 * Culture: Isengard
 * Twilight Cost: 5
 * Type: Possession • Mount
 * Strength: +5
 * Vitality: +3
 * Game Text: Bearer must be a warg-rider. If bearer is Sharku, he is damage +1. Response: If a skirmish special
 * ability is used in a skirmish involving bearer, exert bearer to cancel that action.
 */
public class Card5_059 extends AbstractAttachable {
    public Card5_059() {
        super(Side.SHADOW, CardType.POSSESSION, 5, Culture.ISENGARD, PossessionClass.MOUNT, "Sharku's Warg", null, true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Keyword.WARG_RIDER;
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(
                new StrengthModifier(self, Filters.hasAttached(self), 5));
        modifiers.add(
                new VitalityModifier(self, Filters.hasAttached(self), 3));
        modifiers.add(
                new KeywordModifier(self, Filters.and(Filters.hasAttached(self), Filters.name("Sharku")), Keyword.DAMAGE, 1));
        return modifiers;
    }

    @Override
    public List<? extends Action> getOptionalInPlayAfterActions(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.activated(game, effectResult, Filters.any)) {
            ActivateCardResult activateEffect = (ActivateCardResult) effectResult;
            if (Filters.inSkirmish.accepts(game.getGameState(), game.getModifiersQuerying(), self.getAttachedTo())
                    && activateEffect.getActionTimeword() == Phase.SKIRMISH
                    && PlayConditions.canExert(self, game, Filters.hasAttached(self))) {
                ActivateCardAction action = new ActivateCardAction(self);
                action.appendCost(
                        new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Filters.hasAttached(self)));
                action.appendEffect(
                        new CancelActivatedEffect(self, activateEffect));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
