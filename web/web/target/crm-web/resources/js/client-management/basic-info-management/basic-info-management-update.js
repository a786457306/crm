/*-----------------------------------------------------------------------------
 * @Description:     患者信息管理-基本信息添加
 * @Version:         1.0.0
 * @author:          sunwanlin(1124038074@qq.com)
 * @date             2017.7.21
 * ==NOTES:=============================================
 * v1.0.0(2017.7.21):
 初始生成
 * ---------------------------------------------------------------------------*/
$(document).ready(function(){
    /**
     * 初始化提示信息、验证表单
     */
    showTip();
    formValidatorAddForm();
    /**
     * 取消提示
     */
    function showTip(){
        setTimeout(function(){
            $('.J_tip').hide();
        }, 2000);
    }

//**************************************************************************************************************//    

    /**
     * 点击reset按钮时清空校验、数据
     * @param  {[type]} )
     * [description]
     * @return {[type]}   [description]
     */
    $('.J_reset').on('click', function() {
        $('.J_tableForm').bootstrapValidator('resetForm', true);
        $(".J_tableForm").data('bootstrapValidator').destroy();
        $('.J_tableForm').data('bootstrapValidator', null);
        formValidatorAddForm();
    });
    /**
     * 添加框验证
     * [formValidatorAddForm description]
     * @return {[type]} [description]
     */
    function formValidatorAddForm(){
        $('.J_tableForm').bootstrapValidator({
            message: 'This value is not valid',
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields: {
                name: {
                    validators: {
                        notEmpty: {
                            message: '姓名不能为空'
                        }
                    }
                },
                tel: {
                    validators: {
                        notEmpty: {
                            message: '电话不能为空'
                        },
                        stringLength: {
                            min:11,
                            max:11,
                            message: '必须为11位'
                        }
                    }
                },
                idCardNum: {
                    validators: {
                        stringLength: {
                            min:18,
                            max:18,
                            message: '必须为18位'
                        }
                    }
                },
                secTel:{
                    validators: {
                        stringLength: {
                            min:11,
                            max:11,
                            message: '必须为11位'
                        }
                    }
                }
            }
        });
    }
    /**
     * 点击save按钮时提交数据
     * @param  {[type]} ){                     var data [description]
     * @return {[type]}     [description]
     */
    $('.J_save').click(function(){
        var
            form = $('.J_tableForm'),
            data = form.data('bootstrapValidator');
        if (data) {
            // 修复记忆的组件不验证
            data.validate();

            if (!data.isValid()) {//如果验证不通过
                return false;
            }else{//如果前台验证通过，则调用sendCardId()方法，验证身份证是否唯一
                save();
                return true;
            }
        }
    });
    /**
     * 身份证input框失焦
     * 功能：失焦后开始向后台发送cardId,并验重
     * @param  {[type]} ){} [description]
     * @return {[type]}                    [description]
     */
    $(".J_idcardNum").blur(function(){
        sendCardId();
    });
    /**
     * 身份号码验重
     * 功能：身份号码向后台发ajax，验证是否唯一，唯一则将年龄计算出来显示在input内
     * @return {[type]} [description]
     */
    function sendCardId(){
        var idCard=$(".J_idcardNum").val();
        $.ajax({
            type: "GET",
            url: "/admin/client/basic/ajax/checkUpdateIdCard",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: {
                idCard:idCard
            },     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                if( rs.code == 0){
                    console.log("sendCardId:Ajax提交成功，后台验证成功");
                    //如果发送成功，并且身份证验证唯一，则计算年龄
                    ageCalculated(idCard);
                    //计算年龄之后向后台发送整个表单内容
                }else{
                    alert("身份证号重复！");
                    console.log("sendCardId:Ajax提交成功，后台验证失败");
                }
            },
            error: function (message) {
                console.log("sendCardId:Ajax提交失败，后台验证失败");
            }
        });
    }
    /**
     * 计算年龄函数
     * @return {[type]} [description]
     */
    function ageCalculated(cardId){

        var
            birthYear=cardId.substr(6,4),
            myDate = new Date(),
            nowYear=myDate.getFullYear(),
            age=nowYear-birthYear;

        $(".J_age").val(age);
        
        var ageValue = $(".J_age").val(),
            ageType = isInteger(ageValue);

        if(!ageType){
            if($(".ageValue").length==0){
                $('.J_age').after('<small class="ageValue has-error help-block" data-bv-validator="notEmpty" data-bv-for="name" data-bv-result="INVALID" style="color:#a94442;">年龄必须为大于零的整数</small>');
            }
        }else{
            if($(".ageValue").length>0){
                $('.ageValue').remove();
            }
        }
    }
    /**
     * 验证是否为大于零的整数
     * @param  {[type]}  obj [description]
     * @return {Boolean}     [description]
     */
    function isInteger(obj) {
        return obj%1 == 0 && obj>0 ;
    }
    /**
     * 保存事件
     * @return {[type]} [description]
     */
    function save(){

        var serializeForm=$('.J_tableForm').serializeObject();

        $.ajax({
            type: "POST",
            url: "/admin/client/basic/ajax/update",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: serializeForm,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                if( rs.code == 0){
                    Alert("提示信息","添加成功");
                    window.location.href = "/admin/client/basic";
                }else{
                    console.log("save:Ajax提交成功，后台验证失败");
                    Alert("提示信息",rs.errMsg);
                    location.reload();
                }
            },
            error: function (message) {
                console.log("save:Ajax提交失败，后台验证失败");
            }
        });
    }
});