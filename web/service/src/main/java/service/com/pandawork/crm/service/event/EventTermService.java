package com.pandawork.crm.service.event;

import com.pandawork.core.common.exception.SSException;
import com.pandawork.crm.common.entity.event.EventTerm;

import java.util.List;

/**
 * EventTermService
 * Author： wychen
 * Date: 2017/8/10
 * Time: 9:47
 */
public interface EventTermService {

    /**
     * 获取所有指定状态的活动
     *
     * @return
     * @throws SSException
     */
    public List<EventTerm> listAllByStatus(int status) throws SSException;

    /**
     * 根据名称查询前20个活动
     *
     * @param name
     * @return
     * @throws SSException
     */
    public List<EventTerm> searchTOP20ByName(String name) throws SSException;

}
