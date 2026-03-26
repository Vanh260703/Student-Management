---
name: github
description: Commit and push project changes to the current GitHub remote from a short user note. Use when the user invokes /github or asks to publish code, create a commit from current changes, push the current branch, or turn a brief text note into a git commit message and GitHub push.
---

# GitHub

Use this skill to turn a short user note into a safe Git workflow for the current project.

## Workflow

1. Confirm the current directory is a git repository.
2. Inspect `git status --short`, current branch, and remotes before staging anything.
3. If there is no remote or no GitHub remote, stop and tell the user what is missing.
4. Derive a concise commit message from the user's note.
5. Stage the relevant changed files for the current task. Do not stage unrelated generated files unless they are required.
6. Commit the staged changes.
7. Push the current branch to the appropriate remote.

## Operating Rules

- Treat the user's extra text after `/github` as the primary intent for the commit message.
- If the note is vague, still write a concrete commit message based on both the note and the actual diff.
- Refuse to commit secrets, credential files, or obviously unrelated changes.
- If the worktree contains unrelated user edits, mention that and avoid staging them blindly.
- If there are no changes to commit, say so instead of creating an empty commit unless the user explicitly asks for one.
- Before pushing, verify which branch is checked out and which remote will receive the push.
- If a push fails because authentication or remote access is missing, report the exact blocker and stop.

## Command Pattern

Prefer this sequence:

```bash
git status --short
git branch --show-current
git remote -v
git add <relevant-files>
git commit -m "<generated-message>"
git push <remote> <branch>
```

Use `git add .` only when the full worktree clearly belongs to the requested task.

## Response Pattern

When the workflow succeeds, report:

- the commit message used
- the branch pushed
- the remote used

When it cannot proceed, state the blocking condition directly, such as:

- not a git repository
- no GitHub remote configured
- no changes to commit
- push rejected or authentication failed
