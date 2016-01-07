package me.gerenciar.sjson;

import me.gerenciar.sjson.entity.Request;

public class DSimpleJson
{
	public static void main(String args[])
	{
		/*
		String personSource = "{\"name\":\"Davi de Sousa Arimateia\",\"age\":21,\"salary\":5400.0,\"birthday\":\"2014-09-17T17:26Z\",\"car\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"intArray\":[ [ 1, 10, 100, 1000 ], [ 1, 10, 100, 1000 ], [ 1, 10, 100, 1000 ]],\"map\":{\"car2\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"car3\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"car1\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}},\"list\":null,\"cars\":[{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}], \"numbers\":[[[1,2,3,4, 5],[1,2,3],[1,2,3],[1,2,3]], [[1,2,3],[1,2,3],[1,2,3]]], \"hashList\":[{\"key1\":\"value1\", \"key2\":\"value2\"}]}";
		
		Person person = new Person().toObject(personSource);
		System.out.println(person);
		
		Request request = new Request("me.gerenciar.sjson.DSimpleJson", "main", "arg1", "arg2");
		System.out.println(request);
		*/
		
		String requestSource = "{\"className\":\"irose.server.AccountService\",\"methodName\":null,\"params\":[\"teste\",123],\"paramsClassNames\":[\"java.lang.String\",\"java.lang.String\"]}";
		Request request = new Request().toObject(requestSource);
		System.out.println(request);
		
		/*
		// String source =
		// "{\"teste\":\"ahuhua\", \"idade\":10,\"nome\":\"davi\"}";
		// String source =
		// "[{\"teste\":\"ahuhua\", \"idade\":10,\"nome\":\"davi\"}, {\"teste\":\"ahuhua\", \"idade\":12,\"nome\":\"davi\"}]";
		// String source =
		// "[{\"teste\":[{\"ahuhua1\":\"_ahuhua1\", \"ahuhua2\":\"_ahuhua2\"}, {\"ahuhua1\":\"_ahuhua1\", \"ahuhua2\":\"_ahuhua2\"}], \"idade\":10,\"nome\":\"davi\"}, {\"teste\":\"ahuhua\", \"idade\":10,\"nome\":\"davi\"}]";
		// String source = "{\"huhu\":{\"array\" : [1, 5, 10]}}";
		// String source =
		// "[{\"huhu\":{\"array\" : [1, 5, 10]}}, {\"teste\":[{\"ahuhua1\":\"_ahuhua1\", \"ahuhua2\":\"_ahuhua2\"}, {\"ahuhua1\":\"_ahuhua1\", \"ahuhua2\":\"_ahuhua2\"}], \"idade\":10,\"nome\":\"davi\"}, {\"teste\":\"ahuhua\", \"idade\":10,\"nome\":\"davi\"}]";
		
		// String source =
		// "{\"model\":\"modelo\", \"brand\": \"marca\", \"value\": 99.00}";
		// String source =
		// "{\"com.daviarimateia.dsimplejson.canonicalName\":\"com.daviarimateia.dsimplejson.entity.Person\",\"com.daviarimateia.dsimplejson.name\":\"com.daviarimateia.dsimplejson.entity.Person\",\"com.daviarimateia.dsimplejson.package\":\"package com.daviarimateia.dsimplejson.entity\",\"serialVersionUID\":\"1\",\"name\":\"Davi de Sousa Arimateia\",\"age\":\"21\",\"salary\":5400.0,\"birthday\":\"2014-09-16T18:02Z\",\"car\":{\"com.daviarimateia.dsimplejson.canonicalName\":\"com.daviarimateia.dsimplejson.entity.Car\",\"com.daviarimateia.dsimplejson.name\":\"com.daviarimateia.dsimplejson.entity.Car\",\"com.daviarimateia.dsimplejson.package\":\"package com.daviarimateia.dsimplejson.entity\",\"serialVersionUID\":\"1\",\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"intArray\":[1,10,100,1000],\"map\":{\"car\":{\"com.daviarimateia.dsimplejson.canonicalName\":\"com.daviarimateia.dsimplejson.entity.Car\",\"com.daviarimateia.dsimplejson.name\":\"com.daviarimateia.dsimplejson.entity.Car\",\"com.daviarimateia.dsimplejson.package\":\"package com.daviarimateia.dsimplejson.entity\",\"serialVersionUID\":\"1\",\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"key2\":1,\"key1\":\"value1\"}, \"list\":[\"aaa1\", \"aaa2\", \"aaa3\"]}";
		// String source =
		// "{\"name\":\"Davi de Sousa Arimateia\",\"age\":21,\"salary\":5400.0,\"birthday\":\"2014-09-17T12:30Z\",\"car\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"intArray\":[1,10,100,1000],\"map\":{\"car\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}},\"list\":[1,2, \"huahu\"],\"cars\":[{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}]}";
		String source = "{\"name\":\"Davi de Sousa Arimateia\",\"age\":21,\"salary\":5400.0,\"birthday\":\"2014-09-17T17:26Z\",\"car\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"intArray\":[ [ 1, 10, 100, 1000 ], [ 1, 10, 100, 1000 ], [ 1, 10, 100, 1000 ]],\"map\":{\"car2\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"car3\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},\"car1\":{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}},\"list\":null,\"cars\":[{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0},{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}], \"numbers\":[[[1,2,3,4, 5],[1,2,3],[1,2,3],[1,2,3]], [[1,2,3],[1,2,3],[1,2,3]]], \"hashList\":[{\"key1\":\"value1\", \"key2\":\"value2\"}]}";
		
		Person p = new Reader().read(Person.class, source);
		System.out.println(p);
		
		Car car = new Car();
		car.brand = "Ford";
		car.model = "New Fiesta Titanium Powershift";
		car.value = 53000.00F;
		
		Car c = new Reader().read(Car.class, "{\"model\":\"New Fiesta Titanium Powershift\",\"brand\":\"Ford\",\"value\":53000.0}");
		System.out.println("aki" + c);
		
		Map<String, Car> map = new HashMap<>();
		map.put("car1", car);
		map.put("car2", car);
		map.put("car3", car);
		// map.put("key1", "value1");
		// map.put("key2", 1);
		
		// Map<Car, Integer> map = new HashMap<>();
		// map.put(car, 1);
		// map.put(car, 2);
		
		ArrayList<Integer> numbers = new ArrayList<>();
		numbers.add(1);
		numbers.add(2);
		numbers.add(3);
		
		ArrayList<ArrayList<Integer>> numbers2 = new ArrayList<>();
		numbers2.add(numbers);
		numbers2.add(numbers);
		numbers2.add(numbers);
		
		Person person = new Person();
		person.age = 21;
		person.salary = 5400.00F;
		person.name = "Davi de Sousa Arimateia";
		person.birthday = new Date();
		person.car = car;
		person.intArray = new int[][] { { 1, 10, 100, 1000 }, { 1, 10, 100, 1000 }, { 1, 10, 100, 1000 } };
		person.map = map;
		person.cars = new ArrayList<>();
		person.cars.add(car);
		person.cars.add(car);
		person.cars.add(car);
		person.numbers = new ArrayList<>();
		person.numbers.add(numbers2);
		person.numbers.add(numbers2);
		person.numbers.add(numbers2);
		person.numbers.add(numbers2);
		
		//System.out.println(person);
		 * */
	}
}
