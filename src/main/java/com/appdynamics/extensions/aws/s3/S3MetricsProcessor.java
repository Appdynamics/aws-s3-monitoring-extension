package com.appdynamics.extensions.aws.s3;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.appdynamics.extensions.aws.config.MetricType;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Satish Muddam
 */
public class S3MetricsProcessor implements MetricsProcessor {

    private static final String NAMESPACE = "AWS/S3";

    private static final String[] DIMENSIONS = {"BucketName", "StorageType"};

    private List<MetricType> metricTypes;

    private Pattern excludeMetricsPattern;

    public S3MetricsProcessor(List<MetricType> metricTypes,
                              Set<String> excludeMetrics) {
        this.metricTypes = metricTypes;
        this.excludeMetricsPattern = MetricsProcessorHelper.createPattern(excludeMetrics);
    }

    public List<Metric> getMetrics(AmazonCloudWatch awsCloudWatch) {
        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch,
                NAMESPACE,
                excludeMetricsPattern,
                DIMENSIONS);
    }

    public StatisticType getStatisticType(Metric metric) {
        return MetricsProcessorHelper.getStatisticType(metric, metricTypes);
    }

    public Map<String, Double> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[0], "Bucket Name");
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[1], "Storage Type");


        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

}
