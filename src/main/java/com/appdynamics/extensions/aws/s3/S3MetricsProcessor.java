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
import com.appdynamics.extensions.aws.metric.*;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessor;
import com.appdynamics.extensions.aws.metric.processors.MetricsProcessorHelper;
import com.appdynamics.extensions.aws.predicate.MultiDimensionPredicate;
import com.appdynamics.extensions.metrics.Metric;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

import static com.appdynamics.extensions.aws.s3.util.Constants.*;

/**
 * @author Vishaka Sekar
 */
public class S3MetricsProcessor implements MetricsProcessor {

    private List<IncludeMetric> includeMetrics;
    private List<Dimension> dimensions;
    private static final Logger logger = Logger.getLogger(S3Monitor.class);

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
                null, multiDimensionPredicate);
    }

    @Override
    public StatisticType getStatisticType(AWSMetric awsMetric) {
        return MetricsProcessorHelper.getStatisticType(awsMetric.getIncludeMetric(), includeMetrics);
    }

    @Override
    public List<Metric> createMetricStatsMapForUpload(NamespaceMetricStatistics namespaceMetricStats) {

        List<Metric> stats = new ArrayList<>();
        Map<String, String> dimensionDisplayNameMap = Maps.newHashMap();
        for (Dimension dimension : dimensions) {
            dimensionDisplayNameMap.put(dimension.getName(), dimension.getDisplayName());
        }

        for (AccountMetricStatistics accountMetricStatistics : namespaceMetricStats.getAccountMetricStatisticsList()) {
            String accountPrefix = accountMetricStatistics.getAccountName();

            for (RegionMetricStatistics regionMetricStatistics : accountMetricStatistics.getRegionMetricStatisticsList()) {
                String regionPrefix = regionMetricStatistics.getRegion();

                for (MetricStatistic metricStatistic : regionMetricStatistics.getMetricStatisticsList()) {

                    Map<String, String> dimensionValueMap = Maps.newHashMap();

                    for (com.amazonaws.services.cloudwatch.model.Dimension dimension :
                            metricStatistic.getMetric().getMetric().getDimensions()) {
                        dimensionValueMap.put(dimension.getName(), dimension.getValue());
                    }
                        StringBuilder partialMetricPath = new StringBuilder();
                        buildMetricPath(partialMetricPath, true,
                                accountPrefix, regionPrefix);

                        arrangeMetricPathHierarchy(partialMetricPath, dimensionDisplayNameMap, dimensionValueMap);
                        String awsMetricName = metricStatistic.getMetric().getIncludeMetric().getName();
                        buildMetricPath(partialMetricPath, false, awsMetricName);
                        String fullMetricPath = metricStatistic.getMetricPrefix() + partialMetricPath;
                        if (metricStatistic.getValue() != null) {
                            Map<String, Object> metricProperties = new HashMap<>();
                            IncludeMetric metricWithConfig = metricStatistic.getMetric().getIncludeMetric();
                            metricProperties.put("alias", metricWithConfig.getAlias());
                            metricProperties.put("multiplier", metricWithConfig.getMultiplier());
                            metricProperties.put("aggregationType", metricWithConfig.getAggregationType());
                            metricProperties.put("timeRollUpType", metricWithConfig.getTimeRollUpType());
                            metricProperties.put("clusterRollUpType", metricWithConfig.getClusterRollUpType());
                            metricProperties.put("delta", metricWithConfig.isDelta());
                            Metric metric = new Metric(awsMetricName, Double.toString(metricStatistic.getValue()),
                                    fullMetricPath, metricProperties);
                            stats.add(metric);
                        } else {
                            logger.debug(String.format("Ignoring metric [ %s ] which has value null", fullMetricPath));
                        }

                    }

                }
            }

        return stats;
    }


    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    private static void buildMetricPath(StringBuilder partialMetricPath, boolean appendMetricSeparator, String... elements) {

        for (String element : elements) {
            partialMetricPath.append(element);
            if (appendMetricSeparator) {
                partialMetricPath.append(METRIC_SEPARATOR);
            }
        }
    }

    private void arrangeMetricPathHierarchy(StringBuilder partialMetricPath, Map<String, String> dimensionDisplayNameMap,
                                        Map<String, String> dimensionValueMap) {
    String bucketNameDimension = BUCKET_NAME;
    String bucketNameDisplayName = dimensionDisplayNameMap.get(bucketNameDimension);
    String filterIdDimension = FILTER_ID;
    String filterIdDisplayName = dimensionDisplayNameMap.get(filterIdDimension);
    String storageTypeDimension = STORAGE_TYPE;
    String storageTypeDisplayName = dimensionDisplayNameMap.get(storageTypeDimension);

    //<Account> | <Region> | Bucket Name | <BucketName> |
    buildMetricPath(partialMetricPath, true , bucketNameDisplayName, dimensionValueMap.get(bucketNameDimension) );

    //<Account> | <Region> | Bucket Name | <BucketName> | Filter Id | <FilterIdValue>
    if(dimensionValueMap.get(filterIdDimension)!= null){
        buildMetricPath(partialMetricPath, true, filterIdDisplayName, dimensionValueMap.get(filterIdDimension));
    }
    // <Account> | <Region> | Bucket Name | <BucketName> | Storage Type | <StorageTypes>
    if(dimensionValueMap.get(storageTypeDimension) != null){
        buildMetricPath(partialMetricPath, true, storageTypeDisplayName, dimensionValueMap.get(storageTypeDimension));
    }

    }
}