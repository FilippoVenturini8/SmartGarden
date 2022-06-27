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
	private boolean closeIrrigationSystem = false;
	private boolean updateIrrigationSystemSpeed = false;
	private boolean isIrrigationSleeping;
	private int irrigationSpeed = 50;
	private int lastLux = 0;
	private int lastTemp = 0;
	private String modality = "AUT";
	private boolean isTemperatureInAlarm = false;
	public int tempTemp = 1;
	
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
		
		if(lastLux != lux) {
			if(lux < 5) {
				activateLightSystem = true;
				analogicLightValue = (int)(((lux*8)/5)-1);
			}
			
			if(lux >= 5 && activateLightSystem) {
				activateLightSystem = false;
			}
			
			if(lux < 2 && !this.openIrrigationSystem) {
				this.openIrrigationSystem = true;
			}
			
			if(lux >=2 && !this.closeIrrigationSystem){
				this.closeIrrigationSystem = true;
			}
			
			lastLux = lux;
		}
		
		int temp = lastMeasurement.getTemperature();
		this.isTemperatureInAlarm = false;
		if(temp != lastTemp) {
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
			this.updateIrrigationSystemSpeed = true;
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
	
	public boolean getOpenIrrigationSystem() {
		return this.openIrrigationSystem;
	}
	
	public boolean getCloseIrrigationSystem() {
		return this.closeIrrigationSystem;
	}
	
	public boolean getIsTemperatureInAlarm() {
		return this.isTemperatureInAlarm;
	}
	
	public void setOpenIrrigationSystem(boolean value) {
		this.openIrrigationSystem = value;
	}
	
	public void setCloseIrrigationSystem(boolean value) {
		this.closeIrrigationSystem = value;
	}
	
	public int getIrrigationSpeed() {
		return this.irrigationSpeed;
	}
	
	public boolean getUpdateIrrigationSystemSpeed() {
		return this.updateIrrigationSystemSpeed;
	}
	
	public void setIsIrrigationSleeping(boolean value) {
		this.isIrrigationSleeping = value;
	}

}