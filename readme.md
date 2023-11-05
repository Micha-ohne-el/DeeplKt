[![Kotlin 1.9.10](https://img.shields.io/badge/Kotlin%2FMultiplatform-1.9.10-7F52FF.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/moe.micha/deeplkt?label=Latest%20Version)](https://central.sonatype.com/artifact/moe.micha/deeplkt)
[![Unit Tests status badge](https://img.shields.io/github/actions/workflow/status/Micha-ohne-el/DeeplKt/unit-tests.yaml?label=Unit%20Tests)](https://github.com/Micha-ohne-el/DeeplKt/actions/workflows/unit-tests.yaml)
[![Documentation available](https://img.shields.io/badge/Documentation-available-blue)](https://micha-ohne-el.github.io/DeeplKt)
[![License – MIT](https://img.shields.io/github/license/Micha-ohne-el/DeeplKt?color=gold&label=License)](https://github.com/Micha-ohne-el/DeeplKt/blob/main/license.md)

# DeeplKt

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

Simply add a dependency on DeeplKt to your Gradle project:

```kt
repositories {
    mavenCentral()
}

// for Kotlin/Multiplatform projects:
kotlin {
    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation("moe.micha:deeplkt:$version")
            }
        }
    }
}

// for Kotlin/JVM projects:
dependencies {
    implementation("moe.micha:deeplkt:$version")
}
```

Replace `$version` with the version you want, a list of which can be retrieved on
[Maven Central](https://central.sonatype.com/artifact/moe.micha/deeplkt/versions).

DeeplKt is not available for all possible Kotlin/Multiplatform targets (check [Supported Platforms](#supported-platforms)), so
be sure that the source set you add the dependency to only has targets which are supported.

## Basic Usage

Create an instance of `DeeplClient` with the API token you got from DeepL:

```kt
val deeplClient = DeeplClient("01234567-89AB-CDEF-0123-456789ABCDEF") // include the ":fx" suffix of free-plan tokens!
```

That's all the setup you need!

To translate text, just call `translate` and pass in your arguments:

```kt
val result = deeplClient.translate("Kotlin is amazing!", TargetLang.Dutch)

result.detectedSourceLang // SourceLang.English
result.text // "Kotlin is geweldig!"
```

If you're only interested in the text, fear not, `translateText` has you covered:

```kt
val translatedText = deeplClient.translate("Wow, this is so cool :)", TargetLang.Bulgarian) // "Уау, това е толкова готино :)"
```

You can specify more arguments, such as formality, tag-handling, or formatting-preservation, through a config function:

```kt
deeplClient.translateText("Do you like Kotlin?", TargetLang.German) {
    formality = Formality.More
} // Mögen Sie Kotlin?

deeplClient.translateText("Do you like Kotlin?", TargetLang.German) {
    formality = Formality.Less
} // Magst du Kotlin?
```

If you're having to do a lot of translations with similar options, you can also create a `TranslateOptions` object:

```kt
val options = TranslateOptions(
    tagHandling = TagHandling.Html,
    outlineDetection = OutlineDetection.Enabled,
)

deeplClient.translateText(
    "Kotlin is my favorite programming language.",
    TargetLang.NorwegianBokmal,
    options
) // Kotlin er favorittprogrammeringsspråket mitt.

deeplClient.translate(
    "It isn't perfect, but I do love it <3",
    TargetLanguage.Japanese,
    options
) // 完璧ではないけど、大好きなんだ <3
```

Please note that some overloads require an extra import!

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
