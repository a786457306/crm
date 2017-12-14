package com.pandawork.crm.web.controller.admin.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.annotation.Module;
import com.pandawork.crm.common.dto.event.PrepareNoticeSearchDto;
import com.pandawork.crm.common.entity.event.EventTerm;
import com.pandawork.crm.common.enums.other.ModuleEnums;
import com.pandawork.crm.common.utils.DataUtils;
import com.pandawork.crm.common.utils.URLConstants;
import com.pandawork.crm.service.event.prepareNotice.PrepareNoticeService;
import com.pandawork.crm.web.spring.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * PrepareNoticeController
 *
 * @author Daydreamer
 * @date 2017/8/1 9:18
 */
@Controller
@Module(ModuleEnums.AdminEventNotice)
@RequestMapping(value = URLConstants.ADMIN_EVENT_PREPARE_NOTICE_URL)
public class PrepareNoticeController extends AbstractController{

    private int pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 去列表页
     *
     * @param model
     * @return
     */

    @Module(value = ModuleEnums.AdminEventNotice, extModule = ModuleEnums.AdminEventNoticeList)
    @RequestMapping(value ={"", "list"}, method= RequestMethod.GET)
    public String toList(Model model){
        List<EventTerm> eventTerms = Collections.emptyList();
        int dataCount;
        try {
            int count = prepareNoticeService.countAll();
            dataCount = DataUtils.getPageCount(pageSize, count);
            eventTerms = prepareNoticeService.listAll();
            model.addAttribute("dataCount", dataCount);
            model.addAttribute(eventTerms);
        }catch (SSException e){
            LogClerk.errLog.error(e);
        }
        return "admin/event/prepareNotice/list";
    }

    /**
     * 根据条件查询待通知事务列表
     *
     * @param prepareNoticeSearchDto
     * @return
     */
    @Module(value = ModuleEnums.AdminEventNotice, extModule = ModuleEnums.AdminEventNoticeList)
    @RequestMapping(value = "ajax/list", method = RequestMethod.GET)
    @ResponseBody
    public JSON listNotice(PrepareNoticeSearchDto prepareNoticeSearchDto){
        JSONObject json = new JSONObject();
        List<EventTerm> eventTerms = Collections.emptyList();
        int offset = 0, pageNo = 1;

        if (Assert.isNotNull(prepareNoticeSearchDto.getPageNo())){
            pageNo = prepareNoticeSearchDto.getPageNo() <=0 ? 0 : prepareNoticeSearchDto.getPageNo() - 1;
            offset = pageNo * DEFAULT_PAGE_SIZE;
        }
        prepareNoticeSearchDto.setPageSize(pageSize);
        prepareNoticeSearchDto.setPageNo(pageNo);
        prepareNoticeSearchDto.setOffset(offset);
        int dataCount = 0;
        try {
            int count = prepareNoticeService.countPrepareNotice(prepareNoticeSearchDto);
            dataCount = DataUtils.getPageCount(pageSize, count);
            eventTerms = prepareNoticeService.listBySearchDto(prepareNoticeSearchDto);
            int i = 1 + pageNo * DEFAULT_PAGE_SIZE;
            JSONArray jsonArray = new JSONArray();
            for (EventTerm eventTerm : eventTerms){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", eventTerm.getId());
                jsonObject.put("num", i++);
                jsonObject.put("name", eventTerm.getName());
                jsonObject.put("type", eventTerm.getType());
                String typea = "";
                if (eventTerm.getType().equals(1)){
                    typea = "会员关怀型";
                }
                if (eventTerm.getType().equals(2)){
                    typea = "营销型";
                }
                jsonObject.put("typea",typea);

                String levela = "";
                if (eventTerm.getLevel().equals(1)){
                    levela = "一级";
                }
                if (eventTerm.getLevel().equals(2)){
                    levela = "二级";
                }
                if (eventTerm.getLevel().equals(3)){
                    levela = "三级";
                }
                jsonObject.put("level", eventTerm.getLevel());
                jsonObject.put("levela",levela);

                Date startTime = eventTerm.getStartDate();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String startDate = formatter.format(startTime);

                jsonObject.put("startDate", startDate);
                jsonObject.put("memberGroup", eventTerm.getMemberGroupName());
                jsonObject.put("countPeople", eventTerm.getToBeNoticedPerson());
                jsonObject.put("eventId", eventTerm.getEventId());
                jsonArray.add(jsonObject);
            }
            json.put("code", AJAX_SUCCESS_CODE);
            json.put("list", jsonArray);
            json.put("dataCount", dataCount);
            json.put("numCount", count);
        }catch (SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return json;
    }
}
