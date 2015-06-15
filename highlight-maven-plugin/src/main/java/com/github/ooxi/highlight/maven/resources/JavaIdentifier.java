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

/**
 * Used to convert language and stylesheet names to java identifiers.
 * 
 * @author ooxi
 */
@AutoValue
public abstract class JavaIdentifier {
	
	
	public static JavaIdentifier of(String basename) {
		return new AutoValue_JavaIdentifier(basename);
	}
	
	
	
	
	
	/**
	 * @return Base name from which all identifiers are derived for example
	 *     `visual-basic'
	 */
	protected abstract String getBasename();
	
	
	
	/**
	 * @return Enumeration constant e.g. `VISUAL_BASIC'
	 */
	public final String toEnumerationConstant() {
		
		/* @see HighlightResourcesTest
		 */
		final String identifier = getBasename()
			.toUpperCase()
			.replace('-', '_')
			.replace('.', '_');
		
		if ("1C".equals(identifier)) {
			return "ONE_C";
		}
		
		return identifier;
	}
	
	
	
	/**
	 * @return Class name e.g. `VisualBasic'
	 */
	public final String toClassName() {
		final String constant = toEnumerationConstant().toLowerCase();
		final StringBuilder cn = new StringBuilder(constant.length());
		
		/* Empty constant not supported
		 */
		if (constant.isEmpty()) {
			throw new UnsupportedOperationException("Cannot convert empty constant of base name `"+ getBasename() +"' to class name");
		}
		
		/* First character of class name always uppercase
		 */
		cn.append(Character.toUpperCase(constant.charAt(0)));
		boolean nextUpper = false;
		
		for (int i = 1; i < constant.length(); ++i) {
			final char c = constant.charAt(i);
			
			if ('_' == c) {
				nextUpper = true;
			} else if (nextUpper) {
				cn.append(Character.toUpperCase(c));
				nextUpper = false;
			} else {
				cn.append(Character.toLowerCase(c));
			}
		}
		
		return cn.toString();
	}
	
	
	
	
	
	JavaIdentifier() {
	}
}
