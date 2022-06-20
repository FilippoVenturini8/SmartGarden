package garden_service;

class DataPoint {
	private double temperature;
	private int lux;
	
	public DataPoint(double temperature, int lux) {
		this.temperature = temperature;
		this.lux = lux;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public int getLux() {
		return lux;
	}
}
