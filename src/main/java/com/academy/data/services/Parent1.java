package com.academy.data.services;

interface Parent1 {
   default void fun()
    {
        System.out.println("Parent1");
    }

    void testme();
}
