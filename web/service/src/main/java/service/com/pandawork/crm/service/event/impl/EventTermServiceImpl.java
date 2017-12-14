package com.pandawork.crm.service.event.impl;

import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.entity.event.EventTerm;
import com.pandawork.crm.common.exception.CrmException;
import com.pandawork.crm.mapper.event.EventTermMapper;
import com.pandawork.crm.service.event.EventTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EventTermServiceImpl
 * Author： wychen
 * Date: 2017/8/10
 * Time: 9:48
 */
@Service("eventTermService")
public class EventTermServiceImpl implements EventTermService{

    @Autowired
    private EventTermMapper eventTermMapper;

    @Override
    public List<EventTerm> listAllByStatus(int status) throws SSException{
      try{
          return eventTermMapper.listAllByStatus(status);
      }catch (Exception e){
          LogClerk.errLog.error(e);
          throw SSException.get(CrmException.ListAllProcessingFail, e);
      }
    }

    @Override
    public List<EventTerm> searchTOP20ByName(String name) throws SSException{
        try {
            return eventTermMapper.searchTOP20ByName(name);
        }catch (Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.SearchEventTermByNameFail, e);
        }
    }

}
