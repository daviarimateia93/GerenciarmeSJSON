package me.gerenciar.sjson.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import me.gerenciar.sjson.annotation.Polymorphism;
import me.gerenciar.sjson.util.Json;
import me.gerenciar.sjson.util.ReflectionHelper;

public class Reader
{
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	
	private class Command
	{
		static final int ACTION_BEGIN_OBJECT = 1;
		static final int ACTION_END_OBJECT = 2;
		static final int ACTION_BEGIN_ARRAY = 4;
		static final int ACTION_END_ARRAY = 8;
		static final int ACTION_ATTRIBUTE_NAME = 16;
		static final int ACTION_ATTRIBUTE_VALUE = 32;
		static final int ACTION_SEPARATOR_NAME_VALUE = 64; // :
		static final int ACTION_SEPARATOR_ATTRIBUTE = 128; // ,
		static final int ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN = 256; // "
		static final int ACTION_SEPARATOR_ATTRIBUTE_NAME_END = 512; // "
		
		static final String VALUE_TYPE_NUMBER = "NUMBER";
		static final String VALUE_TYPE_BOOLEAN = "BOOLEAN";
		static final String VALUE_TYPE_STRING = "STRING";
		
		int action;
		String name;
		String value;
		boolean inArray = false;
	}
	
	private class Mutable<T>
	{
		T object;
		
		Mutable(T object)
		{
			this.object = object;
		}
	}
	
	public Reader()
	{
	
	}
	
	protected boolean isReadable(Field field, Object instance)
	{
		return !Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
	}
	
	public Json read(String source)
	{
		return read(Json.class, source);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> type, String source)
	{
		try
		{
			TreeMap<String, Object> parsedMap = parse(source);
			
			if(type.isAssignableFrom(Json.class))
			{
				return (T) new Json(parsedMap);
			}
			else
			{
				T instance = type.newInstance();
				
				read(parsedMap.get("ROOT"), new Mutable<Object>(instance), null);
				
				return instance;
			}
		}
		catch(InstantiationException | IllegalAccessException exception)
		{
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void read(Object mapItem, Mutable<Object> instance, Type fieldGenericType)
	{
		try
		{
			if(mapItem instanceof Map)
			{
				Map<String, Object> map = (Map<String, Object>) mapItem;
				
				for(Map.Entry<String, Object> entry : map.entrySet())
				{
					if(instance.object != null ? instance.object.getClass().equals(Object.class) : true)
					{
						instance.object = newInstance(map.get("__className__").toString());
					}
					
					Field field = ReflectionHelper.getField(entry.getKey(), instance.object.getClass());
					
					if(field != null)
					{
						if(isReadable(field, instance.object))
						{
							field.setAccessible(true);
							
							Object newInstance = newInstance(field, instance.object, entry.getValue());
							
							if(newInstance != null)
							{
								Mutable<Object> mutable = new Mutable<>(newInstance);
								
								read(entry.getValue(), mutable, field.getGenericType());
								
								field.set(instance.object, mutable.object);
							}
						}
					}
					else
					{
						if(fieldGenericType instanceof ParameterizedType)
						{
							Type keyType = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
							Type valueType = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[1];
							
							Object keyNewInstance;
							
							if(keyType instanceof ParameterizedType)
							{
								keyNewInstance = ReflectionHelper.generateDefaultValue(((Class<?>) ((ParameterizedType) keyType).getRawType()));
							}
							else
							{
								keyNewInstance = ReflectionHelper.generateDefaultValue(((Class<?>) keyType));
							}
							
							Object valueNewInstance;
							
							if(valueType instanceof ParameterizedType)
							{
								valueNewInstance = ReflectionHelper.generateDefaultValue(((Class<?>) ((ParameterizedType) valueType).getRawType()));
							}
							else
							{
								valueNewInstance = ReflectionHelper.generateDefaultValue(((Class<?>) valueType));
							}
							
							if(keyNewInstance != null)
							{
								Mutable<Object> keyMutable = new Mutable<>(keyNewInstance);
								Mutable<Object> valueMutable = new Mutable<>(valueNewInstance);
								
								read(entry.getKey(), keyMutable, keyType);
								read(entry.getValue(), valueMutable, valueType);
								
								Map instanceMap = (Map) instance.object;
								instanceMap.put(keyMutable.object, valueMutable.object);
							}
						}
					}
				}
			}
			else if(mapItem instanceof List)
			{
				List<Object> list = (List<Object>) mapItem;
				
				for(int i = 0; i < list.size(); i++)
				{
					if(ReflectionHelper.isArray(instance.object))
					{
						Mutable<Object> mutable = new Mutable<Object>(Array.get(instance.object, i));
						
						read(list.get(i), mutable, null);
						
						Array.set(instance.object, i, mutable.object);
					}
					else
					{
						List instanceList = (List) instance.object;
						
						if(fieldGenericType instanceof ParameterizedType)
						{
							Type type = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
							
							Object newInstance;
							
							if(type instanceof ParameterizedType)
							{
								newInstance = ReflectionHelper.generateDefaultValue(((Class<?>) ((ParameterizedType) type).getRawType()));
							}
							else
							{
								newInstance = ReflectionHelper.generateDefaultValue(((Class<?>) type));
							}
							
							if(newInstance != null)
							{
								Mutable<Object> mutable = new Mutable<>(newInstance);
								
								read(list.get(i), mutable, type);
								
								instanceList.add(mutable.object);
							}
						}
					}
				}
			}
			else
			{
				if(instance.object instanceof Date)
				{
					try
					{
						instance.object = dateFormat.parse(mapItem.toString());
					}
					catch(ParseException exception)
					{
						// ignore, let it be null
					}
				}
				else
				{
					if(mapItem != null)
					{
						String mapItemAsString = mapItem.toString();
						int indexOfMapItemSeparator = mapItemAsString.indexOf(";");
						String mapItemValue = null;
						boolean setted = false;
						
						if(indexOfMapItemSeparator > -1)
						{
							String mapItemType = mapItemAsString.substring(0, indexOfMapItemSeparator);
							mapItemValue = mapItemAsString.substring(indexOfMapItemSeparator + 1, mapItemAsString.length());
							Class<?> mapItemTypeType = null;
							
							if(instance.object == null)
							{
								switch(mapItemType)
								{
									case Command.VALUE_TYPE_BOOLEAN:
									{
										mapItemTypeType = Boolean.class;
										break;
									}
									
									case Command.VALUE_TYPE_NUMBER:
									{
										mapItemTypeType = Double.class;
										break;
									}
									
									case Command.VALUE_TYPE_STRING:
									{
										mapItemTypeType = String.class;
										break;
									}
								}
								
								if(fieldGenericType != null)
								{
									try
									{
										Class<?> type = Class.forName(fieldGenericType.getTypeName());
										
										if(type.isEnum() && String.class.isAssignableFrom(mapItemTypeType))
										{
											instance.object = Enum.valueOf((Class<Enum>) type, mapItemValue);
											
											setted = true;
										}
									}
									catch(ClassNotFoundException | IllegalArgumentException exception)
									{
										// ignore it
									}
								}
								else if(mapItemTypeType != null)
								{
									instance.object = newInstance(mapItemTypeType);
								}
							}
						}
						else
						{
							mapItemValue = mapItemAsString;
						}
						
						if(!setted)
						{
							instance.object = ReflectionHelper.generateBasicValue(mapItemValue, instance.object);
						}
					}
					else
					{
						instance.object = null;
					}
				}
			}
		}
		catch(SecurityException | IllegalArgumentException |
		
		IllegalAccessException exception)
		
		{
			exception.printStackTrace();
		}
		
	}
	
	protected Object newInstance(Field field, Object instance, Object mapItem)
	{
		Object newInstance = null;
		
		try
		{
			newInstance = field.get(instance);
		}
		catch(IllegalArgumentException | IllegalAccessException exception)
		{
			// ignore
		}
		
		if(field.getType().isArray() && mapItem instanceof List)
		{
			List<Integer> sizes = new ArrayList<>();
			
			Object list = ((List<?>) mapItem);
			
			sizes.add(((List<?>) list).size());
			
			do
			{
				int bigger = -1;
				int index = -1;
				
				for(int i = 0; i < ((List<?>) list).size(); i++)
				{
					if(((List<?>) list).get(i) instanceof List)
					{
						int size = ((List<?>) ((List<?>) list).get(i)).size();
						
						if(size > bigger)
						{
							bigger = size;
							index = i;
						}
					}
				}
				
				if(index == -1)
				{
					break;
				}
				
				list = ((List<?>) list).get(index);
				
				sizes.add(((List<?>) list).size());
			}
			while(list instanceof List);
			
			int[] dimensions = new int[sizes.size()];
			
			for(int i = 0; i < sizes.size(); i++)
			{
				dimensions[i] = sizes.get(i);
			}
			
			if(ReflectionHelper.isArray(field))
			{
				Class<?> componentType = field.getType().getComponentType();
				Class<?> lastComponentType = componentType;
				
				while((componentType = componentType.getComponentType()) != null)
				{
					lastComponentType = componentType;
				}
				
				newInstance = Array.newInstance(lastComponentType, dimensions);
			}
		}
		
		if(newInstance == null)
		{
			if(field.isAnnotationPresent(Polymorphism.class))
			{
				String className = field.getAnnotation(Polymorphism.class).value();
				
				try
				{
					newInstance = Class.forName(className).newInstance();
				}
				catch(ClassNotFoundException | InstantiationException | IllegalAccessException exception)
				{
					newInstance = ReflectionHelper.generateDefaultValue(field.getType());
				}
			}
			else
			{
				newInstance = newInstance(field.getType());
			}
		}
		
		if(newInstance == null)
		{
			Mutable<Object> mutable = new Mutable<>(newInstance);
			
			read(mapItem, mutable, field.getType());
			
			return mutable.object;
		}
		else
		{
			return newInstance;
		}
	}
	
	private Object newInstance(String className)
	{
		try
		{
			if(className.contains(";"))
			{
				className = className.split("\\;")[1];
			}
			
			return newInstance(Class.forName(className));
		}
		catch(ClassNotFoundException exception)
		{
			return null;
		}
	}
	
	private Object newInstance(Class<?> type)
	{
		try
		{
			return type.newInstance();
		}
		catch(IllegalAccessException | InstantiationException exception)
		{
			return ReflectionHelper.generateDefaultValue(type);
		}
	}
	
	private TreeMap<String, Object> parse(String source)
	{
		// beginCommand creation
		Command beginCommand = new Command();
		
		String trimmedSource = source.trim();
		
		if(trimmedSource.startsWith("{"))
		{
			beginCommand.action = Command.ACTION_BEGIN_OBJECT;
		}
		else if(trimmedSource.startsWith("["))
		{
			beginCommand.action = Command.ACTION_BEGIN_ARRAY;
		}
		else
		{
			return null;
		}
		
		// auxiliary variables declaration/population
		Stack<Command> commands = new Stack<>();
		commands.push(beginCommand);
		
		boolean first = true;
		
		char read;
		
		Command command;
		
		TreeMap<String, Object> map = new TreeMap<>();
		
		Stack<Object> mapValues = new Stack<>();
		
		// parse
		for(int i = 0; i < source.length(); i++)
		{
			read = source.charAt(i);
			
			if(commands.size() == 0)
			{
				if(read == ' ')
				{
					continue;
				}
				else
				{
					return null;
				}
			}
			
			command = commands.peek();
			
			// ignore spaces when object, array or separators
			if(((Command.ACTION_BEGIN_OBJECT & command.action) != 0 || (Command.ACTION_END_OBJECT & command.action) != 0 || (Command.ACTION_BEGIN_ARRAY & command.action) != 0 || (Command.ACTION_END_ARRAY & command.action) != 0 || (Command.ACTION_SEPARATOR_NAME_VALUE & command.action) != 0 || (Command.ACTION_SEPARATOR_ATTRIBUTE & command.action) != 0 || (Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN & command.action) != 0) && read == ' ')
			{
				continue;
			}
			
			if((Command.ACTION_BEGIN_OBJECT & command.action) != 0 && read == '{')
			{
				if(first)
				{
					TreeMap<String, Object> treeMap = new TreeMap<>();
					
					map.put("ROOT", treeMap);
					mapValues.add(treeMap);
					
					command.name = "ROOT";
					command.action = Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN;
					
					first = false;
				}
				else
				{
					TreeMap<String, Object> newTreeMap = new TreeMap<>();
					
					if(mapValues.peek() instanceof TreeMap)
					{
						@SuppressWarnings("unchecked")
						TreeMap<String, Object> treeMap = (TreeMap<String, Object>) mapValues.peek();
						treeMap.put(command.name, newTreeMap);
						
						mapValues.add(newTreeMap);
					}
					else if(mapValues.peek() instanceof ArrayList)
					{
						@SuppressWarnings("unchecked")
						ArrayList<Object> arrayList = (ArrayList<Object>) mapValues.peek();
						arrayList.add(newTreeMap);
						
						mapValues.add(newTreeMap);
					}
					
					Command newCommand = new Command();
					newCommand.action = Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN;
					
					commands.add(newCommand);
				}
			}
			else if((Command.ACTION_BEGIN_ARRAY & command.action) != 0 && read == '[')
			{
				if(first)
				{
					ArrayList<Object> arrayList = new ArrayList<>();
					
					map.put("ROOT", arrayList);
					mapValues.add(arrayList);
					
					command.name = "ROOT";
					command.action = Command.ACTION_BEGIN_ARRAY | Command.ACTION_BEGIN_OBJECT | Command.ACTION_ATTRIBUTE_VALUE;
					
					first = false;
				}
				else
				{
					ArrayList<Object> newArrayList = new ArrayList<>();
					
					if(mapValues.peek() instanceof TreeMap)
					{
						@SuppressWarnings("unchecked")
						TreeMap<String, Object> treeMap = (TreeMap<String, Object>) mapValues.peek();
						treeMap.put(command.name, newArrayList);
						
						mapValues.add(newArrayList);
					}
					else if(mapValues.peek() instanceof ArrayList)
					{
						@SuppressWarnings("unchecked")
						ArrayList<Object> arrayList = (ArrayList<Object>) mapValues.peek();
						arrayList.add(newArrayList);
						
						mapValues.add(newArrayList);
					}
					
					Command newCommand = new Command();
					newCommand.action = Command.ACTION_BEGIN_ARRAY | Command.ACTION_BEGIN_OBJECT | Command.ACTION_ATTRIBUTE_VALUE;
					newCommand.inArray = true;
					
					commands.add(newCommand);
				}
			}
			else if((Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN & command.action) != 0 && read == '"' && !command.inArray)
			{
				command.action = Command.ACTION_ATTRIBUTE_NAME;
			}
			else if(command.action == Command.ACTION_ATTRIBUTE_NAME)
			{
				// read attribute name, escaping \"
				StringBuilder attributeName = new StringBuilder();
				
				while(read != '"')
				{
					if(read == '\\' && source.charAt(i + 1) == '"')
					{
						attributeName.append("\"");
						
						i++;
					}
					else
					{
						attributeName.append(read);
					}
					
					read = source.charAt(++i);
				}
				
				--i;
				
				command.name = attributeName.toString();
				command.action = Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_END;
			}
			else if((Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_END & command.action) != 0 && read == '"')
			{
				command.action = Command.ACTION_SEPARATOR_NAME_VALUE;
			}
			else if((Command.ACTION_SEPARATOR_NAME_VALUE & command.action) != 0 && read == ':')
			{
				command.action = Command.ACTION_ATTRIBUTE_VALUE;
			}
			else if((Command.ACTION_ATTRIBUTE_VALUE & command.action) != 0)
			{
				// ignore spaces
				while(read == ' ')
				{
					read = source.charAt(++i);
				}
				
				if(read == '{')
				{
					command.action = Command.ACTION_BEGIN_OBJECT;
					--i;
					
					continue;
				}
				
				if(read == '[')
				{
					command.action = Command.ACTION_BEGIN_ARRAY;
					--i;
					
					continue;
				}
				
				boolean hasQuotes = read == '"';
				
				if(hasQuotes)
				{
					read = source.charAt(++i);
				}
				
				String scapes = hasQuotes ? "\"" : " ,}]";
				
				StringBuilder attributeValue = new StringBuilder();
				
				while(!scapes.contains(String.valueOf(read)))
				{
					if(read == '\\' && scapes.contains(String.valueOf(source.charAt(i + 1))))
					{
						attributeValue.append(source.charAt(i + 1));
						
						i++;
					}
					else
					{
						attributeValue.append(read);
					}
					
					read = source.charAt(++i);
				}
				
				if(!hasQuotes)
				{
					i--;
				}
				
				String type = hasQuotes ? Command.VALUE_TYPE_STRING : "false".equalsIgnoreCase(attributeValue.toString()) || "true".equalsIgnoreCase(attributeValue.toString()) ? Command.VALUE_TYPE_BOOLEAN : Command.VALUE_TYPE_NUMBER;
				
				command.value = !hasQuotes && "null".equalsIgnoreCase(attributeValue.toString()) ? null : type + ";" + attributeValue.toString();
				command.action = Command.ACTION_SEPARATOR_ATTRIBUTE | Command.ACTION_END_OBJECT | Command.ACTION_END_ARRAY;
				
				if(mapValues.peek() instanceof TreeMap)
				{
					@SuppressWarnings("unchecked")
					TreeMap<String, Object> treeMap = (TreeMap<String, Object>) mapValues.peek();
					treeMap.put(command.name, command.value);
				}
				else if(mapValues.peek() instanceof ArrayList)
				{
					@SuppressWarnings("unchecked")
					ArrayList<Object> arrayList = (ArrayList<Object>) mapValues.peek();
					arrayList.add(command.value);
				}
			}
			else if((Command.ACTION_SEPARATOR_ATTRIBUTE & command.action) != 0 || (Command.ACTION_END_OBJECT & command.action) != 0 || (Command.ACTION_END_ARRAY & command.action) != 0)
			{
				if(read == '}')
				{
					mapValues.pop();
					commands.pop();
					
					if(commands.size() == 0)
					{
						continue;
					}
					
					command = commands.peek();
					
					command.action = Command.ACTION_SEPARATOR_ATTRIBUTE | Command.ACTION_END_OBJECT | Command.ACTION_END_ARRAY;
				}
				else if(read == ']')
				{
					mapValues.pop();
					commands.pop();
					
					if(commands.size() == 0)
					{
						continue;
					}
					
					command = commands.peek();
					
					command.action = Command.ACTION_SEPARATOR_ATTRIBUTE | Command.ACTION_END_OBJECT | Command.ACTION_END_ARRAY;
				}
				else if(read == ',')
				{
					command.action = Command.ACTION_SEPARATOR_ATTRIBUTE_NAME_BEGIN | Command.ACTION_BEGIN_OBJECT | Command.ACTION_BEGIN_ARRAY | Command.ACTION_END_OBJECT | Command.ACTION_END_ARRAY | Command.ACTION_ATTRIBUTE_VALUE;
				}
			}
		}
		
		return map;
	}
}
