package com.gempukku.lotro.cards.set1.gondor;

import com.gempukku.lotro.cards.AbstractAttachableFPPossession;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseAndWoundCharactersEffect;
import com.gempukku.lotro.logic.modifiers.KeywordModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: The Fellowship of the Ring
 * Side: Free
 * Culture: Gondor
 * Twilight Cost: 1
 * Type: Possession • Hand Weapon
 * Strength: +2
 * Game Text: Bearer must be Boromir. He is damage +1. Skirmish: Exert Boromir to wound an Orc or Uruk-hai he is
 * skirmishing.
 */
public class Card1_095 extends AbstractAttachableFPPossession {
    public Card1_095() {
        super(1, 2, 0, Culture.GONDOR, PossessionClass.HAND_WEAPON, "Blade of Gondor", null, true);
    }

    @Override
    protected Filter getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.boromir;
    }

    @Override
    protected List<? extends Modifier> getNonBasicStatsModifiers(PhysicalCard self) {
        return Collections.singletonList(new KeywordModifier(self, Filters.hasAttached(self), Keyword.DAMAGE));
    }

    @Override
    protected List<? extends Action> getExtraInPlayPhaseActions(final String playerId, LotroGame game, final PhysicalCard self) {
        if (PlayConditions.canUseFPCardDuringPhase(game, Phase.SKIRMISH, self)
                && PlayConditions.canExert(self, game, self.getAttachedTo())) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new ExertCharactersEffect(action, self, self.getAttachedTo()));
            action.appendEffect(
                    new ChooseAndWoundCharactersEffect(action, playerId, 1, 1, Filters.or(Race.ORC, Race.URUK_HAI), Filters.inSkirmishAgainst(Filters.hasAttached(self))));
            return Collections.singletonList(action);
        }
        return null;
    }
}
