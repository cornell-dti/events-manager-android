package com.dti.cornell.events.utils;

import com.dti.cornell.events.models.Event;

import java.util.Comparator;

/**
 * Created by jboss925 on 8/30/18.
 */

public class Comparators {

    public static final Comparator<TagUtil.IDAndFrequency> FREQUENCY = new Comparator<TagUtil.IDAndFrequency>() {
        @Override
        public int compare(TagUtil.IDAndFrequency if1, TagUtil.IDAndFrequency if2) {
            return if1.frequency.compareTo(if2.frequency);
        }

    };

    public static final Comparator<Event> NUM_ATTENDEES = new Comparator<Event>() {
        @Override
        public int compare(Event e1, Event e2) {
            return e1.numAttendees - e2.numAttendees;
        }

    };
}
