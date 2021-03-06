package com.gempukku.lotro.cards.set3.sauron;

import com.gempukku.lotro.cards.AbstractOldEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.DiscardTopCardFromDeckEffect;
import com.gempukku.lotro.cards.effects.choose.ChooseAndExertCharactersEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;

/**
 * Set: Realms of Elf-lords
 * Side: Shadow
 * Culture: Sauron
 * Twilight Cost: 1
 * Type: Event
 * Game Text: Regroup: Exert a [SAURON] minion to discard a card from the top of the Free Peoples player's draw deck
 * for each of these races you can spot in the fellowship: Dwarf, Elf, Man, and Wizard.
 */
public class Card3_092 extends AbstractOldEvent {
    public Card3_092() {
        super(Side.SHADOW, Culture.SAURON, "Massing in the East", Phase.REGROUP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canExert(self, game, Culture.SAURON, CardType.MINION);
    }

    @Override
    public int getTwilightCost() {
        return 1;
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendCost(
                new ChooseAndExertCharactersEffect(action, playerId, 1, 1, Culture.SAURON, CardType.MINION));
        int cardsCount = 0;
        if (Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.COMPANION, Race.DWARF))
            cardsCount++;
        if (Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.COMPANION, Race.ELF))
            cardsCount++;
        if (Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.COMPANION, Race.MAN))
            cardsCount++;
        if (Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), CardType.COMPANION, Race.WIZARD))
            cardsCount++;
        if (cardsCount > 0)
            action.appendEffect(
                    new DiscardTopCardFromDeckEffect(self, game.getGameState().getCurrentPlayerId(), cardsCount, true));

        return action;
    }
}
