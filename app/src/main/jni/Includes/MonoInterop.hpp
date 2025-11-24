#ifndef MONOINTEROP_HPP
#define MONOINTEROP_HPP

#include <iostream>
#include <string>
#include <vector>
#include <memory>
#include <stdexcept>
#include <dlfcn.h>

// Forward Declaration of monoString
struct monoString;

// Type Definitions
typedef monoString* (*Il2cppStringNewFunc)(const char*);

// Struct: monoString
struct monoString {
    std::shared_ptr<void> klass;
    std::shared_ptr<void> monitor;
    int length;
    std::u16string chars; // UTF-16 characters

    monoString() : klass(nullptr), monitor(nullptr), length(0), chars() {}
};

// Function: CreateMonoString
inline monoString* CreateMonoString(const char* str) {
    if (str == nullptr) {
        LOGE("CreateMonoString: Input string is null.");
        return nullptr;
    }

    // Directly open the library handle
    void* handle = dlopen(("libil2cpp.so"), RTLD_LAZY);
    if (!handle) {
        LOGE(("Failed to open the library. Error: %s"), dlerror());
        return nullptr;
    }

    // Lookup the function symbol
    void* func_ptr = dlsym(handle, "il2cpp_string_new");
    if (!func_ptr) {
        const char* symerror = dlerror();
        LOGE(("Unable to locate the required symbol 'il2cpp_string_new'. Error: %s"), symerror);
        dlclose(handle); // Close the library handle
        return nullptr;
    }

    // Cast the function pointer
    auto il2cpp_string_new = reinterpret_cast<Il2cppStringNewFunc>(func_ptr);

    // Call the il2cpp_string_new function to create a monoString
    monoString* result = il2cpp_string_new(str);

    // Close the library handle after use
    dlclose(handle);

    return result;
}

#endif // MONOINTEROP_HPP
