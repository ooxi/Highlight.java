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

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.stream.Stream;

/**
 * @author ooxi
 */
final class JavaScriptResourceGenerator extends ResourceGenerator {

	@Override
	protected String getBasePath() {
		return "node_modules/highlight.js/lib/";
	}

	@Override
	protected Stream<HighlightResource> getResources() {
		return HighlightResources.getJavaScriptResources().stream()
			.map(this::fixTestRe);
	}
	
	
	
	/**
	 * The JDK 8 Nashorn JavaScript implementation fails when testing
	 * certain regular expressions. Therefore the `highlight.js' Script has
	 * to be patched.
	 * 
	 * ---( Source )---
	 * ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
	 * engine.eval("/(((1{0,1}[0-9]){0,1}).){3,3}/m.exec(\"1e100\")");
	 * ---
	 * 
	 * ---( Exception )---
	 * java.lang.StringIndexOutOfBoundsException: String index out of range: -1
	 *     at java.lang.String.substring(String.java:1955)
	 *     at jdk.nashorn.internal.runtime.regexp.JoniRegExp$JoniMatcher.group(JoniRegExp.java:159)
	 *     at jdk.nashorn.internal.objects.NativeRegExp.groups(NativeRegExp.java:609)
	 *     at jdk.nashorn.internal.objects.NativeRegExp.execInner(NativeRegExp.java:569)
	 *     at jdk.nashorn.internal.objects.NativeRegExp.exec(NativeRegExp.java:625)
	 *     at jdk.nashorn.internal.objects.NativeRegExp.exec(NativeRegExp.java:309)
	 *     at jdk.nashorn.internal.scripts.Script$\^eval\_.:program(<eval>:1)
	 *     at jdk.nashorn.internal.runtime.ScriptFunctionData.invoke(ScriptFunctionData.java:636)
	 *     at jdk.nashorn.internal.runtime.ScriptFunction.invoke(ScriptFunction.java:229)
	 *     at jdk.nashorn.internal.runtime.ScriptRuntime.apply(ScriptRuntime.java:387)
	 *     at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:437)
	 *     at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:401)
	 *     at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:397)
	 *     at jdk.nashorn.api.scripting.NashornScriptEngine.eval(NashornScriptEngine.java:152)
	 *     at javax.script.AbstractScriptEngine.eval(AbstractScriptEngine.java:264)
	 *     at ….test(MyTest.java:56)
	 * ---
	 * 
	 * To avoid this problem the `testRe' function is patched to ignore Java
	 * exceptions and return null instead
	 * 
	 * ---( Original source )---
	 * function testRe(re, lexeme) {
	 *     var match = re && re.exec(lexeme);
	 *     return match && match.index == 0;
	 * }
	 * ---
	 * 
	 * ---( Patched source )---
	 * function testRe(re, lexeme) {
	 *     try {
	 *         return _original_testRe(re, lexeme);
	 *     } catch (e) {
	 *         print(e);
	 *         return null;
	 *     }
	 * }
	 * 
	 * function _original_testRe(re, lexeme) {
	 *     var match = re && re.exec(lexeme);
	 *     return match && match.index == 0;
	 * }
	 * ---
	 * 
	 * @param resource Original resource
	 * @return Patched resource
	 */
	private HighlightResource fixTestRe(HighlightResource resource) {
		if (!resource.getPath().equals("highlight.js")) {
			return resource;
		}
		
		
		/* Copy original resource into buffer
		 */
		final Charset CHARSET = Charset.forName("UTF-8");
		final String original;
		
		try (InputStream is = resource.openStream()) {
			original = new String(ByteStreams.toByteArray(is), CHARSET);
			
		} catch (IOException e) {
			throw new ResourceGeneratorException("Failed patching `highlight.js' resource", e);
		}
		

		/* Patch original source
		 */
		String patched = original.replace("  function testRe(re, lexeme) {", new StringBuilder()
			.append("  function testRe(re, lexeme) {\n")
			.append("    try {\n")
			.append("      return originalTestRe(re, lexeme);")
			.append("    } catch (e) {\n")
			.append("       print(\"Will swallow exception\", e);\n")
			.append("       return null;\n")
			.append("    }\n")
			.append("  }\n")
			.append("\n")
			.append("  function originalTestRe(re, lexeme) {")
		.toString());
		
		
		/* Return patched resource
		 */
		return HighlightResource.of(() -> {
			return new ByteArrayInputStream(patched.getBytes(CHARSET));
		}, resource.getPath());
	}
}
