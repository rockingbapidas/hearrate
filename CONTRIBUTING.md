# Contributing to Heart Rate Monitor

We love your input! We want to make contributing to this project as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features

## Our Development Process

1.  **Fork** the repo and create your branch from `master`.
2.  If you've added code that should be tested, **add tests**.
3.  Ensure the test suite passes (`./gradlew test`).
4.  Make sure your code lints (`./gradlew lint`).
5.  **Issue that pull request!**

## Coding Conventions

- We use **Kotlin** for all new code.
- Follow [Official Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html).
- **Jetpack Compose** is used for the UI layer.
- Use **Dagger Hilt** for Dependency Injection.
- Prefer **StateFlow** over LiveData for modern UI state management.

## Pull Request Guidelines

- Provide a clear and concise title.
- Describe the changes in detail in the PR description.
- Link to any related issues.
- Include screenshots if you've made UI changes.

## Reporting Bugs

We use GitHub issues to track public bugs. Report a bug by opening a new issue; it's that easy!

**Great Bug Reports** tend to have:

- A quick summary and/or background.
- Steps to reproduce (be specific!).
- What you expected would happen.
- What actually happened.
- Notes (possibly including why you think this might be happening, or stuff you tried that didn't work).
