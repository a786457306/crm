/*-----------------------------------------------------------------------------
* @Description:     已归档活动详情页
* @Version:         1.0.0
* @author:          zhangfc(546641398@qq.com)
* @date             2017.7.28
---------------------------------------------------------*/
$(function(){

	var eventId = $(".eventId").val();
	Pagination(1,{id:eventId});

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
    function Pagination(page,extraData){

        var
            currentPage = page,            
            str = '',
            data = {
                pageNo: currentPage,
                id : $(".eventId").val()
            };
        jQuery.extend(data, extraData);

        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/detailList",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: data,     //JSON.stringify
            dataType: "json",
            cache:false,
            success: function (rs) {
                $('#J_template').empty();
                if( rs.code == 0){  
                    $('.J_record').html(rs.numCount);
                    $.each(rs.list, function(index, item){
                        str += '<tr data-id="'+ item.id +'">\
                                    <td>'+ item.name +'</td>\
                                    <td>'+ item.idcard +'</td>\
                                    <td>'+ item.tel +'</td>\
                                    <td>'+ item.noticeStatus+'</td>\
                                    <td>'+ item.BQStartDate +'</td>\
                                    <td>\
                                        <a href="/admin/event/processing/current/participant?id='+ item.id +'" class=" J_participation" > '+ item.eventPaticipant +'</a>\
                                    </td>\
                                    <td>'+ item.eventPaticipantStatus +'</td>\
                                    <td>\
                                        <a href="/admin/event/processing/total/participant?id='+ item.id +'" class=" J_totalParticipation" > '+ item.accJoinSituation +'</a>\
                                    </td>\
                                </tr>'
                    });
                    $('#J_template').append(str);
                    //暂无数据提示
                    var tr = $('#J_template').children();
                    if(tr.length == 0){
                        $('#J_template').append('<tr><td colspan="8">暂无数据</td></tr>');
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
     * 模糊匹配-可输入也可下拉选择
     */
    $('#editable-select1').editableSelect1({
        effects: 'slide'  
    });
    $('#editable-select2').editableSelect2({
        effects: 'slide'  
    });
    $('#html1').editableSelect1();
    $('#html2').editableSelect2();

    /**
     * 姓名-模糊匹配-keyup事件
     */
    $('.J_selectName').keyup(function(){
        var param = {
            name : $('input.J_selectName').val(),
            id  : $('.eventId').val()
        };


        $(".es-list1").empty();     
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/searchByParticipantName",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: param,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                    li = "";

                $(rs.list).each(function(key, item){
                    li +='<li class="es-visible" style="display: block;">' + item.name + '</li>';                   
                    // console.log(li);
                });
                $(".es-list1").append(li);
            },
            error: function (errMsg) {
                console.log(errMsg);
            }
        });
    });

    /**
     * 身份证-模糊匹配-keyup事件
     */
    $('.J_selectId').keyup(function(){
        var param = {
                idcard : $('input.J_selectId').val(),
                id  : $('.eventId').val()
            };


        $(".es-list2").empty();     
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/searchByParticipantIdcard",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: param,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                    li = "";
                if(rs.code == 0){
                    $(rs.list).each(function(key, item){
                        li +='<li class="es-visible" style="display: block;">' + item + '</li>';
                        // console.log(li);
                    });
                    $(".es-list2").append(li);
                }
            },
            error: function (errMsg) {
                console.log(errMsg);
            }
        });
    });

    /**
     * 列表点击搜索事件
     * @param  {[type]} ){                     }
     * @return {[type]}     [description]
     */
    $(".J_search").click(function(){
        var
            form = $(".J_searchForm").serializeObject();

        Pagination(1, form);
    });

});