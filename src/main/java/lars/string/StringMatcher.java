package lars.string;

public class StringMatcher {

	public static long usage = 0;
	public static long calls = 0;

	public static boolean wildcardStringMatch(String string,
			String wildcardString) {
		// long start = System.nanoTime();
		boolean result = wildcardStringMatch_ownImpl(string, wildcardString);
		// boolean result = wildcardStringMatch_usingRegex(string,
		// wildcardString);
		// usage += System.nanoTime() - start;
		// calls++;
		return result;
	}

	public static boolean wildcardStringMatch_ownImpl(String string,
			String wildcardString) {
		String[] fragments = wildcardString.split("\\*", -2);
		int pos = 0;
		for (int j = 0; j < fragments.length; j++) {
			String fragment = fragments[j];
			/* An empty fragment matches everything */
			if (fragment.length() > 0) {
				int indexOfFragment = string.indexOf(fragment, pos);
				/* at beginning matching must start of string */
				if (j == 0) {
					if (indexOfFragment != 0) {
						return false;
					}
				}
				/* at end matching must end at string end */
				if (j == fragments.length - 1) {
					if (indexOfFragment + fragment.length() != string.length()) {
						return false;
					}
				}
				/* there has to be a matching anywhere */
				if (indexOfFragment < 0) {
					return false;
				}
				// fragment found in string
				pos += indexOfFragment + fragment.length();
			}
		}
		return true;
	}

	// Easier but about 10 times slower
	public static boolean wildcardStringMatch_usingRegex(String string,
			String wildcardString) {
		String regex = wildcardString.replaceAll("\\*", ".*").replaceAll("\\?",
				".");
		return string.matches(regex);
	}
}
