package com.gempukku.lotro.cards.set2.gandalf;

import com.gempukku.lotro.cards.AbstractPermanent;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

/**
 * Set: Mines of Moria
 * Side: Free
 * Culture: Gandalf
 * Twilight Cost: 0
 * Type: Condition
 * Game Text: Plays to your support area. The twilight cost of each possession or artifact played on Gandalf or
 * a character who has the Gandalf signet is -1.
 */
public class Card2_023 extends AbstractPermanent {
    public Card2_023() {
        super(Side.FREE_PEOPLE, 0, CardType.CONDITION, Culture.GANDALF, Zone.SUPPORT, "Gandalf's Wisdom");
    }

    @Override
    public Modifier getAlwaysOnModifier(LotroGame game, PhysicalCard self) {
        return new AbstractModifier(self, "The twilight cost of each possession or artifact played on Gandalf or a character who has the Gandalf signet is -1",
                Filters.or(
                        CardType.POSSESSION,
                        CardType.ARTIFACT), ModifierEffect.TWILIGHT_COST_MODIFIER) {
            @Override
            public int getPlayOnTwilightCostModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard target) {
                if (physicalCard.getBlueprint().getCardType() == CardType.POSSESSION || physicalCard.getBlueprint().getCardType() == CardType.ARTIFACT) {
                    CardType cardType = target.getBlueprint().getCardType();
                    if (target.getBlueprint().getName().equals("Gandalf") || (cardType == CardType.COMPANION && target.getBlueprint().getSignet() == Signet.GANDALF))
                        return -1;
                }
                return 0;
            }
        };
    }
}
