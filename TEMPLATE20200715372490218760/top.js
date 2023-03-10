$(document).ready(function(){
    //顶部弹框
    $(".WeiBo,.WeChat,.book,.phone").mouseover(function () {
      $(".bullet").removeClass('bullet_show');
    });
    $(".WeiBo,.WeChat,.book,.phone").mouseout(function () {
      $(".bullet").addClass('bullet_show');
    });
    $(".simple").click(function () {
        transformLang(0);
        $(".simple").addClass('font_switch').siblings().removeClass('font_switch');
    })
    $(".complex").click(function () {
      transformLang(1)
      $(".complex").addClass('font_switch').siblings().removeClass('font_switch');
    })
})