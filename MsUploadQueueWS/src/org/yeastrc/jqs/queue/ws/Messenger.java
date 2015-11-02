/**
 * Messenger.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Messenger {

	private List<String> messages;
	
	public Messenger() {
		this.messages = new ArrayList<String>();
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public List<String> getMessages() {
		return messages;
	}
	
	public String getMessagesString() {
		StringBuilder buf = new StringBuilder();
		for(String message: messages)
			buf.append(message+"\n");
		return buf.toString();
	}
	
	public void addError(String message) {
		addMessage("ERROR: "+message);
	}
	
	public void addWarning(String message) {
		addMessage("WARNING: "+message);
	}
}
