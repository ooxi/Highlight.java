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

});
