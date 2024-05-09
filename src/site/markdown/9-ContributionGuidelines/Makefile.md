# Makefile

## Target Name Conventions

We use `Makefile` to share common procedures within the team.
But sometimes it has too many targets, and it becomes difficult to identify important ones.
To address this challenge, we have the following conventions:

```Makefile

## Describe what this target does after two pound signs.
public_target: _private_target
	does something

# Describe what this target does after single pound sign.
_private_target:
	does something else

```

`make2help` commands generates a help message only for targets with comments starting with double pounds (`##`) by default.
Also, targets you intend to be used by other targets, not directly by human, start their name with a single pound sign (`#`).

We don't change existing target names because we don't have tests over `Makefile`.
But, when we make a change in an existing target or creating a new target, and it doesn't conform to the rules, we'll make it follow them.

## Frequently Used Targets

Since the development model that `bravo` is practicing is quite different from traditional `Makefile` based projects, it is very challenging to our make target names follow widely accepted conventions, completely.
However, it is important to make it easy to conduct and understand very basic operations.

Here are such examples:

Running your test locally, do:

```
$ make stop-test-db start-test-db clean-up-test-db seed-test-db test
```

By this, you can ensure the database is clean before executing the tests.
Those targets do what their names suggest. 
You can skip any of them to save your time at your own risk.
Also, you can run the tests inside docker:

```
$ make test-on-docker
```

MySQL is a dependency of "bravo" project and it is implemented as a docker container.
Targets for operating it are marked "[docker]" in this help.





