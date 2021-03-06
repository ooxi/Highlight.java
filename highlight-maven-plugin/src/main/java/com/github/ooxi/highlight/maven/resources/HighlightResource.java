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

import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;

/**
 * @author ooix
 */
@AutoValue
public abstract class HighlightResource {
	
	
	public static HighlightResource of(ContentSupplier contentSupplier, String path) {
		return new AutoValue_HighlightResource(contentSupplier, path);
	}
	
	
	protected abstract ContentSupplier getContentSupplier();
	public abstract String getPath();
	
	
	public final InputStream openStream() throws IOException {
		return getContentSupplier().openStream();
	}
	
	public final String getBasename() {
		return FilenameUtils.getBaseName(getPath());
	}
	
	public final JavaIdentifier toJavaIdentifier() {
		return JavaIdentifier.of(getBasename());
	}
	
	
	
	
	
	public static interface ContentSupplier {
		
		public InputStream openStream() throws IOException;
	}
	
	
	
	HighlightResource() {
	}
}
