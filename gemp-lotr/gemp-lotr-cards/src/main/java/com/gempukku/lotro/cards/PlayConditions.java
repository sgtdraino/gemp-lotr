package com.gempukku.lotro.cards;

import com.gempukku.lotro.cards.effects.AddBurdenEffect;
import com.gempukku.lotro.cards.effects.ExertCharactersEffect;
import com.gempukku.lotro.cards.effects.TakeControlOfASiteEffect;
import com.gempukku.lotro.common.*;
import com.gempukku.lotro.filters.Filter;
import com.gempukku.lotro.filters.Filters;
import com.gempukku.lotro.game.LotroCardBlueprint;
import com.gempukku.lotro.game.PhysicalCard;
import com.gempukku.lotro.game.PhysicalCardVisitor;
import com.gempukku.lotro.game.state.GameState;
import com.gempukku.lotro.game.state.LotroGame;
import com.gempukku.lotro.logic.effects.*;
import com.gempukku.lotro.logic.modifiers.ModifierFlag;
import com.gempukku.lotro.logic.modifiers.ModifiersQuerying;
import com.gempukku.lotro.logic.timing.Effect;
import com.gempukku.lotro.logic.timing.EffectResult;
import com.gempukku.lotro.logic.timing.results.*;

import java.util.List;
import java.util.Map;

public class PlayConditions {
    public static boolean nonPlayZone(Zone zone) {
        return zone != Zone.SHADOW_CHARACTERS && zone != Zone.SUPPORT
                && zone != Zone.SUPPORT && zone != Zone.FREE_CHARACTERS
                && zone != Zone.ATTACHED && zone != Zone.ADVENTURE_PATH;
    }

    public static boolean canPayForShadowCard(LotroGame game, PhysicalCard self, int twilightModifier) {
        return game.getModifiersQuerying().getTwilightCost(game.getGameState(), self) + twilightModifier <= game.getGameState().getTwilightPool();
    }

    private static boolean containsPhase(Phase[] phases, Phase phase) {
        for (Phase phase1 : phases) {
            if (phase1 == phase)
                return true;
        }
        return false;
    }

    public static boolean canDiscardFromHand(LotroGame game, String playerId, int count, Filterable... cardFilter) {
        return Filters.filter(game.getGameState().getHand(playerId), game.getGameState(), game.getModifiersQuerying(), cardFilter).size() >= count;
    }

    public static boolean canDiscardCardsFromHandToPlay(PhysicalCard source, LotroGame game, String playerId, int count, Filterable... cardFilter) {
        return Filters.filter(game.getGameState().getHand(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(cardFilter, Filters.not(source))).size() >= count;
    }

    public static boolean canPlayCardDuringPhase(LotroGame game, Phase phase, PhysicalCard self) {
        return (phase == null || game.getGameState().getCurrentPhase() == phase)
                && self.getZone() == Zone.HAND
                && (!self.getBlueprint().isUnique() || !Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.name(self.getBlueprint().getName())));
    }

    public static boolean canPlayCardDuringPhase(LotroGame game, Phase[] phases, PhysicalCard self) {
        return (phases == null || containsPhase(phases, game.getGameState().getCurrentPhase()))
                && self.getZone() == Zone.HAND
                && (!self.getBlueprint().isUnique() || !Filters.canSpot(game.getGameState(), game.getModifiersQuerying(), Filters.name(self.getBlueprint().getName())));
    }

    public static boolean canUseFPCardDuringPhase(GameState gameState, Phase phase, PhysicalCard self) {
        return (phase == null || gameState.getCurrentPhase() == phase) && (self.getZone() == Zone.SUPPORT || self.getZone() == Zone.FREE_CHARACTERS || self.getZone() == Zone.ATTACHED);
    }

    public static boolean canUseShadowCardDuringPhase(GameState gameState, Phase phase, PhysicalCard self, int twilightCost) {
        return (phase == null || gameState.getCurrentPhase() == phase) && (self.getZone() == Zone.SUPPORT || self.getZone() == Zone.SHADOW_CHARACTERS || self.getZone() == Zone.ATTACHED)
                && twilightCost <= gameState.getTwilightPool();
    }

    public static boolean canUseShadowCardDuringPhase(LotroGame game, Phase phase, PhysicalCard self, int twilightCost) {
        return (phase == null || game.getGameState().getCurrentPhase() == phase) && (self.getZone() == Zone.SUPPORT || self.getZone() == Zone.SHADOW_CHARACTERS || self.getZone() == Zone.ATTACHED)
                && twilightCost <= game.getGameState().getTwilightPool();
    }

    public static boolean canUseStackedShadowCardDuringPhase(GameState gameState, Phase phase, PhysicalCard self, int twilightCost) {
        return (phase == null || gameState.getCurrentPhase() == phase) && self.getZone() == Zone.STACKED
                && twilightCost <= gameState.getTwilightPool();
    }

    public static boolean isPhase(LotroGame game, Phase phase) {
        return (game.getGameState().getCurrentPhase() == phase);
    }

    public static boolean canUseStackedFPCardDuringPhase(GameState gameState, Phase phase, PhysicalCard self) {
        return (phase == null || gameState.getCurrentPhase() == phase) && self.getZone() == Zone.STACKED;
    }

    public static boolean canUseSiteDuringPhase(GameState gameState, Phase phase, PhysicalCard self) {
        return (phase == null || gameState.getCurrentPhase() == phase) && (gameState.getCurrentSite() == self);
    }

    public static boolean stackedOn(PhysicalCard card, LotroGame game, Filterable... filters) {
        return Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), card.getStackedOn());
    }

    public static boolean checkUniqueness(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        LotroCardBlueprint blueprint = self.getBlueprint();
        return (!blueprint.isUnique()
                || (
                !Filters.canSpot(gameState, modifiersQuerying, Filters.name(blueprint.getName()))
                        && (self.getZone() == Zone.DEAD || (Filters.filter(gameState.getDeadPile(self.getOwner()), gameState, modifiersQuerying, Filters.name(blueprint.getName())).size() == 0))));
    }

    private static int getTotalCompanions(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        return Filters.countActive(gameState, modifiersQuerying, Filters.type(CardType.COMPANION))
                + Filters.filter(gameState.getDeadPile(playerId), gameState, modifiersQuerying, Filters.type(CardType.COMPANION)).size();
    }

    public static boolean checkRuleOfNine(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (self.getZone() == Zone.DEAD)
            return (getTotalCompanions(self.getOwner(), gameState, modifiersQuerying) <= 9);
        else
            return (getTotalCompanions(self.getOwner(), gameState, modifiersQuerying) < 9);
    }

    public static boolean canHealByDiscarding(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        LotroCardBlueprint blueprint = self.getBlueprint();
        if (self.getZone() == Zone.HAND
                && (blueprint.getCardType() == CardType.COMPANION || blueprint.getCardType() == CardType.ALLY)
                && gameState.getCurrentPhase() == Phase.FELLOWSHIP
                && blueprint.isUnique()) {
            PhysicalCard matchingName = Filters.findFirstActive(gameState, modifiersQuerying, Filters.name(blueprint.getName()));
            if (matchingName != null)
                return gameState.getWounds(matchingName) > 0;
        }
        return false;
    }

    public static boolean canSelfExert(PhysicalCard self, LotroGame game) {
        return canExert(self, game, Filters.sameCard(self));
    }

    public static boolean canSelfExert(PhysicalCard self, int times, LotroGame game) {
        return canExert(self, game, times, Filters.sameCard(self));
    }

    public static boolean canExert(PhysicalCard source, LotroGame game, Filterable... filters) {
        return canExert(source, game.getGameState(), game.getModifiersQuerying(), filters);
    }

    public static boolean canExert(PhysicalCard source, LotroGame game, int times, Filterable... filters) {
        return canExert(source, game.getGameState(), game.getModifiersQuerying(), times, filters);
    }

    public static boolean canSpot(LotroGame game, Filterable... filters) {
        return canSpot(game, 1, filters);
    }

    public static boolean canSpot(LotroGame game, int count, Filterable... filters) {
        return Filters.countSpottable(game.getGameState(), game.getModifiersQuerying(), filters) >= count;
    }

    public static boolean canSpotThreat(LotroGame game, int count) {
        return game.getGameState().getThreats() >= count;
    }

    public static boolean canSpotBurdens(LotroGame game, int count) {
        return game.getGameState().getBurdens() >= count;
    }

    public static boolean hasInitiative(LotroGame game, Side side) {
        return game.getModifiersQuerying().hasInitiative(game.getGameState()) == side;
    }

    public static boolean canAddThreat(LotroGame game, PhysicalCard card, int count) {
        return Filters.countActive(game.getGameState(), game.getModifiersQuerying(), CardType.COMPANION) - game.getGameState().getThreats() >= count;
    }

    public static boolean canRemoveThreat(LotroGame game, PhysicalCard card, int count) {
        return game.getGameState().getThreats() >= count;
    }

    public static boolean canRemoveBurdens(LotroGame game, PhysicalCard card, int count) {
        return game.getGameState().getBurdens() >= count;
    }

    public static boolean canExertMultiple(PhysicalCard source, LotroGame game, int times, int count, Filterable... filters) {
        return canExertMultiple(source, game.getGameState(), game.getModifiersQuerying(), times, count, filters);
    }

    public static boolean canExertMultiple(final PhysicalCard source, final GameState gameState, final ModifiersQuerying modifiersQuerying, final int times, final int count, Filterable... filters) {
        final Filter filter = Filters.and(filters);
        return gameState.iterateActiveCards(
                new PhysicalCardVisitor() {
                    private int _exertableCount;

                    @Override
                    public boolean visitPhysicalCard(PhysicalCard physicalCard) {
                        if (filter.accepts(gameState, modifiersQuerying, physicalCard)
                                && (modifiersQuerying.getVitality(gameState, physicalCard) > times)
                                && modifiersQuerying.canBeExerted(gameState, source, physicalCard))
                            _exertableCount++;
                        return _exertableCount >= count;
                    }
                });
    }

    public static boolean canPlayFromHand(String playerId, LotroGame game, Filterable... filters) {
        return Filters.filter(game.getGameState().getHand(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(filters, Filters.playable(game))).size() > 0;
    }

    public static boolean canPlayFromHand(String playerId, LotroGame game, int twilightModifier, Filterable... filters) {
        return Filters.filter(game.getGameState().getHand(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(filters, Filters.playable(game, twilightModifier))).size() > 0;
    }

    public static boolean canPlayFromDeadPile(String playerId, LotroGame game, Filterable... filters) {
        return Filters.filter(game.getGameState().getDeadPile(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(filters, Filters.playable(game))).size() > 0;
    }

    public static boolean canPlayFromDiscard(String playerId, LotroGame game, Filterable... filters) {
        if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.CANT_PLAY_FROM_DISCARD_OR_DECK))
            return false;
        return Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(filters, Filters.playable(game))).size() > 0;
    }

    public static boolean canPlayFromDiscard(String playerId, LotroGame game, int modifier, Filterable... filters) {
        if (game.getModifiersQuerying().hasFlagActive(game.getGameState(), ModifierFlag.CANT_PLAY_FROM_DISCARD_OR_DECK))
            return false;
        return Filters.filter(game.getGameState().getDiscard(playerId), game.getGameState(), game.getModifiersQuerying(), Filters.and(filters, Filters.playable(game, modifier))).size() > 0;
    }

    public static boolean canDiscardFromPlay(final PhysicalCard source, LotroGame game, final PhysicalCard card) {
        return game.getModifiersQuerying().canBeDiscardedFromPlay(game.getGameState(), card, source);
    }

    public static boolean canSelfDiscard(PhysicalCard source, LotroGame game) {
        return canDiscardFromPlay(source, game, source);
    }

    public static boolean canDiscardFromPlay(final PhysicalCard source, LotroGame game, int count, final Filterable... filters) {
        return Filters.countActive(game.getGameState(), game.getModifiersQuerying(), Filters.and(filters,
                new Filter() {
                    @Override
                    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                        return modifiersQuerying.canBeDiscardedFromPlay(gameState, physicalCard, source);
                    }
                })) >= count;
    }

    public static boolean canDiscardFromPlay(final PhysicalCard source, LotroGame game, final Filterable... filters) {
        return canDiscardFromPlay(source, game, 1, filters);
    }

    public static boolean canExert(final PhysicalCard source, final GameState gameState, final ModifiersQuerying modifiersQuerying, Filterable... filters) {
        return canExert(source, gameState, modifiersQuerying, 1, filters);
    }

    public static boolean canExert(final PhysicalCard source, final GameState gameState, final ModifiersQuerying modifiersQuerying, final int times, Filterable... filters) {
        final Filter filter = Filters.and(filters);
        return gameState.iterateActiveCards(
                new PhysicalCardVisitor() {
                    @Override
                    public boolean visitPhysicalCard(PhysicalCard physicalCard) {
                        return filter.accepts(gameState, modifiersQuerying, physicalCard)
                                && (modifiersQuerying.getVitality(gameState, physicalCard) > times)
                                && modifiersQuerying.canBeExerted(gameState, source, physicalCard);
                    }
                });
    }

    public static boolean startOfPhase(LotroGame game, EffectResult effectResult, Phase phase) {
        return (effectResult.getType() == EffectResult.Type.START_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase);
    }

    public static boolean endOfPhase(LotroGame game, EffectResult effectResult, Phase phase) {
        return (effectResult.getType() == EffectResult.Type.END_OF_PHASE
                && game.getGameState().getCurrentPhase() == phase);
    }

    public static boolean canExert(PhysicalCard source, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return canExert(source, gameState, modifiersQuerying, Filters.sameCard(card));
    }

    public static boolean controllsSite(GameState gameState, ModifiersQuerying modifiersQuerying, String playerId) {
        return Filters.findFirstActive(gameState, modifiersQuerying, Filters.siteControlled(playerId)) != null;
    }

    public static boolean winsSkirmish(LotroGame game, EffectResult effectResult, Filterable... filters) {
        return winsSkirmish(game.getGameState(), game.getModifiersQuerying(), effectResult, filters);
    }

    public static boolean winsSkirmish(EffectResult effectResult, PhysicalCard character) {
        EffectResult.Type effectType = effectResult.getType();
        if (effectType == EffectResult.Type.RESOLVE_SKIRMISH || effectType == EffectResult.Type.OVERWHELM_IN_SKIRMISH) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            List<PhysicalCard> winners = skirmishResult.getWinners();
            return winners.contains(character);
        }
        return false;
    }

    public static boolean winsSkirmish(GameState gameState, ModifiersQuerying modifiersQuerying, EffectResult effectResult, Filterable... filter) {
        EffectResult.Type effectType = effectResult.getType();
        if (effectType == EffectResult.Type.RESOLVE_SKIRMISH || effectType == EffectResult.Type.OVERWHELM_IN_SKIRMISH) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            return (Filters.filter(skirmishResult.getWinners(), gameState, modifiersQuerying, filter).size() > 0);
        }
        return false;
    }

    public static boolean winsSkirmishAgainst(LotroGame game, EffectResult effectResult, Filterable winnerFilter, Filterable loserFilter) {
        return winsSkirmishAgainst(game.getGameState(), game.getModifiersQuerying(), effectResult, winnerFilter, loserFilter);
    }

    public static boolean winsSkirmishAgainst(GameState gameState, ModifiersQuerying modifiersQuerying, EffectResult effectResult, Filterable winnerFilter, Filterable loserFilter) {
        EffectResult.Type effectType = effectResult.getType();
        if (effectType == EffectResult.Type.RESOLVE_SKIRMISH || effectType == EffectResult.Type.OVERWHELM_IN_SKIRMISH) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            List<PhysicalCard> winners = skirmishResult.getWinners();
            List<PhysicalCard> losers = skirmishResult.getLosers();
            return (Filters.filter(winners, gameState, modifiersQuerying, winnerFilter).size() > 0)
                    && (Filters.filter(losers, gameState, modifiersQuerying, loserFilter).size() > 0);
        }
        return false;
    }

    public static boolean losesSkirmish(GameState gameState, ModifiersQuerying modifiersQuerying, EffectResult effectResult, Filter filter) {
        EffectResult.Type effectType = effectResult.getType();
        if (effectType == EffectResult.Type.RESOLVE_SKIRMISH || effectType == EffectResult.Type.OVERWHELM_IN_SKIRMISH) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            return (Filters.filter(skirmishResult.getLosers(), gameState, modifiersQuerying, filter).size() > 0);
        }
        return false;
    }

    public static boolean losesSkirmishAgainst(GameState gameState, ModifiersQuerying modifiersQuerying, EffectResult effectResult, Filter loserFilter, Filter winnerFilter) {
        EffectResult.Type effectType = effectResult.getType();
        if (effectType == EffectResult.Type.RESOLVE_SKIRMISH || effectType == EffectResult.Type.OVERWHELM_IN_SKIRMISH) {
            SkirmishResult skirmishResult = (SkirmishResult) effectResult;
            return (Filters.filter(skirmishResult.getLosers(), gameState, modifiersQuerying, loserFilter).size() > 0)
                    && Filters.filter(skirmishResult.getWinners(), gameState, modifiersQuerying, winnerFilter).size() > 0;
        }
        return false;
    }

    public static boolean addedBurden(LotroGame game, EffectResult effectResult, Filterable... sourceFilters) {
        if (effectResult.getType() == EffectResult.Type.ADD_BURDEN) {
            AddBurdenResult burdenResult = (AddBurdenResult) effectResult;
            return (Filters.and(sourceFilters).accepts(game.getGameState(), game.getModifiersQuerying(), burdenResult.getSource()));
        }
        return false;
    }

    public static boolean addedThreat(LotroGame game, EffectResult effectResult, Filterable... sourceFilters) {
        if (effectResult.getType() == EffectResult.Type.ADD_THREAT) {
            AddThreatResult burdenResult = (AddThreatResult) effectResult;
            return (Filters.and(sourceFilters).accepts(game.getGameState(), game.getModifiersQuerying(), burdenResult.getSource()));
        }
        return false;
    }

    public static boolean assigned(LotroGame game, EffectResult effectResult, Side side, Filter againstFilter, Filterable... cardFilters) {
        if (effectResult.getType() == EffectResult.Type.ASSIGNMENT) {
            AssignmentResult assignmentResult = (AssignmentResult) effectResult;
            if (side != null) {
                if (assignmentResult.getPlayerId().equals(game.getGameState().getCurrentPlayerId())) {
                    if (side == Side.SHADOW)
                        return false;
                } else {
                    if (side == Side.FREE_PEOPLE)
                        return false;
                }
            }

            final Map<PhysicalCard, List<PhysicalCard>> assignments = assignmentResult.getAssignments();
            for (PhysicalCard matchingFPCard : Filters.filter(assignments.keySet(), game.getGameState(), game.getModifiersQuerying(), cardFilters)) {
                if (Filters.filter(assignments.get(matchingFPCard), game.getGameState(), game.getModifiersQuerying(), againstFilter).size() > 0)
                    return true;
            }

            for (PhysicalCard matchingAgainstCard : Filters.filter(assignments.keySet(), game.getGameState(), game.getModifiersQuerying(), againstFilter)) {
                if (Filters.filter(assignments.get(matchingAgainstCard), game.getGameState(), game.getModifiersQuerying(), cardFilters).size() > 0)
                    return true;
            }
        }
        return false;
    }

    public static boolean canCardAssignToSkirmish(PhysicalCard source, LotroGame game, PhysicalCard card) {
        return Filters.canBeAssignedToSkirmishByEffect(source.getBlueprint().getSide()).accepts(game.getGameState(), game.getModifiersQuerying(), card);
    }

    public static boolean canCardAssignToSkirmish(PhysicalCard source, LotroGame game, Filter filter) {
        return Filters.countSpottable(game.getGameState(), game.getModifiersQuerying(), Filters.canBeAssignedToSkirmishByEffect(source.getBlueprint().getSide()), filter) > 0;
    }

    public static boolean isWounded(EffectResult effectResult, PhysicalCard character) {
        if (effectResult.getType() == EffectResult.Type.WOUND) {
            return ((WoundResult) effectResult).getWoundedCards().contains(character);
        }
        return false;
    }

    public static boolean isTakingControlOfSite(Effect effect, LotroGame game, Filterable... sourceFilters) {
        if (effect.getType() == Effect.Type.BEFORE_TAKE_CONTROL_OF_A_SITE) {
            TakeControlOfASiteEffect takeControlOfASiteEffect = (TakeControlOfASiteEffect) effect;
            return !takeControlOfASiteEffect.isPrevented() && Filters.and(sourceFilters).accepts(game.getGameState(), game.getModifiersQuerying(), takeControlOfASiteEffect.getSource());
        }
        return false;
    }

    public static boolean isAddingBurden(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_ADD_BURDENS) {
            AddBurdenEffect addBurdenEffect = (AddBurdenEffect) effect;
            return !addBurdenEffect.isPrevented() && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), addBurdenEffect.getSource());
        }
        return false;
    }

    public static boolean isAddingTwilight(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_ADD_TWILIGHT) {
            AddTwilightEffect addTwilightEffect = (AddTwilightEffect) effect;
            return !addTwilightEffect.isPrevented() && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), addTwilightEffect.getSource());
        }
        return false;
    }

    public static boolean isGettingHealed(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_HEALED) {
            HealCharactersEffect healEffect = (HealCharactersEffect) effect;
            return Filters.filter(healEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingKilled(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_KILLED) {
            KillEffect killEffect = (KillEffect) effect;
            return Filters.filter(killEffect.getCharactersToBeKilled(), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingWoundedBy(Effect effect, LotroGame game, Filterable sourceFilter, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_WOUND) {
            WoundCharactersEffect woundEffect = (WoundCharactersEffect) effect;
            if (woundEffect.getSources() != null && Filters.filter(woundEffect.getSources(), game.getGameState(), game.getModifiersQuerying(), sourceFilter).size() > 0)
                return Filters.filter(woundEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingExerted(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_EXERT) {
            ExertCharactersEffect woundEffect = (ExertCharactersEffect) effect;
            return Filters.filter(woundEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingWounded(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_WOUND) {
            WoundCharactersEffect woundEffect = (WoundCharactersEffect) effect;
            return Filters.filter(woundEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingDiscardedBy(Effect effect, LotroGame game, Filterable sourceFilter, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_DISCARD_FROM_PLAY) {
            DiscardCardsFromPlayEffect discardEffect = (DiscardCardsFromPlayEffect) effect;
            if (discardEffect.getSource() != null && Filters.and(sourceFilter).accepts(game.getGameState(), game.getModifiersQuerying(), discardEffect.getSource()))
                return Filters.filter(discardEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean isGettingDiscarded(Effect effect, LotroGame game, Filterable... filters) {
        if (effect.getType() == Effect.Type.BEFORE_DISCARD_FROM_PLAY) {
            DiscardCardsFromPlayEffect discardEffect = (DiscardCardsFromPlayEffect) effect;
            return Filters.filter(discardEffect.getAffectedCardsMinusPrevented(game), game.getGameState(), game.getModifiersQuerying(), filters).size() > 0;
        }
        return false;
    }

    public static boolean played(GameState gameState, ModifiersQuerying modifiersQuerying, EffectResult effectResult, Filter filter) {
        if (effectResult.getType() == EffectResult.Type.PLAY) {
            PhysicalCard playedCard = ((PlayCardResult) effectResult).getPlayedCard();
            return filter.accepts(gameState, modifiersQuerying, playedCard);
        }
        return false;
    }

    public static boolean activated(LotroGame game, EffectResult effectResult, Filterable... filters) {
        if (effectResult.getType() == EffectResult.Type.ACTIVATE) {
            PhysicalCard source = ((ActivateCardResult) effectResult).getSource();
            return Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), source);
        }
        return false;
    }

    public static boolean played(LotroGame game, EffectResult effectResult, Filterable... filters) {
        return played(game.getGameState(), game.getModifiersQuerying(), effectResult, Filters.and(filters));
    }

    public static boolean movesTo(LotroGame game, EffectResult effectResult, Filterable... filters) {
        if (effectResult.getType() == EffectResult.Type.WHEN_MOVE_TO
                && Filters.and(filters).accepts(game.getGameState(), game.getModifiersQuerying(), game.getGameState().getCurrentSite())) {
            return true;
        }
        return false;
    }

    public static boolean canRemoveTokens(LotroGame game, Token token, int count, Filterable... fromFilters) {
        return Filters.filterActive(game.getGameState(), game.getModifiersQuerying(), Filters.and(fromFilters, Filters.hasToken(token, count))).size() > 0;
    }

    public static boolean canRemoveToken(GameState gameState, PhysicalCard from, Token token) {
        return canRemoveToken(gameState, from, token, 1);
    }

    public static boolean canRemoveToken(GameState gameState, PhysicalCard from, Token token, int count) {
        return gameState.getTokenCount(from, token) >= count;
    }
}
