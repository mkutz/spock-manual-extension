Spock Manual Extension
======================

[![Build Status](https://travis-ci.org/mkutz/spock-manual-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-manual-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-manual-extension.svg)](https://coveralls.io/r/mkutz/spock-manual-extension)

Extension to define manual test case specification using the [Spock framwork](http://spockframework.org/).

When executing a Specification maked ``@Manual`` the test will be set "igored" and the block comments will be written to a test plan file (currently HTML and/or CSV).
