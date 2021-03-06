package com.gempukku.lotro.cards.set20.wraith;

import com.gempukku.lotro.cards.AbstractAttachable;
import com.gempukku.lotro.cards.modifiers.VitalityModifier;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.modifiers.CantTakeWoundsModifier;
import com.gempukku.lotro.logic.modifiers.GameHasCondition;
import com.gempukku.lotro.logic.modifiers.Modifier;
import com.gempukku.lotro.logic.modifiers.StrengthModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * 3
 * •Black Beast of Angmar, Death in Flight
 * Ringwraith	Possession • Mount
 * 2	1
 * Bearer must be The Witch-king.
 * While the Ring-bearer has 6 or less resistance, The Witch-king may not take wounds.
 */
public class Card20_284 extends AbstractAttachable {
    public Card20_284() {
        super(Side.SHADOW, CardType.POSSESSION, 3, Culture.WRAITH, PossessionClass.MOUNT, "Black Beast of Angmar", "Death in Flight", true);
    }

    @Override
    protected Filterable getValidTargetFilter(String playerId, LotroGame game, PhysicalCard self) {
        return Filters.witchKing;
    }

    @Override
    public List<? extends Modifier> getAlwaysOnModifiers(LotroGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CantTakeWoundsModifier(self,
                new GameHasCondition(Filters.ringBearer, Filters.maxResistance(6)), Filters.witchKing));
        modifiers.add(
                new StrengthModifier(self, Filters.hasAttached(self), 2));
        modifiers.add(
                new VitalityModifier(self, Filters.hasAttached(self), 1));
        return modifiers;
    }
}
