package monstertrainergame.controller;

import aetherdriven.Maths;
import com.badlogic.gdx.math.Vector2;
import monstertrainergame.model.Battle;
import monstertrainergame.model.FieldedMonster;

public class Pathfinder {
    // Not owned
    private final Battle battle;
    // Utilities
    private final Vector2 movement = new Vector2();
    private final Vector2 vec = new Vector2();

    public Pathfinder(Battle battle) {
        this.battle = battle;
    }

    public FieldedMonster determineMovementDestinationTowards(FieldedMonster movingMonster, FieldedMonster targetMonster, Vector2 result) {
        return determineMovementDestinationTowards(movingMonster, targetMonster.getPosition(), result);
    }

    public FieldedMonster determineMovementDestinationTowards(FieldedMonster movingMonster, Vector2 position, Vector2 result) {
        return determineMovementDestinationTowards(movingMonster, position.x, position.y, result);
    }

    public FieldedMonster determineMovementDestinationTowards(FieldedMonster movingMonster, float x, float y, Vector2 result) {
        movement.set(x, y).sub(movingMonster.getPosition());

        // Apply limited movement range
        float limit = movingMonster.getRemainingMovementRange();
        float distance = movement.len();
        if (distance > limit) {
            movement.scl(limit / distance);
        }
        result.set(movement).add(movingMonster.getPosition());

        // Find nearest monster in the moving monster's path
        return determineFirstIntersection(movingMonster, result);
    }

    public FieldedMonster determineFirstIntersection(FieldedMonster movingMonster, Vector2 result) {
        float minDist2 = Float.MAX_VALUE;
        FieldedMonster intersectingMonster = null;

        // For each monster, check if it is in the path
        for (FieldedMonster monster : battle) {
            if (monster != movingMonster && isInFrontOf(movingMonster, monster)) {
                float dist2 = determineIntersection(movingMonster, monster, vec);
                if (dist2 < movement.len2() && dist2 < minDist2) {
                    minDist2 = dist2;
                    result.set(vec);
                    intersectingMonster = monster;
                }
            }
        }

        return intersectingMonster;
    }

    private boolean isInFrontOf(FieldedMonster movingMonster, FieldedMonster otherMonster) {
        vec.set(otherMonster.getPosition()).sub(movingMonster.getPosition());
        return movement.dot(vec) > 0;
    }

    private float determineIntersection(FieldedMonster movingMonster, FieldedMonster otherMonster, Vector2 result) {
        // Determine coefficients
        float a = movement.y;
        float b = -movement.x;
        float c = a * movingMonster.getPosition().x + b * movingMonster.getPosition().y;
        float x0 = otherMonster.getPosition().x;
        float y0 = otherMonster.getPosition().y;

        // Determine intersections
        int n = Maths.intersection(a, b, c, x0, y0, movingMonster.getRadius() + otherMonster.getRadius());

        // Return nearest intersection
        if (n == 2) {
            float intersection1dist2 = Maths.intersection1.dst2(movingMonster.getPosition());
            float intersection2dist2 = Maths.intersection2.dst2(movingMonster.getPosition());
            if (intersection1dist2 < intersection2dist2) {
                result.set(Maths.intersection1);
                return intersection1dist2;
            } else {
                result.set(Maths.intersection2);
                return intersection2dist2;
            }
        } else {
            return Float.MAX_VALUE;
        }
    }
}
