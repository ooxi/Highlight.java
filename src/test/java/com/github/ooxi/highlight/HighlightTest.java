/**
 * Copyright (c) 2015 ooxi
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
package com.github.ooxi.highlight;

import com.google.common.io.ByteStreams;
import static com.google.common.truth.Truth.assert_;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import javax.script.ScriptException;
import org.junit.Test;

/**
 *
 * @author ooxi
 */
public class HighlightTest {
	
	
	@Test
	public void testHighlightAsciidoc() throws ScriptException, IOException {
		Highlight highlight = Highlight.newInstance();
		
		final String test = load("asciidoc.test");
		final String expected = load("asciidoc.expect");
		final String actual = highlight.highlight(test);
		
		assert_().withFailureMessage("Failed highlighting asciidoc")
			.that(actual).isEqualTo(expected);
	}
	
	
	
	private static String load(String resource) throws IOException {
		URL url = HighlightTest.class.getResource(resource);
		
		if (null == url) {
			throw new NoSuchElementException("Cannot find resource `"+ resource +"'");
		}
		
		try (InputStream is = url.openStream()) {
			byte[] buf = ByteStreams.toByteArray(is);
			return new String(buf, "UTF-8");
		}
	};
}
