package com.heretic.bitpieces_practice;

import java.util.List;

import org.javalite.activejdbc.Base;

/**
 * Hello world!
 *
 */
public class Blarp 
{
	public static void main( String[] args )
	{
		System.out.println( "Hello World!" );
		Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/activejdbc_test", "root", "teresa");

		Employee e = new Employee();
		e.set("first_name", "John");
		e.set("last_name", "Doe");
		//        e.saveIt();

		Employee c = new Employee();
		c.set("first_name", "Tim");
		c.set("last_name", "Bear");
		//        c.saveIt();

		Employee f = Employee.findFirst("first_name = ?", "John");

		System.out.println(f);

		List<Employee> employees = Employee.findAll();

		System.out.println(employees);

		Employee g = Employee.findFirst("first_name = ?", "John");
		g.set("last_name", "turdly").saveIt();

	}
}
