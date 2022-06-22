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
		
		JLabel lblModality = new JLabel("Modality: ");
		JLabel lblLux = new JLabel("Luminosity: ");
		JLabel lblTmp = new JLabel("Temperature: ");
		
		lblModality.setBounds(320, 25, 300, 50);
		lblLux.setBounds(320, 50, 200, 50);
		lblTmp.setBounds(320, 100, 200, 50);
		
		panel.add(lblModality);
		panel.add(lblLux);
		panel.add(lblTmp);
		
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
				  lblModality.setText("Modality: "+response.getJsonObject(0).getString("modality"));
				  lblLux.setText("Luminosity: "+response.getJsonObject(0).getString("lux"));
				  lblTmp.setText("Temperature: "+response.getJsonObject(0).getString("temperature"));
			      System.out.println(response.encodePrettily());
			  })
			  .onFailure(err ->
			    System.out.println("Something went wrong " + err.getMessage()));
			Thread.sleep(1000);
		}
		
	}
}
