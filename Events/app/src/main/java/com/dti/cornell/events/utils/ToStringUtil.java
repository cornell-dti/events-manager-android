package com.dti.cornell.events.utils;

import com.google.common.collect.ImmutableList;

import java.util.regex.Pattern;

public final class ToStringUtil
{
	private ToStringUtil() {}

	private static final String TAG = ToStringUtil.class.getSimpleName();
	public static final String FIELD_SEPARATOR = "\0";
	public static final String ARRAY_SEPARATOR = "|";

	public static ImmutableList<String> listFromString(String input)
	{
		return ImmutableList.copyOf(input.split(Pattern.quote(ARRAY_SEPARATOR)));
	}
	public static ImmutableList<Integer> intListFromString(String input)
	{
		String[] split = input.split(Pattern.quote(ARRAY_SEPARATOR));
		ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<>();
		for (String string : split)
			builder.add(Integer.valueOf(string));
		return builder.build();
	}
}
