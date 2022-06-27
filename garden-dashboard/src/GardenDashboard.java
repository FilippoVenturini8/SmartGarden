import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class GardenDashboard {
	public static void main(String[] args) throws Exception {		
		
		String host = "localhost"; // "b1164b27.ngrok.io";
		int port = 8080;

		Vertx vertx = Vertx.vertx();

		WebClient client = WebClient.create(vertx);
		
		JFrame frame = new JFrame();
		
		frame.setTitle("Garden Dashboard");
		frame.setSize(750, 500) ;
		frame.setLocationRelativeTo(null);
		
		final JPanel panel = new JPanel();
		
		panel.setLayout(null);
		
		JLabel lblTitle = new JLabel("Garden Dashboard");
		JLabel lblModality = new JLabel("Modality: ");
		JLabel lblLux = new JLabel("Luminosity: ");
		JLabel lblTmp = new JLabel("Temperature: ");
		JLabel lblIrrigation = new JLabel("Irrigation System: ");
		
		lblTitle.setFont(new Font("Serif", Font.BOLD, 30));
		
		lblTitle.setBounds(240, 25, 300, 50);
		lblModality.setBounds(290, 100, 300, 50);
		lblLux.setBounds(290, 150, 200, 50);
		lblTmp.setBounds(290, 200, 200, 50);
		lblIrrigation.setBounds(290, 250, 200, 50);
		
		panel.add(lblTitle);
		panel.add(lblModality);
		panel.add(lblLux);
		panel.add(lblTmp);
		panel.add(lblIrrigation);
		
		frame.getContentPane().add( panel );
		
		frame.setVisible(true);

		while (true){
			System.out.println("Getting data items... ");
			client
			  .get(port, host, "/api/data")
			  .send()
			  .onSuccess(res -> { 
				  System.out.println("Getting - Received response with status code: " + res.statusCode());
				  JsonArray response = res.bodyAsJsonArray();
				  
				  String modality = response.getJsonObject(0).getString("modality");
				  
				  switch(modality) {
					  case "AUT":
						  lblModality.setText("Modality: AUTO");
						  break;
					  case "MAN":
						  lblModality.setText("Modality: MANUAL");
						  break;
					  case "ARM":
						  lblModality.setText("Modality: ALARM");
						  break;
				  }
				  
				  lblLux.setText("Luminosity: "+response.getJsonObject(0).getString("lux"));
				  lblTmp.setText("Temperature: "+response.getJsonObject(0).getString("temperature"));
				  
				  String isIrrigationSleeping = response.getJsonObject(0).getString("irrigationSleeping");
				  
				  if(isIrrigationSleeping.equals("true")) {
					  lblIrrigation.setText("Irrigation System: SLEEPING");  
				  }else if(isIrrigationSleeping.equals("false")) {
					  lblIrrigation.setText("Irrigation System: ACTIVE");
				  }
				  
				  
			      System.out.println(response.encodePrettily());
			  })
			  .onFailure(err ->
			    System.out.println("Something went wrong " + err.getMessage()));
			Thread.sleep(1000);
		}
		
	}
}
