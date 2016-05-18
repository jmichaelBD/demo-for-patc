/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.blackducksoftware.integration.hub.global;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.util.TestLogger;

public class HubCredentialsBuilderTest {
	private static final String VALID_PASSWORD = "Password";
	private static final String VALID_USERNAME = "User";
	private static final String ERROR_MSG_NO_PASSWORD_FOUND = "No Hub Password was found.";
	private static final String ERROR_MSG_NO_USER_FOUND = "No Hub Username was found.";

	private List<String> expectedMessages;

	private TestLogger logger;

	@Before
	public void setUp() {
		expectedMessages = new ArrayList<String>();
		logger = new TestLogger();
	}

	@After
	public void tearDown() {
		final List<String> outputList = logger.getOutputList();
		final String outputString = logger.getOutputString();
		assertEquals("Too many/not enough messages expected: \n" + outputString, expectedMessages.size(),
				outputList.size());

		for (final String expectedMessage : expectedMessages) {
			assertTrue("Did not find the expected message : " + expectedMessage, outputList.contains(expectedMessage));
		}
	}

	@Test
	public void testvalidateCredentialsNull() throws Exception {
		expectedMessages.add(ERROR_MSG_NO_USER_FOUND);
		expectedMessages.add(ERROR_MSG_NO_PASSWORD_FOUND);

		final HubCredentialsBuilder builder = new HubCredentialsBuilder();

		assertFalse(builder.validateCredentials(logger));
	}

	@Test
	public void testvalidateCredentialsEmpty() throws Exception {
		expectedMessages.add(ERROR_MSG_NO_USER_FOUND);
		expectedMessages.add(ERROR_MSG_NO_PASSWORD_FOUND);

		final HubCredentialsBuilder builder = new HubCredentialsBuilder();
		builder.setUsername("");
		builder.setPassword("   ");

		assertFalse(builder.validateCredentials(logger));
	}

	@Test
	public void testvalidateCredentials() throws Exception {
		final HubCredentialsBuilder builder = new HubCredentialsBuilder();
		builder.setUsername(VALID_USERNAME);
		builder.setPassword(VALID_PASSWORD);

		assertTrue(builder.validateCredentials(logger));
	}

	@Test
	public void testValidateHubUserNull() throws Exception {
		expectedMessages.add(ERROR_MSG_NO_USER_FOUND);

		final HubCredentialsBuilder builder = new HubCredentialsBuilder();

		assertFalse(builder.validateUsername(logger));
	}

	@Test
	public void testValidateHubUser() throws Exception {
		final HubCredentialsBuilder builder = new HubCredentialsBuilder();
		builder.setUsername(VALID_USERNAME);
		assertTrue(builder.validateUsername(logger));
	}

	@Test
	public void testValidateHubPasswordNull() throws Exception {
		expectedMessages.add(ERROR_MSG_NO_PASSWORD_FOUND);

		final HubCredentialsBuilder builder = new HubCredentialsBuilder();

		assertFalse(builder.validatePassword(logger));
	}

	@Test
	public void testValidateHubPassword() throws Exception {
		final HubCredentialsBuilder builder = new HubCredentialsBuilder();
		builder.setPassword(VALID_PASSWORD);
		assertTrue(builder.validatePassword(logger));
	}

}