$(function(){

    /**
     * 初始化提示信息、验证表单
     */
    showTip();
    Pagination(1,{});

    /**
     * 取消提示
     */
    function showTip(){
        setTimeout(function(){
            $('.J_tip').hide();
        }, 2000);
    }

    /**
     * 分页
     */
    $('#pageLimit').bootstrapPaginator({
        size: "small",
        bootstrapMajorVersion: 3,
        alignment: "right",
        numberOfPages: 5,
        itemTexts: function (type, page, current) {
            switch (type) {
                case "first": return "首页";
                case "prev": return "<";
                case "next": return ">";
                case "last": return "末页";
                case "page": return page;
            }
        },
        onPageClicked: function (event, originalEvent, type, page) {
            Pagination(page);
        }
    });

    /**
     * 分页刷数据
     */
    function Pagination(page){
        var
            currentPage = page,
            str = '';
        $.ajax({
            type: "GET",
            url: "/admin/event/affair/prepare/ajax/list",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {page: currentPage},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                $('#J_template').empty();
                if( rs.code == 0){
                    $.each(rs.list, function(index, item){
                        str += '<tr>\
                                    <td>'+ item.number +'</td>\
                                    <td>'+ item.createdTime +'</td>\
                                    <td>'+ item.approvalStatus +'</td>\
                                    <td>'+ item.name +'</td>\
                                    <td>\
                                           <a href="#" class="label-info J_deal" data-target="#dealDialog"><i class="fa fa-edit"></i>&nbsp;处理</a>\
                                    </td>\
                                </tr>'
                    });
                    $('#J_template').append(str);
                    //暂无数据提示
                    var tr = $('#J_template').children();
                    if(tr.length == 0){
                        $('#J_template').append('<tr><td colspan="6">暂无数据</td></tr>');
                    }
                }else{
                    Alert("提示信息","数据刷新失败！");
                }
                $('#pageLimit').bootstrapPaginator({totalPages: rs.dataCount});
            },
            error: function (message) {
                Alert("提示信息","数据刷新失败！");
            }
        });
    }

    /**
     * 表格处理按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $(document).on('click', '.J_deal', function(e){
        var
            tr = $(e.target).parents('tr'),
            id = $(tr).attr('data-id');
        $('#dealDialog').modal();
        $('.hidId').val(id);
    });

    /**
     * 对话框确定按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $('.J_dealDlg').click(function(){
        del();
    });

    /**
     * 确定事件
     */
    function del(){
        var
            id = $('.hidId').val(),
            dealId = {
                id: id
            };

        $.ajax({
            type: "GET",
            url: jQuery.url.ECRBManagement.delBacklog,
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: dealId,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                $('#dealDialog').modal('hide');
                if( rs.code == 0){
                    // console.log(1);
                    location.reload();
                }
                else{
                    $('#modalDialog').modal();
                }
            },
            error: function (message) {
                $('#delDialog').modal('hide');
                $('#modalDialog').modal();
            }
        });
    }

});