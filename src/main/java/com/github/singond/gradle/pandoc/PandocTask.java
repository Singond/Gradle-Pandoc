package com.github.singond.gradle.pandoc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.process.ExecSpec;

import groovy.lang.Closure;

public class PandocTask extends DefaultTask implements PatternFilterable {

	private static final Logger logger = Logging.getLogger("Pandoc");

	private FileCollection sources;
	private final PatternFilterable filter;
	private File outputDir;
	private final Set<Format> formats;
	private boolean separateDirs = true;
	private String pandocPath = null;

	public PandocTask() {
		formats = new LinkedHashSet<Format>();
		filter = new PatternSet();
	}

	@InputFiles
	@SkipWhenEmpty
	public FileTree getSources() {
		return sources.getAsFileTree().matching(filter);
	}

	/**
	 * Sets the source files to be processed by this task.
	 *
	 * @param sources the files to be processed
	 */
	public void sources(Object... sources) {
		this.sources = getProject().files(sources);
	}

	public void sources(Object sources, Closure<?> config) {
		this.sources = getProject().files(sources, config);
	}

	@Override
	public PatternFilterable exclude
			(@SuppressWarnings("rawtypes") Closure excludeSpec) {
		filter.exclude(excludeSpec);
		return this;
	}

	@Override
	public PatternFilterable exclude(Iterable<String> excludes) {
		filter.exclude(excludes);
		return this;
	}

	@Override
	public PatternFilterable exclude(Spec<FileTreeElement> excludeSpec) {
		filter.exclude(excludeSpec);
		return this;
	}

	@Override
	public PatternFilterable exclude(String... excludes) {
		filter.exclude(excludes);
		return this;
	}

	@Override
	public Set<String> getExcludes() {
		return filter.getExcludes();
	}

	@Override
	public Set<String> getIncludes() {
		return filter.getExcludes();
	}

	@Override
	public PatternFilterable include
			(@SuppressWarnings("rawtypes") Closure includeSpec) {
		filter.include(includeSpec);
		return this;
	}

	@Override
	public PatternFilterable include(Iterable<String> includes) {
		filter.include(includes);
		return this;
	}

	@Override
	public PatternFilterable include(Spec<FileTreeElement> includeSpec) {
		filter.include(includeSpec);
		return this;
	}

	@Override
	public PatternFilterable include(String... includes) {
		filter.include(includes);
		return this;
	}

	@Override
	public PatternFilterable setExcludes(Iterable<String> excludes) {
		filter.setExcludes(excludes);
		return this;
	}

	@Override
	public PatternFilterable setIncludes(Iterable<String> includes) {
		filter.setIncludes(includes);
		return this;
	}

	@OutputDirectory
	public File getOutputDir() {
		return outputDir;
	}

	/**
	 * Sets the directory for output files.
	 *
	 * @param outputDir the output directory
	 */
	public void outputDir(Object outputDir) {
		this.outputDir = getProject().file(outputDir);
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

	public void separateOutput(boolean separate) {
		separateDirs = separate;
	}

	public void pandocPath(String path) {
		pandocPath = path;
	}

	public void fileTraversalDemo() {
		logger.quiet("Sources");
		for (File s : sources) {
			logger.quiet("- Contains: " + s);
		}
		logger.quiet("Sources as files");
		for (File s : sources.getFiles()) {
			logger.quiet("- Contains: " + s);
		}
		logger.quiet("Sources as tree");
		for (File s : sources.getAsFileTree()) {
			logger.quiet("- Contains: " + s);
		}
		logger.quiet("Individual sources as trees (filtered)");
		for (File s : sources) {
			logger.quiet("- Source: " + s);
			for (File f : getProject().fileTree(s).matching(filter)) {
				logger.quiet("  - Contains: " + f);
			}
		}
	}

	/**
	 * Example of a system call.
	 */
	public void systemCallDemo() {
		logger.quiet("Creating newfile.txt");
		getProject().exec(new Action<ExecSpec>() {
			@Override
			public void execute(ExecSpec e) {
				e.executable("touch");
				e.args("newfile.txt");
			}
		});
	}

	@TaskAction
	public void run() throws IOException {
		if (formats.isEmpty()) {
			logger.error("No format specified for task '{}'", getName());
		}
		convert(sources, filter, outputDir, formats, separateDirs);
	}

	private void convert (FileCollection sources, PatternFilterable filter,
			File outputDir, Set<Format> formats, boolean separate)
			throws IOException {
		PandocExec pandoc = new PandocExec(pandocPath);
		Path tgtBase = outputDir.toPath();
		// Consider each source element separately
		for (File s : sources) {
			// The source element is considered the base directory
			Path srcBase = s.toPath();
			logger.quiet("Base directory: " + srcBase);
			for (File f : getProject().fileTree(srcBase).matching(filter)) {
				Path src = f.toPath();
				pandoc.setSource(src);
				src = srcBase.relativize(src);
				for (Format fmt : formats) {
					Path tgt;
					if (separate) {
						String dirName;
						if (Objects.equals(fmt.format, fmt.extension))
							dirName = fmt.format;
						else
							dirName = fmt.format + "-" + fmt.extension;
						tgt = tgtBase.resolve(dirName).resolve(src);
					} else {
						tgt = tgtBase.resolve(src);
					}
					Path parent = tgt.getParent();
					if (Files.notExists(parent)) {
						logger.debug("Creating directory {}", parent);
						Files.createDirectories(parent);
					}
					tgt = PathUtil.changeExtension(tgt, fmt.extension);
					pandoc.setTarget(tgt);
					pandoc.setFormat(fmt);
					logger.quiet("Creating {}", tgt);
					getProject().exec(pandoc);
				}
			}
		}
	}

	/**
	 * A format of an output file, along with filename extension.
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

	private static class PandocExec implements Action<ExecSpec> {

		/** Path to pandoc. If null, it is expected to be on PATH. */
		private final String path;
		/** Absolute path to the source file */
		private Path source;
		/** Absolute path to the target file */
		private Path target;
		/** The format to be used */
		private Format format;

		public PandocExec(String path) {
			// If the path to Pandoc is not given, we must assume it is on PATH
			this.path = path != null ? path : "pandoc";
			logger.debug("The path to Pandoc is {}", this.path);
		}

		public void setSource(Path source) {
			this.source = source;
		}

		public void setTarget(Path target) {
			this.target = target;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		@Override
		public void execute(ExecSpec e) {
			e.executable(path);
			e.args("--standalone");
			e.args(source.toString());
			e.args("--to=" + format.format);
			e.args("--output=" + target.toString());
		}
	}
}
