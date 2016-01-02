package me.gerenciar.sjson.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gerenciar.sjson.annotation.Polymorphism;
import me.gerenciar.sjson.gateway.Gateway;

public class Person extends Gateway
{
	private transient static final long serialVersionUID = 8543471302722590306L;
	
	public String name;
	public Short age;
	public Float salary;
	public Date birthday;
	public Car car;
	public int[][] intArray;
	public Map<String, Car> map = new HashMap<>();
	@Polymorphism("java.util.ArrayList")
	public List<String> list;
	@Polymorphism("java.util.ArrayList")
	public List<Car> cars;
	@Polymorphism("java.util.ArrayList")
	public List<ArrayList<ArrayList<Integer>>> numbers;
	@Polymorphism("java.util.ArrayList")
	public List<HashMap<String, String>> hashList;
}
