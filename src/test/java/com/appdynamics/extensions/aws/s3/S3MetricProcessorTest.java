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

        List<com.amazonaws.services.cloudwatch.model.Dimension> dimensionsList1 = Lists.newArrayList();
        List<com.amazonaws.services.cloudwatch.model.Dimension> dimensionsList2 = Lists.newArrayList();
        List<com.amazonaws.services.cloudwatch.model.Dimension> dimensionsList3 = Lists.newArrayList();
        List<com.amazonaws.services.cloudwatch.model.Dimension> dimensionsList4 = Lists.newArrayList();

        com.amazonaws.services.cloudwatch.model.Dimension dimension1 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension1.withName("BucketName").withValue("test-bucket");

        com.amazonaws.services.cloudwatch.model.Dimension dimension2 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension2.withName("StorageType").withValue("StandardStorage");

        com.amazonaws.services.cloudwatch.model.Dimension dimension3 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension3.withName("FilterId").withValue("EntireBucket");

        com.amazonaws.services.cloudwatch.model.Dimension dimension4 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension4.withName("FilterId").withValue("Test");

        com.amazonaws.services.cloudwatch.model.Dimension dimension5 = new com.amazonaws.services.cloudwatch.model.Dimension();
        dimension5.withName("FilterId").withValue("Prod");

        dimensionsList1.add(dimension1); //BucketName, StorageType for Storage Metrics
        dimensionsList1.add(dimension2);


        dimensionsList2.add(dimension1);//BucketName, FilterId for Request Metrics
        dimensionsList2.add(dimension3);

        dimensionsList3.add(dimension1);
        dimensionsList3.add(dimension4);

        dimensionsList4.add(dimension1);
        dimensionsList4.add(dimension5);

        AccountMetricStatistics accountMetricStatistics = new AccountMetricStatistics();
        accountMetricStatistics.setAccountName("AppD");
        namespaceMetricStatistics.setNamespace("AWS/S3");
        RegionMetricStatistics regionMetricStatistics = new RegionMetricStatistics();
        regionMetricStatistics.setRegion("us-west-1");

        //-- 1st metric//
        com.amazonaws.services.cloudwatch.model.Metric metric1 = new com.amazonaws.services.cloudwatch.model.Metric();
        metric1.setDimensions(dimensionsList1);

        List includeMetrics1 = Lists.newArrayList();
        IncludeMetric includeMetric1 = new IncludeMetric();
        includeMetric1.setName("testmetric1");
        includeMetrics1.add(includeMetric1);

        AWSMetric awsMetric1 = new AWSMetric();
        awsMetric1.setIncludeMetric(includeMetric1);
        awsMetric1.setMetric(metric1);

        MetricStatistic metricStatistic1 = new MetricStatistic();
        metricStatistic1.setValue(1.0);
        metricStatistic1.setUnit("TestUnits");
        metricStatistic1.setMetricPrefix("Custom Metric|AWS S3|");
        metricStatistic1.setMetric(awsMetric1);

        //-2nd metric--//
        com.amazonaws.services.cloudwatch.model.Metric metric2 = new com.amazonaws.services.cloudwatch.model.Metric();
        metric2.setDimensions(dimensionsList2);

        List includeMetrics2 = Lists.newArrayList();
        IncludeMetric includeMetric2 = new IncludeMetric();
        includeMetric2.setName("testmetric2");
        includeMetrics2.add(includeMetric2);

        AWSMetric awsMetric2 = new AWSMetric();
        awsMetric2.setIncludeMetric(includeMetric2);
        awsMetric2.setMetric(metric2);

        MetricStatistic metricStatistic2 = new MetricStatistic();
        metricStatistic2.setValue(1.0);
        metricStatistic2.setUnit("TestUnits");
        metricStatistic2.setMetricPrefix("Custom Metric|AWS S3|");
        metricStatistic2.setMetric(awsMetric2);

        //-- 3rd metric//
        com.amazonaws.services.cloudwatch.model.Metric metric3 = new com.amazonaws.services.cloudwatch.model.Metric();
        metric3.setDimensions(dimensionsList3);

        List includeMetrics3 = Lists.newArrayList();
        IncludeMetric includeMetric3 = new IncludeMetric();
        includeMetric3.setName("testmetric3");
        includeMetrics3.add(includeMetric3);

        AWSMetric awsMetric3 = new AWSMetric();
        awsMetric3.setIncludeMetric(includeMetric3);
        awsMetric3.setMetric(metric3);

        MetricStatistic metricStatistic3 = new MetricStatistic();
        metricStatistic3.setValue(1.0);
        metricStatistic3.setUnit("TestUnits");
        metricStatistic3.setMetricPrefix("Custom Metric|AWS S3|");
        metricStatistic3.setMetric(awsMetric3);

        //--4th metric-//

        com.amazonaws.services.cloudwatch.model.Metric metric4  = new com.amazonaws.services.cloudwatch.model.Metric();
        metric4.setDimensions(dimensionsList4);

        List includeMetrics4 = Lists.newArrayList();
        IncludeMetric includeMetric4 = new IncludeMetric();
        includeMetric4.setName("testmetric4");
        includeMetrics4.add(includeMetric4);

        AWSMetric awsMetric4 = new AWSMetric();
        awsMetric4.setIncludeMetric(includeMetric4);
        awsMetric4.setMetric(metric4);

        MetricStatistic metricStatistic4 = new MetricStatistic();
        metricStatistic4.setValue(1.0);
        metricStatistic4.setUnit("TestUnits");
        metricStatistic4.setMetricPrefix("Custom Metric|AWS S3|");
        metricStatistic4.setMetric(awsMetric4);

        //add all metrics to region metrics
        regionMetricStatistics.addMetricStatistic(metricStatistic1);
        regionMetricStatistics.addMetricStatistic(metricStatistic2);
        regionMetricStatistics.addMetricStatistic(metricStatistic3);
        regionMetricStatistics.addMetricStatistic(metricStatistic4);

        accountMetricStatistics.add(regionMetricStatistics);
        namespaceMetricStatistics.add(accountMetricStatistics);

    }

    @Test
    public void whenPrintingMetricThenCheckMetricPath(){

        List<Dimension> dimensionsFromConfig = Lists.newArrayList();
        Dimension dimension1 = new Dimension();
        dimension1.setName("BucketName");
        dimension1.setDisplayName("Bucket Name");
        dimensionsFromConfig.add(dimension1);

        Dimension dimension2 = new Dimension();
        dimension2.setName("StorageType");
        dimension2.setDisplayName("Storage Type");
        dimensionsFromConfig.add(dimension2);

        Dimension dimension3 = new Dimension();
        dimension3.setName("FilterId");
        dimension3.setDisplayName("Filter Id");
        dimensionsFromConfig.add(dimension3);

        S3MetricsProcessor s3MetricsProcessor = new S3MetricsProcessor(new ArrayList(), dimensionsFromConfig);

        List<Metric> stats = s3MetricsProcessor.createMetricStatsMapForUpload(namespaceMetricStatistics);

        Assert.assertNotNull(stats);
        Assert.assertEquals(stats.get(0).getMetricPath(),
                "Custom Metric|AWS S3|AppD|us-west-1|Bucket Name|test-bucket|Storage Type|StandardStorage|testmetric1" );
        Assert.assertEquals(stats.get(1).getMetricPath(),
                "Custom Metric|AWS S3|AppD|us-west-1|Bucket Name|test-bucket|Filter Id|EntireBucket|testmetric2" );
        Assert.assertEquals(stats.get(2).getMetricPath(),
                "Custom Metric|AWS S3|AppD|us-west-1|Bucket Name|test-bucket|Filter Id|Test|testmetric3"  );
        Assert.assertEquals(stats.get(3).getMetricPath(),
                "Custom Metric|AWS S3|AppD|us-west-1|Bucket Name|test-bucket|Filter Id|Prod|testmetric4"  );

    }
}
