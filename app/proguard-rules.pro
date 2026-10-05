# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize

# Note for US developers: Please be aware that this (and the other holiday
# names) are the name of the Unicode character 'LATIN CAPITAL LETTER I WITH
# DOT ABOVE' (U+0130).

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
