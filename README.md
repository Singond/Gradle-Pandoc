Pandoc plugin for Gradle
========================
This plugin enables using the Pandoc converter from a build script.
[Pandoc](https://pandoc.org/index.html) is a universal tool which can convert
documents to and from a wide array of formats, including (but not limited to)
Markdown, HTML, Texinfo, man, LaTeX, PDF and even Microsoft Word docx.

The plugin does _not_ come bundled with Pandoc. Instead, it uses system calls
to invoke whichever version of Pandoc is installed on your system. Please see
the Pandoc website in order to download the tool.

Requirements
------------
- [Pandoc](https://pandoc.org/index.html) and its dependencies must be
  installed on your system.

Usage
=====
To enable the plugin, add the following to the top of your `build.gradle`:

```groovy
buildscript {
	repositories {
		jcenter()
	}
	dependencies {
		// Prefer a fixed version instead of '0.+'
		classpath 'com.github.singond:gradle-pandoc:0.+'
	}
}
apply plugin: 'com.github.singond.pandoc'
```

A new task type `Pandoc` is now available; its fully-qualified name is
`com.github.singond.gradle.pandoc.Pandoc`.
Also, a single instance of this task has been added to your project under
the name `pandoc`. You need to configure it first, for example:

```groovy
pandoc {
	sources "docs"
	outputDir "$buildDir/docs"
	format "html", "html"
	format "latex", "pdf"
}
```

This will convert every file in the `docs` directory into a corresponding
`html` and `pdf` file in the `${buildDir}/docs` directory, assuming thah
`pandoc` is installed on your system and available in `PATH`.


Locating Pandoc Installation
--------------
If the `pandoc` tool is not in your `PATH`, the above build will fail.
In order to correct this, specify the full path to your installation
of `pandoc` by adding:

```groovy
pandoc {
	...
	pandocPath "/full/path/to/pandoc"
}
```

Controlling Output
------------------

### Output Format
If you don't specify any conversion format, this plugin will not do anything.
To add a conversion format, specify its name, and (optionally) a corresponding
file name extension in the `format` method.
The format name (here, `latex`) must be a format name recognized by Pandoc.
Please refere to the `pandoc` manual for the list of available formats.

```groovy
pandoc {
	...
	format "latex", "pdf"
}
```

This tells Gradle to convert every source file into a `pdf` file by using
the `latex` conversion format. It roughly translates into:

```sh
pandoc filename.md --to=latex --output=filename.pdf
```

If the format name and file extension is the same, you can omit the second
argument:

```
pandoc {
	...
	format "html"
}
```

You can also specify more than one format:

```
pandoc {
	...
	format "latex", "pdf"
	format "html"
}
```

This will convert every source file into a `pdf` file (through LaTeX)
and a `html` file.

### Output Directory
The converted files are placed into the `${outputDir}` directory.
By default, they are divided into subdirectories according to their type.
For example, consider we have the following file structure in the project root:

```
docs/
	document-1.md
	document-2.md
	document-3.md
```

This is our `build.gradle`:

```
pandoc {
	sources "docs"
	outputDir "$buildDir/docs"
	format "html"
	format "latex", "pdf"
}
```

Running `gradle pandoc` will result in the following files being created
(assuming your build directory is set to `build`, ie. the default):

```
build/
	docs/
		html/
			document-1.html
			document-2.html
			document-3.html
		latex-pdf/
			document-1.pdf
			document-2.pdf
			document-3.pdf
```

If you prefer not to split the output files into subdirectories by type,
you can set the `separateOutput` option to false:

```
pandoc {
	sources "docs"
	outputDir "$buildDir/docs"
	format "html"
	format "latex", "pdf"
	separateOutput false
}
```
Assuming the same source files structure, this will yield:

```
build/
	docs/
		document-1.html
		document-1.pdf
		document-2.html
		document-2.pdf
		document-3.html
		document-3.pdf
```
