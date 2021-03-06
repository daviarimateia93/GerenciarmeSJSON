package me.gerenciar.sjson.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.gerenciar.sjson.util.ReflectionHelper;

public class Writer
{
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	
	public Writer()
	{
	
	}
	
	protected boolean isWritable(Field field, Object instance)
	{
		return !Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
	}
	
	public String write(Object object)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		writeObject(stringBuilder, object);
		
		return stringBuilder.toString();
	}
	
	private void writeObject(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append('{');
		
		int serializedFields = 0;
		
		for(Field field : ReflectionHelper.getFields(object))
		{
			if(isWritable(field, object))
			{
				field.setAccessible(true);
				
				try
				{
					if(serializedFields > 0)
					{
						stringBuilder.append(',');
					}
					
					stringBuilder.append("\"" + field.getName() + "\":");
					
					write(stringBuilder, field, object);
					
					serializedFields++;
				}
				catch(IllegalArgumentException | IllegalAccessException exception)
				{
					writeNull(stringBuilder);
				}
			}
		}
		
		stringBuilder.append(",\"__className__\":\"" + object.getClass().getName() + "\"");
		
		stringBuilder.append('}');
	}
	
	private void writeNull(StringBuilder stringBuilder)
	{
		stringBuilder.append("null");
	}
	
	private void writeArray(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append('[');
		
		int length = Array.getLength(object);
		
		for(int i = 0; i < length; i++)
		{
			if(i > 0)
			{
				stringBuilder.append(',');
			}
			
			write(stringBuilder, Array.get(object, i));
		}
		
		stringBuilder.append(']');
	}
	
	private void writeList(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append('[');
		
		List<?> list = (List<?>) object;
		
		for(int i = 0; i < list.size(); i++)
		{
			if(i > 0)
			{
				stringBuilder.append(',');
			}
			
			write(stringBuilder, list.get(i));
		}
		
		stringBuilder.append(']');
	}
	
	private void writeSet(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append('[');
		
		Set<?> set = (Set<?>) object;
		
		int i = 0;
		
		for(Object item : set)
		{
			if(i > 0)
			{
				stringBuilder.append(',');
			}
			
			write(stringBuilder, item);
			
			i++;
		}
		
		stringBuilder.append(']');
	}
	
	private void writeMap(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append('{');
		
		Map<?, ?> map = (Map<?, ?>) object;
		
		int i = 0;
		
		for(Map.Entry<?, ?> entry : map.entrySet())
		{
			if(i > 0)
			{
				stringBuilder.append(',');
			}
			
			String key = null;
			
			if(entry.getKey() == null)
			{
				key = "null";
			}
			else if(ReflectionHelper.isPrimitive(entry.getKey()))
			{
				key = entry.getKey().toString();
			}
			
			if(key != null)
			{
				stringBuilder.append("\"" + key + "\"" + ":");
				write(stringBuilder, entry.getValue());
				
				i++;
			}
		}
		
		stringBuilder.append('}');
	}
	
	private void writeQuoted(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append("\"" + object.toString() + "\"");
	}
	
	private void writeUnquoted(StringBuilder stringBuilder, Object object)
	{
		stringBuilder.append(object.toString());
	}
	
	private void writeString(StringBuilder stringBuilder, Object object)
	{
		writeQuoted(stringBuilder, object);
	}
	
	private void writeCharacter(StringBuilder stringBuilder, Object object)
	{
		writeQuoted(stringBuilder, object);
	}
	
	private void writeNumeric(StringBuilder stringBuilder, Object object)
	{
		writeUnquoted(stringBuilder, object);
	}
	
	private void writeBoolean(StringBuilder stringBuilder, Object object)
	{
		writeUnquoted(stringBuilder, object);
	}
	
	private void writeDate(StringBuilder stringBuilder, Object object)
	{
		writeString(stringBuilder, dateFormat.format((Date) object));
	}
	
	private void write(StringBuilder stringBuilder, Field field, Object instance) throws IllegalArgumentException, IllegalAccessException
	{
		write(stringBuilder, field.get(instance));
	}
	
	protected void write(StringBuilder stringBuilder, Object object)
	{
		if(ReflectionHelper.isNull(object))
		{
			writeNull(stringBuilder);
		}
		else if(ReflectionHelper.isArray(object))
		{
			writeArray(stringBuilder, object);
		}
		else if(ReflectionHelper.isList(object))
		{
			writeList(stringBuilder, object);
		}
		else if(ReflectionHelper.isSet(object))
		{
			writeSet(stringBuilder, object);
		}
		else if(ReflectionHelper.isMap(object))
		{
			writeMap(stringBuilder, object);
		}
		else if(ReflectionHelper.isString(object))
		{
			writeString(stringBuilder, object);
		}
		else if(ReflectionHelper.isCharacter(object))
		{
			writeCharacter(stringBuilder, object);
		}
		else if(ReflectionHelper.isNumeric(object))
		{
			writeNumeric(stringBuilder, object);
		}
		else if(ReflectionHelper.isBoolean(object))
		{
			writeBoolean(stringBuilder, object);
		}
		else if(object instanceof Enum)
		{
			writeString(stringBuilder, ((Enum<?>) object).name());
		}
		else if(object instanceof Date)
		{
			writeDate(stringBuilder, object);
		}
		else
		{
			writeObject(stringBuilder, object);
		}
	}
}
