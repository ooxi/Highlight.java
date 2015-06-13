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

import com.google.common.io.ByteStreams;
import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author ooxi
 */
public class FileSubject extends Subject<FileSubject, File> {

	public FileSubject(FailureStrategy fs, File subject) {
		super(fs, subject);
	}
	
	
	
	public void doesExist() {
		isNotNull();
		
		if (!getSubject().exists()) {
			fail("does exist");
		}
	}
	
	
	public void isFile() {
		doesExist();
		
		if (!getSubject().isFile()) {
			fail("is file");
		}
	}
	
	
	public void isDirectory() {
		doesExist();
		
		if (!getSubject().isDirectory()) {
			fail("is directory");
		}
	}
	
	
	public void hasContent(String expected, Charset charset) {
		isFile();
		
		try (InputStream is = new FileInputStream(getSubject())) {
			String actual = new String(ByteStreams.toByteArray(is), charset);
			
			if (!expected.equals(actual)) {
				fail("has content `"+ expected +"' (actual content is `"+ actual +"')");
			}
			
			
		} catch (IOException e) {
			throw new AssertionError("Failed reading `"+ getSubject().getAbsolutePath() +"'", e);
		}
	}
	
	
	public void hasContent(String expected, String charset) {
		hasContent(expected, Charset.forName(charset));
	}
}
