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
			float temperature = res.getFloat("temperature");
			int lux = res.getInteger("lux");
			
			values.addFirst(new DataPoint(temperature, lux));
			if (values.size() > MAX_SIZE) {
				values.removeLast();
			}
			
			log("New Temp: " + temperature + " Lux " + lux);
			response.setStatusCode(200).end();
		}
	}
	
	private void handleGetData(RoutingContext routingContext) {
		JsonArray arr = new JsonArray();
		for (DataPoint p: values) {
			JsonObject data = new JsonObject();
			data.put("lux", p.getLux());
			data.put("temperature", p.getTemperature());
			arr.add(data);
		}
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

}