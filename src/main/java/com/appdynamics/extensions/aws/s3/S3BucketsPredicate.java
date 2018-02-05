/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.aws.s3;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import static com.appdynamics.extensions.aws.s3.util.Constants.*;
import java.util.List;

/**
 * Created by aditya.jagtiani on 11/8/17.
 */
public class S3BucketsPredicate implements Predicate<Metric> {
    private List<String> buckets;
    private Predicate<CharSequence> patternPredicate;

    public S3BucketsPredicate(List<String> buckets) {
        this.buckets = buckets;
        build();
    }

    private void build() {
        if (buckets != null && !buckets.isEmpty()) {
            for (String pattern : buckets) {
                Predicate<CharSequence> charSequencePredicate = Predicates.containsPattern(pattern);
                if (patternPredicate == null) {
                    patternPredicate = charSequencePredicate;
                } else {
                    patternPredicate = Predicates.or(patternPredicate, charSequencePredicate);
                }
            }
        }
    }

    public boolean apply(Metric metric) {
        List<Dimension> dimensions = metric.getDimensions();

        for (Dimension dimension : dimensions) {
            String name = dimension.getName();
            String value = dimension.getValue();
            if (BUCKET_DIMENSION_NAME.equalsIgnoreCase(name)) {
                return patternPredicate.apply(value);
            }
        }
        return false;
    }
}
