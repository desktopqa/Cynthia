package com.sogou.qadev.service.cynthia.util;

import java.util.Arrays;

public class StringArrayHash {
	private String[] array = null;
	private int hashCode = 0;

	public StringArrayHash(String[] array) {
		this.array = array;
		if (array != null)
			for (String s : array)
				if (s != null)
					hashCode ^= s.hashCode();
	}

	public String get(int i) {
		return array[i];
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:hashCode</p>
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof StringArrayHash))
			return false;
		StringArrayHash sah = (StringArrayHash) o;
		if (hashCode != sah.hashCode)
			return false;
		if (array.length != sah.array.length)
			return false;
		if (array == sah.array)
			return true;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null && sah.array[i] != null)
				return false;
			if (array[i] != null && !array[i].equals(sah.array[i]))
				return false;
		}
		return true;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toString</p>
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Arrays.asList(array).toString();
	}
}