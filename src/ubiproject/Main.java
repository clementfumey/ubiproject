package ubiproject;
import nfc.PCSC;
import gui.Gui;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// New thread for RFID Reader
		//PCSC pcsc = new PCSC();
		//pcsc.start();
		//New thread for GUI
		@SuppressWarnings("unused")
		Gui gui = new Gui();		
	}

}
