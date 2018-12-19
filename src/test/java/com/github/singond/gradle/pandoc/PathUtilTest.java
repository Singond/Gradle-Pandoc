package com.github.singond.gradle.pandoc;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class PathUtilTest {

	@Test
	public void stripExtension1() {
		Path orig = Paths.get("/home/user/directory/file.ext");
		Path txt = PathUtil.stripExtension(orig);
		assertEquals(Paths.get("/home/user/directory/file"), txt);
	}

	@Test
	public void stripExtensionDot() {
		Path orig = Paths.get("/home/user/directory/.cmdrc");
		Path txt = PathUtil.stripExtension(orig);
		assertEquals(Paths.get("/home/user/directory/.cmdrc"), txt);
	}

	@Test
	public void changeExtension1() {
		Path orig = Paths.get("/home/user/directory/file.ext");
		Path txt = PathUtil.changeExtension(orig, "txt");
		assertEquals(Paths.get("/home/user/directory/file.txt"), txt);
	}
}
