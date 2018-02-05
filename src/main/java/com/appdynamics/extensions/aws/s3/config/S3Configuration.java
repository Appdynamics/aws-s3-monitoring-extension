/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3.config;

import com.appdynamics.extensions.aws.config.Configuration;

import java.util.List;

/**
 * Created by aditya.jagtiani on 11/8/17.
 */
public class S3Configuration extends Configuration {

    private List<String> buckets;

    public void setBuckets(List<String> buckets) {
        this.buckets = buckets;
    }

    public List<String> getBuckets() {
        return this.buckets;
    }
}
