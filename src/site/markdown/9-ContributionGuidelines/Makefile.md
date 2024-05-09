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

