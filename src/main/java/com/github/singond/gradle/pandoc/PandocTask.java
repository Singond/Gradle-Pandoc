package com.github.singond.gradle.pandoc;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PandocTask extends DefaultTask {

	@TaskAction
	public void executeTask() {
		System.out.println("Executing Pandoc task");
	}
}
