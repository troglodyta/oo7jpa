/* RandomUtil.java
 *
 * Copyright (C) 2006 Pieter van Zyl
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */
package za.co.OO7J.utils;

import java.util.Random;

public class RandomUtil {
	private static Random randomGenerator = null;

	/**
	 * 
	 * This method is taken from OZONE's BenchmarkImpl class
	 * 
	 * @param lower
	 * @param upper
	 * @return 19-Apr-2006
	 */
	static int getRandomInt(int lower, int upper) {
		if (randomGenerator == null) {
			randomGenerator = new Random();
			randomGenerator.setSeed(1L);
		}

		int value;
		do {
			value = randomGenerator.nextInt();
			value %= upper;
			System.out.println("value: " + value + " lower: " + lower
					+ " upper: " + upper);
		} while (value < lower || value >= upper);
		return value;
	}

	/**
	 * 
	 * nextValue >= 1
	 * 
	 * @return 19-Apr-2006public
	 */
	public static int nextPositiveInt() {
		int nextValue = 0;

		while (nextValue < 1) {
			nextValue = getRandomGenerator().nextInt();
		}
		return nextValue;

	}

	/**
	 * 
	 * nextValue >= 0
	 * 
	 * @return 19-Apr-2006
	 */
	public static int nextInt() {
		int nextValue = 0;

		nextValue = Math.abs(getRandomGenerator().nextInt());

		return nextValue;

	}

	/**
	 * 
	 * added RandomUtil.getRandomGenerator() becuase Math.random() % value
	 * always returned 1 as the value. ALSO: using the same random number
	 * generator creates a better spread and less repetition that using a new
	 * one/obj each time
	 * 
	 * @return 19-Apr-2006
	 */
	private static Random getRandomGenerator() {
		if (randomGenerator == null) {
			randomGenerator = new Random();
			randomGenerator.setSeed(1L);
		}
		return randomGenerator;
	}

	private static void setRandomGenerator(Random randomGenerator) {
		RandomUtil.randomGenerator = randomGenerator;
	}

}
