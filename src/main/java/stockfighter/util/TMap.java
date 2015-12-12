package stockfighter.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TMap extends LinkedHashMap<String, Object> {

	public TMap() {
	}

	public TMap(Map<? extends String, ?> m) {
		super(m);
	}

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

	public List<TMap> getList(String key) {
		List<Map> list = (List<Map>) get(key);
		if (list == null) {
			return Collections.emptyList();
		} else {
			Stream<TMap> stream = list.stream().map(TMap::new);
			return stream.collect(Collectors.toList());
		}
	}
}
