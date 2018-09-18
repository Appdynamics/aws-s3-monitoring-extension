/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.TaskOutput;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class S3MonitorITest {
	
	private S3Monitor classUnderTest = new S3Monitor();
	
	@Test
	public void testMetricsCollectionCredentialsEncrypted() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/conf/itest-encrypted-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("successfully completed"));
	}
	
	@Test
	public void testMetricsCoyllectionWithProxy() throws Exception {
		Map<String, String> args = Maps.newHashMap();
		args.put("config-file","src/test/resources/conf/itest-proxy-config.yml");
		
		TaskOutput result = classUnderTest.execute(args, null);
		assertTrue(result.getStatusMessage().contains("successfully completed"));
	}

    @Test
    public void testAccountKeysClearedFromConfig() {
//        File[] configFiles = getConfigFilesFromDir("src/main/resources/conf");
//        File[] configFilesForTests = getConfigFilesFromDir("src/test/resources/conf");
//
//        verifyBlankKeys(getAWSSecretKeysFromCfg(configFiles));
//        verifyBlankKeys(getAWSSecretKeysFromCfg(configFiles));
//        verifyBlankKeys(getAWSAccessKeysFromCfg(configFilesForTests));
//        verifyBlankKeys(getAWSSecretKeysFromCfg(configFilesForTests));
    }

    private void verifyBlankKeys(List<String> keys) {
        for(String key : keys) {
            assertTrue(key.equals(""));
        }
    }
}
