# We use Makefile for simplifying mvn command's usage.
# Since autotest-ca is a Java-based component, its binary build and release should be done through mvn, without relying on Makefile.

BASH:=$(shell which bash)
PROJ_DIR:=$(shell pwd)
CA_WB_MODULE_DIR:=$(PROJ_DIR)/ca_web

## This is a Makefile for "autotest-ca" project.
## - https://github.com/moneyforward/autotest-ca/wiki/9-ContributionGuidelines%7CMakefile
ABOUT: help
	@echo "__ENV_RC__='${__ENV_RC__}'"
	:

## Pull secret variables from the special orphan branch
pull-secrets:
	@git fetch origin environment --depth 1
	@git restore --source origin/environment -- .env

print-secrets:
	@cat .env | sed 's/export //g'

## Cleans all intermediate files, which should be generated only under `target` directory.
clean: clean-mfdoc
	mvn -B clean

## Compiles production source code only
compile:
	mvn -B clean compile

## Executes unit tests
test:
	mvn -B clean compile test

## Generates wiki-site on your local.
## Generated site is found under .work/doc/wiki
## Please upgrade your local bash version by using `brew install bash`.
compile-wiki:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/mfdoc.sh compile-wiki -- "*.md:src/site/markdown:"

## Publishes generated site to the repo's wiki.
## Use with caution.
publish-wiki: clean-mfdoc compile-wiki _publish-wiki
	:

# Deploys wiki-site
_publish-wiki:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/mfdoc.sh publish-wiki

## Generates techdocs on your local.
## Generated site is found under .work/doc/techdocs
## Please upgrade your local bash version by using `brew install bash` before trying this target.
compile-techdocs:
	mvn -B pre-site
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/mfdoc.sh compile-techdocs -- \
	                                                 "*.md:src/site/markdown:" \
	                                                 "*.md:target/classes/JavaMarkdown:3-APISpecification"

## Publishes generated site to the techdocs branch
## Use with caution.
publish-techdocs: clean-mfdoc compile-techdocs _publish-techdocs
	:

# Deploys wiki-site
_publish-techdocs:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/mfdoc.sh publish-techdocs \
	                                                 -- "*.md:src/site/markdown:"

## Cleans intermediate files generated by the `mfdoc.sh` tool.
clean-mfdoc:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/mfdoc.sh clean

## Generate Javadoc under `target/site/apidocs` dir.
javadoc:
	mvn -B clean compile test javadoc:javadoc

## Run all tests
## You need to run `build` target beforehand.
run-all-tests:
	java --add-opens java.base/java.lang.invoke=ALL-UNNAMED \
	       -jar ca_web/target/autotest-caweb.jar \
	       -q 'classname:~.*' \
	       run

## Run all tests in idev environment
## You need to run `build` target beforehand.
run-all-tests-on-idev-%:
	java --add-opens java.base/java.lang.invoke=ALL-UNNAMED \
	       -jar ca_web/target/autotest-caweb.jar \
	       -q 'classname:~.*' \
	       "--execution-profile=domain:ca-web-${@:run-all-tests-on-idev-%=%}.idev.test.musubu.co.in" \
	       run

## Compile test report.
## You need to run `run` target beforehand.
compile-test-report:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/tr-processor.sh ./target/testResult/ > target/testReport.xml

## Publish test report.
## You need to run `compile-test-report` target beforehand.
publish-test-report:
	$(BASH) -eu $(CA_WB_MODULE_DIR)/src/build_tools/tr-publish.sh target/testReport.xml

## Creates a autotest-caweb.jar without javadoc to save time
package-without-javadoc:
	mvn -B -Dmaven.javadoc.skip=true clean compile package


## Run all the tests.
## Internally executes `run-all-tests` target.
## You need to run `build` beforehand.
run: run-all-tests
	:

## Build.
## Internally executes `package-without-javadoc` target.
build: package-without-javadoc
	:

## Show help.
help:
	make2help $(MAKEFILE_LIST)
