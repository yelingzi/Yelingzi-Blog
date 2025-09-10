package com.yeling.other.service.Impl;

import com.yeling.other.mapper.ClassesTableMapper;
import com.yeling.other.domian.entity.ClassesTable;
import com.yeling.other.service.ClassesTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassesTableServiceImpl implements ClassesTableService {

    @Autowired
    private ClassesTableMapper classTableMapper;

    @Override
    public List<ClassesTable> list(String semester){return classTableMapper.list(semester);}

    @Override
    public void addClassTable(ClassesTable classesTable){
        classTableMapper.addClassTable(classesTable);
    }

    @Override
    public List<ClassesTable> getClassTable(){
        return classTableMapper.getClassTable();
    }

    @Override
    public Boolean editClassTable(ClassesTable classesTable){
        Long l = classTableMapper.getClassTableById(classesTable.getId());
        if(l == 0){
            return false;
        }else {
            classTableMapper.updateClassTable(classesTable);
            return true;
        }
    }

}
