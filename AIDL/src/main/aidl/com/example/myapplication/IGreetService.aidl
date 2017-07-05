// IGreetService.aidl
package com.example.myapplication;

import com.example.myapplication.Person;

interface IGreetService {
    String greet(in Person person);
    Person getPerson(in Person person);
    String getName();
}
