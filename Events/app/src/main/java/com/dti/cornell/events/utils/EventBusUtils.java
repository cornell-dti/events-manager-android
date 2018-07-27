package com.dti.cornell.events.utils;

import com.google.common.eventbus.EventBus;

public class EventBusUtils
{
	public static final EventBus SINGLETON = new EventBus();

	public static final class DateChanged
	{}
}
