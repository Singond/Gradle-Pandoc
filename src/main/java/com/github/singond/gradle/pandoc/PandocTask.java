package com.github.singond.gradle.pandoc;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandocTask extends DefaultTask {

	private FileCollection sources;
	private File outputDir;

	private static final Logger logger = LoggerFactory.getLogger("Pandoc");

	@InputFiles
	@SkipWhenEmpty
	public FileCollection getSources() {
		return sources;
	}

	public void sources(FileCollection sources) {
		this.sources = sources;
	}

	@OutputDirectory
	public File getOutputDir() {
		return outputDir;
	}

	public void outputDir(File ouputDir) {
		this.outputDir = ouputDir;
	}

	// Testing only
	public void printContents() {
		System.out.println("Sources: " + sources);
		for (File s : sources) {
			System.out.println("Contains: " + s);
		}
		System.out.println("Sources as files: " + sources.getFiles());
		for (File s : sources.getFiles()) {
			System.out.println("Contains: " + s);
		}
		System.out.println("Sources as tree: " + sources.getAsFileTree());
		for (File s : sources.getAsFileTree()) {
			System.out.println("Contains: " + s);
		}
	}

	@TaskAction
	public void executeTask() {
		for (File s : sources.getAsFileTree()) {
			logger.info("Processing: " + s);
		}
		logger.info("Creating newfile.txt");
		getProject().exec(new Action<ExecSpec>() {
			@Override
			public void execute(ExecSpec e) {
				e.executable("touch");
				e.args("newfile.txt");
			}
		});
	}
}
