# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             zbarjni

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
                src/main/cpp/zbarjni.c
                src/main/cpp/libiconv-1.14/lib/iconv.c
                src/main/cpp/libiconv-1.14/libcharset/lib/localcharset.c
                src/main/cpp/libiconv-1.14/lib/relocatable.c
                src/main/cpp/zbar/img_scanner.c 
                src/main/cpp/zbar/decoder.c 
                src/main/cpp/zbar/image.c 
                src/main/cpp/zbar/symbol.c 
                src/main/cpp/zbar/convert.c 
                src/main/cpp/zbar/config.c 
                src/main/cpp/zbar/scanner.c 
                src/main/cpp/zbar/error.c 
                src/main/cpp/zbar/refcnt.c 
                src/main/cpp/zbar/video.c 
                src/main/cpp/zbar/video/null.c 
                src/main/cpp/zbar/decoder/code128.c 
                src/main/cpp/zbar/decoder/code39.c 
                src/main/cpp/zbar/decoder/code93.c 
                src/main/cpp/zbar/decoder/codabar.c 
                src/main/cpp/zbar/decoder/databar.c 
                src/main/cpp/zbar/decoder/ean.c 
                src/main/cpp/zbar/decoder/i25.c 
                src/main/cpp/zbar/decoder/qr_finder.c 
                src/main/cpp/zbar/qrcode/bch15_5.c 
                src/main/cpp/zbar/qrcode/binarize.c 
                src/main/cpp/zbar/qrcode/isaac.c 
                src/main/cpp/zbar/qrcode/qrdec.c 
                src/main/cpp/zbar/qrcode/qrdectxt.c 
                src/main/cpp/zbar/qrcode/rs.c 
                src/main/cpp/zbar/qrcode/util.c
             )

ADD_DEFINITIONS(
                -Wno-multichar
                -D_ANDROID -DLIBDIR="c"
                -DBUILDING_LIBICONV
                -DBUILDING_LIBCHARSET
                -DIN_LIBRARY
                -DHAVE_WORKING_O_NOFOLLOW="1")

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       zbarjni
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                      )

include_directories(
                src/main/cpp/include
                src/main/cpp/zbar
                src/main/cpp/libiconv-1.14/include
                src/main/cpp/libiconv-1.14/libcharset
                src/main/cpp/libiconv-1.14/libcharset/include
            )