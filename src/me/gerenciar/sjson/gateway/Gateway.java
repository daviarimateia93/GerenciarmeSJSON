package me.gerenciar.sjson.gateway;

import java.io.Serializable;

import me.gerenciar.sjson.parser.Reader;
import me.gerenciar.sjson.parser.Writer;

public abstract class Gateway implements Serializable
{
	private transient static final long serialVersionUID = -2498129071535935413L;
	
	protected String className;
	private transient Reader reader;
	private transient Writer writer;
	
	public Gateway()
	{
		className = getClass().getName();
		reader = new Reader();
		writer = new Writer();
	}
	
	public Gateway(Reader reader)
	{
		this.className = getClass().getName();
		this.reader = reader;
		this.writer = new Writer();
	}
	
	public Gateway(Writer writer)
	{
		this.className = getClass().getName();
		this.reader = new Reader();
		this.writer = writer;
	}
	
	public Gateway(Reader reader, Writer writer)
	{
		this.className = getClass().getName();
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
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object object)
	{
		return object != null ? toString().equals(object.toString()) : false;
	}
}
