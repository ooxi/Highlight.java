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
var gulp = require("gulp");

var fs = require("fs");
var recursive_readdir = require("recursive-readdir");
var string_endsWith = require("lodash/string/endsWith");





var HighlightResource = function(basepath, absolute) {
	this.absolute = absolute;
	this.relative = absolute.replace(basepath, "");
};





gulp.task("build", function () {
	
	
	/**
	 * Will recursivly walk through all files and directories of `path' and
	 * return all file names accepted by predicate.
	 * 
	 * @param {string} path Base path for recursive search
	 * @param {function(HighlightResource) → boolean} predicate Determines
	 *     which paths get included in resulting file list
	 * @param {function(err, HighlightResource[])} cb Will be invoked on
	 *     operation completion
	 */
	var recursive_readdir_predicate = function(path, predicate, cb) {
		recursive_readdir(path, function (err, files) {
			if (err) return cb(err);
			var accepted_resources = [];
			
			files.forEach(function(file) {
				var highlight_resource = new HighlightResource(path, file);
				
				if (predicate(highlight_resource)) {
					accepted_resources.push(highlight_resource);
				}
			});
			
			cb(null, accepted_resources);
		});
	};
	
	var to_json_resource_bundle = function(filename) {
		return function(err, files) {
			if (err) throw err;

			fs.writeFileSync(filename, JSON.stringify(files), {
				encoding:	"utf8"
			});
		}
	};
	
	
	
	
	
	/* List all JavaScript resources in `hljs-javascript.json' and languages
	 * in `hljs-language.json'
	 */
	var is_javascript = function(file) {
		return string_endsWith(file.relative, ".js");
	};
	
	recursive_readdir_predicate("node_modules/highlight.js/lib/", is_javascript, to_json_resource_bundle("./hljs-javascript.json"));
	recursive_readdir_predicate("node_modules/highlight.js/lib/languages/", is_javascript, to_json_resource_bundle("./hljs-language.json"));
	
	
	
	/* List all Stylesheet resources in `hljs-stylesheet.json'
	 */
	var is_stylesheet = function(file) {
		return string_endsWith(file.relative, ".css");
	};
	
	recursive_readdir_predicate("node_modules/highlight.js/styles/", is_stylesheet, to_json_resource_bundle("./hljs-stylesheet.json"));


	/* Download all language examples and highlight them using highlight.js
	 */
	require("./download-test-resources.js")("https://highlightjs.org/static/demo/", to_json_resource_bundle("./hljs-tests.json"));
});
