package business.alg.gen.model;

import java.util.HashMap;
import java.util.Map;

public class Metrics {

	private static final Metrics INSTANCE = new Metrics();

	private Map<String, String> metrics;

	private Metrics() {
		this.metrics = new HashMap<String, String>();
	}

	public static Metrics getInstance() {
		return INSTANCE;
	}

	public void set(String name, int i) {
		this.metrics.put(name, Integer.toString(i));
	}

	public void set(String name, long l) {
		this.metrics.put(name, Long.toString(l));
	}

	public void set(String name, double d) {
		this.metrics.put(name, Double.toString(d));
	}

	public int getInt(String name) {
		String value = this.metrics.get(name);
		return value != null ? Integer.parseInt(value) : 0;
	}

	public long getLong(String name) {
		String value = this.metrics.get(name);
		return value != null ? Long.parseLong(value) : 0L;
	}

	public double getDouble(String name) {
		String value = this.metrics.get(name);
		return value != null ? Double.parseDouble(value) : Double.NaN;
	}

}
