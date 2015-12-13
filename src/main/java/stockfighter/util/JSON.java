package stockfighter.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Throwables;

public class JSON {
	
	public static String stringify(Map<String, Object> obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw Throwables.propagate(e);
		}
	}

	public static TMap parse(String json) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, TMap.class);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

}
