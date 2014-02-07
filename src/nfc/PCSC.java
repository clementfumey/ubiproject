package nfc;

import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

/**
 * Class reading a NFC chip.
 * Specifically adapted to read the UID of a Mifare chip from the first reader, but can easily be adapted.
 * @author Jacob Tardieu
 *
 */
public class PCSC extends Thread{

	private byte[] atr = null;
	private String protocol = null;
	private byte[] historical = null;
	private String UID;

	public PCSC() {

	}

	/**
	 * Method allowing to select which terminal we would like to use.
	 * Specially adapted to select the first available terminal, but commented part can allow to choose.
	 * @return the selected terminal
	 */
	public CardTerminal selectCardTerminal()
	{
		try
		{
			// show the list of available terminals
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();
			ListIterator<CardTerminal> terminalsIterator = terminals.listIterator();
			CardTerminal terminal = null;
			CardTerminal defaultTerminal = null;
			/*if(terminals.size() > 1)
                        {
                                System.out.println("Please choose one of these card terminals (1-" + terminals.size() + "):");
                                int i = 1;
                                while(terminalsIterator.hasNext())
                                {
                                        terminal = terminalsIterator.next();
                                        System.out.print("["+ i + "] - " + terminal + ", card present: "+terminal.isCardPresent());
                                        if(i == 1) 
                                        {
                                                defaultTerminal = terminal;
                                                System.out.println(" [default terminal]");
                                        }
                                        else
                                        {
                                                System.out.println();
                                        }                                       
                                        i++;
                                }
                                Scanner in = new Scanner(System.in);
                                try
                                {
                                        int option = in.nextInt();
                                        terminal = terminals.get(option-1);
                                }
                                catch(Exception e2)
                                {
                                        //System.err.println("Wrong value, selecting default terminal!");
                                        terminal = defaultTerminal;

                                }
                                System.out.println("Selected: "+terminal.getName());
                                //Console console = System.console(); 
                                return terminal;
                        }*/

			//Code selecting by default the second terminal.
			if (terminals.size() > 1) {
				terminal = terminals.get(0);
			}
			return terminal;


		}
		catch(Exception e)
		{
			System.err.println("Error occured:");
			e.printStackTrace();
		}
		return null;
	}

	public String byteArrayToHexString(byte[] b) 
	{
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public static byte[] hexStringToByteArray(String s) 
	{     
		int len = s.length();     
		byte[] data = new byte[len / 2];     
		for (int i = 0; i < len; i += 2) 
		{         
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));     
		}     
		return data; 
	}       

	/**
	 * Method establishing connection with the card terminal.
	 * Method adapted to use the default protocol and to request the UID.
	 * @param ct
	 * @return
	 * @throws CardException
	 */
	public void establishConnection(CardTerminal ct) throws CardException
	{
		this.atr = null;
		this.historical = null;
		this.protocol = null;

		/*System.out.println("To establish connection, please choose one of these protocols (1-4):");
		System.out.println("[1] - T=0");
		System.out.println("[2] - T=1");
		System.out.println("[3] - T=CL");
		System.out.println("[4] - * [default]");

		String p = "*";
		Scanner in = new Scanner(System.in);

		try
		{
			int option = in.nextInt();

			if(option == 1) p = "T=0";
			if(option == 2) p = "T=1";
			if(option == 3) p = "T=CL";
			if(option == 4) p = "*";                        
		}
		catch(Exception e)
		{
			//System.err.println("Wrong value, selecting default protocol!");
			p = "*";
		}

		System.out.println("Selected: "+p);*/
		
		String p = "*"; //using default value
		
		while (ct.waitForCardPresent(0)) {
			System.out.println("Card inserted");
			Card card = null;
			try 
			{
				card = ct.connect(p);
			} 
			catch (CardException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				//return null;
			}
			if (card != null) {
				ATR atr = card.getATR();
				System.out.println("Connected:");       
				System.out.println(" - ATR:  "+ byteArrayToHexString(atr.getBytes()));
				System.out.println(" - Historical: "+ byteArrayToHexString(atr.getHistoricalBytes()));
				System.out.println(" - Protocol: "+card.getProtocol());

				this.atr = atr.getBytes();
				this.historical = atr.getHistoricalBytes();
				this.protocol = card.getProtocol();

				CardChannel channel = card.getBasicChannel();
				CommandAPDU command = new CommandAPDU( new byte[] { (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0xD4, (byte) 0x4A, (byte) 0x01, (byte) 0x00 });
				ResponseAPDU response = channel.transmit(command);
				//card.disconnect(true);
				if (response.getSW1() == 0x90) {
					byte[] data = response.getData();
					data = Arrays.copyOfRange(data, 0x08, data.length);
					System.out.println("UID : "+this.byteArrayToHexString((data)));
					this.UID = this.byteArrayToHexString(data);
				}
				ct.waitForCardAbsent(0);
				System.out.println("Card removed");
				//return card;   
			}
		}

	}

	/**
	 * Run method selecting a terminal then asking the card its UID to store it in the UID variable.
	 * @param args
	 */
	public void run() {
		// TODO Auto-generated method stub

		//PCSC pcsc = new PCSC();
		CardTerminal ct = this.selectCardTerminal();
		Card c = null;
		if(ct != null)
		{
			/*try {
				c = pcsc.establishConnection(ct);
			} catch (CardException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			CardChannel cc = c.getBasicChannel();
			//byte[] SELECT = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x09, (byte) 0x74, (byte) 0x69, (byte) 0x63, (byte) 0x6B, (byte) 0x65, (byte) 0x74, (byte) 0x69, (byte) 0x6E, (byte) 0x67, (byte) 0x00};
			//byte[] baCommandAPDU = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x08, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00};
			//byte[] baCommandAPDU = {(byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x51, (byte) 0x00, (byte) 0x00};
			
			// command for asking the UID of a Mifare chip
			byte[] baCommandAPDU = { (byte) 0xFF, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0xD4, (byte) 0x4A, (byte) 0x01, (byte) 0x00 };
			try 
			{
				System.out.println("TRANSMIT: "+pcsc.byteArrayToHexString(baCommandAPDU));      
				ResponseAPDU r = cc.transmit(new CommandAPDU(baCommandAPDU));
				System.out.println("RESPONSE: "+pcsc.byteArrayToHexString(r.getBytes()));
				this.UID = pcsc.byteArrayToHexString(r.getBytes());
			} 
			catch (CardException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			try {
				this.establishConnection(ct); //lauch the method checking if a card is here
			} catch (CardException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getUid() {
		return UID;
	}


}
