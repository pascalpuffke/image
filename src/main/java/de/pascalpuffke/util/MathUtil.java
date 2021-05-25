package de.pascalpuffke.util;

public class MathUtil {

	public static int average(int... nums) {
		var ans = 0;
		for(var i : nums)
			ans += i;
		return ans / nums.length;
	}

	public static int max(int... nums) {
		var ans = nums[0];
		for(var i : nums)
			if(i > ans)
				ans = i;
		return ans;
	}

	public static int maxClamp(int max, int... nums) {
		return Math.min(max(nums), max);
	}

	public static int min(int... nums) {
		var ans = nums[0];
		for(var i : nums)
			if(i < ans)
				ans = i;
		return ans;
	}

	public static int minClamp(int min, int... nums) {
		return Math.max(min(nums), min);
	}

}
