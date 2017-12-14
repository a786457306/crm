/*-----------------------------------------------------------------------------
* @Description:     活动管理-进行中的活动
* @Version:         1.0.0
* @author:          gts(616603151@qq.com)
* @date             2017.7.24
* ==NOTES:=============================================
*
* ---------------------------------------------------------------------------*/
$(document).ready(function() {
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
     * 模糊匹配-可输入也可下拉选择（引用插件）
     * @type {String}
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
     * 姓名-模糊匹配keyup事件——下拉框
     * @param  {[type]} ){                                          var            name [description]
     * @param  {[type]} dataType: "json"        [description]
     * @param  {String} success:  function      (rs)          {                                       var                                       li [description]
     * @param  {[type]} error:    function      (errMsg)      {                                       console.log(errMsg);            }                        } [description]
     * @return {[type]}           [description]
     */
    $('.J_selectName').keyup(function(){
    	var
    		name = $('input.J_selectName').val();

    	$(".es-list1").empty();
    	$.ajax({
            type: "GET",
            // url: jQuery.url.ClientManagement.selectNameData,
            url: "/admin/event/processing/ajax/searchByName",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {name: name},     //JSON.stringify
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
     * 身份证-模糊匹配keyup事件——下拉框
     * @param  {[type]} ){                                          var            idcard [description]
     * @param  {[type]} dataType: "json"        [description]
     * @param  {String} success:  function      (rs)          {                                         var                                       li [description]
     * @param  {[type]} error:    function      (errMsg)      {                                         console.log(errMsg);            }                        } [description]
     * @return {[type]}           [description]
     */
    $('.J_selectId').keyup(function(){
    	var
    		idcard = $('input.J_selectId').val();

    	$(".es-list2").empty();
    	$.ajax({
            type: "GET",
            // url: jQuery.url.ClientManagement.selectIdData,
            url: "/admin/event/processing/ajax/searchByIdcard",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {idcardNum: idcard},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                	li = "";

                $(rs.list).each(function(key, item){
                	li +='<li class="es-visible" style="display: block;">' + item.idcardNum + '</li>';
                	// console.log(li);
                });
                $(".es-list2").append(li);
            },
            error: function (errMsg) {
                console.log(errMsg);
            }
        });
    });

    /**
     * 活动名称-模糊匹配keyup事件——下拉框
     * @param  {[type]} ){                                          var            activityName[description]
     * @param  {[type]} dataType: "json"        [description]
     * @param  {String} success:  function      (rs)          {                                        var                                       li [description]
     * @param  {[type]} error:    function      (errMsg)      {                                        console.log(errMsg);            }                        } [description]
     * @return {[type]}           [description]
     */
    $('.J_selectActivity').keyup(function(){
    	var
            eventName= $('input.J_selectActivity').val();

    	$(".es-list3").empty();
    	$.ajax({
            type: "GET",
            // url: jQuery.url.ECRBManagement.selectActivityName,
            url: "/admin/event/processing/ajax/searchByEventName",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {name: eventName},     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                var
                	li = "";

                $(rs.list).each(function(key, item){
                	li +='<li class="es-visible" style="display: block;">' + item.name+ '</li>';
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
     * 点击搜索事件
     * @param  {[type]} ){                     search();    } [description]
     * @return {[type]}     [description]
     */

    $(".J_search").click(function(){
        var
            form = $(".J_searchForm").serializeObject();

        Pagination(1, form);

    });
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
            var
                form = $(".J_searchForm").serializeObject();

            Pagination(page, form);
        }
    });

    /**
     * 分页刷数据
     */
    function Pagination(page, extraData){

        var
            currentPage = page,
            str = '',
            data = {
                pageNo: currentPage
            };
        jQuery.extend(data, extraData);

        $.ajax({
            type: "POST",
            // url: jQuery.url.ECRBManagement.searchActivityIng,
            url: "/admin/event/processing/ajax/list",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: data,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                $('#J_template').empty();
                if( rs.code == 0){
                    $.each(rs.list, function(index, item){
                        str += '<tr data-id="'+ item.id +'">\
                                    <td>'+ item.name+'</td>\
                                    <td>'+ item.typeValue +'</td>\
                                    <td>'+ item.levelValue+'</td>\
                                    <td>'+ item.statusValue +'</td>\
                                    <td>\
                                      <a href="/admin/event/processing/toNotice?eventTermId='+ item.id +'">'+ item.toBeNoticedPerson +' </a>\
                                    </td>\
                                    <td>'+ new Date(item.startDate).format("yyyy-MM-dd") +'</td>\
                                    <td>'+ item.memberGroupName +'</td>\
                                    <td>'+ new Date(item.eventEndDate).format("yyyy-MM-dd") +'</td>\
                                    <td>'+ item.toBeFinishedPerson +'</td>\
                                    <td>'+ item.createdPartyName +'</td>\
                                    <td>'+ new Date(item.createdTime).format("yyyy-MM-dd hh:mm:ss") +'</td>\
                                    <td>\
                                        <shiro:checkPermission name="Admin:Event:Processing:Detail">\
                                 		<a href="/admin/event/processing/detail?eventTermId='+ item.id +'" class="label-info"><i class="fa fa-book"></i>&nbsp;详情</a>\
                                        </shiro:checkPermission>\
                                        <shiro:checkPermission name="Admin:Event:Processing:Pause">\
                                        <a href="#" class="label-info  J_stop" data-toggle="modal" data-target="#stopDialog"><i class="fa fa-stop"></i>&nbsp;暂停下一期</a>\
                                        </shiro:checkPermission>\
                                        <shiro:checkPermission name="Admin:Event:Processing:Pause">\
                                        <a href="#" class="label-info  J_stopAll" data-toggle="modal" data-target="#stopAllDialog"><i class="fa fa-stop"></i>&nbsp;暂停所有</a>\
                                        </shiro:checkPermission>\
                                    </td>\
                                </tr>'
                    });
                    $('#J_template').append(str);
                    //暂无数据提示
                    var tr = $('#J_template').children();
                    if(tr.length == 0){
                        $('#J_template').append('<tr><td colspan="12">暂无数据</td></tr>');
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
     * 表格下一期暂停按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $(document).on('click', '.J_stop', function(e){
        var
            tr = $(e.target).parents('tr'),
            id = $(tr).attr('data-id');

        $('.hidId').val(id);
    });

    /**
     * 暂停下一期按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $('.J_stopDlg').click(function(){
        stop();
    });

    /**
     * 暂停下一期事件
     */
    function stop(){
        var
            id = $('.hidId').val(),
            form = {
                eventTermId: id
            };

        $.ajax({
            type: "POST",
            // url: jQuery.url.ECRBManagement.stopActivity,
            url: "/admin/event/processing/ajax/pauseTerm",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: form,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                if(rs.code == 0){
                    $('#stopDialog').modal('hide');
                    location.reload();
                }else{
                    $('#stopDialog').modal('hide');
                    $('#modalDialog').modal();
                }
            },
            error: function (message) {
                $('#modalDialog').modal();
            }
        });
    }
    /**
     * 表格暂停所有按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $(document).on('click', '.J_stopAll', function(e){
        var
            tr = $(e.target).parents('tr'),
            id = $(tr).attr('data-id');

        $('.hidId').val(id);
    });

    /**
     * 暂停所有按钮
     * @param  {[type]} e){                 } [description]
     * @return {[type]}      [description]
     */
    $('.J_stopAllDlg').click(function(){
        stopall();
    });

    /**
     * 暂停所有事件
     */
    function stopall(){
        var
            id = $('.hidId').val(),
            form = {
                eventId: id
            };

        $.ajax({
            type: "POST",
            url: jQuery.url.ECRBManagement.stopActivity,
            url: "/admin/event/processing/ajax/pauseEvent",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: form,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                $('#stopAllDialog').modal('hide');
                if(rs.code == 0){

                    location.reload();
                }else{
                    $('#stopAllDialog').modal('hide');
                    $('#modalDialog').modal();
                }
            },
            error: function (message) {
                $('#modalDialog').modal();
            }
        });
    }

});