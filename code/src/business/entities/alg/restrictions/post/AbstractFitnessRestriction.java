package business.entities.alg.restrictions.post;

public abstract class AbstractFitnessRestriction implements FitnessRestriction {
	
	private double weight;
	
	public AbstractFitnessRestriction(double weight) {
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
