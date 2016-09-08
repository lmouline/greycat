/**
 * Created by lucasm on 13/07/16.
 */

// Include gulp
var gulp = require('gulp');
// Include plugins
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var util = require('gulp-util');
var mainBowerFiles = require('gulp-main-bower-files');
var filter = require('gulp-filter');
var ignore = require('gulp-ignore');

var babelify = require('babelify');
var browserify = require('browserify');
var buffer = require('vinyl-buffer');
var source = require('vinyl-source-stream');

var del = require('del');
var webserver = require('gulp-webserver');




//Use babel on the react_layout.js to transform it to normal JS
gulp.task('babelify', function () {
    var bundler = browserify('src/js/react_layout.js');
    bundler.transform(babelify);

    bundler.bundle()
        .on('error', function (err) { console.error(err); })
        .pipe(source('react_layout.babelify.js'))
        .pipe(buffer())
        .pipe(gulp.dest('src/js'));
});

//Concatenate JS except react_layout.js because it needs type="text/babel"
gulp.task('scripts', ['babelify'], function() {
    return gulp.src('src/js/*.js')
        .pipe(ignore('react_layout.js'))
        .pipe(concat('main.js'))
        .pipe(gulp.dest('build/js'));
});

//Concatenate mwg JS
gulp.task('mwg', function() {
    //if you want to get the last version of mwg : use your own path
     /*return gulp.src('../mwDB/plugins/alljs/target/classes/!*.min.js')
     .pipe(concat('all-mwg.js'))
     .pipe(rename({suffix: '.min'}))
     .pipe(uglify().on('error', util.log))
     .pipe(gulp.dest('build/js'));*/


    //using the version in lib/
    return gulp.src('lib/all-mwg.min.js')
        .pipe(gulp.dest('build/js'));
});


//Copy the fonts from font-awesome to build/
gulp.task('copy-fonts', function() {
    return gulp.src('bower_components/font-awesome/fonts/**.*')
        .pipe(gulp.dest('build/fonts'));
});


//Transforms .scss and .sass into build/style.min.css
var sass = require('gulp-ruby-sass');
gulp.task('sass', function() {
    return sass('src/scss/style.scss', {style: 'compressed'})
        .pipe(rename({suffix: '.min'}))
        .pipe(gulp.dest('build/css'));
});

//Concatenate all js from bower_components
gulp.task('bower-scripts', function() {
    var filterJS = filter('**/*.js', { restore: true });

    return gulp.src('bower.json')
        .pipe(mainBowerFiles({
            overrides: {
                bulma: {
                    main: []
                },
                "font-awesome": {
                    main: []
                }
            }
        }))
        .pipe(filterJS)
        .pipe(concat('vendor.js'))
        .pipe(filterJS.restore)
        .pipe(gulp.dest('build/js'));
});

//Launch a webserver
gulp.task('webserver', function() {
    gulp.src('.')
        .pipe(webserver({
            livereload: true,
            directoryListing: true,
            open: true,
            port: 8080
        }));
});

//Watchers
gulp.task('watch', function() {
    // Watch .js files
    gulp.watch('src/js/*.js', ['scripts']);
    // Watch .scss files
    gulp.watch('src/scss/*.scss', ['sass']);
    // Watch mwg changes
    gulp.watch('../mwDB/plugins/alljs/target/classes/*.min.js', ['mwg']);
    // Watch bower changes
    gulp.watch('bower.json', ['bower-scripts']);
});


// Default Task
gulp.task('default', ['scripts', 'mwg', 'copy-fonts', 'sass', 'webserver', 'watch', 'bower-scripts']);
