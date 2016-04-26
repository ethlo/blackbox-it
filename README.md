# blackbox-it
Blackbox API test suite

Simple helper for using [Rest Assured](https://github.com/jayway/rest-assured).

## Features ##
* Reloads testdata.sql between API invocations
* Sorts tests by `@Readonly` annotation so the number of database reloads is kept to a minimum
