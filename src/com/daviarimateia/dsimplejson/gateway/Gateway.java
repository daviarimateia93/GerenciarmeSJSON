package com.daviarimateia.dsimplejson.gateway;

import java.io.Serializable;

import com.daviarimateia.dsimplejson.parser.Reader;
import com.daviarimateia.dsimplejson.parser.Writer;

public abstract class Gateway implements Serializable
{
	private transient static final long serialVersionUID = -2498129071535935413L;
	
	private transient Reader reader;
	private transient Writer writer;
	
	public Gateway()
	{
		reader = new Reader();
		writer = new Writer();
	}
	
	public Gateway(Reader reader)
	{
		this.reader = reader;
		writer = new Writer();
	}
	
	public Gateway(Writer writer)
	{
		reader = new Reader();
		this.writer = writer;
	}
	
	public Gateway(Reader reader, Writer writer)
	{
		this.reader = reader;
		this.writer = writer;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Gateway> T toObject(String source)
	{
		return (T) reader.read(getClass(), source);
	}
	
	@Override
	public String toString()
	{
		return writer.write(this);
	}
}
