package com.appdynamics.extensions.aws.s3;

import static com.appdynamics.extensions.aws.Constants.CONFIG_ARG;
import static com.appdynamics.extensions.aws.Constants.CONFIG_REGION_ENDPOINTS_ARG;
import static com.appdynamics.extensions.aws.Constants.METRIC_PATH_SEPARATOR;

import com.appdynamics.extensions.aws.SingleNamespaceCloudwatchMonitor;
import com.appdynamics.extensions.aws.collectors.NamespaceMetricStatisticsCollector;
import com.appdynamics.extensions.aws.config.Configuration;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.s3.config.S3Configuration;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Satish Muddam
 */
public class S3Monitor extends SingleNamespaceCloudwatchMonitor<S3Configuration> {

    private static final Logger LOGGER = Logger.getLogger("com.singularity.extensions.aws.S3Monitor");

    private static final String DEFAULT_METRIC_PREFIX = String.format("%s%s%s%s",
            "Custom Metrics", METRIC_PATH_SEPARATOR, "Amazon S3", METRIC_PATH_SEPARATOR);

    public S3Monitor() {
        super(S3Configuration.class);
        LOGGER.info(String.format("Using AWS S3 Monitor Version [%s]",
                this.getClass().getPackage().getImplementationTitle()));
    }

    @Override
    protected NamespaceMetricStatisticsCollector getNamespaceMetricsCollector(
            S3Configuration config) {
        MetricsProcessor metricsProcessor = createMetricsProcessor(config);

        return new NamespaceMetricStatisticsCollector
                .Builder(config.getAccounts(),
                config.getConcurrencyConfig(),
                config.getMetricsConfig(),
                metricsProcessor)
                .withCredentialsEncryptionConfig(config.getCredentialsDecryptionConfig())
                .withProxyConfig(config.getProxyConfig())
                .build();
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected String getMetricPrefix(S3Configuration config) {
        return StringUtils.isNotBlank(config.getMetricPrefix()) ?
                config.getMetricPrefix() : DEFAULT_METRIC_PREFIX;
    }

    private MetricsProcessor createMetricsProcessor(S3Configuration config) {
        return new S3MetricsProcessor(
                config.getMetricsConfig().getMetricTypes(),
                config.getMetricsConfig().getExcludeMetrics(), config.getBuckets());
    }
}
