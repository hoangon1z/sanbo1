//动态调整html根节点的字号
(function (doc, win) {
  var docEl = doc.documentElement;

  var resizeEvt =
    "orientationchange" in window ? "orientationchange" : "resize";

  function recalc() {
    var clientWidth = docEl.clientWidth;
    if (!clientWidth) return;
    if (clientWidth >= 750) {
      docEl.style.fontSize = "100px";
    } else {
      docEl.style.fontSize = 100 * (clientWidth / 750) + "px";
    }
  }

  if (!doc.addEventListener) return;

  win.addEventListener(resizeEvt, recalc, false);
  doc.addEventListener("DOMContentLoaded", recalc, false);
})(document, window);

//鼠标悬停
function bigImgA() {
  $(".operating_B_1_img1").addClass("business_bigImg");
  $(".operating_B_1_img2").removeClass("operating_B_1_img2_none");
}
function normalImgA() {
  $(".operating_B_1_img1").removeClass("business_bigImg");
  $(".operating_B_1_img2").addClass("operating_B_1_img2_none");
}
function bigImgB() {
  $(".operating_B_2_img1").addClass("business_bigImg");
  $(".operating_B_2_img2").removeClass("operating_B_2_img2_none");
}
function normalImgB() {
  $(".operating_B_2_img1").removeClass("business_bigImg");
  $(".operating_B_2_img2").addClass("operating_B_2_img2_none");
}
function bigImgC() {
  $(".operating_B_3_img1").addClass("business_bigImg");
  $(".operating_B_3_img2").removeClass("operating_B_3_img2_none");
}
function normalImgC() {
  $(".operating_B_3_img1").removeClass("business_bigImg");
  $(".operating_B_3_img2").addClass("operating_B_3_img2_none");
}

