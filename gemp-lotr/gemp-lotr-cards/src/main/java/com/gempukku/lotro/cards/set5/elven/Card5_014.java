package com.gempukku.lotro.cards.set5.elven;

import com.gempukku.lotro.cards.AbstractEvent;
import com.gempukku.lotro.cards.PlayConditions;
import com.gempukku.lotro.cards.actions.PlayEventAction;
import com.gempukku.lotro.cards.effects.LiberateASiteEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.ChooseAndHealCharactersEffect;

/**
 * Set: Battle of Helm's Deep
 * Side: Free
 * Culture: Elven
 * Twilight Cost: 2
 * Type: Event
 * Game Text: Regroup: Spot 3 Elf companions to heal a companion and liberate a site.
 */
public class Card5_014 extends AbstractEvent {
    public Card5_014() {
        super(Side.FREE_PEOPLE, 2, Culture.ELVEN, "That Is No Orc Horn", Phase.REGROUP);
    }

    @Override
    public boolean checkPlayRequirements(String playerId, LotroGame game, PhysicalCard self, int withTwilightRemoved, int twilightModifier, boolean ignoreRoamingPenalty, boolean ignoreCheckingDeadPile) {
        return super.checkPlayRequirements(playerId, game, self, withTwilightRemoved, twilightModifier, ignoreRoamingPenalty, ignoreCheckingDeadPile)
                && PlayConditions.canSpot(game, 3, Race.ELF, CardType.COMPANION);
    }

    @Override
    public PlayEventAction getPlayCardAction(String playerId, LotroGame game, PhysicalCard self, int twilightModifier, boolean ignoreRoamingPenalty) {
        PlayEventAction action = new PlayEventAction(self);
        action.appendEffect(
                new ChooseAndHealCharactersEffect(action, playerId, 1, 1, CardType.COMPANION) {
                    @Override
                    public String getText(LotroGame game) {
                        return "Heal a companion";
                    }
                });
        action.appendEffect(
                new LiberateASiteEffect(self, playerId, null));
        return action;
    }
}
