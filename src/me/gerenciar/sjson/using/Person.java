package me.gerenciar.sjson.using;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.gerenciar.sjson.annotation.Polymorphism;

public class Person
{
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
