package com.example.JsonMapper;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyValue {
	private String name;
	private int age;
	public static ObjectMapper mapper = new ObjectMapper();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public JsonNode transfer(JsonNode source, JsonNode destenation, Map<String, String> mapping) {
		for (Map.Entry<String, String> entry : mapping.entrySet()) {
			List<String> pathDestenation = getPath(entry.getKey());
			entry.setValue(getSourceNodeValue(entry.getValue(), source));
			destenation = update(pathDestenation, destenation, entry.getValue());

		}

		return null;
	}

	private JsonNode update(List<String> pathDestenation, JsonNode destenation, String value) {
		JsonNode root = null;
		try {

			root = mapper.readTree(destenation.toString());
			JsonNode d = root;
			for (String s : pathDestenation) {
				if (!s.equals(pathDestenation.get(pathDestenation.size() - 1))) {
					d = d.path(s);
					if(d instanceof ArrayNode){
						change(d, pathDestenation.get(pathDestenation.size() - 1), value);
					}

				} else {
					change(d, s, value);
				}
				System.out.println(d);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;

	}

	public void change(JsonNode parent, String fieldName, String newValue) {
		if (parent.has(fieldName)) {
			((ObjectNode) parent).put(fieldName, newValue);
		}

		// Now, recursively invoke this method on all properties
		for (JsonNode child : parent) {
			change(child, fieldName, newValue);
		}
	}

	private String getSourceNodeValue(String value, JsonNode source) {
		try {
			Map<String, String> sourceValue = mapper.readValue(source.toString(), Map.class);
			return sourceValue.get(value);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<String> getPath(String value) {
		// TODO Auto-generated method stub
		return Arrays.asList(value.split("\\."));
	}

	// NOTE: if using getters/setters, can keep fields `protected` or `private`
	public static void main(String[] args) throws IOException {

		JsonNode destenation = mapper.readTree(TypeReference.class.getResourceAsStream("/MySimpleRequest.json"));
		JsonNode source = mapper.readTree(TypeReference.class.getResourceAsStream("/MySimpleRequest2.json"));
		Map<String, String> mapping = new HashMap<>();
		//mapping.put("Leg.Instrument.Attributes.MaturityDate.Value", "maturityDate");
		//mapping.put("Leg.Instrument.Underlyings.Symbol", "ticker");
		mapping.put("Leg.Instrument.Underlyings.AltIds.RIC.Value", "ticker");
		MyValue m = new MyValue();
		m.transfer(source, destenation, mapping);
		// or:

	}
}