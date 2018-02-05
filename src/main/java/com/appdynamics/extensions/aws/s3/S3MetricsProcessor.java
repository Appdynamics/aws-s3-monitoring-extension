/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.DimensionFilter;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.appdynamics.extensions.aws.config.MetricType;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;

import java.util.*;
import java.util.regex.Pattern;

import static com.appdynamics.extensions.aws.s3.util.Constants.*;

/**
 * @author Satish Muddam, Aditya Jagtiani
 */
public class S3MetricsProcessor implements MetricsProcessor {

    private List<MetricType> metricTypes;

    private Pattern excludeMetricsPattern;

    private List<String> buckets;

    public S3MetricsProcessor(List<MetricType> metricTypes,
                              Set<String> excludeMetrics, List<String> buckets) {
        this.metricTypes = metricTypes;
        this.excludeMetricsPattern = MetricsProcessorHelper.createPattern(excludeMetrics);
        this.buckets = buckets;
    }

    public List<Metric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName) {
        List<DimensionFilter> dimensions = new ArrayList<DimensionFilter>();

        DimensionFilter nameDimensionFilter = new DimensionFilter();
        nameDimensionFilter.withName(DIMENSIONS[0]);

        DimensionFilter storageDimensionFilter = new DimensionFilter();
        storageDimensionFilter.withName(DIMENSIONS[1]);


        dimensions.add(nameDimensionFilter);
        dimensions.add(storageDimensionFilter);

        S3BucketsPredicate predicate = new S3BucketsPredicate(buckets);
        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch,
                NAMESPACE,
                excludeMetricsPattern,
                dimensions, predicate);
    }

    public StatisticType getStatisticType(Metric metric) {
        return MetricsProcessorHelper.getStatisticType(metric, metricTypes);
    }

    public Map<String, Double> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {
        Map<String, String> dimensionToMetricPathNameDictionary = new HashMap<String, String>();
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[0], BUCKET_DIMENSION_VALUE);
        dimensionToMetricPathNameDictionary.put(DIMENSIONS[1], STORAGE_DIMENSION_VALUE);


        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    public String getNamespace() {
        return NAMESPACE;
    }

}
