package org.tangbean.proxypatterndemo.dao;

public class SubjectProxy implements Subject {
    private Subject subject;

    public SubjectProxy(Subject subject) {
        this.subject = subject;
    }

    @Override
    public void request() {
        preRequest();
        subject.request();
        postRequest();
    }

    @Override
    public void request1() {
        preRequest();
        subject.request1();
        postRequest();
    }

    @Override
    public void request2() {
        preRequest();
        subject.request2();
        postRequest();
    }

    @Override
    public void request3() {
        preRequest();
        subject.request3();
        postRequest();
    }

    @Override
    public void request4() {
        preRequest();
        subject.request4();
        postRequest();
    }

    @Override
    public void request5() {
        preRequest();
        subject.request5();
        postRequest();
    }

    @Override
    public void request6() {
        preRequest();
        subject.request6();
        postRequest();
    }

    @Override
    public void anotherNameMethod() {
        preAnotherNameMethod();
        System.out.println("---- Another Name Method... ----");
        postAnotherNameMethod();
    }

    private void preRequest() {
        System.out.println("---- Before request ----");
    }

    private void postRequest() {
        System.out.println("---- After request ----");
    }

    private void preAnotherNameMethod() {
        System.out.println("---- Before another name method ----");
    }

    private void postAnotherNameMethod() {
        System.out.println("---- After another name method ----");
    }
}
