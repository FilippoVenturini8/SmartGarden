package garden_service;

class DataPoint {
	private int temperature;
	private int lux;
	private String modality;
	
	public DataPoint(int temperature, int lux, String modality) {
		this.temperature = temperature;
		this.lux = lux;
		this.modality = modality;
	}
	
	public int getTemperature() {
		return temperature;
	}
	
	public int getLux() {
		return lux;
	}
	
	public String getModality() {
		return modality;
	}
}
