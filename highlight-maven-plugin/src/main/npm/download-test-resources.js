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
var cheerio = require("cheerio");
var highlight = require("highlight.js");
var request = require("request");





var TestResource = function(language, plain_example) {
	this.language = language;
	this.plain_example = plain_example;
	this.automatically_highlighted_example = highlight.highlightAuto(plain_example).value;
	this.manually_highlighted_example = highlight.highlight(language, plain_example).value;
};





/**
 * Will download test resources from an URL. The URL is expected to contain the
 * highlight.js live demo.
 * 
 * @param {string} url URL to download the test resources from
 * @param {function(err, TestResource[])} Will be invoked with a collection of
 *     test resources upon completion
 */
var DownloadTestResources = function(url, cb) {
	
	request(url, function(err, response, body) {
		if (err) return cb(err);
		if (200 !== response.statusCode) return cb(new Error("Invalid response status code `"+ response.statusCode +"'"));
		
		var $ = cheerio.load(body);
		var test_resources = []
		
		$("pre > code").each(function() {
			var $this = $(this);
			
			var language = $this.attr("class");
			var plain_example = $this.text();
			
			test_resources.push(new TestResource(language, plain_example));
		});
		
		cb(null, test_resources);
	});
};



/* Export public API
 */
module.exports = DownloadTestResources;
