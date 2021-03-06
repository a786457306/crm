<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.pandawork.net/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>积分管理详情</title>
    <jsp:include flush="true" page="/WEB-INF/views/admin/common/head.jsp"></jsp:include>
</head>
<body>
<jsp:include flush="true" page="/WEB-INF/views/admin/common/header.jsp"></jsp:include>
<div class="page clearfix">
    <div class="holder">
        <div class="container">
            <div class="row">
                <div class="col-sm-12 ">
                    <ol class="breadcrumb">
                        <li><a href="/admin"><i class="fa fa-home"></i>&nbsp;首页</a></li>
                        <li><a href="/admin/client/basic">患者管理</a></li>
                        <li class="active">积分管理详情</li>
                    </ol>
                    <%--<div class="alert alert-success J_tip" role="alert">刷新成功!</div>--%>
                    <input type="hidden"  class="clientId" name="clientId" value="${clientId}">
                    <input type="hidden" class="pageDataCount" name="pageDataCount" value="${dataCount}">
                </div>
                <div class="col-sm-12 margin-top--10">
                    <form class="form-horizontal J_searchForm">
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <h4>搜索</h4>
                            </div>
                            <div class="panel-body">        
                                <div class="form-group col-sm-12" >
                                    <label class="col-sm-1 control-label">积分类型</label>
                                    <div class="col-sm-2">
                                        <select id="integralType" class="form-control J_selectName w180 " name="pointsFrom">
                                            <option value="">请选择</option>
                                            <option value="0">消费类型</option>
                                            <option value="1">活动类型</option>
                                            
                                        </select>
                                    </div>
                                    <label class="col-sm-2 control-label">消费积分</label>
                                    <div class="col-sm-2">
                                        <input id="J_point" class="form-control w180" name="pointsSize">
                                    </div>
                                </div>
                                <div class="form-group col-sm-12" >
                                    <label class="col-sm-1 control-label">关联活动</label>
                                    <div class="col-sm-2">
                                        <input id="J_activity" class="form-control  w180 " name="eventName" >
                                    </div>
                                    <label class="col-sm-2 control-label">积分日期</label>
                                    <div class="col-sm-7">
                                        <input id="start" name="startDate" readonly="readonly" class="Wdate w200" type="text" onclick="WdatePicker({maxDate:'#F{$dp.$D(\'end\')}'})"/>
                                        <span class="to">~</span>
                                        <input id="end" name="endDate" readonly="readonly" class="Wdate w200" type="text" onclick="WdatePicker({minDate:'#F{$dp.$D(\'start\')}'})"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12 margin-bottom-10 margin-top--10 padding-left">
                            <shiro:checkPermission name="Admin:Client:Points:DetailList">
                                <button type="submit"  class="btn-sm btn-success J_search"><i class="fa fa-search"></i>&nbsp;查询</button>
                            </shiro:checkPermission>
                        </div>
                    </form>
                </div>
                <div class="col-sm-12">
                    <div class="panel panel-info">
                        <!--   <div class="panel-heading">
                              <h4>积分列表</h4>
                          </div> -->
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-hover table-bordered">
                                    <thead>
                                    <tr>
                                        <th>积分日期</th>
                                        <th>积分类型</th>
                                        <th>消费积分</th>
                                        <th>当前积分</th>
                                        <th>积分累计</th>
                                        <th>关联活动名称</th>

                                    </tr>
                                    </thead>
                                    <tbody id="J_template">
                                    <!-- <tr data-id="1">
                                        <td>1</td>
                                        <td>2017.7.20</td>
                                        <td>消费积分</td>
                                        <td>300</td>
                                        <td>12345</td>
                                        <td>15222</td>
                                        <td>现金消费基本礼物兑换</td>
                                    </tr>
                                    <tr data-id="2">
                                        <td>2</td>
                                        <td>2017.7.20</td>
                                        <td>消费积分</td>
                                        <td>300</td>
                                        <td>12345</td>
                                        <td>15222</td>
                                        <td>现金消费基本礼物兑换</td>
                                    </tr> -->
                                    </tbody>
                                </table>
                                <div class="pull-right">
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination" id="pageLimit">
                                            <li>
                                                <a href="#" aria-label="Previous">
                                                    <span aria-hidden="true">&laquo;</span>
                                                </a>
                                            </li>
                                            <li class="active"><a href="#">1</a></li>
                                            <li><a href="#">2</a></li>
                                            <li><a href="#">3</a></li>
                                            <li><a href="#">4</a></li>
                                            <li><a href="#">5</a></li>
                                            <li>
                                                <a href="#" aria-label="Next">
                                                    <span aria-hidden="true">&raquo;</span>
                                                </a>
                                            </li>
                                        </ul>
                                    </nav>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="/js/client-management/accumlate-points-management/accumlate-points-management-details.js"></script>
<script src="/js/tool/calendar/WdatePicker.js"></script>
</body>
</html>
