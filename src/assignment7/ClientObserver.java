/* ChatClient ClientObserver.java
 * EE422C Project 7 submission by
 * Zhaofeng Liang
 * zl4685
 * 16230
 * Zohaib Imam
 * szi58
 * 16230
 * Slip days used: 1
 * https://github.com/iamzoh/assignment7
 * Spring 2017
 */
package assignment7;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver extends PrintWriter implements Observer {
	public ClientObserver(OutputStream out) {
		super(out);
	}

	@Override 
	public void update(Observable o, Object arg) { 
		this.println(arg); //writer.println(arg); 
		this.flush(); //writer.flush(); 
		}
	}
