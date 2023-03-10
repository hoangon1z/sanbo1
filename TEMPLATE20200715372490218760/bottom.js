$(document).ready(function(){
    //底部仿select
    $(".bottom_select_content1").click(function () {
        pType = $(this).data("type");
        $(".select_"+pType).addClass('bottom_select_show');
        $(".bottom_select_overlay").addClass('bottom_select_show');
        $(this).addClass('bottom_select_background');
    });
    $(".bottom_select_overlay,.bottom_select_content2").click(function () {
        $(".bottom_select_content2,.bottom_select_overlay").removeClass('bottom_select_show');
        $(".bottom_select_content1").removeClass('bottom_select_background');
    });
    
//加入收藏
function AddFavorite(sURL, sTitle) {
    try {
        window.external.addFavorite(sURL, sTitle);
    } catch (e) {
        try {
            window.sidebar.addPanel(sTitle, sURL, "");
        } catch (e) {
            alert("加入收藏失败，请使用Ctrl+D进行添加");
        }
    }
  }
  //设为首页
  function SetHome(obj,url){
    try{
        //IE
        obj.style.behavior='url(#default#homepage)';
        obj.setHomePage(url);
    }catch(e){
        //Firefox
        if(window.netscape) {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
            }catch (e) {
                alert("此操作被浏览器拒绝！\n请在浏览器地址栏输入“about:config”并回车\n然后将 [signed.applets.codebase_principal_support]的值设置为'true',双击即可。");
            }
            var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components.interfaces.nsIPrefBranch);
            prefs.setCharPref('browser.startup.homepage',url);
        }else{
            alert("您的浏览器不支持，请按照下面步骤操作：1.打开浏览器设置。2.点击设置网页。3.输入："+url+"点击确定。");
        }
    }
  }
})