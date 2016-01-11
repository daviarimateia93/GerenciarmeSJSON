package me.gerenciar.sjson.util;

import java.util.TreeMap;

public class Json extends TreeMap<String, Object>
{
	private static final long serialVersionUID = -1842361675292136534L;
	
	public Json()
	{
	
	}
	
	public Json(TreeMap<String, Object> treeMap)
	{
		super(treeMap);
	}
}
