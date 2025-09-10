package com.yeling.other.controller;

import com.yeling.other.domian.entity.ClassesTable;
import com.yeling.entity.ApiResponse;
import com.yeling.other.service.ClassesTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ClassesTableController {

    /**
     *
     */
    @Autowired
    private ClassesTableService classesTableService;

    /**
     * 根据week查询课程表
     */

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/classes/{semester}")
    public ApiResponse classesTableList(@PathVariable String semester){

        List<ClassesTable> classTableList = classesTableService.list(semester);
        return ApiResponse.success(classTableList);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/classes/add")
    public ApiResponse addClassTable(@RequestBody ClassesTable classesTable){

        log.info("添加课程：{}，学期：{}",classesTable.getName(),classesTable.getSemester());

        classesTableService.addClassTable(classesTable);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/admin/classes/table")
    public ApiResponse getClassTableList(){

        List<ClassesTable> classTableList = classesTableService.getClassTable();
        return ApiResponse.success(classTableList);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/classes/edit")
    public ApiResponse editClassTable(@RequestBody ClassesTable classesTable){

        log.info("编辑课程：{}，学期：{}",classesTable.getName(),classesTable.getSemester());

        if(classesTableService.editClassTable(classesTable)){
            return ApiResponse.success();
        }
        return ApiResponse.error("课程不存在！");

    }


}
