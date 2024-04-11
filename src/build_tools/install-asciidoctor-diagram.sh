#!/usr/bin/env bash
set -eu

mkdir -p .generated/build/gems
gem install concurrent-ruby -i .generated/build/gems
gem install asciidoctor-diagram --version=2.3.0 -i .generated/build/gems