package com.gempukku.lotro.cards.set3.dwarven;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.TriggerConditions;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.actions.OptionalTriggerAction;
import com.gempukku.lotro.logic.effects.DrawCardsEffect;
import com.gempukku.lotro.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Realms of Elf-lords
 * Side: Free
 * Culture: Dwarven
 * Twilight Cost: 2
 * Type: Possession
 * Game Text: Tale. Bearer must be a Dwarf. At the start of each fellowship phase when the fellowship is at site 4
 * or higher, you may draw a card for each Dwarf companion.
 */
public class Card3_001 extends AbstractAttachable {
    public Card3_001() {
        super(Side.FREE_PEOPLE, CardType.POSSESSION, 2, Culture.DWARVEN, null, "Book of Mazarbul", null, true);
        addKeyword(Keyword.TALE);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Race.DWARF;
    }

    @Override
    public List<OptionalTriggerAction> getOptionalAfterTriggers(String playerId, LotroGame game, EffectResult effectResult, PhysicalCard self) {
        if (TriggerConditions.startOfPhase(game, effectResult, Phase.FELLOWSHIP)
                && game.getGameState().getCurrentSiteNumber() >= 4
                && game.getGameState().getCurrentSiteBlock() == Block.FELLOWSHIP) {
            OptionalTriggerAction action = new OptionalTriggerAction(self);
            int dwarfCompanions = Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Race.DWARF, CardType.COMPANION);
            action.appendEffect(
                    new DrawCardsEffect(action, playerId, dwarfCompanions));
            return Collections.singletonList(action);
        }
        return null;
    }
}
