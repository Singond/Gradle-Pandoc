Pandoc plugin for Gradle
========================
This plugin enables using the Pandoc converter
(see [https://pandoc.org/index.html](https://pandoc.org/index.html))
from a build script.

Requirements
------------
- [Pandoc](https://pandoc.org/index.html) and its dependencies must be
  installed on your system.

Example
-------
```
apply plugin: 'com.github.singond.pandoc'

pandoc {
	sources "docs"
	outputDir "$buildDir/docs"
	format "html", "html"
	format "latex", "pdf"
}
```