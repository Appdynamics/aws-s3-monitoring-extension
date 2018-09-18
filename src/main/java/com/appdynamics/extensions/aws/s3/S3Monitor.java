/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static com.appdynamics.extensions.aws.s3.util.Constants.DEFAULT_METRIC_PREFIX;
import static com.appdynamics.extensions.aws.s3.util.Constants.MONITOR_NAME;

/**
 * @author Vishaka Sekar
 */
public class S3Monitor extends SingleNamespaceCloudwatchMonitor<Configuration> {

    private static final Logger LOGGER = Logger.getLogger(S3Monitor.class);

    public S3Monitor() {
        super(Configuration.class);
    }

    @Override
    public String getMonitorName() { return MONITOR_NAME; }

    @Override
    protected String getDefaultMetricPrefix() {
        return DEFAULT_METRIC_PREFIX;
    }

    @Override
    protected int getTaskCount() {
        return 3;
    }

    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
            Configuration config) {
        MetricsProcessor metricsProcessor = createMetricsProcessor(config);

        return new NamespaceMetricStatisticsCollector
                .Builder(config.getAccounts(),
                config.getConcurrencyConfig(),
                config.getMetricsConfig(),
                metricsProcessor,
                config.getMetricPrefix())
                .withCredentialsDecryptionConfig(config.getCredentialsDecryptionConfig())
                .withProxyConfig(config.getProxyConfig())
                .build();
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    private MetricsProcessor createMetricsProcessor(Configuration config) {
        return new S3MetricsProcessor(
                config.getMetricsConfig().getIncludeMetrics(), config.getDimensions());
    }


}
