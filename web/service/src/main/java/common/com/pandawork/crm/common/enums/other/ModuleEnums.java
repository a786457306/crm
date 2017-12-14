package com.pandawork.crm.common.enums.other;

/**
 * ModuleEnums
 *
 * @author: zhangteng
 * @time: 15/10/16 上午10:42
 */
public enum ModuleEnums {

    Null(""),

    // 后台
    Admin("Admin"),

    // 后台首页
    AdminIndex("Admin:Index"),

    // 超级管理
    AdminSAdmin("Admin:SAdmin"),

    // 权限
    AdminParty("Admin:SAdmin:Party"),
    AdminPartySecurity("Admin:SAdmin:Party:Security"),

    // 字典值管理
    AdminPartyDictionary("Admin:SAdmin:Party:Dictionary"),
    AdminPartyDictionaryList("Admin:SAdmin:Party:Dictionary:List"),
    AdminPartyDictionaryNew("Admin:SAdmin:Party:Dictionary:New"),
    AdminPartyDictionaryUpdate("Admin:SAdmin:Party:Dictionary:Update"),
    AdminPartyDictionaryDelete("Admin:SAdmin:Party:Dictionary:Delete"),
    // 基础权限管理
    AdminPartySecurityPermission("Admin:SAdmin:Party:Security:Permission"),
    AdminPartySecurityPermissionList("Admin:SAdmin:Party:Security:Permission:List"),
    AdminPartySecurityPermissionNew("Admin:SAdmin:Party:Security:Permission:New"),
    AdminPartySecurityPermissionUpdate("Admin:SAdmin:Party:Security:Permission:Update"),
    AdminPartySecurityPermissionDelete("Admin:SAdmin:Party:Security:Permission:Delete"),
    // 安全组管理
    AdminPartySecurityGroup("Admin:SAdmin:Party:Security:Group"),
    AdminPartySecurityGroupList("Admin:SAdmin:Party:Security:Group:List"),
    AdminPartySecurityGroupNew("Admin:SAdmin:Party:Security:Group:New"),
    AdminPartySecurityGroupUpdate("Admin:SAdmin:Party:Security:Group:Update"),
    AdminPartySecurityGroupDel("Admin:SAdmin:Party:Security:Group:Del"),
    AdminPartySecurityGroupPermission("Admin:SAdmin:Party:Security:Group:Permission"),
    AdminPartySecurityGroupPermissionList("Admin:SAdmin:Party:Security:Group:Permission:List"),
    AdminPartySecurityGroupPermissionNew("Admin:SAdmin:Party:Security:Group:Permission:New"),
    AdminPartySecurityGroupPermissionDelete("Admin:SAdmin:Party:Security:Group:Permission:Delete"),
    //会员组管理
    AdminPartyMemberGroup("Admin:SAdmin:Party:Member:Group"),
    AdminPartyMemberGroupList("Admin:SAdmin:Party:Member:Group:List"),
    AdminPartyMemberGroupNew("Admin:SAdmin:Party:Member:Group:New"),
    AdminPartyMemberGroupUpdate("Admin:SAdmin:Party:Member:Group:Update"),
    AdminPartyMemberGroupDel("Admin:SAdmin:Party:Member:Group:Del"),

    //积分转换管理
    AdminPartyPointsConvert("Admin:SAdmin:Party:PointsConvert"),
    AdminPartyPointsConvertList("Admin:SAdmin:Party:PointsConvert:List"),
    AdminPartyPointsConvertUpdate("Admin:SAdmin:Party:PointsConvert:Update"),

    // 用户管理
    AdminUserManagement("Admin:SAdmin:User:Management"),

    // 员工管理
    AdminUserManagementEmployee("Admin:SAdmin:User:Management:Employee"),
    AdminUserManagementEmployeeList("Admin:SAdmin:User:Management:Employee:List"),
    AdminUserManagementEmployeeUpdate("Admin:SAdmin:User:Management:Employee:Update"),
    AdminUserManagementEmployeeDelete("Admin:SAdmin:User:Management:Employee:Delete"),
    AdminUserManagementEmployeeNew("Admin:SAdmin:User:Management:Employee:New"),
    AdminUserManagementEmployeeDetail("Admin:SAdmin:User:Management:Employee:Detail"),
    AdminUserManagementEmployeeSetRole("Admin:SAdmin:User:Management:Employee:SetRole"),

    // 全局设置
    AdminConstant("Admin:Constant"),
    AdminConstantList("Admin:Constant:List"),
    AdminConstantUpdate("Admin:Constant:Update"),


    //患者基础信息管理
    AdminClientBasic("Admin:Client:Basic"),
    AdminClientBasicList("Admin:Client:Basic:List"),
    AdminClientBasicNew("Admin:Client:Basic:New"),
    AdminClientBasicDelete("Admin:Client:Basic:Delete"),
    AdminClientBasicUpdate("Admin:Client:Basic:Update"),
    AdminClientBasicDetail("Admin:Client:Basic:Detail"),
    AdminClientBasicAddMember("Admin:Client:Basic:AddMember"),

    //会员管理
    AdminClientMember("Admin:Client:Member"),
    AdminClientMemberList("Admin:Client:Member:List"),
    AdminClientMemberNew("Admin:Client:Member:New"),
    AdminClientMemberDelete("Admin:Client:Member:Delete"),
    AdminClientMemberUpdate("Admin:Client:Member:Update"),
    AdminClientMemberDetail("Admin:Client:Member:Detail"),

    //问卷管理
    AdminClientQuest("Admin:Client:Quest"),
    AdminClientQuestList("Admin:Client:Quest:List"),
    AdminClientQuestNew("Admin:Client:Quest:New"),
    AdminClientQuestDetail("Admin:Client:Quest:Detail"),

    //患者来访管理
    AdminClientVisit("Admin:Client:Visit"),
    AdminClientVisitList("Admin:Client:Visit:List"),
    AdminClientVisitDetailList("Admin:Client:Visit:DetailList"),
    AdminClientVisitDetailDelete("Admin:Client:Visit:DetailDelete"),
    AdminClientVisitDetailNew("Admin:Client:Visit:DetailNew"),
    AdminClientVisitDetailUpdate("Admin:Client:Visit:DetailUpdate"),

    //患者积分管理
    AdminClientPoints("Admin:Client:Points"),
    AdminClientPointsList("Admin:Client:Points:List"),
    AdminClientPointsDetailList("Admin:Client:Points:DetailList"),

    /***********************客户画像模块*********************/

    //标签类型
    AdminProfileLabelType("Admin:Profile:Label:Type"),
    AdminProfileLabelTypeList("Admin:Profile:Label:Type:List"),
    AdminProfileLabelTypeNew("Admin:Profile:Label:Type:New"),
    AdminProfileLabelTypeUpdate("Admin:Profile:Label:Type:Update"),
    AdminProfileLabelTypeDelete("Admin:Profile:Label:Type:Delete"),

    //标签项
    AdminProfileLabel("Admin:Profile:Label"),
    AdminProfileLabelItem("Admin:Profile:Label:Item"),
    AdminProfileLabelItemList("Admin:Profile:Label:Item:List"),
    AdminProfileLabelItemNew("Admin:Profile:Label:Item:New"),
    AdminProfileLabelItemUpdate("Admin:Profile:Label:Item:Update"),
    AdminProfileLabelItemDelete("Admin:Profile:Label:Item:Delete"),

    //画像管理
    AdminLabelType("Admin:Label:Type"),
    AdminLabelTypeList("Admin:Label:Type:List"),
    AdminLabelTypeNew("Admin:Label:Type:New"),
    AdminLabelTypeUpdate("Admin:Label:Type:Update"),
    AdminLabelTypeDelete("Admin:Label:Type:Delete"),

    //患者画像
    AdminProfilePortrayal("Admin:Profile:Portrayal"),
    AdminProfilePortrayalList("Admin:Profile:Portrayal:List"),
    AdminProfilePortrayalNew("Admin:Profile:Portrayal:New"),
    AdminProfilePortrayalUpdate("Admin:Profile:Portrayal:Update"),

    //行为分析
    AdminProfileAction("Admin:Profile:Action"),
    AdminProfileActionList("Admin:Profile:Action:List"),


    //统计分析
    AdminProfileStatistics("Admin:Profile:Statistics"),

    /***********************客户关怀模块*********************/
    AdminEvent("Admin:Event"),
    //待开展活动
    AdminEventPrepare("Admin:Event:Prepare"),
    AdminEventPrepareList("Admin:Event:Prepare:List"),
    AdminEventPrepareNew("Admin:Event:Prepare:New"),
    AdminEventPrepareUpdate("Admin:Event:Prepare:Update"),
    AdminEventPrepareDetail("Admin:Event:Prepare:Detail"),
    AdminEventPrepareLogout("Admin:Event:Prepare:Logout"),
    AdminEventPrepareApproval("Admin:Event:Prepare:Approval"),

    //进行中活动
    AdminEventProcessing("Admin:Event:Processing"),
    AdminEventProcessingList("Admin:Event:Processing:List"),
    AdminEventProcessingDetail("Admin:Event:Processing:Detail"),
    AdminEventProcessingPause("Admin:Event:Processing:Pause"),
    AdminEventProcessingBatchNotice("Admin:Event:Processing:BatchNotice"),
    AdminEventProcessingBatchPoints("Admin:Event:Processing:BatchPoints"),
    AdminEventProcessingHandle("Admin:Event:Processing:Handle"),
    AdminEventProcessingRecord("Admin:Event:Processing:Record"),

    //已归档活动
    AdminEventArchived("Admin:Event:Archived"),
    AdminEventArchivedList("Admin:Event:Archived:List"),
    AdminEventArchivedDetail("Admin:Event:Archived:Detail"),
    AdminEventArchivedDetailNotice("Admin:Event:Archived:Notice"),

    //模板管理
    AdminEventTemplate("Admin:Event:Template"),
    AdminEventTemplateList("Admin:Event:Template:List"),
    AdminEventTemplateNew("Admin:Event:Template:New"),
    AdminEventTemplateUpdate("Admin:Event:Template:Update"),
    AdminEventTemplateDetail("Admin:Event:Template:Detail"),
    AdminEventTemplateDelete("Admin:Event:Template:Delete"),

    //待办事务
    AdminEventAffairPrepare("Admin:Event:Affair:Prepare"),
    AdminEventAffairPrepareList("Admin:Event:Affair:Prepare:List"),

    //待办活动通知
    AdminEventNotice("Admin:Event:Notice"),
    AdminEventNoticeList("Admin:Event:Notice:List"),
    AdminEventNoticeDetail("Admin:Event:Notice:Detail")
    ;

    private String name;

    ModuleEnums(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
