/*-----------------------------------------------------------------------------
* @Description:     进行中的活动通知页
* @Version:         1.0.0
* @author:          zhangfc(546641398@qq.com)
* @date             2017.7.28
---------------------------------------------------------*/
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
    /**
     * 分页刷数据
     */
    function Pagination(page,extraData){

        var
            currentPage = page,
            str = '',
            data = {
                pageNo: currentPage
            };
        jQuery.extend(data, extraData);
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/list",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: data,        //JSON.stringify
            dataType: "json",
            success: function (rs) {
                $('#J_template').empty();
                if( rs.code == 0){
                    $.each(rs.list, function(index, item){
                       // str += '<tr data-id="'+ item.id +'"> <td>'+ item.id +'</td> <td>'+ item.dic_quest_item +'</td> <td>'+ item.quest_content +'</td> <td>'+ item.start_data +'</td> <td>'+ item.end_data +'</td> </tr>' 
                    str += '<tr data-id="'+ item.id +'">\
                                    <td>'+ item.eventName +'</td>\
                                    <td>'+ item.eventType +'</td>\
                                    <td>'+ item.eventLevel +'</td>\
                                    <td>'+ item.eventStatus+'</td>\
                                    <td>'+ item.BQStartDate +'</td>\
                                    <td>'+ item.memberName +'</td>\
                                    <td>'+ item.createdPartyName +'</td>\
                                    <td>'+ new Date(item.createdTime).format("yyyy-MM-dd hh:mm:ss")+'</td>\
                                    <td>\
                                        <a href="/admin/event/archived/detail?id='+ item.id +'" class="label-info J_details"><i class="fa fa-book"></i>&nbsp;详情</a>\
                                    </td>\
                                </tr>'

                    }); 
                    $('#J_template').append(str);
                    //暂无数据提示
                    var tr = $('#J_template').children();
                    if(tr.length == 0){
                        $('#J_template').append('<tr><td colspan="9">暂无数据</td></tr>');
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
    $('#editable-select3').editableSelect3({
        effects: 'slide'  
    });
    $('#html1').editableSelect1();
    $('#html2').editableSelect2();
    $('#html3').editableSelect3();

    /**
     * 姓名-模糊匹配-keyup事件
     */
    $('.J_selectName').keyup(function(){
        var
            createdPartyName = $('input.J_selectName').val();

        $(".es-list1").empty();     
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/searchByName",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {createdPartyName: createdPartyName},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                    li = "";

                $(rs.list).each(function(key, item){
                    li +='<li class="es-visible" style="display: block;">' + item.createdPartyName + '</li>';
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
        var
            idcardNum = $('input.J_selectId').val();

        $(".es-list2").empty();     
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/searchByIdcard",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {idcardNum: idcardNum},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                    li = "";

                if(rs.code == 0) {
                    $(rs.list).each(function (key, item) {
                        li += '<li class="es-visible" style="display: block;">' + item + '</li>';
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
     * 活动名称-模糊匹配-keyup事件
     */
    $('.J_selectActivity').keyup(function(){
        var
            eventName = $('input.J_selectActivity').val();

        $(".es-list3").empty();     
        $.ajax({
            type: "GET",
            url: "/admin/event/archived/ajax/searchByEventName",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {eventName: eventName},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                    li = "";

                $(rs.list).each(function(key, item){
                    li +='<li class="es-visible" style="display: block;">' + item.eventName + '</li>';
                    // console.log(li);
                });
                $(".es-list3").append(li);
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

        console.log(form);
        Pagination(1, form);
    });
})