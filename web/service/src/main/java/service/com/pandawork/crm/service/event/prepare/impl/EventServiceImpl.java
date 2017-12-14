package com.pandawork.crm.service.event.prepare.impl;

import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.core.framework.dao.CommonDao;
import com.pandawork.crm.common.dto.event.EventSearchDto;
import com.pandawork.crm.common.entity.client.basic.Client;
import com.pandawork.crm.common.entity.event.Event;
import com.pandawork.crm.common.entity.event.EventRecordNotice;
import com.pandawork.crm.common.entity.event.EventTerm;
import com.pandawork.crm.common.entity.profile.analysis.AnalysisResult;
import com.pandawork.crm.common.enums.event.*;
import com.pandawork.crm.common.exception.CrmException;
import com.pandawork.crm.common.utils.DateUtils;
import com.pandawork.crm.mapper.event.EventMapper;
import com.pandawork.crm.service.client.basic.ClientService;
import com.pandawork.crm.service.event.EventRecordNoticeService;
import com.pandawork.crm.service.event.prepare.EventService;
import com.pandawork.crm.service.party.member.MemberGroupService;
import com.pandawork.crm.service.profile.analysis.AnalysisResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * EventServiceImpl
 *
 * @author Flying
 * @date 2017/7/20 20:56
 */
@Service("eventService")
public class EventServiceImpl implements EventService {

    @Autowired
    @Qualifier("commonDao")
    private CommonDao commonDao;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private MemberGroupService memberGroupService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EventRecordNoticeService eventRecordNoticeService;

    @Autowired
    private AnalysisResultService analysisResultService;

    @Override
    public List<Event> listAll() throws SSException {
        try {
            return eventMapper.listAll();
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryCarriedOutEventInfoFail, e);
        }
    }

    @Override
    public Event queryById(int id) throws SSException {
        Event event = new Event();
        try {
            //检查id是否为空
            if (Assert.isNull(id)) {
                throw SSException.get(CrmException.EventIdNotNull);
            }
            event = commonDao.queryById(Event.class, id);
            setExtraValue(event);
            return event;
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryCarriedOutEventByIdFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public Event newEvent(Event event) throws SSException {
        try {
            return commonDao.insert(event);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.InsertNewEventFail, e);
        }
    }

    @Override
    public boolean checkEventNameIsExit(String name) throws SSException {
        int num = 0;
        try {
            num = eventMapper.countByName(name);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.EventNameIsExist, e);
        }
        return num > 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void updateEvent(Event event) throws SSException {
        try {
            commonDao.update(event);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateEventFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void updateApprovalStatus(int id, int partyId, int status, String comment) throws SSException {
        try {
            if (Assert.isNull(id)) {
                throw SSException.get(CrmException.EventIdNotNull);
            }
            if (Assert.isNull(partyId)) {
                throw SSException.get(CrmException.PartyIdError);
            }
            if (Assert.isNull(status)) {
                throw SSException.get(CrmException.EventStatusNotNull);
            }
            eventMapper.updateApprovalStatus(id, partyId, status, comment);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateEventStatusFail, e);
        }
    }

    @Override
    public List<Event> listNotLogoutEvent() throws SSException {
        try {
            return eventMapper.listNotLogoutEvent();
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryNotLogoutEventFail, e);
        }
    }

    @Override
    public List<Event> listNotAdoptEvent() throws SSException {
        try {
            return eventMapper.listNotAdoptEvent();
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryNotAdoptEventFail, e);
        }
    }

    @Override
    public List<Event> listNotLogoutAndNotAdoptEvent() throws SSException {
        try {
            return eventMapper.listNotLogoutAndNotAdoptEvent();
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryNotLogoutAndNotAdoptEventFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void autoLogoutEvent() throws SSException {
        long between = 0;
        try {
            //获取所有审核未通过的活动
            List<Event> eventList = eventMapper.listNotLogoutAndNotAdoptEvent();
            if (Assert.isNull(eventList)) {
                throw SSException.get(CrmException.SQlWithoutNotAdoptEvent);
            }
            for (Event events : eventList) {
                //获取当天时间
                Date today = new Date();
                between = (events.getStartDate().getTime() - today.getTime());
                int day = (int) Math.ceil(((double) between) / (24 * 60 * 60 * 1000));
                if (day < events.getRemindTime()) {
                    eventMapper.updateEventToLogout(events.getId());
                }
            }
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.AutoLogoutEventFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void toLogoutEvent(int id) throws SSException {
        try {
            if (Assert.isNull(id)) {
                throw SSException.get(CrmException.EventIdNotNull);
            }
            eventMapper.updateEventToLogout(id);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.LogoutEventFail, e);
        }
    }

    @Override
    public List<Event> listByEventSearchDto(EventSearchDto eventSearchDto) throws SSException {
        try {
            return eventMapper.listByEventSearchDto(eventSearchDto);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.QueryBySearchDtoFail, e);
        }
    }

    @Override
    public int countByEventSearchDto(EventSearchDto eventSearchDto) throws SSException {
        Integer count = 0;
        try {
            count = eventMapper.countByEventSearchDto(eventSearchDto);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.CountBySearchDtoFail, e);
        }
        return count == null ? 0 : count;
    }

    @Override
    public boolean checkIsCanLogoutById(int id, int partyId) throws SSException {
        Event event = new Event();
        try {
            event = commonDao.queryById(Event.class, id);
            if (event.getIsLogout() != 0) {
                throw SSException.get(CrmException.EventHaveLogout);
            }
            if (event.getApprovalStatus() == 2) {
                throw SSException.get(CrmException.AdoptEventCanNotLogout);
            }
            if (event.getCreatedPartyId() != partyId) {
                throw SSException.get(CrmException.PartyCanNotLogoutEvent);
            }
            return true;
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.CheckIsCanLogoutByIdFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void updateTemplateToUsed(int id) throws SSException {
        try {
            if (Assert.isNull(id)) {
                throw SSException.get(CrmException.TemplateIdNotNull);
            }
            eventMapper.updateTemplateToUsed(id);
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateTemplateStatusFail, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
    public void autoCreateEventRecordNoticeAndEventTerm() throws SSException {
        try {
            //获取所有审核通过的活动
            List<Event> eventList = eventMapper.listAdoptEvent();
            for (Event event : eventList){
                Date today = new Date();
                long between = (event.getStartDate().getTime() - today.getTime());
                int day = (int) Math.ceil(((double) between) / (24 * 60 * 60 * 1000));
                if (day == event.getRemindTime()){
                    //处理每期活动记录表
                    EventTerm eventTerm = new EventTerm();
                    //处理活动结束时间
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(today);
                    //处理活动粒度为年的活动的第一期的结束时间
                    if (event.getType() == EventTypeEnums.Promotion.getId()){
                        eventTerm.setEndDate(event.getEndDate());
                    }
                    //处理活动力度为月的活动的第一期的结束时间
                    //活动类型为会员关怀型
                    if (event.getType() == EventTypeEnums.Member.getId()){
                        cal.add(Calendar.MONTH, 1);
                        cal.add(Calendar.DATE, - 1);
                        Date endDate = cal.getTime();
                        if (endDate.compareTo(event.getEndDate()) >= 0){
                            eventTerm.setEndDate(event.getEndDate());
                        }else {
                            eventTerm.setEndDate(endDate);
                        }
                    }
                    //活动类型为营销型活动
                    if (event.getType() == EventTypeEnums.Promotion.getId()){
                        eventTerm.setEndDate(event.getEndDate());
                    }
                    eventTerm.setEventId(event.getId());
                    eventTerm.setStartDate(event.getStartDate());
                    eventTerm.setMemberGroupId(event.getMemberGroupId());
                    eventTerm.setStatus(EventTermStatusEnums.Noticing.getId());
                    EventTerm eventTerm1 = commonDao.insert(eventTerm);

                    //处理活动记录通知表
                    //获取此活动参与的会员组下的参与人员
                    if (Assert.isNotNull(eventTerm1)){
                        //处理营销型活动
                        if (event.getType() == EventTypeEnums.Promotion.getId()){
                            List<AnalysisResult> analysisResultList = analysisResultService.listByMemberGroupId(eventTerm1.getMemberGroupId());
                            for (AnalysisResult analysisResult : analysisResultList) {
                                //操作活动记录通知表
                                EventRecordNotice eventRecordNotice = new EventRecordNotice();
                                Client client = clientService.queryById(analysisResult.getClientId());
                                eventRecordNotice.setEventId(eventTerm1.getEventId());
                                eventRecordNotice.setEventStartDate(eventTerm1.getStartDate());
                                eventRecordNotice.setEventEndDate(eventTerm1.getEndDate());
                                eventRecordNotice.setMemberGroupId(eventTerm1.getMemberGroupId());
                                eventRecordNotice.setClientId(client.getId());
                                eventRecordNotice.setParticipantName(client.getName());
                                eventRecordNotice.setParticipantIdcard(client.getIdCardNum());
                                eventRecordNotice.setParticipantTel(client.getTel());
                                eventRecordNotice.setNoticeStatus(NoticeStatusEnums.NotNotified.getId());
                                eventRecordNotice.setEventParticipantTimes(0);
                                eventRecordNotice.setEventParticipantStatus(0);
                                eventRecordNotice.setEventStatus(EventStatusEnums.NotNotice.getId());
                                eventRecordNotice.setEventTermId(eventTerm1.getId());
                                eventRecordNoticeService.newEventRecordNotice(eventRecordNotice);
                            }

                        }
                        //处理会员关怀型活动
                        if (event.getType() == EventTypeEnums.Member.getId()){
                            List<Client> clientList = clientService.listByMemberGroupId(eventTerm1.getMemberGroupId());
                            for (Client client : clientList) {
                                //操作活动记录通知表
                                EventRecordNotice eventRecordNotice = new EventRecordNotice();
                                eventRecordNotice.setEventId(eventTerm1.getEventId());
                                eventRecordNotice.setEventStartDate(eventTerm1.getStartDate());
                                eventRecordNotice.setEventEndDate(eventTerm1.getEndDate());
                                eventRecordNotice.setMemberGroupId(eventTerm1.getMemberGroupId());
                                eventRecordNotice.setClientId(client.getId());
                                eventRecordNotice.setParticipantName(client.getName());
                                eventRecordNotice.setParticipantIdcard(client.getIdCardNum());
                                eventRecordNotice.setParticipantTel(client.getTel());
                                eventRecordNotice.setNoticeStatus(NoticeStatusEnums.NotNotified.getId());
                                eventRecordNotice.setEventParticipantTimes(0);
                                eventRecordNotice.setEventParticipantStatus(0);
                                eventRecordNotice.setEventStatus(EventStatusEnums.NotNotice.getId());
                                eventRecordNotice.setEventTermId(eventTerm1.getId());
                                eventRecordNoticeService.newEventRecordNotice(eventRecordNotice);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.UpdateTemplateStatusFail, e);
        }
    }

//    @Override
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {SSException.class, Exception.class, RuntimeException.class})
//    public void createEventRecordNoticeAndEventTerm() throws SSException {
//        try {
//            //获取所有审批通过的活动
//            List<Event> eventList = eventMapper.listAdoptEvent();
//            for (Event event : eventList) {
//                //获取提醒的开始时间
//                Date startDate = DateUtils.aheadDay(event.getStartDate(), event.getRemindTime());
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(startDate);
//                int startYear = cal.get(cal.YEAR); //获取活动开始的---“年”
//                int startMonth = cal.get(cal.MONTH) + 1; //获取活动的开始---“月”
//                int startDay = cal.get(cal.DATE); //获取活动的开始的---“日”
//
//                //获取整个活动的结束时间
//                Date endDate = event.getStartDate();
//                Calendar cal2 = Calendar.getInstance();
//                cal.setTime(endDate);
//                int endYear = cal2.get(cal.YEAR); //获取活动开始的---“年”
//                int endMonth = cal2.get(cal.MONTH) + 1; //获取活动的开始---“月”
//                int endDay = cal2.get(cal.DATE); //获取活动的开始的---“日”
//
//                //获取当前系统时间
//                Calendar cal3 = Calendar.getInstance();
//                Date currentDate = new Date();
//                int currentYear = cal3.get(Calendar.YEAR); //获取当前时间的---“年”
//                int currentMonth = cal.get(Calendar.MONTH) + 1; //获取当前时间的---“月”
//                int currentDay = cal3.get(Calendar.DATE); //获取当前时间的---“日”
//
//                //获取当前时间的前n天（n为提醒时间）
//                Date currentBeforeDate = DateUtils.aheadDay(currentDate, event.getRemindTime());
//
//                //获取当前时间的下一个月的后n天的前一天
//                cal3.add(Calendar.MONTH, 1);
//                cal3.add(Calendar.DATE, event.getRemindTime() - 1);
//                Date BeforeLastDate = cal3.getTime();
//
//
//                //获取当前时间的后n天（n为提醒时间）
//                Calendar cal4 = Calendar.getInstance();
//                cal4.add(Calendar.DATE, event.getRemindTime());
//                Date currentLastDate = cal4.getTime();
//
//                //获取下一年的当前时间的后n天的前一天
//                Calendar cal5 = Calendar.getInstance();
//                cal5.add(Calendar.YEAR, 1);
//                cal5.add(Calendar.DATE, event.getRemindTime() - 1);
//                Date currentLastYear = cal5.getTime();
//
//                //获取下一个月的同一天的前一天
////                cal3.add(Calendar.MONTH, 1);
////                cal3.add(Calendar.DAY_OF_MONTH, -1);
////                Date BeforeLastDate = cal3.getTime();
//
//
//                //判断活动类型
//                //会员关怀型活动
//                if (event.getType() == EventTypeEnums.Member.getId()) {
//                    //活动粒度为---年
//                    if (event.getPollingTime() == 1) {
//                        //判断当前时间减去提醒时间后比活动结束时间小
//                        if (currentBeforeDate.compareTo(event.getEndDate()) < 0) {
//                            if (currentMonth == startMonth && currentDay == startDay) {
//                                if (currentYear >= startYear && currentYear <= endYear) {
//                                    //处理开始的月份
//                                    if (startMonth == endMonth) {
//                                        //处理生成记录的时候开始时间和结束时间
//                                        if ((endYear - startYear) <= 1) {
//                                            //生成活动记录通知和本期活动记录
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), event.getStartDate(), event.getEndDate());
//                                        } else {
//                                            //生成活动记录通知和本期活动记录
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), currentLastDate, event.getEndDate());
//                                        }
//                                    } else if (startMonth > endMonth) {
//                                        if (endYear == currentYear) {
//                                            //生成活动记录通知和本期活动记录
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), currentLastDate, event.getEndDate());
//                                        } else {
//                                            //生成活动记录通知和本期活动记录---结束时间为下一年的今天-（提醒时间-1）
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), currentLastDate, currentLastYear);
//                                        }
//
//                                    } else {
//                                        if (endYear == startYear) {
//                                            //开始的月份小于结束的月份，且同一年的活动
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), event.getStartDate(), event.getEndDate());
//                                        } else if (startYear < endYear ) {
//                                            if (currentYear == endYear) {
//                                                //开始的月份小于结束的月份，且是跨年的活动，且是最后一年
//                                                this.createEventRecordNoticeAndEventTerm(event.getId(), currentLastDate, event.getEndDate());
//                                            } else {
//                                                //开始的月份小于结束的月份，且是跨年的活动，不是最后一年
//                                                this.createEventRecordNoticeAndEventTerm(event.getId(), currentLastDate, currentLastYear);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        //活动粒度为---月
//                        if (Assert.lessOrEqualZero(currentYear - startYear)) {
//                            //同年的活动
//                            if (currentMonth >= startMonth) {
//                                if (currentDay == startDay) {
//                                    if (currentDay < endDay) {
//                                        //生成活动记录通知和本期活动记录
//                                        this.createEventRecordNoticeAndEventTerm(event.getId(), currentDate, BeforeLastDate);
//                                    }
//                                }
//                            }
//                        } else {
//                            //对于跨年的活动，如果年份相等，则只有当当前月份大于等于开始月份的情况下才能生成记录
//                            //如果是当前年份大于活动开始的年份，则只需日期相等就生成记录
//                            if (currentYear == startYear) {
//                                if (currentMonth >= startMonth) {
//                                    if (currentDay == startDay) {
//                                        //生成活动记录通知和本期活动记录
//                                        this.createEventRecordNoticeAndEventTerm(event.getId(), currentDate, BeforeLastDate);
//                                    }
//                                }
//                            } else if (currentYear > startYear && currentYear <= endYear) {
//                                if (currentYear == endYear) {
//                                    if (currentDay == startDay) {
//                                        if (currentMonth < endMonth) {
//                                            //生成活动记录通知和本期活动记录
//                                            this.createEventRecordNoticeAndEventTerm(event.getId(), currentDate, BeforeLastDate);
//                                        }
//                                    }
//                                } else if (currentYear < endYear) {
//                                    if (currentDay == startDay) {
//                                        //生成活动记录通知和本期活动记录
//                                        this.createEventRecordNoticeAndEventTerm(event.getId(), currentDate, BeforeLastDate);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                //营销型活动
//                if (event.getType() == EventTypeEnums.Promotion.getId()) {
//                    if (Assert.isNotNull(event.getMemberGroupId())) {
//                        if (DateUtils.isSameDay(event.getStartDate(), currentDate)) {
//                            //生成活动记录通知和本期活动记录
//                            this.createEventRecordNoticeAndEventTerm(event.getId(), event.getStartDate(), event.getEndDate());
//                        }
//                    }
//
//                }
//            }
//        } catch (Exception e) {
//            LogClerk.errLog.error(e);
//            throw SSException.get(CrmException.AutoCreateEventRecordNoticeAndEventTermFail, e);
//        }
//    }

    /**
     * 私有方法
     * 处理枚举字段的value值
     *
     * @param event
     * @throws Exception
     */
    private void setExtraValue(Event event) throws Exception {
        try {
            if (Assert.isNotNull(event)) {
                if (Assert.isNotNull(event.getLevel())) {
                    event.setLevelValue(EventLevelEnums.valueOf(event.getLevel()).getLevel());
                }
                if (Assert.isNotNull(event.getType())) {
                    event.setTypeValue(EventTypeEnums.valueOf(event.getType()).getType());
                }
                if (Assert.isNotNull(event.getMemberGroupId())) {
                    event.setMemberGroupName(memberGroupService.queryById(event.getMemberGroupId()).getName());
                }
            }
        } catch (Exception e) {
            LogClerk.errLog.error(e);
            throw SSException.get(CrmException.SetExtraDataFail, e);
        }

    }

}
