package business.alg.greed.logic.collisions;

import java.util.HashMap;
import java.util.Map;

import business.problem.model.Group;

public class CollisionManager {
	/**
	 * Map that represents the collisions between groups.<br>
	 * <br>
	 * Key: The group ({@link Group}) ID.<br>
	 * Value: Map containing all the groups evaluated against the key group,
	 * with a boolean that indicates whether or not they collide.
	 */
	private Map<String, Map<String, Boolean>> collisionMap;

	public CollisionManager() {
		this.collisionMap = new HashMap<String, Map<String, Boolean>>();
	}

	public boolean groupsCollide(Group g1, Group g2)
	{
		Map<String, Boolean> g1CollisionMap, g2CollisionMap;

		g1CollisionMap = this.collisionMap.get(g1.getCode());
		g2CollisionMap = this.collisionMap.get(g2.getCode());
		if (g1CollisionMap == null) {
			g1CollisionMap = new HashMap<String, Boolean>();
		}
		if (g2CollisionMap == null) {
			g2CollisionMap = new HashMap<String, Boolean>();
		}
		// If the collision has been checked before
		if (g1CollisionMap.get(g2.getCode()) != null)
			return g1CollisionMap.get(g2.getCode());

		// If the collision has not been checked before
		boolean collision = g1.collidesWith(g2);
		g1CollisionMap.put(g2.getCode(), collision);
		g2CollisionMap.put(g1.getCode(), collision);

		this.collisionMap.put(g1.getCode(), g1CollisionMap);
		this.collisionMap.put(g2.getCode(), g2CollisionMap);

		return collision;
	}
}
