package hjärna.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Parser {

	public enum Token {
		NAME, ASSIGN, SECTION_OPEN, SECTION_CLOSE, LIST_OPEN, LIST_CLOSE, INDENT,
	}

	public static List<Pair<Token, String>> parseLine(String line) {
		List<Pair<Token, String>> tokens = new ArrayList<>(3);
		Pair<Token, String> nextToken;
		String buffer = new String();
		byte[] chars = line.getBytes();
		int i = 0, x = 0;

		// strip whitespace and tabs from front
		int whitespace = 0;
		outer: for (; x < chars.length; x++) {
			switch (chars[x]) {
			case '\t':
				tokens.add(new Pair<>(Token.INDENT));
				break;
			case ' ':
				whitespace++;
				if (whitespace == 4) {
					whitespace = 0;
					tokens.add(new Pair<>(Token.INDENT));
				}
				break;
			default:
				break outer;
			}
		}

		if (chars.length < 3) {
			return tokens;
		}

		chars = Arrays.copyOfRange(chars, x, chars.length);

		if (chars[0] == '[') {
			if (chars[1] == '[') {
				nextToken = new Pair<>(Token.LIST_OPEN);
				i = 2;
			} else {
				nextToken = new Pair<>(Token.SECTION_OPEN);
				i = 1;
			}
			tokens.add(nextToken);
		}

		for (; i < chars.length; i++) {
			switch (chars[i]) {
			default:
				buffer += (char) chars[i];
				continue;

			case '=':
				tokens.add(new Pair<>(Token.NAME, unpack(buffer)));
				buffer = new String();

				tokens.add(new Pair<>(Token.ASSIGN));
				break;

			case ']':
				tokens.add(new Pair<>(Token.NAME, unpack(buffer)));
				buffer = new String();

				if (i + 1 < chars.length && chars[i + 1] == ']') {
					tokens.add(new Pair<>(Token.LIST_CLOSE));
				} else {
					tokens.add(new Pair<>(Token.SECTION_CLOSE));
				}
				break;
			}
		}

		if (!buffer.isEmpty()) {
			tokens.add(new Pair<>(Token.NAME, unpack(buffer)));
		}

		return tokens;
	}
	
	private static String unpack(String buffer) {
		return buffer.trim().replaceAll("\"", "");
	}
	
	public static Map<String, Object> loadString(String content) throws FileNotFoundException, IOException {
		Stack<Map<String, Object>> stack = new Stack<>();
		Map<String, Object> config = new HashMap<>();
		Map<String, Object> section;
		List<Pair<Token, String>> tokens;
		Pair<Token, String> first = null, second = null, third = null;
		int currentIndent = 0;

		if (content == null) {
			return config;
		}

		stack.push(config);

		for (String line : content.split("\n")) {
			if (line.isEmpty()) {
				continue;
			}

			tokens = parseLine(line);

			// take indents if there are any
			Iterator<Pair<Token, String>> it = tokens.iterator();
			// the indentation level of the current line
			int indented = 0;
			outer: while (it.hasNext()) {
				Pair<Token, String> token = it.next();
				switch (token.key) {
				case INDENT:
					indented++;
					break;
				default:
					// take the current token as first of
					// further processing
					first = token;
					// indentation is done, continue
					break outer;
				}
			}

			// TODO: null checks
			second = it.next();
			third = it.next();

			// adjust stack depth
			if (indented < currentIndent) {
				// remove focused sections to fit indentation level
				for (int c = currentIndent - indented; 0 < c; c--)
					stack.pop();
			}

			// TODO: replace asserts with better checks
			switch (first.key) {
			case NAME:
				assert (second.key == Token.ASSIGN);
				assert (third.key == Token.NAME);
				stack.peek().put(first.value, third.value);
				break;

			case LIST_OPEN:
				assert (second.key == Token.NAME);
				assert (third.key == Token.LIST_CLOSE);

				// if the current section doesn't have the list, initialize it
				if (!stack.peek().containsKey(second.value)) {
					List<Object> list = new ArrayList<>();
					stack.peek().put(second.value, list);
				}

				// add a dummy section and set it to focused
				section = new HashMap<>();
				((List<Object>) stack.peek().get(second.value)).add(section);
				stack.push(section);
				break;

			case SECTION_OPEN:
				assert (second.key == Token.NAME);
				assert (third.key == Token.SECTION_CLOSE);

				section = new HashMap<>();
				stack.peek().put(second.value, section);
				stack.push(section);
				break;

			default:
				System.out.println("Parser error: unexpected token");
			}

			currentIndent = indented;
		}

		return config;
	}

	public static Map<String, Object> loadFile(Path file) throws FileNotFoundException, IOException {
		String content = new String(), line;
		try (Reader reader = new FileReader(file.toFile())) {
			try (BufferedReader buffer = new BufferedReader(reader)) {
				while ((line = buffer.readLine()) != null) {
					content += line + "\n";
				}
			}
		}
		return loadString(content);
	}

}
