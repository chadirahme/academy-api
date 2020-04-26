package com.academy.data.services;


import com.academy.data.controller.FileService;

interface Parent2 extends Parent1 {
   default void fun1()
    {
        System.out.println("Parent2");
    }
}
