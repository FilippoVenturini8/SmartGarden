package garden_service;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop 
 */
public class RunService {


	public static void main(String[] args) throws Exception{
		Vertx vertx = Vertx.vertx();
		DataService service = new DataService(8080);
		vertx.deployVerticle(service);
		
		SerialCommChannel channel = new SerialCommChannel(args[0],9600);
		System.out.println("Waiting Arduino for rebooting...");		
		Thread.sleep(4000);
		System.out.println("Ready.");
		
		channel.sendMsg("RECOVER");
	}
}