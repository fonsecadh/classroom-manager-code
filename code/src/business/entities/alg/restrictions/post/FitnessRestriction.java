package business.entities.alg.restrictions.post;

import java.util.List;

import business.entities.alg.Assignment;
import business.entities.alg.Restriction;

public interface FitnessRestriction extends Restriction {

	public double getWeightedValue(List<Assignment> assignments);

}
