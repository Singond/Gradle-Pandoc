package com.github.singond.gradle.pandoc;

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PandocPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Pandoc task = project.getTasks().create("pandoc", Pandoc.class);
		task.setGroup("Build");
		task.setDescription("Assembles textual documents.");
		task.sources("docs");
		File buildDir = (File) project.property("buildDir");
		task.outputDir(buildDir.toPath().resolve("docs"));
	}

}
