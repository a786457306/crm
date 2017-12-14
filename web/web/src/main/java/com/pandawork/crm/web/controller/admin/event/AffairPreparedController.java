package com.pandawork.crm.web.controller.admin.event;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.annotation.Module;
import com.pandawork.crm.common.entity.event.Event;
import com.pandawork.crm.common.entity.party.group.employee.Employee;
import com.pandawork.crm.common.entity.party.security.SecurityUserGroup;
import com.pandawork.crm.common.enums.other.ModuleEnums;
import com.pandawork.crm.common.utils.DataUtils;
import com.pandawork.crm.common.utils.URLConstants;
import com.pandawork.crm.service.event.AffairPreparedService;
import com.pandawork.crm.service.party.security.SecurityUserGroupService;
import com.pandawork.crm.web.spring.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

/**
 * AffairPreparedController
 * Author： linjie
 * Date: 2017/7/29
 * Time: 17:27
 */
@Controller
@Module(ModuleEnums.AdminEvent)
@RequestMapping(value = URLConstants.ADMIN_EVENT_AFFAIR_PREPARED_URL)
public class AffairPreparedController extends AbstractController{

    /**
     * 去列表页
     *
     * @param model
     * @param httpSession
     * @return
     */
    @Module(value = ModuleEnums.AdminEventAffairPrepare, extModule = ModuleEnums.AdminEventAffairPrepareList)
    @RequestMapping(value = {"","list"},method = RequestMethod.GET)
    public String toList(Model model, HttpSession httpSession){
        int partyId = 0;
        int pageCount = 0;
        try{
            partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
            //根据partyId获取用户Id
            Employee employee = employeeService.queryByPartyId(partyId);
            int userId = employee.getId() ;
            //根据用户Id获取分组角色列表
            List<SecurityUserGroup> securityUserGroupList = securityUserGroupService.listByUserId(userId);
            //遍历并判断用户所拥有权限
            for(SecurityUserGroup securityUserGroup:securityUserGroupList) {
                //慢性主管
                if (securityUserGroup.getGroupId() == 4) {
                    pageCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, affairPreparedService.countToApproval(partyId));
                }else if(securityUserGroup.getGroupId() == 9) {
                    //包医人
                    pageCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, affairPreparedService.countRejected(partyId));
                }
            }
            model.addAttribute("pageCount",pageCount);
        }catch(Exception e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "/admin/event/affair/prepare/list";
    }
    /**
     * ajax根据分页获取事务
     *
     * @param curPage
     * @return
     */
    @Module(value = ModuleEnums.AdminEventAffairPrepare, extModule = ModuleEnums. AdminEventAffairPrepareList)
    @RequestMapping(value = "ajax/list", method = RequestMethod.GET)
    @ResponseBody
    public JSON ajaxListEmployeeInfo(@RequestParam("page") Integer curPage,
                                     HttpSession httpSession){
        JSONObject json = new JSONObject();
        List<Event> eventList = Collections.emptyList();
        JSONArray jsonArray = new JSONArray();
        int dataCount = 0 , numCount = 0;
        int offSet = 0 ;
        int partyId;
        int userId;
        try{
            if(Assert.isNull(curPage)){
                offSet = 0;
            }else{
                //查询当前页第一个游标值
                offSet = (curPage-1) * DEFAULT_PAGE_SIZE;
            }
            partyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
            //根据partyId获取用户Id
            Employee employee = employeeService.queryByPartyId(partyId);
            userId = employee.getId() ;
            //根据用户Id获取分组角色列表
            List<SecurityUserGroup> securityUserGroupList = securityUserGroupService.listByUserId(userId);
            //遍历并判断用户所拥有权限
            for(SecurityUserGroup securityUserGroup:securityUserGroupList) {
                //慢性主管
                if (securityUserGroup.getGroupId() == 4) {
                    numCount = affairPreparedService.countToApproval(partyId);
                    dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, numCount);
                    eventList = affairPreparedService.listAllToApprovalByPage(partyId, offSet, DEFAULT_PAGE_SIZE);
                    affairPreparedService.dateConvert(eventList);
                }else if(securityUserGroup.getGroupId() == 9) {
                    //包医人
                    numCount = affairPreparedService.countRejected(partyId);
                    dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE, numCount);
                    eventList = affairPreparedService.listAllRejectedByPage(partyId, offSet, DEFAULT_PAGE_SIZE);
                    affairPreparedService.dateConvert(eventList);
                }
            }
        }catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        //序号
        int  i = 1 +( curPage - 1 ) * 10;
        for(Event event : eventList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("number", i++);
            jsonObject.put("createdTime",event.getDate() );
            int approvalStatus = event.getApprovalStatus();
            //判断类型，以对应的value值显示
            if(approvalStatus == 1){
                jsonObject.put("approvalStatus", "待审批活动");
            }else if(approvalStatus == 3){
                jsonObject.put("approvalStatus", "审批被驳回");
            }
            jsonObject.put("name",event.getName());
            jsonArray.add(jsonObject);
        }
        json.put("code", AJAX_SUCCESS_CODE);
        json.put("list", jsonArray);
        json.put("dataCount", dataCount);
        json.put("numCount", numCount);
        return json;
    }
}
