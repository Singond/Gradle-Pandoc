package com.github.singond.gradle.pandoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PandocPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		Pandoc task = project.getTasks().create("pandoc", Pandoc.class);
		task.setGroup("Build");
		task.setDescription("Assembles textual documents.");
	}

}
