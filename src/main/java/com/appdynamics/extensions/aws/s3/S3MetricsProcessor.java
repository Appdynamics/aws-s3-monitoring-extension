/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.appdynamics.extensions.aws.config.Dimension;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.StatisticType;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import com.appdynamics.extensions.aws.predicate.MultiDimensionPredicate;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import static com.appdynamics.extensions.aws.s3.util.Constants.NAMESPACE;

/**
 * @author Vishaka Sekar
 */
public class S3MetricsProcessor implements MetricsProcessor {

    private List<IncludeMetric> includeMetrics;
    private List<com.appdynamics.extensions.aws.config.Dimension> dimensions;

    public S3MetricsProcessor(List<IncludeMetric> includeMetrics, List<Dimension> dimensions) {
        this.includeMetrics = includeMetrics;
        this.dimensions = dimensions;
    }

    @Override
    public List<AWSMetric> getMetrics(AmazonCloudWatch awsCloudWatch, String accountName, LongAdder awsRequestsCounter) {
        MultiDimensionPredicate multiDimensionPredicate = new MultiDimensionPredicate(dimensions);
        return MetricsProcessorHelper.getFilteredMetrics(awsCloudWatch, awsRequestsCounter,
                NAMESPACE,
                includeMetrics,
                null,multiDimensionPredicate);
    }

    @Override
    public StatisticType getStatisticType(AWSMetric awsMetric) {
        return MetricsProcessorHelper.getStatisticType(awsMetric.getIncludeMetric() , includeMetrics);
    }

    @Override
    public List<com.appdynamics.extensions.metrics.Metric> createMetricStatsMapForUpload //todo: override
            (NamespaceMetricStatistics namespaceMetricStats) { //todo: manually test MultiDimensionPredicate for [] , [""]
                                                                //todo: test cases for metric path and dimension
        Map<String, String> dimensionToMetricPathNameDictionary = Maps.newHashMap();
        for (Dimension dimension : dimensions) {
            dimensionToMetricPathNameDictionary.put(dimension.getName(), dimension.getDisplayName());
        }
        return MetricsProcessorHelper.createMetricStatsMapForUpload(namespaceMetricStats,
                dimensionToMetricPathNameDictionary, false);
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

}
