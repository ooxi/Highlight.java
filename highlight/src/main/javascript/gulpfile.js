var gulp = require('gulp');

var amdclean = require('amdclean');
var fs = require('fs')
var requirejs = require('requirejs');





gulp.task('build', function () {
	
	requirejs.optimize({
		'findNestedDependencies': true,
		'baseUrl': './node_modules/highlight.js/lib',
		'optimize': 'none',
		'include': ['index'],
		'out': './example.js',
		
		'onModuleBundleComplete': function (data) {
			var outputFile = data.path;

			fs.writeFileSync(outputFile, amdclean.clean({
				'filePath': outputFile
			}));
		}
	});
});
