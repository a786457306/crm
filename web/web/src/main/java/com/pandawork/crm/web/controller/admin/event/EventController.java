package com.pandawork.crm.web.controller.admin.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.annotation.Module;
import com.pandawork.crm.common.dto.event.EventDto;
import com.pandawork.crm.common.dto.event.EventSearchDto;
import com.pandawork.crm.common.entity.client.points.PointsItem;
import com.pandawork.crm.common.entity.event.CheckItem;
import com.pandawork.crm.common.entity.event.Event;
import com.pandawork.crm.common.entity.party.dictionary.Dictionary;
import com.pandawork.crm.common.entity.party.group.employee.Employee;
import com.pandawork.crm.common.entity.party.member.Member;
import com.pandawork.crm.common.enums.event.EventApprovalStatusEnums;
import com.pandawork.crm.common.enums.event.EventLevelEnums;
import com.pandawork.crm.common.enums.event.EventTypeEnums;
import com.pandawork.crm.common.enums.other.ModuleEnums;
import com.pandawork.crm.common.enums.party.dictionary.DictionaryEnums;
import com.pandawork.crm.common.exception.CrmException;
import com.pandawork.crm.common.utils.DataUtils;
import com.pandawork.crm.common.utils.DateUtils;
import com.pandawork.crm.common.utils.URLConstants;
import com.pandawork.crm.service.event.template.TemplateService;
import com.pandawork.crm.web.spring.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * EventController
 *
 * @author Flying
 * @date 2017/7/26 21:36
 */
@Controller
@Module(ModuleEnums.AdminEvent)
@RequestMapping(value = URLConstants.ADMIN_EVENT_PREPARE_URL)
public class EventController extends AbstractController {

    /**
     * 去列表页
     *
     * @param model
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareList)
    @RequestMapping(value = {"", "list"}, method = RequestMethod.GET)
    public String toList(Model model, HttpSession httpSession){
        int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
        int pageCount = 0;
        EventSearchDto eventSearchDto = new EventSearchDto();
        try{
            pageCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, eventService.countByEventSearchDto(eventSearchDto));
            model.addAttribute("pageCount", pageCount);
            model.addAttribute("partyId", partyId);
        } catch(SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "admin/event/prepare/list";
    }

    /**
     * ajax获取列表
     *
     * @param pageNo
     * @param eventSearchDto
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareList)
    @RequestMapping(value = "ajax/list", method = RequestMethod.GET)
    @ResponseBody
    public JSON ajaxList(@RequestParam("pageNo") Integer pageNo,
                         EventSearchDto eventSearchDto,
                         HttpSession httpSession){
        JSONObject json = new JSONObject();
        int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
        int offset = 0;
        if(Assert.isNotNull(pageNo)){
            pageNo = pageNo <= 0 ? 0 : pageNo - 1;
            offset = pageNo * DEFAULT_PAGE_SIZE;
        }
        eventSearchDto.setOffset(offset);
        eventSearchDto.setPageNo(pageNo);
        eventSearchDto.setPageSize(DEFAULT_PAGE_SIZE);
        List<Event> eventList = Collections.emptyList();
        try {
            eventList = eventService.listByEventSearchDto(eventSearchDto);
            JSONArray jsonArray = new JSONArray();
            for (Event event : eventList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", event.getId());
                jsonObject.put("name", event.getName());
                jsonObject.put("type", EventTypeEnums.valueOf(event.getType()).getType());
                jsonObject.put("level", EventLevelEnums.valueOf(event.getLevel()).getLevel());
                jsonObject.put("approvalStatusValue", EventApprovalStatusEnums.valueOf(event.getApprovalStatus()).getStatus());
                jsonObject.put("approvalStatus", event.getApprovalStatus());
                jsonObject.put("startDate", DateUtils.formatDateSimple(event.getStartDate()));
                jsonObject.put("endDate", DateUtils.formatDateSimple(event.getEndDate()));
                if (event.getType() == EventTypeEnums.Member.getId()){
                    jsonObject.put("memberGroupName", event.getMemberGroupName());
                } else {
                    jsonObject.put("memberGroupName", "无");
                }
                jsonObject.put("createdPartyName", event.getCreatedPartyName());
                jsonObject.put("createdPartyId", event.getCreatedPartyId());
                jsonObject.put("createdTime", DateUtils.formatDateSimple(event.getCreatedTime()));
                //判断当前登录人是否可以审批此活动
                List<Employee> employeesList = employeeService.listImmediateSByPartyId(event.getCreatedPartyId());
                if (Assert.isNotNull(employeesList)){
                    for (Employee employee : employeesList){
                        if (Assert.isNotNull(employee.getPartyId())){
                            if (employee.getPartyId() == partyId){
                                //可以审批
                                jsonObject.put("approval", 1);
                            } else {
                                //不可以审批
                                jsonObject.put("approval", 0);
                            }
                        } else {
                            //不可以审批
                            jsonObject.put("approval", 0);
                        }
                    }
                } else {
                    //不可以审批
                    jsonObject.put("approval", 0);
                }
                jsonArray.add(jsonObject);
            }
            int numCount = 0;
            int dataCount = 0;
            numCount = eventService.countByEventSearchDto(eventSearchDto);
            dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, numCount);
            json.put("code", AJAX_SUCCESS_CODE);
            json.put("list", jsonArray);
            json.put("dataCount", dataCount);
            json.put("numCount", numCount);
            return json;
        }catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax活动名称搜索框模糊查询
     *
     * @param name
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareList)
    @RequestMapping(value = "ajax/name", method = RequestMethod.GET)
    @ResponseBody
    public JSON ajaxSearchByName(@RequestParam("name") String name){
        JSONArray jsonArray = new JSONArray();
        EventSearchDto eventSearchDto = new EventSearchDto();
        try{
            eventSearchDto.setName(name);
            List<Event> eventList = eventService.listByEventSearchDto(eventSearchDto);
            //截取前20
            if (eventList.size() > 20){
                eventList.subList(0, 19);
            }
            for(Event event : eventList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",event.getId());
                jsonObject.put("name",event.getName());
                jsonArray.add(jsonObject);
            }
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonArray(jsonArray);
    }

    /**
     * ajax手动注销活动
     *
     * @param id
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareLogout)
    @RequestMapping(value = "ajax/logout", method = RequestMethod.POST)
    @ResponseBody
    public JSON ajaxLogout(@RequestParam("id") Integer id,
                           HttpSession httpSession){
        try{
            Assert.isNotNull(id, CrmException.EventIdNotNull);
            int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
            if (eventService.checkIsCanLogoutById(id, partyId)){
                eventService.toLogoutEvent(id);
                return sendMsgAndCode(AJAX_SUCCESS_CODE, "注销成功");
            }else {
                return sendMsgAndCode(AJAX_FAILURE_CODE, "注销失败");
            }
        }catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax审批活动
     *
     * @param id
     * @param status
     * @param comment
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareApproval)
    @RequestMapping(value = "ajax/approval", method = RequestMethod.POST)
    @ResponseBody
    public JSON ajaxApproval(@RequestParam("id") Integer id,
                             @RequestParam("status") Integer status,
                             @RequestParam("comment") String comment,
                             HttpSession httpSession){
        int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));

        try{
            if(Assert.isNull(id) || Assert.lessOrEqualZero(status) || Assert.isNull(partyId)){
                return sendMsgAndCode(AJAX_FAILURE_CODE, "审核失败,请检查信息是否完善！");
            }else {
                Event event = eventService.queryById(id);
                //判断营销型活动是否已经添加了活动人员，若没有，则不让审核通过
                if (event.getType() == EventTypeEnums.Promotion.getId()){
                    if (Assert.isNull(event.getMemberGroupId())){
                        return sendMsgAndCode(AJAX_SUCCESS_CODE, "审核失败，此活动暂时无活动参与人员！");
                    } else {
                        eventService.updateApprovalStatus(id, partyId, status, comment);
                        return sendMsgAndCode(AJAX_SUCCESS_CODE, "审核成功");
                    }
                } else {
                    eventService.updateApprovalStatus(id, partyId, status, comment);
                    return sendMsgAndCode(AJAX_SUCCESS_CODE, "审核成功");
                }

            }
        }catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * 去新建活动页
     *
     * @param model
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareNew)
    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String toNewEvent(@RequestParam(value = "id", required = false) Integer id, Model model){
        List<Member> memberList = Collections.emptyList();
        List<Dictionary> checkItemList = Collections.emptyList();
        List<Dictionary> pointsItemList = Collections.emptyList();
        try {
            if (Assert.isNotNull(id)){
                Event event = eventService.queryById(id);
                if (event.getType() == EventTypeEnums.Member.getId()){
                    if (Assert.isNotNull(event.getMemberGroupId())){
                        Member member = memberGroupService.queryById(event.getMemberGroupId());
                        event.setMemberGroupName(member.getName());
                    } else {
                        event.setMemberGroupName("");
                    }
                } else {
                    event.setMemberGroupName("");
                }
                model.addAttribute("event", event);
                model.addAttribute("templateId", id);
            }
            //获取所有会员组
            memberList = memberGroupService.isNotDeleted();
            //获取字典中的所有检查项
            checkItemList = dictionaryService.listByPId(DictionaryEnums.DIC_CHECK_ITEM.getId());
            //获取字典中的所有积分项
            pointsItemList = dictionaryService.listByPId(DictionaryEnums.DIC_POINTS_ITEM.getId());
            model.addAttribute("memberList", memberList);
            model.addAttribute("checkItemList", checkItemList);
            model.addAttribute("pointsItemList", pointsItemList);
        }catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "admin/event/prepare/new";
    }

    /**
     * 检查活动名称是否重复，新建活动时
     *
     * @param name
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareNew)
    @RequestMapping(value = "ajax/new/checkname", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject checkEventName(@RequestParam("name") String name){
        if (Assert.isNull(name)){
            return sendJsonObject(AJAX_FAILURE_CODE);
        }
        try {
            if (Assert.isNotNull(name)){
                String name1 = name.replaceAll(" ", "");
                if (name1.equals("")){
                    return sendMsgAndCode(AJAX_FAILURE_CODE, "活动名称不能为空");
                }
            }
            if (eventService.checkEventNameIsExit(name)){
                return sendJsonObject(AJAX_FAILURE_CODE);
            }else {
                return sendJsonObject(AJAX_SUCCESS_CODE);
            }
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * 检查活动名称是否重复，保存为模板时
     *
     * @param name
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareNew)
    @RequestMapping(value = "ajax/new/checkname/template", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject checkEventNameTempalte(@RequestParam("name") String name){
        if (Assert.isNull(name)){
            return sendJsonObject(AJAX_FAILURE_CODE);
        }
        try {
            if (Assert.isNotNull(name)){
                String name1 = name.replaceAll(" ", "");
                if (name1.equals("")){
                    return sendMsgAndCode(AJAX_FAILURE_CODE, "活动名称不能为空");
                }
            }
            if (templateService.checkTemplateNameIsExit(name)){
                return sendJsonObject(AJAX_FAILURE_CODE);
            }else {
                return sendJsonObject(AJAX_SUCCESS_CODE);
            }
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax 新建活动
     *
     * @param eventDto
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareNew)
    @RequestMapping(value = "ajax/new/event", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject newEvent(@RequestBody EventDto eventDto,
                               HttpSession httpSession){
        int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
        if (Assert.isNull(eventDto)){
            return sendMsgAndCode(AJAX_FAILURE_CODE, "提交活动失败，请检查信息");
        }
        try{
            Event event = eventDto.getEvent();
            List<CheckItem> checkItemList = eventDto.getCheckItemList();
            List<PointsItem> pointsItemList = eventDto.getPointsItemList();
            //根据templateId来判断是否之前已经保存过模板
//            if (event.getTemplateId() == null && event.getIsTemplateUsed() == 0){
//                event.setTemplateId(null);
//                event.setIsTemplateUsed(0);
//            }
            //如果event不为空，则将活动信息插入数据库，并用event1来接收新插入的这条数据
            if (Assert.isNotNull(event)){
                //申请人
                event.setCreatedPartyId(partyId);
                //申请时间
                event.setApplyTime(new Date());
                //标记为活动
                event.setIsMarked(0);
                //模板是否被使用-0
                event.setIsTemplateUsed(0);
                if (checkItemList != null && !checkItemList.isEmpty()){
                    event.setIsCheckItemRelated(1);
                }
                if (pointsItemList != null && !pointsItemList.isEmpty()){
                    event.setIsPointsRelated(1);
                }
                Event event1 = eventService.newEvent(event);
                //判断event中是否有templateId
                // 若不为空，则证明之前已经保存过模板了，需要将之前保存的模板的使用状态改为正在被使用
                if (Assert.isNotNull(event.getTemplateId())){
                    Event event2 = new Event();
                    event2.setId(event.getTemplateId());
                    event2.setIsTemplateUsed(1);
                    event2.setIsPureTemplate(0);
                    eventService.updateEvent(event2);
                }
                //判断关联的检查项是否为空，若不为空，则插入数据库
                if(checkItemList != null && !checkItemList.isEmpty()){
                    for (CheckItem checkItem : checkItemList){
                        checkItem.setEventId(event1.getId());
                        checkItem.setCreatedPartyId(partyId);
                        checkItemService.newCheckItem(checkItem);
                    }
                }
                //判断关联的积分项是否为空，若不为空，则插入数据库
                if(pointsItemList != null && !pointsItemList.isEmpty()){
                    for (PointsItem pointsItem : pointsItemList){
                        pointsItem.setEventId(event1.getId());
                        pointsItem.setCreatedPartyId(partyId);
                        pointsItemService.newPointsItem(pointsItem);
                    }
                }
                return sendMsgAndCode(AJAX_SUCCESS_CODE, "提交活动成功");
            } else {
                return sendMsgAndCode(AJAX_FAILURE_CODE, "提交活动失败，请检查信息");
            }
            //eventAttachmentService.newAttachment(file, getRequest());
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax 保存模板
     *
     * @param eventDto
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareNew)
    @RequestMapping(value = "ajax/new/template", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject newTemplate(@RequestBody EventDto eventDto, HttpSession httpSession){
        JSONObject json = new JSONObject();
        int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
        if (Assert.isNull(eventDto)){
            json.put("code", 1);
            json.put("tipMsg", "保存模板失败，请检查信息");
            return json;
        }
        try{
            Event event = eventDto.getEvent();
            List<CheckItem> checkItemList = eventDto.getCheckItemList();
            List<PointsItem> pointsItemList = eventDto.getPointsItemList();
            //根据templateId来判断是否之前已经保存过模板
//            if (event.getTemplateId() == null && event.getIsTemplateUsed() == 0){
//                event.setTemplateId(null);
//                event.setIsTemplateUsed(0);
//            }
            //如果event不为空，则将模板信息插入数据库，并用event1来接收新插入的这条数据
            if (Assert.isNotNull(event)) {
                //设置模板名称 name+模板
                event.setName(event.getName()+"模板");
                //申请人
                event.setCreatedPartyId(partyId);
                //申请时间
                event.setApplyTime(new Date());
                //标记为模板：1是
                event.setIsMarked(1);
                if (checkItemList != null && !checkItemList.isEmpty()) {
                    event.setIsCheckItemRelated(1);
                }
                if (pointsItemList != null && !pointsItemList.isEmpty()) {
                    event.setIsPointsRelated(1);
                }
                Event event1 = eventService.newEvent(event);
                //获取新插入模板的id
                int templateId = event1.getId();
                //判断关联的检查项是否为空，若不为空，则插入数据库
                if (checkItemList != null && !checkItemList.isEmpty()) {
                    for (CheckItem checkItem : checkItemList) {
                        checkItem.setEventId(templateId);
                        checkItem.setCreatedPartyId(partyId);
                        checkItemService.newCheckItem(checkItem);
                    }
                }
                //判断关联的积分项是否为空，若不为空，则插入数据库
                if (pointsItemList != null && !pointsItemList.isEmpty()) {
                    for (PointsItem pointsItem : pointsItemList) {
                        pointsItem.setEventId(templateId);
                        pointsItem.setCreatedPartyId(partyId);
                        pointsItemService.newPointsItem(pointsItem);
                    }
                }
                json.put("isTemplateUsed", 1);
                json.put("templateId", templateId);
                json.put("code", 0);
                json.put("tipMsg", "保存模板成功");
                return json;
            }else {
                json.put("code", 1);
                json.put("tipMsg", "保存模板失败，请检查信息");
                return json;
            }
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * 检查活动名称是否重复，修改活动时
     *
     * @param id
     * @param name
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "ajax/update/checkname", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject checkEventName(@RequestParam("id") Integer id,
                                     @RequestParam("name") String name){
        if (Assert.isNull(name)){
            return sendJsonObject(AJAX_FAILURE_CODE);
        }
        try{
            Assert.isNotNull(id, CrmException.EventIdNotNull);
            Event event = eventService.queryById(id);
            Assert.isNotNull(event, CrmException.QueryCarriedOutEventByIdFail);
            Assert.isNotNull(name, CrmException.EventNameNotNull);
            if (event.getName().equals(name)){
                return sendJsonObject(AJAX_FAILURE_CODE);
            } else {
                if (eventService.checkEventNameIsExit(name)){
                    return sendJsonObject(AJAX_FAILURE_CODE);
                }else {
                    return sendJsonObject(AJAX_SUCCESS_CODE);
                }
            }
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * 去修改页
     *
     * @param id
     * @param model
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "update", method = RequestMethod.GET)
    public String toUpdate(@RequestParam("id") Integer id,
                           Model model){
        List<Dictionary> checkItemList = Collections.emptyList();
        List<Dictionary> pointsItemList = Collections.emptyList();
        String eventAttachmentName = "";
        try {
            Event event = eventService.queryById(id);
            if(Assert.isNotNull(event.getAttachment())){
                eventAttachmentName = eventAttachmentService.queryById(event.getAttachment()).getName();
            }
            //获取所有会员组
            List<Member> memberList = memberGroupService.isNotDeleted();
            //获取字典中的所有检查项
            checkItemList = dictionaryService.listByPId(DictionaryEnums.DIC_CHECK_ITEM.getId());
            //获取字典中的所有积分项
            pointsItemList = dictionaryService.listByPId(DictionaryEnums.DIC_POINTS_ITEM.getId());
            model.addAttribute("checkItemList", checkItemList);
            model.addAttribute("pointsItemList", pointsItemList);
            model.addAttribute("memberList", memberList);
            model.addAttribute("eventAttachmentName", eventAttachmentName);
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "admin/event/prepare/update";
    }

    /**
     * ajax 获取修改页的初始数据
     *
     * @param id
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "ajax/update", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject toAjaxUpdate(@RequestParam("id") Integer id,
                                    HttpSession httpSession){
        try {
            int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
            Event event = eventService.queryById(id);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", event.getName());
            jsonObject.put("level", event.getLevel());
            jsonObject.put("startDate", DateUtils.formatDateSimple(event.getStartDate()));
            if (event.getType() == EventTypeEnums.Member.getId()){
                //根据活动类型处理活动粒度
                jsonObject.put("pollingTime", event.getPollingTime());
                //根据活动类型处理活动人员的组名称
                jsonObject.put("memberGroupId", event.getMemberGroupId());
            } else {
                jsonObject.put("pollingTime", "");
                jsonObject.put("memberGroupId", "");
            }
            jsonObject.put("type", event.getType());
            jsonObject.put("remindTime", event.getRemindTime());
            jsonObject.put("endDate", DateUtils.formatDateSimple(event.getEndDate()));
            jsonObject.put("content", event.getContent());
            jsonObject.put("noticeContent", event.getNoticeContent());
            jsonObject.put("attenchment", "");
            jsonObject.put("code", 0);
            return jsonObject;
            //return sendJsonObject(jsonObject, AJAX_SUCCESS_CODE);
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax 根据活动id获取关联积分项
     *
     * @param id
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "ajax/update/pointsitem", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxUpdatePointsItem(@RequestParam("id") Integer id){
        try{
            Event event = eventService.queryById(id);
            JSONArray jsonArray = new JSONArray();
            List<PointsItem> pointsItemList = pointsItemService.listByEventId(event.getId());
            for (PointsItem pointsItem : pointsItemList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", pointsItem.getId());
                jsonObject.put("name", pointsItem.getName());
                jsonObject.put("pointsValue", pointsItem.getPointsValue());
                jsonArray.add(jsonObject);
            }
            return sendJsonArray(jsonArray);
        } catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax 根据活动id获取关联检查项
     *
     * @param id
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "ajax/update/checkitem", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxUpdateCheckItem(@RequestParam("id") Integer id){
        try{
            Event event = eventService.queryById(id);
            JSONArray jsonArray = new JSONArray();
            List<CheckItem> checkItemList = checkItemService.listByEventId(event.getId());
            for (CheckItem checkItem : checkItemList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", checkItem.getId());
                jsonObject.put("name", checkItem.getName());
                jsonObject.put("content", checkItem.getContent());
                jsonArray.add(jsonObject);
            }
            return sendJsonArray(jsonArray);

        } catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * ajax 修改活动
     *
     * @param eventDto
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareUpdate)
    @RequestMapping(value = "ajax/update/event", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxUpdateEvent(@RequestBody EventDto eventDto,
                                      HttpSession httpSession) {
        //int partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
        try {
            if (Assert.isNull(eventDto)){
                return sendMsgAndCode(AJAX_SUCCESS_CODE, "修改活动信息失败，请检查信息");
            }
            Event event = eventDto.getEvent();
            List<CheckItem> checkItemList = eventDto.getCheckItemList();
            List<PointsItem> pointsItemList = eventDto.getPointsItemList();
            if (Assert.isNull(event)){
                return sendMsgAndCode(AJAX_SUCCESS_CODE, "修改活动信息失败，请检查信息");
            } else {
                if (Assert.isNull(event.getId())){
                    return sendMsgAndCode(AJAX_SUCCESS_CODE, "修改活动信息失败，请检查信息");
                }
                //获取当前活动的id
                int eventId = event.getId();
                //根据id获取之前的活动类型
                Event eventBefore = eventService.queryById(eventId);
                int typeBefore = eventBefore.getType();
                //如果活动之前为会员关怀型，现改成了营销型，则将之前活动的循环粒度和活动人员清空
                if (typeBefore == EventTypeEnums.Member.getId()){
                    if (event.getType() == EventTypeEnums.Promotion.getId()){
                        event.setPollingTime(null);
                        event.setMemberGroupId(null);
                    }
                }
                //判断关联积分项是否为空，若为空，则清除此活动所有的关联检查项
                if (checkItemList != null && !checkItemList.isEmpty()){
                    //若不为空，则先清除，再添加所有
                    checkItemService.deleteByEventId(eventId);
                    for (CheckItem checkItem : checkItemList){
                        checkItem.setCreatedPartyId(eventId);
                        checkItem.setEventId(eventId);
                        checkItemService.newCheckItem(checkItem);
                    }
                    event.setIsCheckItemRelated(1);
                } else {
                    checkItemService.deleteByEventId(eventId);
                }
                //判断关联积分项是否为空，若为空，则清除此活动所有的关联积分项
                if (pointsItemList != null && !pointsItemList.isEmpty()){
                    //若不为空，则先清除，再添加
                    pointsItemService.delByEventId(eventId);
                    for (PointsItem pointsItem : pointsItemList){
                        pointsItem.setCreatedPartyId(eventId);
                        pointsItem.setEventId(eventId);
                        pointsItemService.newPointsItem(pointsItem);
                    }event.setIsPointsRelated(1);
                } else {
                    pointsItemService.delByEventId(eventId);
                }
                //保存活动
                eventService.updateEvent(event);
            }
            return sendMsgAndCode(AJAX_SUCCESS_CODE, "修改成功！");
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return sendErrMsgAndErrCode(e);
        }
    }

    /**
     * 去详情页
     *
     * @param id
     * @param model
     * @return
     */
    @Module(value = ModuleEnums.AdminEventPrepare, extModule = ModuleEnums.AdminEventPrepareDetail)
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String toDetail(@RequestParam("id") Integer id,
                           Model model){
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String eventAttachmentName = "";
        try {
            Event event = eventService.queryById(id);

            if(Assert.isNotNull(event.getAttachment())){
                eventAttachmentName = eventAttachmentService.queryById(event.getAttachment()).getName();
            }

            int memberGroupId = event.getMemberGroupId();
            Member member = memberGroupService.queryById(memberGroupId);
            event.setMemberGroupName(member.getName());
            model.addAttribute("event", event);
            model.addAttribute("eventAttachmentName", eventAttachmentName);
        } catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "admin/event/prepare/detail";
    }

}
