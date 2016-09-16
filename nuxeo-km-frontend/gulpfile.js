/*
@license
Copyright (c) 2015 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/

'use strict';

// Include promise polyfill for node 0.10 compatibility
require('es6-promise').polyfill();

// Include Gulp & tools we'll use
var gulp = require('gulp');
var $ = require('gulp-load-plugins')();
var del = require('del');
var runSequence = require('run-sequence');
var browserSync = require('browser-sync');
var reload = browserSync.reload;
var merge = require('merge-stream');
var path = require('path');
var debug = require('gulp-debug');
var fs = require('fs');
var glob = require('glob');
var historyApiFallback = require('connect-history-api-fallback');
var packageJson = require('./package.json');
var crypto = require('crypto');
var cleanCSS = require('gulp-clean-css');

var AUTOPREFIXER_BROWSERS = [
  'ie >= 10',
  'ie_mob >= 10',
  'ff >= 30',
  'chrome >= 34',
  'safari >= 7',
  'opera >= 23',
  'ios >= 7',
  'android >= 4.4',
  'bb >= 10'
];

var APP = 'src/main/app';
var DIST = 'target/classes/nuxeo.war/km';

var pathIfPresent = function(root, subpath) {
  return !subpath ? root : path.join(root, subpath);
};

var appl = function(subpath) {
  return pathIfPresent(APP, subpath);
};

var dist = function(subpath) {
  return pathIfPresent(DIST, subpath);
};

var styleTask = function(stylesPath, srcs) {
  return gulp.src(srcs.map(function(src) {
      return path.join(APP, stylesPath, src);
    }))
    .pipe(debug())
    .pipe($.changed(stylesPath, {extension: '.css'}))
    .pipe($.autoprefixer(AUTOPREFIXER_BROWSERS))
    .pipe(gulp.dest('.tmp/' + stylesPath))
    .pipe(cleanCSS())
    .pipe(gulp.dest(dist(stylesPath)))
    .pipe($.size({title: stylesPath}));
};

var imageOptimizeTask = function(src, dest) {
  return gulp.src(src)
    .pipe(debug())
    .pipe($.imagemin({
      progressive: true,
      interlaced: true
    }))
    .pipe(gulp.dest(dest))
    .pipe($.size({title: 'images'}));
};

// Compile and automatically prefix stylesheets
gulp.task('styles', function() {
  return styleTask('styles', ['**/*.css']);
});

// Optimize images
gulp.task('images', function() {
  return imageOptimizeTask(appl('images/**/*'), dist('images'));
});

// Copy all files at the root level (app)
gulp.task('copy', function() {
  var app = gulp.src([
    appl('*'),
    '!' + appl('test'),
    '!' + appl('elements'),
    '!' + appl('cache-config.json'),
    '!**/.DS_Store'
  ], {
    dot: true
  }).pipe(debug()).pipe(gulp.dest(dist()));

  // Copy over only the bower_components we need
  // These are things which cannot be vulcanized
  var bower = gulp.src([
    'bower_components/**/*'
  ]).pipe(gulp.dest(dist('bower_components')));

  var elements = gulp.src([appl('elements/**/*.html'),
    appl('elements/**/*.css'),
    appl('elements/**/*.js'),
    '!' + appl('elements/elements.html'),
  ])
  .pipe(gulp.dest(dist('elements')));

  var vulcanized = gulp.src([appl('elements/elements.html')])
    .pipe($.rename('elements.vulcanized.html'))
    .pipe(gulp.dest(dist('elements')));

  return merge(app, bower, elements, vulcanized)
    .pipe($.size({
      title: 'copy'
    }));
});

// Copy web fonts to dist
gulp.task('fonts', function() {
  return gulp.src([appl('/fonts/**')])
    .pipe(debug())
    .pipe(gulp.dest(dist('fonts')))
    .pipe($.size({
      title: 'fonts'
    }));
});

// Scan your HTML for assets & optimize them
gulp.task('build', ['images', 'fonts'], function() {
  return gulp.src([appl('**/*.html'), '!' + appl('/{elements,test}/**/*.html')])
    .pipe(debug())
    .pipe($.if('*.html', $.replace('elements/elements.html', 'elements/elements.vulcanized.html')))
    .pipe($.useref({
      searchPath: [appl(), 'bower_components']
    }))
    .pipe($.if('*.js', $.uglify({
      preserveComments: 'license'
    })))
    .pipe($.if('*.css', cleanCSS()))
    .pipe($.if('*.html', $.minifyHtml({
      quotes: true,
      empty: true,
      spare: true
    })))
    .pipe(gulp.dest(dist()));
});

// Vulcanize granular configuration
gulp.task('vulcanize', function() {
  return gulp.src(dist('elements/elements.vulcanized.html'))
    .pipe($.vulcanize({
      stripComments: true,
      inlineCss: true,
      inlineScripts: true
    }))
    .pipe(gulp.dest(dist('elements')))
    .pipe($.size({title: 'vulcanize'}));
});

// Delete all unnecessary bower dependencies
gulp.task('dist:bower', function (cb) {
  del([
    DIST + '/bower_components/**/*',
    '!' + DIST + '/bower_components/jquery',
    '!' + DIST + '/bower_components/jquery/dist',
    '!' + DIST + '/bower_components/jquery/dist/jquery.min.js',
    '!' + DIST + '/bower_components/moment',
    '!' + DIST + '/bower_components/moment/min',
    '!' + DIST + '/bower_components/moment/min/moment-with-locales.min.js',
    '!' + DIST + '/bower_components/nuxeo-labs-rating',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui/src',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui/src/main',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui/src/main/app',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui/src/main/app/images',
    '!' + DIST + '/bower_components/nuxeo-labs-rating/nuxeo-labs-rating-ui/src/main/app/images/rating.png',
    '!' + DIST + '/bower_components/select2',
    '!' + DIST + '/bower_components/select2/select2.min.js',
    '!' + DIST + '/bower_components/webcomponentsjs',
    '!' + DIST + '/bower_components/webcomponentsjs/webcomponents-lite.min.js'
  ], cb);
});

// Generate config data for the <sw-precache-cache> element.
// This include a list of files that should be precached, as well as a (hopefully unique) cache
// id that ensure that multiple PSK projects don't share the same Cache Storage.
// This task does not run by default, but if you are interested in using service worker caching
// in your project, please enable it within the 'default' task.
// See https://github.com/PolymerElements/polymer-starter-kit#enable-service-worker-support
// for more context.
gulp.task('cache-config', function(callback) {
  var dir = dist();
  var config = {
    cacheId: packageJson.name || path.basename(__dirname),
    disabled: false
  };

  glob([
    'index.html',
    './',
    'bower_components/webcomponentsjs/webcomponents-lite.min.js',
    '{elements,scripts,styles}/**/*.*'],
    {cwd: dir}, function(error, files) {
    if (error) {
      callback(error);
    } else {
      config.precache = files;

      var md5 = crypto.createHash('md5');
      md5.update(JSON.stringify(config.precache));
      config.precacheFingerprint = md5.digest('hex');

      var configPath = path.join(dir, 'cache-config.json');
      fs.writeFile(configPath, JSON.stringify(config), callback);
    }
  });
});

// Clean output directory
gulp.task('clean', function() {
  return del(['.tmp', dist()]);
});

// Watch files for changes & reload
gulp.task('serve', ['styles'], function() {
  // setup our local proxy
  var proxyOptions = require('url').parse('http://localhost:8080/nuxeo');
  proxyOptions.route = '/nuxeo';

  browserSync({
    port: 5000,
    notify: false,
    logPrefix: 'PSK',
    snippetOptions: {
      rule: {
        match: '<span id="browser-sync-binding"></span>',
        fn: function(snippet) {
          return snippet;
        }
      }
    },
    // Run as an https by uncommenting 'https: true'
    // Note: this uses an unsigned certificate which on first access
    //       will present a certificate warning in the browser.
    // https: true,
    server: {
      baseDir: ['.tmp', APP],
      middleware: [historyApiFallback(), require('proxy-middleware')(proxyOptions)],
      routes: {
        '/bower_components': 'bower_components'
      }
    }
  });

  gulp.watch([appl('**/*.html')], reload);
  gulp.watch([appl('styles/**/*.css')], ['styles', reload]);
  gulp.watch([appl('scripts/**/*.js')], reload);
  gulp.watch([appl('images/**/*')], reload);
});

// Build and serve the output from the dist build
gulp.task('serve:dist', ['default'], function() {
  browserSync({
    port: 5001,
    notify: false,
    logPrefix: 'PSK',
    snippetOptions: {
      rule: {
        match: '<span id="browser-sync-binding"></span>',
        fn: function(snippet) {
          return snippet;
        }
      }
    },
    // Run as an https by uncommenting 'https: true'
    // Note: this uses an unsigned certificate which on first access
    //       will present a certificate warning in the browser.
    // https: true,
    server: dist(),
    middleware: [historyApiFallback()]
  });
});

// Build production files, the default task
gulp.task('default', ['clean'], function(cb) {
  // Uncomment 'cache-config' if you are going to use service workers.
  runSequence(
    ['copy', 'styles'],
    'build',
    'vulcanize', 'dist:bower', // 'cache-config',
    cb);
});

// Load tasks for web-component-tester
// Adds tasks for `gulp test:local` and `gulp test:remote`
require('web-component-tester').gulp.init(gulp);
