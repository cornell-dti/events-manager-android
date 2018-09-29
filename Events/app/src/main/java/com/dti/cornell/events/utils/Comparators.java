package com.dti.cornell.events.utils;

import java.util.Comparator;

/**
 * Created by jboss925 on 8/30/18.
 */

class Comparators {

    public static final Comparator<TagUtil.IDAndFrequency> FREQUENCY = new Comparator<TagUtil.IDAndFrequency>() {
        @Override
        public int compare(TagUtil.IDAndFrequency if1, TagUtil.IDAndFrequency if2) {
            return if1.frequency.compareTo(if2.frequency);
        }

    };
}
