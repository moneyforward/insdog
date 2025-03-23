# We use Makefile for simplifying mvn command's usage.
# Since InspektorDog (shortened "insdog") is a Java-based component, its binary build and release should be done through mvn, without relying on Makefile.

BASH:=$(shell which bash)
MVN:=source .dependencies/sdkman/bin/sdkman-init.sh && \
     sdk use java "${SDK_JDK_NAME}" &&                 \
     mvn -B -Dmaven.javadoc.skip=true
MVN_WITH_JAVADOC:=source .dependencies/sdkman/bin/sdkman-init.sh && \
	              sdk use java "${SDK_JAVADOC_JDK_NAME}" &&         \
                  mvn -B -Dmaven.javadoc.skip=false
PROJ_DIR:=$(shell pwd)

## This is a Makefile for "InspektorDog" project.
## - https://github.com/moneyforward/insdog/wiki/9-ContributionGuidelines%7CMakefile
ABOUT: help
	@echo "__ENV_RC__='${__ENV_RC__}'"
	:

## Cleans all intermediate files, which should be generated only under `target` directory.
clean:
	@$(MVN) clean

## Compiles production source code only
compile:
	@$(MVN) clean compile

## Executes unit tests
test:
	@$(MVN) clean compile test

## Does "mvn package" without generating JavaDoc.
package:
	@$(MVN) clean compile package

## Does "mvn deploy" without generating JavaDoc.
deploy:
	@$(MVN) clean compile deploy

## Does "mvn release:prepare" and "mvn release:perform" without generating JavaDoc.
release:
	@$(MVN) release:prepare
	@$(MVN) release:perform

## Generate a site of this product under `target/site` directory.
site: clean _site-generate
	:

## Generate a site of this product under `target/site` directory and publish it to `gh-pages` branch.
site-deploy: clean _site-clone _site-generate
	@git -C target/site/en add --all
	@git -C target/site/en commit -a -m "Update site: $$(date)"
	@git -C target/site/en push origin gh-pages

# Generate Javadoc under `target/site/apidocs` dir.
# Deprecated. Use `site` instead.
javadoc: site
	:

## Build this repository locally.
## This is a synonym for `package`.
build: package
	:

## Show help.
help:
	make2help $(MAKEFILE_LIST)


# private targets
_site-generate:
	@rm -fr target/site/en/*
	@$(MVN_WITH_JAVADOC) compile site
	@./src/build_tools/render-md-into-html.sh src/site/markdown target/site src/site/resources/html
	@./src/build_tools/mangle-javadoc-html-files.sh target/site/en

_site-clone:
	@mkdir -p target/site
	@git -C target/site clone --branch gh-pages --single-branch --depth 1 https://github.com/moneyforward/insdog.git en
	@git -C target/site/en checkout gh-pages

