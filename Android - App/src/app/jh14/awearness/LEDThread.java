package app.jh14.awearness;

public class LEDThread implements Runnable {
	
	RFduinoService service;
	
	public LEDThread(RFduinoService service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		while (true) {
			service.send(HexAsciiHelper.hexToBytes("FF"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			service.send(HexAsciiHelper.hexToBytes("00"));
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
		}
	}

}
