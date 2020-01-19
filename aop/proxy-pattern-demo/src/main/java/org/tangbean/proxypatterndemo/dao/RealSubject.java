package org.tangbean.proxypatterndemo.dao;

public class RealSubject implements Subject {
    @Override
    public void request() {
        System.out.println("---- Requesting... ----");
    }

    @Override
    public void request1() {
        System.out.println("---- 01 Requesting... ----");
    }

    @Override
    public void request2() {
        System.out.println("---- 02 Requesting... ----");
    }

    @Override
    public void request3() {
        System.out.println("---- 03 Requesting... ----");
    }

    @Override
    public void request4() {
        System.out.println("---- 04 Requesting... ----");
    }

    @Override
    public void request5() {
        System.out.println("---- 05 Requesting... ----");
    }

    @Override
    public void request6() {
        System.out.println("---- 06 Requesting... ----");
    }

    @Override
    public void anotherNameMethod() {
        System.out.println("---- Another Name Method... ----");
    }
}
