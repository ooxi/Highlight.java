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

import static com.google.common.base.Preconditions.checkState;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 *
 * @author ooxi
 */
public class Highlight {
	
	private final ScriptObjectMirror hljs;
	
	private Highlight(ScriptObjectMirror hljs) {
		this.hljs = hljs;
	}
	
	
	
	public static Highlight newInstance() throws ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		Environment environment = new Environment(engine);
		
		Object hljs = environment.require("index.js");
		checkState(hljs instanceof ScriptObjectMirror, "`highlight.js' initialization failed");
		
		return new Highlight((ScriptObjectMirror)hljs);
	}
	
	
	
	
	
	public String highlight(String source) {
		Object result = hljs.callMember("highlightAuto", source);
		return getValue("highlightAuto", result);
	}
	
	
	
	public String highlight(String source, Language language) {
		Object result = hljs.callMember("highlight", language.toString(), source);
		return getValue("highlight", result);
	}
	
	
	
	
	
	private static String getValue(String method, Object obj) {
		checkState(obj instanceof ScriptObjectMirror, "`"+ method +"' should return an object but returned `"+ obj +"'");
		ScriptObjectMirror result = (ScriptObjectMirror)obj;
		
		Object valueObj = result.getMember("value");
		checkState(valueObj instanceof String, "`value' member of `highlightAuto' result should be a string but is `"+ valueObj +"'");
		
		return (String)valueObj;
	}
}
