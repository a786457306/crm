package com.pandawork.crm.web.controller.admin.client.visit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pandawork.core.common.exception.SSException;
import com.pandawork.core.common.log.LogClerk;
import com.pandawork.core.common.util.Assert;
import com.pandawork.crm.common.annotation.Module;
import com.pandawork.crm.common.dto.client.visit.VisitDetailSearchDto;
import com.pandawork.crm.common.dto.client.visit.VisitSearchDto;
import com.pandawork.crm.common.entity.client.basic.Client;
import com.pandawork.crm.common.entity.client.points.Points;
import com.pandawork.crm.common.entity.client.visit.Visit;
import com.pandawork.crm.common.entity.party.dictionary.Dictionary;
import com.pandawork.crm.common.enums.client.GenderEnums;
import com.pandawork.crm.common.enums.client.VisitFromEnums;
import com.pandawork.crm.common.enums.other.ModuleEnums;
import com.pandawork.crm.common.enums.party.dictionary.DictionaryEnums;
import com.pandawork.crm.common.utils.DataUtils;
import com.pandawork.crm.common.utils.DateUtils;
import com.pandawork.crm.common.utils.URLConstants;
import com.pandawork.crm.web.spring.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * VisitController
 * Author： liping
 * Date: 2017/7/24
 * Time: 9:49
 */
@Controller
@Module(ModuleEnums.AdminClientVisit)
@RequestMapping(value = URLConstants.ADMIN_CLIENT_VISIT_URL)
public class VisitController extends AbstractController{

    /**
     * 去列表页
     *
     * @param model
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitList)
    @RequestMapping(value = {"","list"},method = RequestMethod.GET)
    public String toList(Model model){
        //用户类型字典值
        String typeName = "";

        int dataCount = 0;
        try{
            dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE,visitService.count());
            List<Dictionary> clientTypeList = dictionaryService.listByPId(DictionaryEnums.DIC_CLIENT_TYPE.getId());
            model.addAttribute("dicTypeList",clientTypeList);
            model.addAttribute("dataCount",dataCount);
        }catch (SSException e){
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        return "admin/client/visit/list";
    }

    /**
     * 查询条件searchDto列表页
     *
     * @param searchDto
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitList)
    @RequestMapping(value = {"ajax/list"},method = RequestMethod.GET)
    @ResponseBody
    public JSON ajaxList(Model model,VisitSearchDto searchDto,HttpSession httpSession) {
        JSONObject json = new JSONObject();
        Integer pageSize = searchDto.getPageSize();
        pageSize = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        searchDto.setPageSize(pageSize);
        //根据page计算offset
        Integer pageNo = searchDto.getPage();
        int offset = 0, numCount = 0;
        if (Assert.isNotNull(pageNo)) {
            pageNo = pageNo <= 0 ? 0 : pageNo - 1;
            offset = pageNo * pageSize;
            searchDto.setOffset(offset);
        }
        JSONArray jsonArray = new JSONArray();
        //用户·性别枚举值
        String genderName = "";
        //用户类型字典值
        String typeName = "";
        List<Visit> visitList = Collections.emptyList();
        int dataCount = 0;
        //处理中文乱码
//        if(searchDto.getClientName() != null & !"".equals(searchDto.getClientName())){
//            try {
//                String str = new String((searchDto.getClientName()).getBytes("iso8859-1"),"utf-8");
//                searchDto.setClientName(str);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        //查询来访次数--默认算入来访信息初始化记录
        if(Assert.isNotNull(searchDto.getVisitTimes())){
            searchDto.setVisitTimes(searchDto.getVisitTimes() + 1);
        }
        try{
            int createdPartyId = DataUtils.objectToInt(httpSession.getAttribute("partyId"));
            //获取登录用户的id
            int userId = securityUserService.queryByPartyId(createdPartyId).getId();
            //根据用户id获取角色id
            int memberGroupId = 0;
            int securityGroupId = securityUserGroupService.queryByUserId(userId).getGroupId();
            if(securityGroupId == 1 || securityGroupId == 8){
                //若为超级管理员可查看所有
                searchDto.setMemberGroupId(null);
            }else{
                //若为其他包医人只可查看自己分组
                memberGroupId = employeeService.getGroupIdByPartyId(createdPartyId);
                searchDto.setMemberGroupId(memberGroupId);
            }
            visitList = visitService.listByVisitSearchDto(searchDto);
            for(int i = 0 ; i < visitList.size() ; i++){
                //设置患者枚举性别
                if(visitList.get(i).getGender() == 1){
                    genderName = GenderEnums.Male.getGender();
                }else if(visitList.get(i).getGender() == 2){
                    genderName = GenderEnums.Female.getGender();
                }else if(visitList.get(i).getGender() == 3){
                    genderName = GenderEnums.Other.getGender();
                }
                visitList.get(i).setGenderName(genderName);
                typeName = dictionaryService.queryById(visitList.get(i).getDicType()).getName();
                //设置患者类型字典值
                visitList.get(i).setTypeName(typeName);
            }
            int  i = 1 + pageNo * 10;
            String firstVisitType = "";
            String visitTime = "";
            String dicType = "";
            for(Visit visit : visitList){
                JSONObject jsonObject = new JSONObject();
                if(Assert.isNotNull(visit.getFirstVisitType())){
                    firstVisitType = visit.getFirstVisitType();
                }
                if(Assert.isNotNull(visit.getVisitTime())){
                    visitTime = DateUtils.formatDateSimple(visit.getVisitTime());
                }
                dicType = dictionaryService.queryById(visit.getDicType()).getName();
                jsonObject.put("number",i++);
                jsonObject.put("clientId", visit.getClientId());
                jsonObject.put("clientName", visit.getClientName());
                jsonObject.put("genderName",visit.getGenderName());
                jsonObject.put("clientIdcardNum",visit.getClientIdcardNum());
                jsonObject.put("clientTel",visit.getClientTel());
                jsonObject.put("clientType",dicType);
                jsonObject.put("firstVisitType",firstVisitType);
                jsonObject.put("visitTimes",visit.getVisitTimes()-1);
                jsonObject.put("visitTime",visitTime);
                jsonObject.put("cost",visit.getCost());
                jsonArray.add(jsonObject);
            }
            //计算总页数
            searchDto.setPageSize(null);
            List<Visit> list = Collections.emptyList();
            list = visitService.listByVisitSearchDto(searchDto);
            numCount = list.size();
            dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE,numCount);
            model.addAttribute("dataCount",dataCount);
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        json.put("code", AJAX_SUCCESS_CODE);
        json.put("list", jsonArray);
        json.put("dataCount", dataCount);
        json.put("numCount", numCount);
        return json;
    }

    /**
     * 姓名搜索框模糊查询
     *
     * @param clientName
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitList)
    @RequestMapping(value = {"ajax/clientName"},method = RequestMethod.GET)
    @ResponseBody
    public JSON searchByName(@RequestParam("clientName")String  clientName){
        JSONArray jsonArray = new JSONArray();
        VisitSearchDto searchDto = new VisitSearchDto();
        try{
            searchDto.setClientName(clientName);
            List<Visit> visitList = visitService.listByVisitSearchDto(searchDto);
            for(Visit visit : visitList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",visit.getClientId());
                jsonObject.put("clientName",visit.getClientName());
                jsonArray.add(jsonObject);
            }
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonArray(jsonArray);
    }

    /**
     * 身份证号搜索框模糊查询
     *
     * @param idcard
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitList)
    @RequestMapping(value = {"ajax/idcard"},method = RequestMethod.GET)
    @ResponseBody
    public JSON searchByIdCard(@RequestParam("idcard")String  idcard){
        JSONArray jsonArray = new JSONArray();
        VisitSearchDto searchDto = new VisitSearchDto();
        try{
            searchDto.setClientIdcardNum(idcard);
            List<Visit> visitList = visitService.listByVisitSearchDto(searchDto);
            for(Visit visit : visitList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",visit.getClientId());
                jsonObject.put("idcard",visit.getClientIdcardNum());
                jsonArray.add(jsonObject);
            }
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonArray(jsonArray);
    }

    /**
     * 电话搜索框模糊查询
     *
     * @param tel
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitList)
    @RequestMapping(value = {"ajax/tel"},method = RequestMethod.GET)
    @ResponseBody
    public JSON searchByTel(@RequestParam("tel")String  tel){
        JSONArray jsonArray = new JSONArray();
        VisitSearchDto searchDto = new VisitSearchDto();
        try{
            searchDto.setClientTel(tel);
            List<Visit> visitList = visitService.listByVisitSearchDto(searchDto);
            for(Visit visit : visitList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",visit.getClientId());
                jsonObject.put("tel",visit.getClientTel());
                jsonArray.add(jsonObject);
            }
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonArray(jsonArray);
    }

    /**
     * 去详情列表页
     *
     * @param model
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitDetailList)
    @RequestMapping(value = {"detailList"},method = RequestMethod.GET)
    public String toDetailList(Model model,@RequestParam("clientId")Integer clientId){
        int dataCount = 0;
        VisitDetailSearchDto searchDto = new VisitDetailSearchDto();
        searchDto.setClientId(clientId);
        try{
            dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE,visitService.countByVisitDetailSearchDto(searchDto));
        } catch (SSException e) {
            LogClerk.errLog.error(e);
            sendErrMsg(e.getMessage());
            return ADMIN_SYS_ERR_PAGE;
        }
        model.addAttribute("dataCount",dataCount);
        model.addAttribute("clientId",clientId);
        return "admin/client/visit/detailList";
    }

    /**
     * 详情页
     *
     * @param searchDto
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitDetailList)
    @RequestMapping(value = {"ajax/detailList"},method = RequestMethod.GET)
    @ResponseBody
    public JSON ajaxList(VisitDetailSearchDto searchDto) {
        JSONObject json = new JSONObject();
        Integer pageSize = searchDto.getPageSize();
        pageSize = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        searchDto.setPageSize(pageSize);
        Integer pageNo = searchDto.getPage();
        int offset = 0;
        if (Assert.isNotNull(pageNo)) {
            pageNo = pageNo <= 0 ? 0 : pageNo - 1;
            offset = pageNo * pageSize;
            searchDto.setOffset(offset);
        }
        if(searchDto.getVisitContent() != null & !"".equals(searchDto.getVisitContent())){
            try {
                String str = new String((searchDto.getVisitContent()).getBytes("iso8859-1"),"utf-8");
                searchDto.setVisitContent(str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        JSONArray jsonArray = new JSONArray();
        List<Visit> visitList = Collections.emptyList();
        int dataCount = 0, numCount = 0;
        String visitFromName = "";
        try{
            visitList = visitService.listByVisitDetailSearchDto(searchDto);
            int  i = 1;
            if(pageNo != null)
            i = 1 + pageNo * 10;
            for(Visit visit : visitList){
                JSONObject jsonObject = new JSONObject();
                if(Assert.isNotNull(visit.getVisitFrom())){
                    visitFromName = VisitFromEnums.valueOf(visit.getVisitFrom()).getVisitFrom();
                }
                jsonObject.put("number",i++);
                jsonObject.put("id",visit.getId());
                jsonObject.put("clientId", visit.getClientId());
                jsonObject.put("clientName", visit.getClientName());
                jsonObject.put("visitFrom",visitFromName);
                jsonObject.put("visitTime",visit.getVisitTime());
                jsonObject.put("visitContent",visit.getVisitContent());
                jsonObject.put("cost",visit.getCost());
                jsonObject.put("partyName",visit.getPartyName());
                jsonObject.put("lastModifiedTime",visit.getLastModifiedTime());
                jsonArray.add(jsonObject);
            }
            numCount = visitService.countByVisitDetailSearchDto(searchDto);
            dataCount = DataUtils.getPageCount(DEFAULT_PAGE_SIZE,numCount);
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        json.put("code", AJAX_SUCCESS_CODE);
        json.put("list", jsonArray);
        json.put("dataCount", dataCount);
        json.put("numCount", numCount);
        return json;
    }

    /**
     * 删除一条来访详情
     *
     * @param id
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitDetailDelete)
    @RequestMapping(value = "ajax/del",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject delVisitDetail(@RequestParam("id") Integer id){
        Visit visit = null;
        try{
            visit = visitService.queryById(id);
            visitService.delById(id);
            //修改患者来访总金额
            clientService.updateAllCost(visit.getClientId());
            Client client = clientService.queryById(visit.getClientId());
            //若为会员产生积分记录
            if(client.getIsMember() == 1){
                //删除积分修改积分
                Points points = new Points();
                points.setVisitId(id);
                pointsService.delPoints(points);
                //更新会员积分
                clientService.updateAllPointsAndMemberPoints(visit.getClientId());
            }
        }catch(SSException e){
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonObject(AJAX_SUCCESS_CODE);
    }

    /**
     * 添加来访详情页
     *
     * @param visit
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitDetailNew)
    @RequestMapping(value = {"ajax/add"},method = RequestMethod.POST)
    @ResponseBody
    public JSONObject ajaxAdd(Visit visit){
        Visit visit1 = null;
        try{
            //获得登录的partyId
            HttpSession session = getRequest().getSession();
            Object o = session.getAttribute("partyId");
            Integer partyId = new Integer(o.toString());
            visit.setCreatedPartyId(partyId);
            visitService.newVisit(visit);
            visit1 = visitService.queryById(visit.getId());
            //修改患者来访总金额
            clientService.updateAllCost(visit.getClientId());
            Client client = clientService.queryById(visit.getClientId());
            //若为会员产生积分记录
            if(client.getIsMember() == 1){
                //新增积分修改积分记录
                Points points = new Points();
                points.setVisitId(visit1.getId());
                points.setClientId(visit1.getClientId());
                int pointSize = visitService.moneyToPoints(visit1.getCost());
                points.setPointsSize(pointSize);
                points.setLastVisitDate(visit1.getVisitTime());
                points.setCreatedPartyId(partyId);
                //设置积分调整日期为当天
                points.setPointsDate(visit1.getVisitTime());
                //积分来源设置来访
                points.setPointsFrom(0);
                //产生积分记录
                pointsService.newPoints(points);
                //更新会员当前积分
                clientService.updateAllPointsAndMemberPoints(visit.getClientId());
            }
        }catch(SSException e) {
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonObject(AJAX_SUCCESS_CODE);
    }

    /**
     * 修改来访详情页
     *
     * @param visit
     * @return
     */
    @Module(ModuleEnums.AdminClientVisitDetailUpdate)
    @RequestMapping(value = {"ajax/update"},method = RequestMethod.POST)
    @ResponseBody
    public JSON ajaxUpdate(Visit visit){
        Visit visit1 = null;
        try{
            //获得登录的partyId
            HttpSession session = getRequest().getSession();
            Object o = session.getAttribute("partyId");
            Integer partyId = new Integer(o.toString());
            visit.setCreatedPartyId(partyId);
            visitService.updateVisit(visit);
            visit1 = visitService.queryById(visit.getId());
            //修改患者来访总金额
            clientService.updateAllCost(visit1.getClientId());
            Client client = clientService.queryById(visit1.getClientId());
            //若为会员产生积分记录
            if(client.getIsMember() == 1) {
                //修改积分
                Points points = new Points();
                points.setVisitId(visit1.getId());
                points.setLastVisitDate(visit1.getVisitTime());
                int pointSize = visitService.moneyToPoints(visit1.getCost());
                points.setPointsSize(pointSize);
                points.setPointsDate(visit.getVisitTime());
                pointsService.updatePoints(points);
                //更新会员当前积分
                clientService.updateAllPointsAndMemberPoints(visit1.getClientId());
            }
        }catch(SSException e) {
            LogClerk.errLog.error(e);
            return sendErrMsgAndErrCode(e);
        }
        return sendJsonObject(AJAX_SUCCESS_CODE);
    }
}
