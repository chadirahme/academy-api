package com.academy.data.services;

import com.academy.data.config.GenerateFileName;
import com.academy.data.controller.FileService;

public class TestParent
        extends GenerateFileName
        implements Parent2   {

    public void fun()
    {
        // use super keyword to call the show
        // method of PI1 interface
        Parent2.super.fun();
        // use super keyword to call the show
        // method of PI2 interface
        Parent2.super.fun();
    }

    @Override
    public void testme() {

    }


    @Override
    public void sound() {
        Parent2.super.fun1();
    }

    @Override
    public void fun1() {

    }
}
