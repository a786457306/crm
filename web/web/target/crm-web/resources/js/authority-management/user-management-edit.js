$(document).ready(function(){
    /**
     * 初始化提示信息、验证表单
     */
    showTip();
    formValidatorAddForm();
    $('#Menu4,#logoMenu4').trigger('click');
    /**
     * 取消提示
     */
    function showTip(){
        setTimeout(function(){
            $('.J_tip').hide();
        }, 2000);
    }
    /**
     * 保存事件
     */
    function save(){

        var serializeForm=$('.J_tableForm').serialize();
        $.ajax({
            type: "POST",
            // url: jQuery.url.AuthorityManagement.updateUser,
            url: "/admin/party/group/employee/ajax/update",
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data: serializeForm,     //JSON.stringify
            dataType: "json",
            success: function (rs) {
                if( rs.code == 0){
                    Alert("提示信息","保存成功！",function () {
                        window.location.href = "/admin/party/group/employee/list"
                    });
                }else{
                    Alert("提示信息","保存失败！");
                }
            },
            error: function (message) {
                Alert("提示信息","保存失败！");
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

            if (!data.isValid()) {
                return false;
            }else{
                save();
                return true;

            }
        }
    });
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
                phone: {
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
                email: {
                    validators: {
                        emailAddress: {
                            message: '不是标准邮箱格式'
                        }
                    }
                }
            }
        });
    }
});