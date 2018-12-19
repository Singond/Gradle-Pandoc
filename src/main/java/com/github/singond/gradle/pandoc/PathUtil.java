package com.github.singond.gradle.pandoc;

import java.nio.file.Path;

final class PathUtil {

	private PathUtil() {
		throw new AssertionError("Non-instantiable class");
	}

	public static Path stripExtension(Path path) {
		String s = path.getFileName().toString();
		if (s.lastIndexOf(".") == 0) {
			return path;
		}
		s = s.replaceAll("\\.[^\\.]+$", "");
		return path.resolveSibling(s);
	}

	public static Path changeExtension(Path path, String ext) {
		return path.resolveSibling(stripExtension(
				path.getFileName()).toString() + "." + ext);
	}
}
