cmake_minimum_required(VERSION 3.4.1)

add_library(permisense
            SHARED
            src/main/cpp/permisense.cpp )

include_directories(src/main/cpp/includes/)

find_library(LOG_LIBS log)
find_package(Threads)

target_link_libraries(permisense ${LOG_LIBS} ${CMAKE_THREAD_LIBS_INIT})