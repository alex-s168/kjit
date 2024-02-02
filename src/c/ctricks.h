#include <stddef.h>

struct allocated {
    void *ptr;
    size_t size;
};

typedef void (*fptr)();

static fptr as_funptr(void *ptr) {
    return (fptr) ptr;
}

#if defined(WIN32) || defined(__WIN32) || defined(__WIN32__)
#define WIN32_LEAN_AND_MEAN
#include <windows.h>

/**
 * Allocate a readable and writable page
 */
static struct allocated alloc_page() {
    SYSTEM_INFO system_info;
    GetSystemInfo(&system_info);
    size_t page_size = system_info.dwPageSize;
    void *p = (void *) VirtualAlloc(NULL, page_size, MEM_COMMIT, PAGE_READWRITE);
    return (struct allocated) { p, page_size };
}

/**
 *  Make a section in the page executable (and readonly)
 */
static void make_exec(void *ptr, size_t size) {
    DWORD dummy;
    VirtualProtect(ptr, size, PAGE_EXECUTE_READ, &dummy);
}

static void free_page(struct allocated page) {
    VirtualFree(page.ptr, 0, MEM_RELEASE);
}

#else
#include <sys/mman.h>
#include <unistd.h>

/**
 * Allocate a readable and writable page
 */
static struct allocated alloc_page() {
    size_t page_size = (size_t) getpagesize();
    void *p = (void *) mmap(0, size,
                            PROT_READ | PROT_WRITE | PROT_EXEC,
                            MAP_PRIVATE | MAP_ANONYMOUS,
                            -1, 0);
    return (struct allocated) { p, page_size };
}

/**
 *  Make a section in the page executable (and readonly)
 */
static void make_exec(void *ptr, size_t size) {

}

static void free_page(struct allocated page) {
    munmap(page.ptr, page.size)
}

#endif