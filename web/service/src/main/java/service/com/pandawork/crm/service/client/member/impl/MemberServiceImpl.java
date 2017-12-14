package com.pandawork.crm.service.client.member.impl;

import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.dto.client.basic.ClientSearchDto;
import com.pandawork.crm.common.dto.client.member.MemberDto;
import com.pandawork.crm.common.dto.client.member.MemberSearchDto;
import com.pandawork.crm.common.entity.client.basic.Client;
import com.pandawork.crm.common.entity.client.points.Points;
import com.pandawork.crm.common.enums.client.MemberStatusEnums;
import com.pandawork.crm.common.exception.CrmException;
import com.pandawork.crm.mapper.client.basic.ClientMapper;
import com.pandawork.crm.service.client.basic.ClientService;
import com.pandawork.crm.service.client.member.MemberService;
import com.pandawork.crm.service.client.points.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MemberServiceImpl
 * Created by shura
 * Date:  2017/7/25.
 * Time:  13:12
 */
@Service("memberService")
public class MemberServiceImpl implements MemberService {

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PointsService pointsService;

    /**
     * 根据searchDto查询会员列表
     *
     * @param searchDto
     * @return
     * @throws SSException
     */
    @Override
    public List<Client> listBySearchDto(MemberSearchDto searchDto)throws SSException{
        List<Client> clientList = new ArrayList<Client>();
        try{
            clientList = clientMapper.listMemberBySearchDto(searchDto);
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.ListClientFailed, e);
        }
        return clientList;
    }

    /**
     * 修改会员状态
     *
     * @param id
     * @param memberStatusEnums
     * @throws SSException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,SSException.class},propagation = Propagation.REQUIRED)
    public void updateMemberStatus(int id, MemberStatusEnums memberStatusEnums)throws SSException{
        try{
            if(Assert.isNull(id) || id < 0){
                throw SSException.get(CrmException.ClientIdError);
            }
            clientMapper.updateMemberStatus(id,memberStatusEnums.getId());
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateMemberStatusFailed, e);
        }
    }

    /**
     *  会员截止期自动锁定会员状态
     *
     * @throws SSException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,SSException.class},propagation = Propagation.REQUIRED)
    public void deadlineLock()throws SSException{
        Date today = new Date();
        Date deadLine =  null;
        Client member = new Client();
        try{
            MemberSearchDto searchDto = new MemberSearchDto();
            List<Client> clientList = clientMapper.listMemberBySearchDto(searchDto);
            System.out.println("成功触发定时任务");
            for(Client client : clientList){
                deadLine = client.getMemberDeadline();
                if(today.getTime() >= deadLine.getTime()){
                    this.updateMemberStatus(client.getId(),MemberStatusEnums.Locked);
                    member = clientService.queryById(client.getId());
                    //会员到期清空当前积分
                    member.setMemberPoints(0);
                    //生成清空的积分记录
                    Points points = new Points();
                    points = pointsService.getLastPointsByClientId(client.getId());
                    points.setPointsDate(new Date());
                    points.setCurrentPoints(0);
                    points.setPointsSize(-points.getCurrentPoints());
                    pointsService.newPoints(points);
                    clientService.updateClient(member);
                }
            }
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateMemberStatusFailed, e);
        }
    }

    /**
     * 统计会员组会员数量
     *
     * @param memberGroupId
     * @return
     * @throws SSException
     */
    @Override
    public int countByGroup(int memberGroupId)throws SSException{
        try{
            int count = clientMapper.countByGroupId(memberGroupId);
            return count;
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.CountMemberByGroupIdFailed, e);
        }
    }

    /**
     * 统计全部会员数量
     *
     * @return
     * @throws SSException
     */
    @Override
    public int countBySearchDto(MemberSearchDto searchDto)throws SSException{
        try{
            int count = clientMapper.countMemberBySearchDto(searchDto);
            return count;
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.CountMemberFailed, e);
        }
    }

    /**
     * 修改会员信息
     *
     * @param memberDto
     * @throws SSException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,SSException.class},propagation = Propagation.REQUIRED)
    public void updateMember(MemberDto memberDto)throws SSException{
        try{
            if(Assert.isNotNull(memberDto.getRecordId())){
                if(checkRecordId(memberDto.getRecordId()) > 0){
                    throw SSException.get(CrmException.RecordIdIsExit);
                }
            }
            clientMapper.updateMember(memberDto);
        }catch(Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateMemberFailed, e);
        }
    }


    /**
     * 检查身份证号数量
     *
     * @param id
     * @return
     * @throws SSException
     */
    private int checkRecordId(String id)throws SSException{
        try{
            int count = clientMapper.countRecordId(id);
            return count;
        }catch (Exception e){
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.CountRecordIdFailed, e);
        }
    }
}
