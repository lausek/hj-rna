package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hjärna.parser.Pair;
import hjärna.parser.Parser;
import hjärna.parser.Parser.Token;

class ParserTest {
	
	List<Pair<Token, String>> tokens;
	
	@Test
	void empty() {
		List<Pair<Token, String>> root = new ArrayList<>();
		
		tokens = Parser.parseLine("");
		
		System.out.println(tokens);
		
		assertEquals(root, tokens);
	}
	
	@Test
	void assignment() {
		tokens = Parser.parseLine("test = value");
		
		System.out.println(tokens);
		
		assertEquals(new Pair<>(Token.NAME, "test"), tokens.get(0));
		assertEquals(new Pair<>(Token.ASSIGN, null), tokens.get(1));
		assertEquals(new Pair<>(Token.NAME, "value"), tokens.get(2));
	}
	
	@Test
	void testConfiguration() {
		Map<String, Object> result, expected, general, obj1, obj2;
		String file = "[general]\n"
				+ "name = 'test'\n"
				+ "key  = 'value'\n"
				+ "\t[[list]]\n"
				+ "\t\tname = 1\n"
				+ "\t\tpath = 1\n"
				+ "\t[[list]]\n"
				+ "\t\tname = 2\n"
				+ "\tmargret = 2\n";
		
		expected = new HashMap<>();
		general = new HashMap<>();
		
		List<Object> list = new ArrayList<>();
		obj1 = new HashMap<>();
		obj1.put("name", "1");
		obj1.put("path", "1");
		
		obj2 = new HashMap<>();
		obj2.put("name", "2");
	
		list.add(obj1);
		list.add(obj2);
		
		expected.put("general", general);
		general.put("name", "'test'");
		general.put("key", "'value'");
		general.put("margret", "2");
		general.put("list", list);
		
		try {
			result = Parser.loadString(file);
			
			System.out.println(result);
			
			assertEquals(expected, result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
}
