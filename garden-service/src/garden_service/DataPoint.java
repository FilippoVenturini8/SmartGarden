package garden_service;

class DataPoint {
	private double temperature;
	private int lux;
	private String modality;
	
	public DataPoint(double temperature, int lux, String modality) {
		this.temperature = temperature;
		this.lux = lux;
		this.modality = modality;
	}
	
	public double getTemperature() {
		return temperature;
	}
	
	public int getLux() {
		return lux;
	}
	
	public String getModality() {
		return modality;
	}
}
