package com.daviarimateia.dsimplejson.entity;

import com.daviarimateia.dsimplejson.gateway.Gateway;

public class Car extends Gateway
{
	private transient static final long serialVersionUID = -7047706723941188702L;
	
	public String model;
	public String brand;
	public Float value;
}
