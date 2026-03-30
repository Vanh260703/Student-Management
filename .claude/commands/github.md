Push all current changes to GitHub with the provided commit message.

Steps:
1. Check if git is initialized. If not, run `git init` then `git remote add origin https://github.com/Vanh260703/Student-Management.git`
2. If git is already initialized, check if the remote `origin` exists. If not, add it: `git remote add origin https://github.com/Vanh260703/Student-Management.git`
3. Run `git add .` to stage all changes
4. Run `git commit -m "$ARGUMENTS"` using the provided message as the commit message
5. Run `git push -u origin main` (or `master` if `main` doesn't exist)
6. Report success or any errors clearly

The commit message is: $ARGUMENTS
