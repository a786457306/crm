<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>角色权限管理</title>
    <jsp:include flush="true" page="/WEB-INF/views/admin/common/head.jsp"></jsp:include>
</head>
<body>
<jsp:include flush="true" page="/WEB-INF/views/admin/common/header.jsp"></jsp:include>
<div class="page clearfix">
    <div class="holder">
        <div class="container">
            <div class="row">
                <div class="col-sm-12">
                    <ol class="breadcrumb">
                        <li>
                            <a href="/admin"><i class="fa fa-home"></i>&nbsp;首页</a>
                        </li>
                        <li>
                            <a href="/admin/party/security/group/list">系统管理</a>
                        </li>
                        <li class="active">角色管理</li>
                    </ol>
                    <%--<div class="alert alert-success J_tip hidden" role="alert">刷新成功!</div>--%>
                    <%--<input type="hidden" class="pageDataCount" value="${dataCount}">--%>
                </div>
                <div class="col-sm-12">
                    <form action="" class="J_form">
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <h4>角色列表</h4>
                            </div>
                            <div class="panel-body">
                                <a href="#" class="btn btn-success margin-bottom-15 J_add" data-toggle="modal" data-target="#addDialog"><i class="fa fa-plus"></i>&nbsp;添加角色</a>
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <th>角色名称</th>
                                            <th>描述</th>
                                            <th>操作</th>
                                        </tr>
                                        </thead>
                                        <tbody id="J_template">

                                        </tbody>
                                    </table>
                                </div>
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
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include flush="true" page="/WEB-INF/views/admin/common/footer.jsp"></jsp:include>
<!-- 存储id，表达式，描述 -->
<input class="hidId" type="hidden">
<!-- <input class="hidExp" type="hidden"></input>
<input class="hidDes" type="hidden"></input> -->
<!-- 操作失败对话框 -->
<div class="modal fade" id="modalDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示信息</h4>
            </div>
            <div class="modal-body">
                <p>操作失败，请重新操作!</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-success" data-dismiss="modal">确定</button>
            </div>
        </div>
    </div>
</div>
<!-- 删除对话框 -->
<div class="modal fade" id="delDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">提示信息</h4>
            </div>
            <div class="modal-body">
                <p>确定删除该权限吗？</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success J_delDlg">确定</button>
            </div>
        </div>
    </div>
</div>
<!-- 编辑对话框 -->
<div class="modal fade" id="editDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">编辑角色</h4>
            </div>
            <div class="modal-body">
                <form action="" class="form-horizontal J_editForm">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">角色名称</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control J_editexpInput" name="express" readonly>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">描述</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control J_editdesInput" name="description">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success J_editDlg">确定</button>
            </div>
        </div>
    </div>
</div>
<!-- 添加对话框 -->
<div class="modal fade" id="addDialog" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">添加角色</h4>
            </div>
            <div class="modal-body">
                <form action="" class="form-horizontal J_addForm">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">角色名称</label>
                        <div class="col-sm-6">
                            <select class="select form-control J_addexpInput" name="express">
                                <c:forEach var="list" items="${dictionaryList}">
                                    <option value="${list.name}">${list.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">描述</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control J_adddesInput" name="description">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-success J_addDlg">确定</button>
            </div>
        </div>
    </div>
</div>
<script src="/js/authority-management/security-group-management.js"></script>
</body>
</html>