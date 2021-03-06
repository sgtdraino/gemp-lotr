package com.gempukku.lotro.cards.set20.wraith;


import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.effects.ChoiceEffect;
import com.gempukku.lotro.cards.effects.SelfDiscardEffect;
import com.gempukku.lotro.cards.effects.TransferPermanentEffect;
import com.gempukku.lotro.cards.effects.TransferPermanentNotFromPlayEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.ActivateCardAction;
import com.gempukku.lotro.logic.effects.ChooseActiveCardEffect;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;
import com.gempukku.lotro.logic.timing.Action;
import com.gempukku.lotro.logic.timing.Effect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 1
 * Morgul Blade
 * Ringwraith	Possession • Hand Weapon
 * 1
 * Bearer must be a Nazgul.
 * This weapon may be borne in addition to 1 other hand weapon.
 * Skirmish: Discard this possession to transfer Blade Tip from your support area or discard pile to a companion bearer is skirmishing.
 */
public class Card20_295  extends AbstractAttachable {
    public Card20_295() {
        super(Side.SHADOW, CardType.POSSESSION, 1, Culture.WRAITH, PossessionClass.HAND_WEAPON, "Morgul Blade");
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new StrengthModifier(self, Filters.hasAttached(self), 1);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.NAZGUL;
    }

    @Override
    public boolean isExtraPossessionClass(LotroGame game, PhysicalCard self, PhysicalCard attachedTo) {
        return true;
    }

    @Override
    protected List<? extends Action> getExtraPhaseActions(String playerId, LotroGame game, PhysicalCard self) {
        if (PlayConditions.canUseShadowCardDuringPhase(game, Phase.SKIRMISH, self, 0)) {
            final ActivateCardAction action = new ActivateCardAction(self);
            action.appendCost(
                    new SelfDiscardEffect(self));

            final PhysicalCard bladeTipInSupport = Filters.findFirstActive(game.getGameState(), game.getModifiersQuerying(), Filters.name("Blade Tip"), Filters.owner(playerId), Zone.SUPPORT);
            final Collection<PhysicalCard> bladeTipsInDiscard = Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.name("Blade Tip"));

            List<Effect> possibleEffects = new LinkedList<Effect>();
            if (bladeTipInSupport != null) {
                possibleEffects.add(
                        new ChooseActiveCardEffect(self, playerId, "Choose a companion", CardType.COMPANION, Filters.inSkirmishAgainst(self.getAttachedTo()), Filters.not(Filters.hasAttached(Filters.name("Blade Tip")))) {
                            @Override
                            protected void cardSelected(LotroGame game, PhysicalCard card) {
                                action.insertEffect(
                                        new TransferPermanentEffect(bladeTipInSupport, card));
                            }

                            @Override
                            public String getText(LotroGame game) {
                                return "Transfer Blade Tip from support area";
                            }
                        });
            }
            if (bladeTipsInDiscard.size() > 0) {
                possibleEffects.add(
                        new ChooseActiveCardEffect(self, playerId, "Choose a companion", CardType.COMPANION, Filters.inSkirmishAgainst(self.getAttachedTo()), Filters.not(Filters.hasAttached(Filters.name("Blade Tip")))) {
                            @Override
                            protected void cardSelected(LotroGame game, PhysicalCard card) {
                                action.insertEffect(
                                        new TransferPermanentNotFromPlayEffect(bladeTipsInDiscard.iterator().next(), card));
                            }

                            @Override
                            public String getText(LotroGame game) {
                                return "Transfer Blade Tip from discard";
                            }
                        });
            }
            action.appendEffect(
                    new ChoiceEffect(action, playerId, possibleEffects));
            return Collections.singletonList(action);
        }
        return null;
    }
}

