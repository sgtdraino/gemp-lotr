package com.gempukku.lotro.cards.modifiers;

import com.gempukku.lotro.common.Filterable;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.AbstractModifier;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifierEffect;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class DoesNotAddToArcheryTotalModifier extends AbstractModifier {
    public DoesNotAddToArcheryTotalModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    public DoesNotAddToArcheryTotalModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Does not add to archery total", affectFilter, condition, ModifierEffect.ARCHERY_MODIFIER);
    }

    @Override
    public boolean addsToArcheryTotal(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return false;
    }
}
