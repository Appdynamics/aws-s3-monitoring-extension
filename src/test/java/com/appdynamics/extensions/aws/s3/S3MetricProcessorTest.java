/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.appdynamics.extensions.aws.config.Dimension;
import com.appdynamics.extensions.aws.config.IncludeMetric;
import com.appdynamics.extensions.aws.dto.AWSMetric;
import com.appdynamics.extensions.aws.metric.AccountMetricStatistics;
import com.appdynamics.extensions.aws.metric.MetricStatistic;
import com.appdynamics.extensions.aws.metric.NamespaceMetricStatistics;
import com.appdynamics.extensions.aws.metric.RegionMetricStatistics;
import com.appdynamics.extensions.metrics.Metric;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class S3MetricProcessorTest {

    NamespaceMetricStatistics namespaceMetricStatistics = new NamespaceMetricStatistics();
    @Before
    public void init(){

        List<com.amazonaws.services.cloudwatch.model.Dimension> dimensionsList = Lists.newArrayList();
        com.amazonaws.services.cloudwatch.model.Dimension dimension1 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension1.withName("BucketName").withValue("test-bucket");
        dimensionsList.add(dimension1);

        com.amazonaws.services.cloudwatch.model.Dimension dimension2 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension2.withName("StorageType").withValue("StandardStorage");
        dimensionsList.add(dimension2);

        AccountMetricStatistics accountMetricStatistics = new AccountMetricStatistics();
        accountMetricStatistics.setAccountName("AppD");

        namespaceMetricStatistics.setNamespace("AWS/S3");

        RegionMetricStatistics regionMetricStatistics = new RegionMetricStatistics();
        regionMetricStatistics.setRegion("us-west-1");

        com.amazonaws.services.cloudwatch.model.Metric metric = new com.amazonaws.services.cloudwatch.model.Metric();
        metric.setDimensions(dimensionsList);

        List includeMetrics = Lists.newArrayList();
        IncludeMetric includeMetric = new IncludeMetric();
        includeMetric.setName("testmetric");
        includeMetrics.add(includeMetric);

        AWSMetric awsMetric = new AWSMetric();
        awsMetric.setIncludeMetric(includeMetric);
        awsMetric.setMetric(metric);

        MetricStatistic metricStatistic = new MetricStatistic();
        metricStatistic.setValue(1.0);
        metricStatistic.setUnit("TestUnits");
        metricStatistic.setMetricPrefix("Custom Metric|AWS S3|");
        metricStatistic.setMetric(awsMetric);

        regionMetricStatistics.addMetricStatistic(metricStatistic);
        accountMetricStatistics.add(regionMetricStatistics);
        namespaceMetricStatistics.add(accountMetricStatistics);

    }

    @Test
    public void createMetricStatsForUploadTest(){

        List<Dimension> dimensionsFromConfig = Lists.newArrayList();
        Dimension dimension1 = new Dimension();
        dimension1.setName("BucketName");
        dimension1.setDisplayName("Bucket Name");
        dimensionsFromConfig.add(dimension1);

        Dimension dimension2 = new Dimension();
        dimension2.setName("StorageType");
        dimension2.setDisplayName("Storage Type");
        dimensionsFromConfig.add(dimension2);

        S3MetricsProcessor s3MetricsProcessor = new S3MetricsProcessor(new ArrayList(), dimensionsFromConfig);

        List<Metric> stats = s3MetricsProcessor.createMetricStatsMapForUpload(namespaceMetricStatistics);

        Assert.assertNotNull(stats.get(0));
        Assert.assertEquals(stats.get(0).getMetricPath(),
                "Custom Metric|AWS S3|AppD|us-west-1|Bucket Name|test-bucket|Storage Type|StandardStorage|testmetric" );

    }
}
