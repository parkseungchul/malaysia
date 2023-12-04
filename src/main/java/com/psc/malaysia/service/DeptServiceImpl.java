package com.psc.malaysia.service;

import com.psc.malaysia.dto.Dept;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeptServiceImpl implements DeptService {


    @Override
    public Dept getDept(Integer deptNo) {
        return null;
    }

    @Override
    public List<Dept> deptList() {
        return null;
    }

    @Override
    public void addDept(Dept dept) {

    }

    @Override
    public void deleteDept(Integer deptNo) {

    }

    @Override
    public void updateDept(Dept dept) {

    }
}