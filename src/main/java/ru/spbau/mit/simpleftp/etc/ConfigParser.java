package ru.spbau.mit.simpleftp.etc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigParser {
	public static Map<String, List<String>> parseConfig(Path path) throws IOException {
		return Files.lines(path).collect(Collectors.groupingBy(x -> x.split(":")[0],
				Collectors.mapping(x -> x.split(":")[1], Collectors.toList())));
	}
}
