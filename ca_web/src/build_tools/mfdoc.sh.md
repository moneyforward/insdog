# `mfdoc.sh`

This is a tool to generate GitHub wiki site and techdocs site from markdown files in a repository.
This tool is designed to be run from a project's root directory, which has `.git` directory under it.

## Syntax

`mfdoc.sh [option]... [subcommand]... [-- [mapping]...]`

Followings are examples:

Re-generate the wiki site and deploy it to the project's wiki site.

* `mfdoc.sh clean compile deploy`.

Re-generate the wiki site only from the markdown files under the `manuals` directory.
The generated files will be placed under `doc/man` directory of the wiki site.

* `mfdoc.sh compile -- "*.md:manuals:man"`


## Options

* `--local-wiki-dir=`: "local wiki directory". 
A directory to store wiki site's contents.
(default: `.work/wiki`)
* `--local-doc-dir=`: "local doc directory". 
A directory to store documentation files in this repository temporarily.
(default: `.work/doc`)
* `--generated-doc-base-in-local-wiki-dir=`: "generated doc base".
A relative path from --local-wiki-dir= to a directory which stores
documentation files in this repository.
(default: `doc`)

## Environment Variables


### `GITHUB_ACTIONS`

If this environment variable is `true`, this tools considers that this tools run under **GitHub Actions**. 

### `GH_PAT`

This environment variable is used in CI environment.

The GitHub Personal Access token that allows the CI user to access the repo and the packages.
`read:packages` and `repo` scopes are needed.
Following is an example to define the environment variable in `publish-docs.yml`.

```yaml
      - name: Compile Documentation Sets
        env:
          GH_PAT: ${{ secrets.GH_PAT }}
        run: |
          git config --global user.email "ci@moneyforward.com"
          git config --global user.name "MoneyForward CI"
          ./build.sh publish-wiki publish-techdocs
```

## "Mappings"

A mapping defines how to collect documents in a repository to its wiki site.

```
  "*.md:tools:packages/tools"
   ^    ^     ^
   |    |     |
   |    |     +----- relative path from --local-doc-dir to a directory to copy target files to.
   |    +----------- relative path from the project's root to the directory that stores target files.
   +---------------- target file patterns.
```

If you have a file `tools/doc/mfdoc.sh.md` in the repo, it will be copied to `{local doc directory}/packages/tools/doc/mddoc-sh.md`.

**Examples:**

- `*:documents:`: All files under `{project root}/documents` will be copied under `{local doc directory}`
- `*.md:moneyforward:packages/MoneyForward`: Files whose name end with `.md` will be copied under `{local doc directory}/packages/MoneyForward`.

Note that copied files will be converted into GitHub style markdown and then copied under `{local wiki directory}/{generated doc base}` 

## Subcommands

`mfdoc.sh` 

* `clean`: Cleans directories specified by `--local-wiki-dir=` and `--local-doc-dir=` options.
* `compile`: Generates the contents of the wiki site.
For more details, check "Site Generation Pipeline" section.
* `deploy`: Commits the generated wiki-site and push it to the github-wiki's repo.   

## Design Details

### Compilation Pipeline

1. Clone .wiki repo to the local wiki directory.
2. Remove
    * the entire local doc directory.
    * the entire generated doc base under the local wiki directory.
3. Copy files in the repository to the specified location under the local doc directory based on the mappings.
4. Generate `index.md` files under the local doc directory.
5. Convert `.md` files into GitHub wiki format (mangle the links in the files and change the file names) and copy them under the 

Note that all the files under generated doc base directory are removed and then re-generated.
Also, files not under the directory are not modified at all.
With this approach, it is ensured that files created/edited through the wiki's interface are not modified by this mechanism. 

### Markdown Conversion

Markdown files copied under the local doc directory are converted into GitHub style and will be copied under the local wiki directory.
This conversion has two aspects.

1. **Filename Conversion:** Files under the local doc directory are copied to the local wiki directory, but they are renamed by replacing `/` in relative paths from the local doc directory with `|`.
Meaning, if you have a file `{local doc directory}/a/b/c.md`, it will be copied to `{local wiki directory}/a|b|c.md`. 
2. **Link Mangling:** Inside GitHub wiki, pages are linked only with "page names", which do not have paths (`a/b/c/`) and extensions (`.md`).
If the tool finds a link in a markdown file and the link target doesn't contain `:` sign, it considers it is a in-site link and applies the same rule as the filename conversion discussed above.
Also, the extension `.md` in the link will be removed .

