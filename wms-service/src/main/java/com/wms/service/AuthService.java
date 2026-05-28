package com.wms.service;

import com.wms.common.core.PageResult;
import com.wms.model.dto.EmployeeDTO;
import com.wms.model.dto.EmployeeUpdateDTO;
import com.wms.model.dto.LoginDTO;
import com.wms.model.dto.RegisterDTO;
import com.wms.model.vo.LoginVO;
import com.wms.model.vo.UserVO;

public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 退出登录
     */
    void logout();

    /**
     * 员工列表
     * 
     * @param page 页码
     * @param size 每页数量
     * @param keyword 搜索关键词（用户名/昵称）
     */
    PageResult<UserVO> listEmployees(int page, int size, String keyword);

    /**
     * 创建员工账户
     */
    void createEmployee(EmployeeDTO dto);

    /**
     * 修改员工信息
     * 
     * @param id 员工ID
     * @param dto 员工更新信息
     */
    void updateEmployee(Long id, EmployeeUpdateDTO dto);

    /**
     * 删除员工
     * 
     * @param id 员工ID
     */
    void deleteEmployee(Long id);
}
