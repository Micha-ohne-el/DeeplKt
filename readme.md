# DeeplKt [![Unit Tests](https://github.com/Micha-ohne-el/DeeplKt/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/Micha-ohne-el/DeeplKt/actions/workflows/unit-tests.yaml)

Kotlin/Multiplatform client library for the popular [DeepL Translator](https://deepl.com).

## Features

DeeplKt works for both the Free and Pro plans of the DeepL API.

### Text Translation

DeeplKt fully supports text translation (except glossaries – check [Missing Features](#missing-features)). This means that you
can translate a string (or several) from any language to any language. The target language is detected automatically if left
unspecified.

### Document Translation

DeeplKt supports document translation with all options available on the DeepL API (except glossaries – check
[Missing Features](#missing-features)).

Because of the way document translation works on the DeepL API, it may potentially take a while longer than you might expect.
DeeplKt uses the supposed remaining time for a translation it receives from the DeepL API, but that value is known to be
unreliable, especially in small documents. For this reason, DeeplKt initially tries to download the finished translation as
quickly as possible (5 times in quick succession) before slowing down and applying a wait time between requests. From my
testing, this behavior is highly desirable, but please let me know if it's causing any issues for you (rate limiting, for
instance).

### Usage Checking

DeeplKt allows you to check the current usage of the API limits. You can get the usage in absolute values (used, limit,
available), or as a percentage (0 to 1).

### Automatic Request Retrying

DeeplKt uses [exponential-backoff](https://en.wikipedia.org/wiki/Exponential_backoff) to retry failed requests with an
increasing delay. By default, each request is only retried three times (with delays of 1, 2, and 4 seconds respectively before
each retry). This can be customized by providing an extra ktor configuration. This is not an ideal solution, and may change in
the future.

## Installation

{{ to be determined once published }}

## Supported Platforms

* JVM (version ≥11)
* Linux (x64)
* MacOS (x64 and arm64)
* Windows (x64)
* JavaScript (for node and browser runtimes)

32-bit platforms will not be supported, but more targets may still be added in the future (such as other Apple platforms or
WASM).

## Contributing

I would love for people to contribute to DeeplKt!

If you feel like the library is missing something or you've encountered a bug, please let me know with a
[GitHub Issue](https://github.com/Micha-ohne-el/DeeplKt/issues)! No need to be shy, there is no format to these Issues,
just type whatever you want :)

If you've already got experience with Kotlin, feel free to work on a fix or feature on your own and submitting a
[Pull Request](https://github.com/Micha-ohne-el/DeeplKt/pulls)! There are no official contribution guidelines, just try your
best and I'll see if I can fix some issues if there are any :)

### Missing Features

* Glossaries – they are not supported at all at the moment.
* Better handling of translation contexts.
* Better handling of placeholder tags (mustaches).
* Support for WASM – need to figure out how to run unit tests.
* Support for more Apple platforms (tvOS, watchOS, etc.) – need to figure out how to run unit tests.
  Might be added on a best-effort basis.
