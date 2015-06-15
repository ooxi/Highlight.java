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
package com.github.ooxi.highlight;

import com.github.ooxi.highlight.Guava.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 *
 * @author ooxi
 */
final class Environment implements EnvironmentBinding {
	
	private final ScriptEngine engine;
	
	public Environment(ScriptEngine engine) {
		this.engine = engine;
		
		final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("__binding", this);
	}
	
	
	
	
	
	@Override
	public Object require(String resource) throws ScriptException {
		final String originalSource = loadSource(resource);
		final String wrappedSource = wrapSource(originalSource);
		
		return engine.eval(wrappedSource);
	}
	
	
	
	
	private static String wrapSource(String source) {
		StringWriter sw = new StringWriter();
		
		try (PrintWriter pw = new PrintWriter(sw)) {
			pw.println(";(function() {");
			
			/* require
			 */
			pw.println("\tvar require = function(resource) {");
			pw.println("\t\treturn __binding.require(resource);");
			pw.println("\t};");
			pw.println();
			
			/* module and exports
			 */
			pw.println("\tvar module = {};");
			pw.println("\tvar exports = {};");
			pw.println();
			
			/* Execute `source'
			 */
			pw.println("\t(function(require, module, exports) {");
			pw.println(source);
			pw.println("\t})(require, module, exports);");
			pw.println();
			
			/* Return either module.exports or exports
			 */
			pw.println("\tif ('exports' in module) {");
			pw.println("\t\treturn module.exports;");
			pw.println("\t} else {");
			pw.println("\t\treturn exports;");
			pw.println("\t}");
			pw.println();
			
			pw.println("})();");
		}
		final String s = sw.toString();
		
//		/*
//		 */
//		String hotfix = s.replace("  function testRe(re, lexeme) {", new StringBuilder()
//			.append("  function testRe(re, lexeme) {\n")
//			.append("    try {\n")
//			.append("      return originalTestRe(re, lexeme);")
//			.append("    } catch (e) {\n")
//			.append("       print(e);\n")
//			.append("       return null;\n")
//			.append("    }\n")
//			.append("  }\n")
//			.append("\n")
//			.append("  function originalTestRe(re, lexeme) {")
//		.toString());
		
		return s;
	}
	
	
	
	private static String loadSource(String resource) throws ScriptException {

		try (InputStream is = findSource(resource).openStream()) {
			byte[] buf = ByteStreams.toByteArray(is);
			return new String(buf, "UTF-8");
		
		} catch (IOException cause) {
			ScriptException exception = new ScriptException("Cannot load source of `"+ resource +"'");
			exception.initCause(cause);
			throw exception;
		}
	}
	
	
	
	private static URL findSource(String resource) {
		final URL direct = Highlight.class.getResource(resource);
		
		if (null != direct) {
			return direct;
		}
		
		
		final URL withJs = Highlight.class.getResource(resource +".js");
		
		if (null != withJs) {
			return withJs;
		}
		
		throw new IllegalArgumentException("Cannot find resource `"+ resource +"'");
	}

}
