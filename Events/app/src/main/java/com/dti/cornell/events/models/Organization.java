package com.dti.cornell.events.models;

import android.text.TextUtils;

import com.dti.cornell.events.utils.ToStringUtil;
import com.google.common.collect.ImmutableList;

public class Organization
{
	public final int id;
	public final String name;
	public final String description;
	public final int pictureID;
	public final ImmutableList<Integer> eventIDs;
	public final ImmutableList<String> memberIDs;
	public final String website;
	public final String email;

	public Organization(int id, String name, String description, int pictureID, ImmutableList<Integer> eventIDs, ImmutableList<String> memberIDs, String website, String email)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.pictureID = pictureID;
		this.eventIDs = eventIDs;
		this.memberIDs = memberIDs;
		this.website = website;
		this.email = email;
	}

	@Override
	public String toString()
	{
		return id + ToStringUtil.FIELD_SEPARATOR +
				name + ToStringUtil.FIELD_SEPARATOR +
				description + ToStringUtil.FIELD_SEPARATOR +
				pictureID + ToStringUtil.FIELD_SEPARATOR +
				TextUtils.join(ToStringUtil.ARRAY_SEPARATOR, eventIDs) + ToStringUtil.FIELD_SEPARATOR +
				TextUtils.join(ToStringUtil.ARRAY_SEPARATOR, memberIDs) + ToStringUtil.FIELD_SEPARATOR +
				website + ToStringUtil.FIELD_SEPARATOR +
				email;
	}

	public static Organization fromString(String string)
	{
		String[] split = string.split(ToStringUtil.FIELD_SEPARATOR);
		int id = Integer.valueOf(split[0]);
		String name = split[1];
		String description = split[2];
		int pictureID = Integer.valueOf(split[3]);
		ImmutableList<Integer> eventIDs = ToStringUtil.intListFromString(split[4]);
		ImmutableList<String> memberIDs = ToStringUtil.listFromString(split[5]);
		String website = split[6];
		String email = split[7];
		return new Organization(id, name, description, pictureID, eventIDs, memberIDs, website, email);
	}
}