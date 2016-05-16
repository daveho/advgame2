#! /usr/bin/env bash

touch test/test-runner.cljs
lein cljsbuild once test
