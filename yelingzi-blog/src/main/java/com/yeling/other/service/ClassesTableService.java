package com.yeling.other.service;

import com.yeling.other.domian.entity.ClassesTable;

import java.util.List;

public interface ClassesTableService {

    /**
     * 查询课程表
     * @return
     */

    List<ClassesTable> list(String semester);

    void addClassTable(ClassesTable classesTable);

    List<ClassesTable> getClassTable();

    Boolean editClassTable(ClassesTable classesTable);

}
