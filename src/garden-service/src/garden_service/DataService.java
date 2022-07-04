package garden_service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 * Data Service as a vertx event-loop 
 */
public class DataService extends AbstractVerticle {

	private int port;
	private static final int MAX_SIZE = 10;
	private LinkedList<DataPoint> values;
	private boolean activateLightSystem = false;
	private int analogicLightValue = 0;
	private boolean openIrrigationSystem = false;
	private boolean updateLuminosityAndSpeed = false;
	private boolean isIrrigationSleeping;
	private int irrigationSpeed = 50;
	private int lastLux = 0;
	private int lastTemp = 0;
	private String modality = "AUT";
	private boolean isTemperatureInAlarm = false;
	
	public DataService(int port) {
		values = new LinkedList<>();		
		this.port = port;
	}

	@Override
	public void start() {		
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);
		router.get("/api/data").handler(this::handleGetData);		
		vertx
			.createHttpServer()
			.requestHandler(router)
			.listen(port);

		log("Service ready.");
	}
	
	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		// log("new msg "+routingContext.getBodyAsString());
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			int temperature = res.getInteger("temperature");
			int lux = res.getInteger("lux");
			
			values.addFirst(new DataPoint(temperature, lux));
			if (values.size() > MAX_SIZE) {
				values.removeLast();
			}
			
			checkData();
			
			//log("New Temp: " + temperature + " Lux " + lux);
			response.setStatusCode(200).end();
		}
	}
	
	private void checkData() {
		DataPoint lastMeasurement = values.getFirst();
		int lux = lastMeasurement.getLux();
		
		if(lastLux != lux) {
			if(lux < 5) {
				
				if(!activateLightSystem) {
					activateLightSystem = true;
				}else {
					updateLuminosityAndSpeed = true;
				}
				
				switch(lux) {
					case 4:
						analogicLightValue = 1;
						break;
					case 3:
						analogicLightValue = 2;
						break;
					case 2:
						analogicLightValue = 3;
						break;
					case 1:
						analogicLightValue = 4;
						break;
					case 0:
						analogicLightValue = 5;
						break;
				}
			}
			
			if(lux >= 5 && activateLightSystem) {
				activateLightSystem = false;
			}
			
			if(lux < 2 && !this.openIrrigationSystem) {
				this.openIrrigationSystem = true;
			}
			
			if(lux >=2 && this.openIrrigationSystem){
				this.openIrrigationSystem = false;
			}
			
			lastLux = lux;
		}
		
		int temp = lastMeasurement.getTemperature();
		if(temp != lastTemp) {
			if(this.openIrrigationSystem) {
				updateLuminosityAndSpeed = true;
			}
			this.isTemperatureInAlarm = false;
			switch(temp) {
				case 1:
					irrigationSpeed = 50;
					break;
				case 2:
					irrigationSpeed = 40;
					break;
				case 3:
					irrigationSpeed = 30;
					break;
				case 4:
					irrigationSpeed = 20;
					break;
				case 5:
					irrigationSpeed = 10;
					this.isTemperatureInAlarm = true;
					break;
			}
			
			lastTemp = temp;
		}
		
	}
	
	private void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();
		DataPoint p = values.getFirst();
		JsonObject data = new JsonObject();
		data.put("lux", p.getLux());
		data.put("temperature", p.getTemperature());
		data.put("modality", this.modality);
		data.put("irrigationSleeping", this.isIrrigationSleeping);
		arr.add(data);
		routingContext.response()
			.putHeader("content-type", "application/json")
			.end(arr.encodePrettily());
	}
	
	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] "+msg);
	}

	public boolean activateLightSystem() {
		return activateLightSystem;
	}

	public int getAnalogicLightValue() {
		return analogicLightValue;
	}
	
	public void setModality(String modality) {
		this.modality = modality;
	}
	
	public boolean getOpenIrrigationSystem() {
		return this.openIrrigationSystem;
	}
	
	public boolean getIsTemperatureInAlarm() {
		return this.isTemperatureInAlarm;
	}
	
	public void setOpenIrrigationSystem(boolean value) {
		this.openIrrigationSystem = value;
	}
	
	public int getIrrigationSpeed() {
		return this.irrigationSpeed;
	}
	
	public void setIsIrrigationSleeping(boolean value) {
		this.isIrrigationSleeping = value;
	}
	
	public boolean getUpdateLuminosityAndSpeed() {
		return this.updateLuminosityAndSpeed;
	}
	
	public void setUpdateLuminosityAndSpeed(boolean value) {
		this.updateLuminosityAndSpeed = value;
	}
	
	public void setIsTemperatureInAlarm(boolean value) {
		this.isTemperatureInAlarm = value;
	}
	

}