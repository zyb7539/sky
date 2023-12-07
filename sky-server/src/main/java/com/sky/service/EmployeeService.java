package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    /**
     * 启用或禁用员工账号
     * @param  status
     * @Param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据Id查询员工
     * @Param id
     * */
    Employee getById(Integer id);
    /**
     * 编辑员工信息
     * @Param employeeDTO
     * */
    void update(EmployeeDTO employeeDTO);
}
