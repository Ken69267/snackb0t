/**
 * KMath.java
 *
 * Copyright (C) 2009 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package kcp;

import java.util.ArrayList;

/**
 * Math and number functions
 * 
 * @author Kenneth Prugh
 */
public class KMath {

	/**
	 * Retrieve all the primes up to the specified number
	 */
	public static ArrayList<Integer> getPrimes(int num) {
		final ArrayList<Integer> primes = new ArrayList<Integer>();	

		// Populate the possible primes list
		primes.add(2);
		for (int i = 3; i <= num; i=i+2) {
			if (isPrime(i)) {
				primes.add(i);
			}
		}
		return primes;
	}

	/**
	 * Returns whether the given number is prime
	 */
	public static boolean isPrime(int num) {
		// 2 is prime
		if (num == 2) {
			return true;
		} else if ((num % 2) == 0) {
			// even numbers are not prime
			return false;
		} else {
			// check if its divisible
			int limit = (int) Math.sqrt(num) +1;
			for (int d = 3; d < limit; d+=2) {
				if ((num % d) == 0) {
					return false;
				}
			}
			// no factors, prime
			return true;
		}
		
	}

	/**
	 * Calculates the given numbers factorial. 
	 *
	 * @return the factorial
	 * @param sum - This should be 1
	 * @param num - The number to find the factorial for
	 */
	public static int factorial(int num, int sum) {
		if (num == 0) {
			return 1;
		} else if (num == 1) {
			return sum; 
		} else {
			return factorial(num-1, sum*num);
		}
	}

	public static void main(String[] args) {
		System.out.println(getPrimes(120));
	}
}

