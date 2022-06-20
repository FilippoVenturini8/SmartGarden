package garden_service;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop 
 */
public class RunService {
	private static boolean isLightSystemOn = false;

	public static void main(String[] args) throws Exception{
		Vertx vertx = Vertx.vertx();
		DataService service = new DataService(8080);
		vertx.deployVerticle(service);
		
		SerialCommChannel channel = new SerialCommChannel(args[0],9600);
		System.out.println("Waiting Arduino for rebooting...");		
		Thread.sleep(4000);
		System.out.println("Ready.");
		
		while(true) {
			if(!isLightSystemOn && service.activateLightSystem()) {
				int value = service.getAnalogicLightValue();
				channel.sendMsg("1|1|"+value+"|"+value);
				isLightSystemOn = true;
			}else if(isLightSystemOn && !service.activateLightSystem()) {
				channel.sendMsg("0|0|0|0");
				isLightSystemOn = false;
			}
			Thread.sleep(500);
		}
		
	}
}