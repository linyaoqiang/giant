package com.giant.study;

import com.giant.annotation.Autowired;
import com.giant.annotation.Service;

@Service
public class Hello {
    @Autowired
    private Student student;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
