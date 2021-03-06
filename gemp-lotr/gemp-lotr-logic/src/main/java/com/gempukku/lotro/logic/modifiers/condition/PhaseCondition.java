package com.gempukku.lotro.logic.modifiers.condition;

import com.gempukku.lotro.common.Phase;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.logic.modifiers.Condition;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;

public class PhaseCondition implements Condition {
    private Phase _phase;

    public PhaseCondition(Phase phase) {
        _phase = phase;
    }

    @Override
    public boolean isFullfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return _phase == null || gameState.getCurrentPhase() == _phase;
    }
}
