package com.test;
/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  https://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 * <p>
 * Prerequisites: You must have a valid Amazon Web Services developer account,
 * and be signed up to use Amazon SQS. For more information about Amazon SQS,
 * see https://aws.amazon.com/sqs
 * <p>
 * Make sure that your credentials are located in ~/.aws/credentials
 */
public class SQSSimpleJavaClientExample {

	private static final Logger LOGGER = Logger.getLogger(SQSSimpleJavaClientExample.class.getName());

	public static void main(String[] args) {
		/*
		 * Create a new instance of the builder with all defaults (credentials and
		 * region) set automatically. For more information, see Creating Service Clients
		 * in the AWS SDK for Java Developer Guide.
		 */
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

		LOGGER.info("===============================================");

		try {
			// Create a queue.
			LOGGER.info("Creating a new SQS queue called simpleTestQueue.\n");
			final CreateQueueRequest createQueueRequest = new CreateQueueRequest("simpleTestQueue");
			final String simpleTestQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List all queues.
			LOGGER.info("Listing all queues in your account.\n");
			for (final String queueUrl : sqs.listQueues().getQueueUrls()) {
				LOGGER.info("  QueueUrl: " + queueUrl);
			}

			// Send a message.
			LOGGER.info("Sending a message to simpleTestQueue.\n");
//			new SendMessageRequest(simpleTestQueueUrl, "This is my message text.")

			Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
			messageAttributes.put("Name", new MessageAttributeValue().withDataType("String").withStringValue("Dariya"));
			messageAttributes.put("Age", new MessageAttributeValue().withDataType("String").withStringValue("25"));
			messageAttributes.put("Gender", new MessageAttributeValue().withDataType("String").withStringValue("Male"));
			messageAttributes.put("Status",
					new MessageAttributeValue().withDataType("String").withStringValue("Single"));

			SendMessageRequest sendMessageRequest = new SendMessageRequest();
			sendMessageRequest.withMessageBody("This is my message text.");
			sendMessageRequest.withQueueUrl(simpleTestQueueUrl);
			sqs.sendMessage(sendMessageRequest.withMessageAttributes(messageAttributes));

			// Receive messages.
			LOGGER.info("Receiving messages from simpleTestQueue.\n");
			final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(simpleTestQueueUrl);
			final List<Message> messages = sqs.receiveMessage(receiveMessageRequest.withMessageAttributeNames("All"))
					.getMessages();
			
			
			for (final Message message : messages) {
				LOGGER.info("Message");
				LOGGER.info("  MessageId:     " + message.getMessageId());
				LOGGER.info("  ReceiptHandle: " + message.getReceiptHandle());
				LOGGER.info("  MD5OfBody:     " + message.getMD5OfBody());
				LOGGER.info("  Body:          " + message.getBody());

				for (final Entry<String, MessageAttributeValue> entry : message.getMessageAttributes().entrySet()) {
					LOGGER.info("Attributes :");
					LOGGER.info("  Name:  " + entry.getKey()+"  Value: " + entry.getValue().getStringValue());
				}

			}
/*
 * delete msg and queue commented
 * 
 * */

			// Delete the message.
//            LOGGER.info("Deleting a message.\n");
//            final String messageReceiptHandle = messages.get(0).getReceiptHandle();
//            sqs.deleteMessage(new DeleteMessageRequest(simpleTestQueueUrl,
//                    messageReceiptHandle));

			// Delete the queue.
//            LOGGER.info("Deleting the test queue.\n");
//            sqs.deleteQueue(new DeleteQueueRequest(simpleTestQueueUrl));
		} catch (final AmazonServiceException ase) {
			LOGGER.info(
					"Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
							+ "rejected with an error response for some reason.");
			LOGGER.info("Error Message:    " + ase.getMessage());
			LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
			LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
			LOGGER.info("Error Type:       " + ase.getErrorType());
			LOGGER.info("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			LOGGER.info("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			LOGGER.info("Error Message: " + ace.getMessage());
		}
	}
}