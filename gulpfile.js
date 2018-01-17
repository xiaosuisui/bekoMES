/**
 * Created by Ricardo on 2017/8/12.
 */
"use strict";

var gulp = require('gulp'), //将上面down下来的插件都要引入，以便需要
    gutil = require('gulp-util'),
    wiredep = require('wiredep').stream

gulp.task('html', function() {  // 创建task任务:可以在cmd命令中执行任务
    gulp.src('src/main/webapp/index.html')
        .pipe(wiredep({  // 调用插件wiredep执行方法
            optional: 'configuration',
            goes: 'here'
        }))
        .pipe(gulp.dest('src/main/webapp'))
})