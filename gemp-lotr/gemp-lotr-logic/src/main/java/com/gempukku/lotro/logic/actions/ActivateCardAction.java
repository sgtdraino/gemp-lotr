package com.gempukku.lotro.logic.actions;

import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.GameUtils;
import com.gempukku.lotro.logic.effects.ActivateCardEffect;
import com.gempukku.lotro.logic.timing.Effect;

public class ActivateCardAction extends AbstractCostToEffectAction {
    private PhysicalCard _physicalCard;

    private ActivateCardEffect _activateCardEffect;

    private String _message;

    private boolean _sentMessage;
    private boolean _activated;

    private boolean _prevented;

    private String _text;

    public ActivateCardAction(PhysicalCard physicalCard) {
        _physicalCard = physicalCard;
        _text = "Use " + GameUtils.getFullName(_physicalCard);
        _message = GameUtils.getCardLink(_physicalCard) + " is used";
    }

    @Override
    public Type getType() {
        return Type.SPECIAL_ABILITY;
    }

    @Override
    public PhysicalCard getActionSource() {
        return _physicalCard;
    }

    @Override
    public PhysicalCard getActionAttachedToCard() {
        return _physicalCard;
    }

    public void setText(String text) {
        _text = text;
    }

    @Override
    public String getText(LotroGame game) {
        return _text;
    }

    public void prevent() {
        _prevented = true;
    }

    @Override
    public Effect nextEffect(LotroGame game) {
        if (!_sentMessage) {
            _sentMessage = true;
            if (_physicalCard != null && _physicalCard.getZone().isInPlay())
                game.getGameState().activatedCard(getPerformingPlayer(), _physicalCard);
            if (_message != null)
                game.getGameState().sendMessage(_message);
        }

        if (!isCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_activated) {
                _activated = true;
                _activateCardEffect = new ActivateCardEffect(_physicalCard, getActionTimeword());
                return _activateCardEffect;
            }

            if (!_activateCardEffect.getActivateCardResult().isEffectCancelled() && !_prevented) {
                Effect effect = getNextEffect();
                if (effect != null)
                    return effect;
            }
        }
        return null;
    }
}
