package com.test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

public class Application implements RequestHandler<SQSEvent, Void> {

	private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

	private static Map<String, MessageAttribute> hm = new HashMap<>();

	@Override
	public Void handleRequest(SQSEvent event, Context context) {
		for (SQSMessage msg : event.getRecords()) {
			LOGGER.log(Level.INFO, new String(msg.getBody()));
			hm = msg.getMessageAttributes();
			for (String entry : hm.keySet()) {
				LOGGER.log(Level.INFO, entry + ": " + hm.get(entry).getStringValue());
			}
		}
		return null;
	}
}