// IGreetService.aidl
package com.example.liubin.myapplication;

import com.example.liubin.myapplication.Person;

interface IGreetService {
    String greet(in Person person);
    Person getPerson(in Person person);
    String getName();
}
