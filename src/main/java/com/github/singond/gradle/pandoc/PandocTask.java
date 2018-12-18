package com.github.singond.gradle.pandoc;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

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

	private static final Logger logger = LoggerFactory.getLogger("Pandoc");

	private FileCollection sources;
	private File outputDir;
	private final Set<Format> formats;

	public PandocTask() {
		formats = new LinkedHashSet<Format>();
	}

	@InputFiles
	@SkipWhenEmpty
	public FileCollection getSources() {
		return sources;
	}

	/**
	 * Sets the source files to be processed by this task.
	 *
	 * @param sources the files to be processed
	 */
	public void sources(FileCollection sources) {
		this.sources = sources;
	}

	@OutputDirectory
	public File getOutputDir() {
		return outputDir;
	}

	/**
	 * Sets the directory for output files.
	 *
	 * @param ouputDir the output directory
	 */
	public void outputDir(File ouputDir) {
		this.outputDir = ouputDir;
	}

	/**
	 * Adds an output format to this task with the given filename extension.
	 * When executed, the task will convert every source file into all formats
	 * registered by this method. The names of the output files will have
	 * the extension specified in this method.
	 * <p>
	 * Note that the exact procedure for generating the output file is
	 * controlled by Pandoc based on both the format and the extension.
	 * Running <pre>myPandocTask.format("latex", "pdf");</pre>
	 * translates roughly into <pre>pandoc --to=latex -o filename.pdf,</pre>
	 * where {@code filename} is the name of the file being processed.
	 * <p>
	 * For example, consider the following configuration:
	 * <pre>
	 * myPandocTask.sources("docs");
	 * myPandocTask.outputDir("gen");
	 * myPandocTask.format("html", "html");
	 * myPandocTask.format("latex", "pdf");</pre>
	 * Running the {@code myPandocTask} will convert every file in {@code doc/}
	 * directory into a html file called {@code <filename>.html} and a PDF file
	 * called {@code <filename>.pdf} created by the LaTeX engine.
	 *
	 * @param format the output format understood by Pandoc.
	 *        This must be a format name supported by Pandoc.
	 * @param extension the file extension for the output file
	 */
	public void format(String format, String extension) {
		formats.add(new Format(format, extension));
	}

	/**
	 * Adds an output format to this task with the filename extension equal
	 * to the format name.
	 * For example, running
	 * <pre>format(outputformat)</pre>
	 * is equivalent to calling
	 * <pre>format(outputformat, outputformat).</pre>
	 *
	 * @param format the output format understood by Pandoc.
	 *        This must be a format name supported by Pandoc.
	 * @see #format(String, String)
	 */
	public void format(String format) {
		formats.add(new Format(format, format));
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
		if (formats.isEmpty()) {
			logger.error("No format specified for task '{}'", getName());
		}
		for (File s : sources.getAsFileTree()) {
			logger.info("Processing: " + s);
			for (Format f : formats) {
				logger.info("Will create {}.{}", s.getName(), f.extension);
			}
		}

		// Example of a system call:
		logger.info("Creating newfile.txt");
		getProject().exec(new Action<ExecSpec>() {
			@Override
			public void execute(ExecSpec e) {
				e.executable("touch");
				e.args("newfile.txt");
			}
		});
	}

	/**
	 * An format of an output file, along with filename extension.
	 */
	private static class Format {
		final String format;
		final String extension;

		public Format(String format, String extension) {
			this.format = format;
			this.extension = extension;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					 + ((extension == null) ? 0 : extension.hashCode());
			result = prime * result
					 + ((format == null) ? 0 : format.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (!(obj instanceof Format)) return false;
			Format other = (Format) obj;
			if (extension == null) {
				if (other.extension != null) return false;
			} else if (!extension.equals(other.extension)) return false;
			if (format == null) {
				if (other.format != null) return false;
			} else if (!format.equals(other.format)) return false;
			return true;
		}

	}
}
