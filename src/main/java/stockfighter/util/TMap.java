package stockfighter.util;

import java.util.LinkedHashMap;

public class TMap extends LinkedHashMap<String, Object> {

	public Boolean getBool(String key) {
		return (Boolean) get(key);
	}

	public String getString(String key) {
		return (String) get(key);
	}

	public Integer getInt(String key) {
		return (Integer) get(key);
	}

	public TMap getObj(String key) {
		return (TMap) get(key);
	}
}
