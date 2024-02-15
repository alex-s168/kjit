package me.alex_s168.kollektions

fun <T, C: Comparable<C>> SortedArrayList(sorter: (T) -> C) =
    SortedList(ArrayList(), sorter)