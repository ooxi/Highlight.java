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
package com.github.ooxi.highlight.maven.resources;

import com.github.ooxi.highlight.maven.ResourceMojo;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Stream;

/**
 * Writes the content of `HighlightResource' instance into a directory relative
 * to a given path.
 * 
 * @author ooxi
 */
abstract class ResourceGenerator implements ResourceMojo.Generator {
	
	/**
	 * @return Base path of resource which should be ignored, e.g. a base
	 *     path of `node_modules/highlight.js/lib' will cause the resource
	 *     `node_modules/highlight.js/lib/lang/1c.js' to be written into the
	 *     file `lang/1c.js'
	 */
	protected abstract String getBasePath();
	
	
	
	/**
	 * @return Resources to be written
	 * @throws IOException iff resources cannot be loaded
	 */
	protected abstract Stream<HighlightResource> getResources();
	
	
	
	
	
	/**
	 * Write all resources relative to `directory'.
	 * 
	 * @param directory Directory where resources should be written into
	 */
	@Override
	public void generate(File directory) {
		getResources().forEach(resource -> {
			final String path = resource.getPath();
			
			File file = new File(directory, path);
			File parent = file.getParentFile();
			
			if (!parent.exists()) {
				if (!parent.mkdirs()) {
					throw new ResourceGeneratorException("Cannot create parent directory `"+ parent.getAbsolutePath() +"' of file `"+ file.getAbsolutePath() +"' for path `"+ path +"'");
				}
			}
			
			try (	InputStream is = resource.openStream();
				OutputStream os = new FileOutputStream(file);
			) {
				ByteStreams.copy(is, os);
				
			} catch (IOException e) {
				throw new ResourceGeneratorException("Failed writing `"+ path +"' to `"+ file.getAbsolutePath() +"'");
			}
		});
	}
}
