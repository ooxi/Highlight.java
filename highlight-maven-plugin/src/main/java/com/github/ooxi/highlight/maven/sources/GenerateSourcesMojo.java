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
package com.github.ooxi.highlight.maven.sources;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Generates an interface, implementation and enumeration of all `highlight.js'
 * languages as well as stylesheets.
 * 
 * @author ooxi
 */
@Mojo(name = "generate-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public final class GenerateSourcesMojo extends AbstractMojo {
	
	@Parameter(property = "project", readonly = true, required = true)
	private MavenProject project;
	
	@Parameter(alias = "package", required = true)
	private String pkg;
	
	@Parameter(alias = "generated-sources-directory", defaultValue="${project.build.directory}/generated-sources/highlight", required = true)
	private File sourceDirectory;

	
	
	@Override
	public void execute() throws MojoExecutionException {
		
		try {
			/* Generate target directory if not existing
			 */
			if (!sourceDirectory.exists()) {
				if (!sourceDirectory.mkdirs()) {
					throw new MojoExecutionException("Cannot create source directory `"+ sourceDirectory.getAbsolutePath() +"'");
				}
			}
			
			
			/* Generate Java sources
			 */
			JCodeModel model = new JCodeModel();
			
			new LanguagesSourceGenerator(model).generate(pkg);
			new StylesheetsSourceGenerator(model).generate(pkg);
			
			/* @deprecated Charset dependend!
			 */
			model.build(sourceDirectory);
			
			
			/* Add directory with generated sources to compile
			 * source root
			 */
			project.addCompileSourceRoot(sourceDirectory.getAbsolutePath());

		
		} catch (IOException | JClassAlreadyExistsException | RuntimeException e) {
			throw new MojoExecutionException("Failed generating sources", e);
		}
	}
}
