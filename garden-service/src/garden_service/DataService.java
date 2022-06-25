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
	private boolean toggleIrrigationSystem = false;
	private int irrigationSpeed = 0;
	private int lastLux = 0;
	private int lastTemp = 0;
	private String modality = "AUT";
	private boolean isTemperatureInAlarm = false;
	public int tempTemp = 5;
	
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
			
			values.addFirst(new DataPoint(tempTemp, lux, modality));
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
		if(lux < 5 && lastLux != lux) {
			activateLightSystem = true;
			analogicLightValue = (int)(((lux*8)/5)-1);
			lastLux = lux;
			System.out.println(analogicLightValue);
		}else if(lux >= 5 && activateLightSystem) {
			activateLightSystem = false;
		}
		
		int temp = lastMeasurement.getTemperature();
		this.isTemperatureInAlarm = false;
		if(temp != lastTemp) {
			switch(temp) {
				case 1:
					irrigationSpeed = 0;
					break;
				case 2:
					irrigationSpeed = 50;
					break;
				case 3:
					irrigationSpeed = 40;
					break;
				case 4:
					irrigationSpeed = 30;
					break;
				case 5:
					irrigationSpeed = 20;
					this.isTemperatureInAlarm = true;
					break;
			}
			toggleIrrigationSystem = true;
		}
		
	}
	
	private void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();
		DataPoint p = values.getFirst();
		JsonObject data = new JsonObject();
		data.put("lux", p.getLux());
		data.put("temperature", p.getTemperature());
		data.put("modality", p.getModality());
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
	
	public boolean toggleIrrigationSystem() {
		return this.toggleIrrigationSystem;
	}
	
	public boolean getIsTemperatureInAlarm() {
		return this.isTemperatureInAlarm;
	}
	
	public void setToggleIrrigationSystem(boolean value) {
		this.toggleIrrigationSystem = value;
	}
	
	public int getIrrigationSpeed() {
		return this.irrigationSpeed;
	}

}