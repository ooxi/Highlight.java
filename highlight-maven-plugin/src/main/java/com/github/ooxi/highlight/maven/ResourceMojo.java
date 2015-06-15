/**
 * Copyright (c) 2015 ooxi. All rights reserved.
 *     https://github.com/ooxi/Highlight.java
 *     violetland@mail.ru
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in a
 *     product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 * 
 *  3. This notice may not be removed or altered from any source distribution.
 */
package com.github.ooxi.highlight.maven;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.Collection;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * All resource generating mojos are basically the same and therefore only have
 * to specify the {@link Generator} implementation 😊
 * 
 * @author ooxi
 */
public abstract class ResourceMojo extends AbstractMojo {
	
	@Parameter(property = "project", readonly = true, required = true)
	protected MavenProject project;
	
	@Parameter(alias = "package", required = true)
	private String pkg;
	
	
	
	private final ImmutableList<Generator> generators;
	
	protected ResourceMojo(Collection<Generator> generators) {
		this.generators = ImmutableList.copyOf(generators);
	}
	
	

	
	
	protected final Resource createResource(File resourceDirectory) throws MojoExecutionException {
		
		/* Generate target directory if not existing
		 */
		if (!resourceDirectory.exists()) {
			if (!resourceDirectory.mkdirs()) {
				throw new MojoExecutionException("Cannot create resource directory `"+ resourceDirectory.getAbsolutePath() +"'");
			}
		}


		/* Create a directory representing the package
		 */
		File pkgDirectory = new File(resourceDirectory, pkg.replace('.', File.separatorChar));

		if (!pkgDirectory.exists()) {
			if (!pkgDirectory.mkdirs()) {
				throw new MojoExecutionException("Cannot create resource directory `"+ pkgDirectory.getAbsolutePath() +"' for package `"+ pkg +"' inside `"+ resourceDirectory.getAbsolutePath() +"'");
			}
		}


		/* Write JavaScript and Stylesheet resources into that
		 * directory
		 */
		for (Generator generator : generators) {
			generator.generate(pkgDirectory);
		}


		/* Prepare resource for adding to maven project
		 */
		Resource resource = new Resource();
		resource.setDirectory(resourceDirectory.getAbsolutePath());
		resource.setFiltering(false);
		
		return resource;
	}



	public static interface Generator {
		public void generate(File directory);
	}
}
