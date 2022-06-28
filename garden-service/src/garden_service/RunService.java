package garden_service;

import io.vertx.core.Vertx;

/*
 * Data Service as a vertx event-loop 
 */
public class RunService {
	
	private static DataService service;
	private static SerialCommChannel channel;
	
	private static boolean isLightSystemOn = false;
	private static String modality = "AUT";
	private static boolean alarmSent = false;
	private static String isIrrigationSleeping = "0";
	private static String[] splittedMsg;
	private static String newModality;
	

	public static void main(String[] args) throws Exception{
		Vertx vertx = Vertx.vertx();
		service = new DataService(8080);
		vertx.deployVerticle(service);
		
		channel = new SerialCommChannel(args[0],9600);
		System.out.println("Waiting Arduino for rebooting...");		
		Thread.sleep(4000);
		System.out.println("Ready.");
		
		while(true) {
			
			checkSystemAlarm();
			
			if(channel.isMsgAvailable()) {
				if(receiveSerialMessage()) {
					changeSystemModality();
				}
			}
			
			if(modality.equals("AUT")) {
				autoControlStrategy();
			}
			
			Thread.sleep(500);
		}
		
	}
	
	private static void checkSystemAlarm() {
		if(!alarmSent && service.getIsTemperatureInAlarm() && isIrrigationSleeping.equals("1")) {
			modality = "ARM";
			service.setModality("ARM");
			channel.sendMsg("-1|-1|-1|-1|-1|-1|"+modality);
			alarmSent = true;
		}
	}
	
	private static boolean receiveSerialMessage() throws InterruptedException {
		String msg = channel.receiveMsg();
		splittedMsg = msg.split("\\|");
		System.out.println(msg);
		if(!msg.contains("|") || splittedMsg.length != 2) {
			return false;
		}
		
		newModality = splittedMsg[0];
		isIrrigationSleeping = splittedMsg[1];
		
		if(isIrrigationSleeping.equals("0")){
			service.setIsIrrigationSleeping(false);
		}else if (isIrrigationSleeping.equals("1")) {
			service.setIsIrrigationSleeping(true);
		}
		
		return true;
	}
	
	private static void changeSystemModality() {
		if(modality.equals("ARM") && newModality.equals("MAN")) {
			modality = "MAN";
			service.setModality(modality);
			alarmSent = false;
			service.tempTemp = 4;
		}else if(!newModality.equals(modality) && newModality.equals("AUT")) {
			modality = "AUT";
			service.setModality(modality);
		}else if(!newModality.equals(modality) && newModality.equals("MAN")) {
			modality = "MAN";
			service.setModality(modality);
		}
	}
	
	private static void autoControlStrategy() {
		if(!isLightSystemOn && service.activateLightSystem()) {
			int value = service.getAnalogicLightValue();
			channel.sendMsg("1|1|"+value+"|"+value+"|-1|-1|-1");
			isLightSystemOn = true;
		}else if(isLightSystemOn && !service.activateLightSystem()) {
			channel.sendMsg("0|0|0|0|-1|-1|-1");
			isLightSystemOn = false;
		}
		
		if(service.getOpenIrrigationSystem()) {
			int irrigationSpeed = service.getIrrigationSpeed();
			channel.sendMsg("-1|-1|-1|-1|1|"+irrigationSpeed+"|-1");
			service.setOpenIrrigationSystem(false);
		}
		
		if(service.getCloseIrrigationSystem()) {
			channel.sendMsg("-1|-1|-1|-1|0|-1|-1");
			service.setCloseIrrigationSystem(false);
		}
	}
}