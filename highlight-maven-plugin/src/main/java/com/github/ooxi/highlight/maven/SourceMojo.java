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
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * All source generating mojos are basically the same and therefore only have
 * to specify the {@link Generator} implementation 😊
 * 
 * @author ooxi
 */
public abstract class SourceMojo extends AbstractMojo {
	
	@Parameter(property = "project", readonly = true, required = true)
	protected MavenProject project;
	
	@Parameter(alias = "package", required = true)
	private String pkg;
	
	
	
	private final ImmutableList<GeneratorProvider> providers;
	
	protected SourceMojo(Collection<GeneratorProvider> providers) {
		this.providers = ImmutableList.copyOf(providers);
	}
	
	
	
	
	
	protected final void createSource(File sourceDirectory) throws IOException, JClassAlreadyExistsException, MojoExecutionException {
		
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

		for (GeneratorProvider provider : providers) {
			provider.newGenerator(model, pkg).generate();
		}

		model.build(new FileCodeWriter(sourceDirectory, "UTF-8"));
	}
	
	
	
	
	
	public static interface GeneratorProvider {
		
		public Generator newGenerator(JCodeModel model, String pkg);
	}
	
	
	
	public static interface Generator {
		
		public void generate() throws IOException, JClassAlreadyExistsException;
	}
}
