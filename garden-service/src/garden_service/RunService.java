package garden_service;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop 
 */
public class RunService {
	private static boolean isLightSystemOn = false;
	private static String modality = "AUT";
	private static boolean alarmSent = false;

	public static void main(String[] args) throws Exception{
		Vertx vertx = Vertx.vertx();
		DataService service = new DataService(8080);
		vertx.deployVerticle(service);
		
		SerialCommChannel channel = new SerialCommChannel(args[0],9600);
		System.out.println("Waiting Arduino for rebooting...");		
		Thread.sleep(4000);
		System.out.println("Ready.");
		
		String[] splittedMsg;
		String mod;
		String isIrrigationSleeping = "0";
		
		while(true) {
			if(channel.isMsgAvailable()) {
				String msg = channel.receiveMsg();
				splittedMsg = msg.split("\\|");
				System.out.println(msg);
				if(!msg.contains("|") || splittedMsg.length != 2) {
					break;
				}
				
				mod = splittedMsg[0];
				isIrrigationSleeping = splittedMsg[1];
				
				if(!mod.equals(modality) && mod.equals("MAN")) {
					modality = "MAN";
					service.setModality(modality);
					alarmSent = false;
					service.tempTemp = 4;
				}else if(!mod.equals(modality) && mod.equals("AUT")) {
					modality = "AUT";
					service.setModality(modality);
				}
			}
			
			if(modality.equals("AUT")) {
				if(!isLightSystemOn && service.activateLightSystem()) {
					int value = service.getAnalogicLightValue();
					channel.sendMsg("1|1|"+value+"|"+value+"|-1|-1|-1");
					isLightSystemOn = true;
				}else if(isLightSystemOn && !service.activateLightSystem()) {
					channel.sendMsg("0|0|0|0|-1|-1|-1");
					isLightSystemOn = false;
				}
				if(service.toggleIrrigationSystem()) {
					int irrigationSpeed = service.getIrrigationSpeed();
					if(irrigationSpeed == 0) {
						channel.sendMsg("-1|-1|-1|-1|0|0|-1");
					}else {
						channel.sendMsg("-1|-1|-1|-1|1|"+irrigationSpeed+"|-1");
					}
					service.setToggleIrrigationSystem(false);
				}
			}
			
			if(!alarmSent && service.getIsTemperatureInAlarm() && isIrrigationSleeping.equals("1")) {
				modality = "ARM";
				service.setModality("ARM");
				channel.sendMsg("-1|-1|-1|-1|1|-1|"+modality);
				alarmSent = true;
			}
			
			
			Thread.sleep(500);
		}
		
	}
}