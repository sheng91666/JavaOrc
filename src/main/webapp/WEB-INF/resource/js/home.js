$(function () {

    $("#submitFile").unbind("click").click(function () {

        var formdata = new FormData($("#domeform")[0]);
        var isDouble = true;

        if (isDouble) {
            isDouble = false;
            $.ajax({
                url: "/uploadFile",
                type: "POST",
                data: formdata,
                dataType: "json",
                processData: false,  // 告诉jQuery不要去处理发送的数据
                contentType: false,   // 告诉jQuery不要去设置Content-Type请求头
                success: function (data) {
                    isDouble = true;
                    if (data.status == 1 || data.status == '1') {
                        var reg = new RegExp("\n", "g");//g,表示全部替换
                        var replace = data.msg.replace(reg, "<br/>");
                        $("#resultMsg").html(replace);
                    } else {
                        $("#resultMsg").html(data.msg)
                    }
                }
            })
        }

    })
})
