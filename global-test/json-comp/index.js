#!/usr/bin/node

var assert = require('assert');
var fs = require('fs');
var colors = require('colors');


try {
  //assert.equals(process.argv.length, 3);

  var testname = process.argv[2];
  var filename1 = process.argv[3];
  var filename2 = process.argv[4];

  var content1 = fs.readFileSync(filename1, 'utf-8');
  var content2 = fs.readFileSync(filename2, 'utf-8');

  var json1 = JSON.parse(content1);
  var json2 = JSON.parse(content2);

  assert.deepEqual(json1, json2);
  console.log(testname + "\t\t\t" + colors.green("OK"));
  process.exit(0);
} catch (e) {
  console.log(testname + "\t\t\t" + colors.red("KO"));
  console.log(e);
  process.exit(1);
}
