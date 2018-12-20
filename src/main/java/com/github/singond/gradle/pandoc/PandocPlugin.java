package com.github.singond.gradle.pandoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PandocPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getTasks().create("pandoc", Pandoc.class);
	}

}
