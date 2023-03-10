
<template>
  <div class="invite">
    <div class="header">
      <div class="invite-title">
        <div class="my-link-text">{{$t('invite.myinvitelink')}}：</div>
        <div class="my-link-content">{{myInfo.inviteLink}}</div>
        <a class="copy-button" v-clipboard:copy="myInfo.inviteLink" v-clipboard:success="onCopy" v-clipboard:error="onError" href="javascript:;">{{$t('invite.copy')}}</a>
      </div>
      <div class="invite-detail">
        <div class="item">
          <p class="i-v">{{myInfo.levelone}}</p>
          <p class="i-t">{{$t('invite.mylevelone')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.levelteam}}</p>
          <p class="i-t">{{$t('invite.mylevelteam')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.power}}</p>
          <p class="i-t">{{$t('invite.mypower')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.teamPower}}</p>
          <p class="i-t">{{$t('invite.mypowerteam')}}</p>
        </div>
        <div class="item">
          <p class="i-v i-v-2">V{{myInfo.levelId}}  {{myInfo.partnerlevel}}</p>
          <p class="i-t">{{$t('invite.partnerlevel')}}</p>
        </div>
        <div style="clear:both;"></div>
      </div>
      <div class="qr-code" style="background:#FFF;padding: 5px 5px;">
        <qriously :value="myInfo.inviteLink" :size="140" foreground="#000" />
      </div>

      <div class="mask" v-if="!isLogin">
        <div class="login-btn">
              <ButtonGroup size="large">
                  <Button type="warning" to="/login">{{$t("common.login")}}</Button>
                  <Button type="warning" to="/register">{{$t("common.register")}}</Button>
              </ButtonGroup>
        </div>
      </div>
    </div>
    <p class="headertip">* {{$t('invite.headertip')}}</p>
    <div class="main" >
      <div class="invite-content shouji_show">
        <h2>{{$t('invite.ruledetail')}}</h2>
        <div class="content">
          <p style="margin-bottom:20px;border-left: 5px solid #e7e7e7;padding-left: 25px;">{{$t('invite.ruleprofile1')}}<b>{{$t('invite.ruleprofile2')}}</b><b>{{$t('invite.ruleprofile3')}}</b>{{$t('invite.ruleprofile4')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
          <table class="percent-table">
            <tr style="background:#EDEDED;">
            <td>{{$t('invite.level')}}</td><td>{{$t('invite.levelonecount')}}</td><td>{{$t('invite.levelonecommission')}}</td>
            <td>{{$t('invite.leveltwocommission')}}</td><td>{{$t('invite.partnerdivide')}}</td></tr>

            <tr><td>V1({{$t('invite.lv1')}})</td><td>{{$t('invite.noPermission')}}</td><td>150 USDT</td><td>{{$t('invite.noDiscount')}}</td><td>{{$t('invite.noRequest')}}</td></tr>
            <tr><td>V2({{$t('invite.lv2')}})</td><td>0.3 {{$t('invite.wan')}} USDT</td><td>0.3 {{$t('invite.wan')}} USDT</td><td>98{{$t('invite.fold')}}</td><td>{{$t('invite.teamsl')}}10KB/S</td></tr>
            <tr><td>V3({{$t('invite.lv3')}})</td><td>0.85 {{$t('invite.wan')}} USDT</td><td>0.85 {{$t('invite.wan')}} USDT</td><td>97{{$t('invite.fold')}}</td><td>{{$t('invite.teamsl2')}}2{{$t('invite.teamsl3')}}V2{{$t('invite.teamsl4')}}30KB/S</td></tr>
            <tr><td>V4({{$t('invite.lv4')}})</td><td>3.3 {{$t('invite.wan')}} USDT</td><td>3.3 {{$t('invite.wan')}} USDT</td><td>96{{$t('invite.fold')}}</td><td>{{$t('invite.teamsl2')}}3{{$t('invite.teamsl3')}}V3{{$t('invite.teamsl4')}}90KB/S</td></tr>
            <tr><td>V5({{$t('invite.lv5')}})</td><td>12 {{$t('invite.wan')}} USDT</td><td>12 {{$t('invite.wan')}} USDT</td><td>95{{$t('invite.fold')}}</td><td>{{$t('invite.teamsl2')}}3{{$t('invite.teamsl3')}}V4{{$t('invite.teamsl4')}}270KB/S</td></tr>
            <tr><td>V6({{$t('invite.lv6')}})</td><td>44 {{$t('invite.wan')}} USDT</td><td>44 {{$t('invite.wan')}} USDT</td><td>94{{$t('invite.fold')}}</td><td>{{$t('invite.teamsl2')}}3{{$t('invite.teamsl3')}}V5{{$t('invite.teamsl4')}}810KB/S</td></tr>
          </table>
          <div style="font-size: 12px;padding: 10px;margin-left: 30px;width: 90%;margin-bottom: 10px;border: 1px solid #c8c8c8;">
            <p style="font-size: 13px;font-weight:bold;">{{$t('invite.ruleexampletitle')}}</p>
            <p style="text-indent: 25px;">    {{$t('invite.ruleexamplecontent1')}}</p>
          </div>
          <!-- <p>{{$t('invite.ruletext8')}}</p>
          <p>{{$t('invite.ruletext9')}}</p>
          <p>{{$t('invite.ruletext10')}}</p>
          <p>{{$t('invite.ruletext11')}}</p> -->
          <p class="rule-update">{{$t('invite.lastupdate')}}：2020/9/30 12:15:34</p>
        </div>
      </div>
      <div class="promotion-tools">
        <h2>{{$t('invite.ptools')}}</h2>
        <div class="tools">
          <div class="item" @click="showCardItem">
              <img src="../../assets/images/tuiguang1.png"></img>
              <p class="title">{{$t('invite.pt_title')}}<span style="color:#FF0000;margin-left:10px;font-size:13px;">{{$t('invite.pt_card_title_tips')}}</span></p>
              <p class="desc">{{$t('invite.pt_desc')}}</p>
          </div>
          <div class="item" @click="useInviteImage">
              <img src="../../assets/images/tuiguang2.png"></img>
              <p class="title">{{$t('invite.pt_invite_title')}}</p>
              <p class="desc">{{$t('invite.pt_invite_desc')}}</p>
          </div>
          <div class="more">
            <p style="color:#c2c2c2;font-size:16px;">{{$t('invite.pt_more')}}</p>
          </div>
        </div>
        <p class="bottom">{{$t('invite.pt_tips')}}</p>
      </div>
      <!-- <div class="invite-rank">
        <Row :gutter="80">
          <Col span="12">
            <h2>{{$t('invite.totalcommissionrank')}}TOP20{{$t('invite.rankall')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastUpdate}}</span></p>
            <Table class="rank-table" :columns="columns" :data="topRewardRank"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip1')}}</p>
          </Col>
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankall')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastUpdate}}</span></p>
            <Table class="rank-table" :columns="columns" :data="topInviteCountRank"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
        </Row>
      </div> -->

      <div class="invite-rank" style="display:none;">
        <Row :gutter="80">
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankweek')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastStasticWeek}}</span></p>
            <Table class="rank-table" :columns="columns1" :data="topRankWeek"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankmonth')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastStasticMonth}}</span></p>
            <Table class="rank-table" :columns="columns1" :data="topRankMonth"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
        </Row>
      </div>
    </div>
    <H1 class="bottom_text">{{$t('invite.thanks')}}</H1>

    <Drawer :title="$t('invite.pt_title')" :closable="false" v-model="showCardModal" class="tankuang">
      <div class="ptcard-header">
        <img class="card-img" src="../../assets/images/tuiguang1.png"></img>
        <div class="desc">
          <p class="title">{{$t('invite.pt_title')}}</p>
          <p class="amount">{{$t('invite.pt_card_amount')}}：9.9 KICK</p>
          <p class="deadline">{{$t('invite.pt_card_deadline')}}：60{{$t('invite.pt_card_day')}}</p>
          <template v-if="isLogin">
          <Button v-if="!hasCard" type="error" size="large" @click="getFreeCard">{{$t('invite.pt_card_btn')}}</Button>
          <Button v-if="hasCard" type="error" size="large" @click="usePromotionImage">{{$t('invite.usepromotion')}}</Button>
          </template>
          <Button v-else to="/login" type="success" size="large">{{$t('invite.pt_card_btn_login')}}</Button>
        </div>
      </div>
      <Divider class="guize" orientation="left">{{$t('invite.pt_card_rule')}}</Divider>
      <div class="ptcard-info">
        <p class="title">{{$t('invite.pt_card_summary')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule1')}}：promotion@kickstarterkit.com。</p>
        <p class="detail">{{$t('invite.pt_card_rule2')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule3')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule4')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule5')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule6')}}</p>
      </div>
    </Drawer>

    <Drawer :title="promotionTitle" :closable="false" v-model="showPromotionModal" class="tankuang2" style="text-align:center;">
      <div class="tankuang2_content" id="promotionImage" ref="promotionImage">
        <img style="width:100%;display:block;" src="../../assets/images/promotion/promotionbg1.jpg" crossorigin="anonymous"/>
        <p class="tuiguang">{{promotionCode}}</p>
        <p class="tuiguang2">{{$t('invite.redemptionCode')}}</p>
      </div>
      <p style="text-align:center;font-size:12px;color:#888;margin-top: 10px;">{{$t('invite.imagetips')}}</p>
      <Button type="error" size="large" :loading="saveImageLoading" long style="margin-top: 20px;" @click="saveImage">{{$t('invite.saveimage')}}</Button>
    </Drawer>

    <Drawer :title="$t('invite.invitePicture')" :closable="false" v-model="showInviteImageModal" class="tankuang2" style="text-align:center;">
      <div  class="tankuang2_content" id="inviteImage" ref="inviteImage">
        <img style="width:100%;display:block;" src="../../assets/images/promotion/invitebg1.jpg"  crossorigin="anonymous"/>
        <div class="qr-code tankuang2_text">
          <qriously :value="myInfo.inviteLink" :size="80" foreground="#000" />
        </div>
        <p class="tankuang2_text2">{{$t('invite.scanToLearnMore')}}</p>
        <p class="tankuang2_text3">{{$t('invite.myInvitationCode')}}{{inviteCode}}</p>
      </div>
      <p style="text-align:center;font-size:12px;color:#888;margin-top: 10px;">{{$t('invite.imagetips')}}</p>
      <Button type="error" size="large" :loading="saveImageLoading" long style="margin-top: 20px;" @click="saveInviteImage">{{$t('invite.saveimage')}}</Button>
    </Drawer>
  </div>
</template>

<script>
import Vue from "vue";
import VueQriously from "vue-qriously";
import html2canvas from 'html2canvas';

Vue.use(VueQriously);

export default {
  data() {
    let self = this;
    return {
      showCardModal: false,
      hasCard: false,
      showPromotionModal: false,
      saveImageLoading: false,
      showInviteImageModal: false,
      promotionTitle: "",
      inviteCode: "", // 邀请码
      promotionCode: "", // 兑换码
      myInfo: {
        levelone: 0,
        leveltwo: 0,
        levelteam: 0,
        power: 0,
        teamPower: 0,
        commission: 0,
        extrareward: 0,
        partnerlevel: "-",
        inviteLink: "",
        levelId:''
      },
      promotionRecordPage: {
        pageNo: 1,
        pageSize: 10
      },
      columns: [
          {
              title: self.$t("invite.colum_text0"),
              type: "index",
              width: 60,
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text1"),
              key: 'userIdentify',
              align: 'center',
              width: 105
          },
          {
              title: self.$t("invite.colum_text2"),
              key: 'levelOne',
              align: 'center',
              width: 85
          },
          {
              title: self.$t("invite.colum_text3"),
              key: 'estimatedReward',
              align: 'center',
              minWidth: 140
          },
          {
              title: self.$t("invite.colum_text4"),
              key: 'extraReward',
              align: 'center',
              minWidth: 85
          }
      ],
      topRankMonth:[],
      topRankWeek: [],
      lastStasticWeek: "",
      lastStasticMonth: "",
      columns1: [
          {
              title: self.$t("invite.colum_text0"),
              type: "index",
              width: 80,
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text1"),
              key: 'userIdentify',
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text2"),
              key: 'levelOne',
              align: 'center'
          }
      ],
      lastUpdate: "",
      topRewardRank: [],
      topInviteCountRank: [],
      dataFanyong: [
          {
              sort: 1,
              name: '186****9837',
              age: 18,
              address: '18273.98',
              date: '+23'
          },
          {
              sort: 2,
              name: '186****9837',
              age: 24,
              address: '18273.98',
              date: '+23'
          }
      ],
      dataFanyong1: [
          {
              sort: 1,
              name: '186****9837',
              age: 18,
              address: '18273.98',
              date: '+23'
          }
      ]
    };
  },
  created: function() {
    this.init();
  },
  computed: {
    lang() {
      this.updateLangData();
      return this.$store.state.lang;
    },
    langPram(){
      if(this.$store.state.lang == "简体中文"){
        return "CN";
      }
      if(this.$store.state.lang == "English"){
        return "EN";
      }
      return "CN";
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-invite");
      if(this.isLogin){
        this.getMemberInfo();
        this.getCardInfo();
      }
      this.getRank();
    },
    useInviteImage(){
      if(!this.isLogin) {
        this.$Message.error(this.$t("common.logintip"));
        return;
      }
      this.showInviteImageModal = true;
      window.pageYOffset = 0;
      document.documentElement.scrollTop = 0;
      document.body.scrollTop = 0;
    },
    usePromotionImage(){
      if(!this.isLogin) {
        this.$Message.error(this.$t("common.logintip"));
        return;
      }
      this.showPromotionModal = true;
      window.pageYOffset = 0;
      document.documentElement.scrollTop = 0;
      document.body.scrollTop = 0;
    },
    getCardInfo(){
      this.$http.post(this.host + this.api.uc.mycardlist).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          if(resp.data != null && resp.data.length > 0){
            this.promotionTitle = resp.data[0].cardName;
            this.promotionCode = resp.data[0].cardNo;
            this.hasCard = true;
          }
        } else {
          this.$Message.error(this.loginmsg);
        }
        this.loading = false;
      });
    },
    dataURLToBlob(dataurl) {
        let arr = dataurl.split(',');
        let mime = arr[0].match(/:(.*?);/)[1];
        let bstr = atob(arr[1]);
        let n = bstr.length;
        let u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], { type: mime });
    },
    saveInviteImage(){
      this.save("inviteImage", "邀请图片");
      this.saveImageLoading = true;
    },
    saveImage(){
      this.save("promotionImage", "全球传递爱图片");
      this.saveImageLoading = true;
    },
    blobToBase64(blob) {
      return new Promise((resolve, reject) => {
        const fileReader = new FileReader();
        fileReader.onload = (e) => {
          resolve(e.target.result);
        };
        // readAsDataURL
        fileReader.readAsDataURL(blob);
        fileReader.onerror = () => {
          reject(new Error('blobToBase64 error'));
        };
      });
    },
    save(divText, imgText) {
        window.pageYoffset = 0;
        document.documentElement.scrollTop = 0;
        document.body.scrollTop = 0;
        let canvasID = this.$refs[divText];
        let that = this;
        let scale = 1;
        let a = document.createElement('a');
        setTimeout(() => {
          html2canvas(canvasID, {
            scale,
            useCORS:true,
            width: canvasID.offsetWidth * scale,
            height: canvasID.offsetHeight * scale
          }).then(canvas => {
              const context = canvas.getContext('2d');
              // 【重要】关闭抗锯齿 https://segmentfault.com/a/1190000011478657
              context.mozImageSmoothingEnabled = false;
              context.webkitImageSmoothingEnabled = false;
              context.msImageSmoothingEnabled = false;
              context.imageSmoothingEnabled = false;
              context.scale(scale, scale);

              let dom = document.body.appendChild(canvas);
              dom.style.display = 'none';
              a.style.display = 'none';
              document.body.removeChild(dom);
              let blob = that.dataURLToBlob(dom.toDataURL('image/png'));
              this.blobToBase64(blob).then(res => {
                let base64 = res;
                // 转化后的base64
                console.log('base64', res)
                a.setAttribute('href', base64);
                //这块是保存图片操作  可以设置保存的图片的信息
                a.setAttribute('download', imgText + '.png');
                document.body.appendChild(a);
                a.click();
                URL.revokeObjectURL(blob);
                document.body.removeChild(a);

                this.saveImageLoading = false;
              })
              return;
              // a.setAttribute('href', URL.createObjectURL(blob));
              // //这块是保存图片操作  可以设置保存的图片的信息
              // a.setAttribute('download', imgText + '.png');
              // document.body.appendChild(a);
              // a.click();
              // URL.revokeObjectURL(blob);
              // document.body.removeChild(a);

              // this.saveImageLoading = false;
          });
        });
    },
    showCardItem(){
      this.showCardModal = true;
    },
    onCopy(e) {
      this.$Message.success(
        this.$t("uc.finance.recharge.copysuccess") + e.text
      );
    },
    onError(e) {
      this.$Message.error(this.$t("uc.finance.recharge.copysuccess"));
    },
    getMemberInfo(){
      console.log(this.$store.state.member.promotionPrefix)
      this.myInfo.inviteLink = this.$store.state.member.promotionPrefix + this.$store.state.member.promotionCode;
      this.inviteCode = this.$store.state.member.promotionCode;

      // 获取邀请人数
      this.$http
        .post(this.host + this.api.uc.memberInfo, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.myInfo.levelone = resp.data.firstLevel;
            this.myInfo.leveltwo = resp.data.secondLevel;
            this.myInfo.levelteam = resp.data.teamLevel;
            this.myInfo.power = resp.data.power;
            this.myInfo.teamPower = resp.data.teamPower;
            this.myInfo.partnerlevel = resp.data.level.name;
            this.myInfo.levelId = resp.data.level.id; 
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });

      // 获取个人佣金信息
      this.$http
        .post(this.host + this.api.uc.mypromotion, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.myInfo.commission = resp.data.estimatedReward;
            this.myInfo.extrareward = resp.data.extraReward;
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });
    },
    getFreeCard: function(){
      this.$http
        .post(this.host + this.api.uc.getfreecard, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Notice.success({
              title: this.$t("common.tip"),
              desc:this.$t("invite.pt_card_receivew_success"),
              duration: 30
            });

            this.getCardInfo();
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message,
              duration: 15
            });
          }
        });
    },
    getTopRankWeek: function(){
      // 获取全球传递爱排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotionweek, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRankWeek = resp.data.topinviteweek;
            if(this.topRankWeek.length > 0) {
              this.lastStasticWeek = this.topRankWeek[0].stasticDate;
            }
          }
        });
    },
    getTopRankMonth: function(){
      // 获取全球传递爱排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotionmonth, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRankMonth = resp.data.topinvitemonth;
            if(this.topRankMonth.length > 0) {
              this.lastStasticMonth = this.topRankMonth[0].stasticDate;
            }
          }
        });
    },
    getRank: function(){
      // 获取全球传递爱排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotion, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRewardRank = resp.data.topreward;
            this.topInviteCountRank = resp.data.topinvite;
            if(this.topRewardRank.length > 0) {
              this.lastUpdate = this.topRewardRank[0].stasticDate;
            }
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });
    },
    // partnerNameByCount: function(count) {
    //   if(count == undefined || count == null || count == ""){
    //     return "-";
    //   }
    //   let partnerName = "Lv1(" + this.$t('invite.lv1') +")";
    //   if(count > 10) partnerName = "Lv2(" + this.$t('invite.lv2') +")";
    //   if(count > 100) partnerName = "Lv3(" + this.$t('invite.lv3') +")";
    //   if(count > 500) partnerName = "Lv4(" + this.$t('invite.lv4') +")";
    //   if(count > 1500) partnerName = "Lv5(" + this.$t('invite.lv5') +")";
    //   if(count > 3000) partnerName = "Lv6(" + this.$t('invite.lv6') +")";
    //   return partnerName;
    // },
    updateLangData: function(){
      this.columns[0].title = this.$t("invite.colum_text0");
      this.columns[1].title = this.$t("invite.colum_text1");
      this.columns[2].title = this.$t("invite.colum_text2");
      this.columns[3].title = this.$t("invite.colum_text3");
      this.columns[4].title = this.$t("invite.colum_text4");
    }
  }
};
</script>
<style lang="scss" scoped>
.invite {
  display:flex;
  flex-direction: column;
  background: linear-gradient(200deg, #f19100, #ffc84b, #ffc540);
  padding: 0 12%;
  padding-bottom: 100px;
  min-height: 400px;
  .header{
    position: relative;
    margin-top: 100px;
    padding: 30px 50px;
    width:100%;
    height: 180px;
    color: #000;
    background: linear-gradient(200deg, #ff9900, #ffbe2b, #ffa500);
    background-size: 100% 100%;
    box-shadow: 0px 0px 20px #5b5b5b8c;
    margin-bottom: 10px;
    border-radius: 8px;
    .invite-title{
      font-size: 18px;letter-spacing: 2px;display:flex;
      flex-direction: row;
      margin-right: 170px;
      padding-bottom: 15px;
      border-bottom: 1px dashed #e3a700;
      .my-link-text{
        height: 38px;line-height: 38px;
        min-width: 140px;
      }
      .my-link-content{
        background:rgba(0, 0, 0, 0);border-radius: 3px;height: 38px;line-height: 38px;width: 600px;text-align: center;
        box-shadow: 0px 0px 15px #00000026 inset;
      }
      .copy-button{
        height:32px;line-height:32px;background:linear-gradient(350deg, #e1e1e1, #ffffff);padding:0 20px;margin-left: 20px;border-radius: 5px;color: #000;font-size: 14px;margin-top:3px;box-shadow: 0px 0px 10px #afafaf;width:100px;text-align:center;
        cursor: pointer;
        &:hover{
          background:linear-gradient(350deg, #efefef, #e1e1e1);
        }
        &:focus{
          box-shadow: 0px 0px 10px #00000026 inset;
        }
      }
    }
    .invite-detail{
      margin-right: 170px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      padding-top: 10px;
      .item{
        width: 18%;
        text-align:center;
        .i-t{
          font-size: 12px;
          background: #0000000f;
          padding: 5px 0;
        }
        .i-v{
          height: 35px;
          font-size: 26px;
          font-weight: bold;
        }
        .i-v-2{
          font-size: 22px;
        }
      }
    }
    .qr-code{
      position: absolute;
      top: 15px;
      right: 20px;
      height: 150px;
      width: 150px;
      border-radius: 10px;
      box-shadow: 0px 0px 15px #00000057 inset;
    }
    .mask{
      width: 100%;
      background: #ffffffe0;
      position: absolute;
      top: 0;
      left: 0;
      border-radius: 8px;
      height: 180px;
      text-align: center;
      .login-btn{
        width: 150px;
        height: 35px;
        line-height: 35px;
        border-radius: 3px;
        margin: 0 auto;
        margin-top: 80px;
      }
    }
  }
  p.headertip{
    font-size:12px;text-align:right;color:rgba(0, 0, 0, 0.48);text-shadow: 0 0 25px #ffffff;
  }
  .main {
    min-height: 300px;
    width: 100%;
    margin: 0 auto;
    border-radius: 6px;
    margin-top: 30px;
    .invite-content{
      background: #FFF;
      padding: 30px 30px;
      margin-bottom: 30px;
      color: #6d6d6d;
      border-radius: 5px;
      box-shadow: 0px 0px 20px #909090;
      .content{
        margin-top: 20px;
        min-height: 100px;
        padding-left: 40px;
        line-height: 30px;
        letter-spacing: 1px;
        .percent-table{
          margin-bottom: 10px;
          background:#EDEDED;
          text-align:center;margin-left: 30px;font-size:12px;width:90%;margin-top: 10px;
          border-right: 1px solid #c8c8c8;
          border-bottom: 1px solid #c8c8c8;
          border-collapse:collapse;
          tr{
            background:#FFF;
            td{
              border-left: 1px solid #c8c8c8;
              border-top:1px solid #c8c8c8;
            }
          }
        }
        .rule-update{
          font-size: 12px;text-align:right;color:#AFAFAF;margin-top: 20px;padding-top: 20px;border-top: 1px solid #EDEDED;
        }
      }
      >h2{
        padding-bottom: 10px;
        border-bottom: 1px solid #EDEDED;
      }
    }
    .promotion-tools{
      color: #3a3a3a;
      position:relative;
      h2{
        padding-bottom: 0px;
        text-align:left;
        margin-bottom: 20px;
        margin-top: 60px;
      }
      .tools{
        min-height: 250px;
        background: #FFF;
        border-radius: 5px;
        box-shadow: 0px 0px 20px #909090;
        padding: 20px 20px 50px 20px;
        display: flex;
        flex-direction: row;
        .item{
          width: 310px;padding: 5px;
          img{
            width: 300px;border-radius: 5px;height: 160px;
            &:hover{
              opacity:0.9;
            }
          }
          p.title{
            font-size:16px;padding: 0 5px;
          }
          p.desc{
            font-size:12px;padding: 0 5px;color: #999;
          }
          &:hover{
            cursor:pointer;
          }
        }
      }
      .more{
        margin: 5px 20px;
        border-radius: 5px;
        line-height: 153px;
        width: 300px;
        height: 160px;
        padding: 5px;
        background: #EEE;
        text-align: center;
      }
      .bottom{
        color:#C2C2C2;font-size:12px;text-align:right;margin-top: 10px;position:absolute;bottom: 5px;position:absolute;width:96%;bottom: 10px;left:2%;
        border-top: 1px dashed #C2C2C2;padding-top: 10px;text-align:center;
      }
    }
    .invite-rank{
      margin-top: 60px;
      p.ranktitle{
        text-align:left;font-size:12px;margin-bottom: 20px;color:#383838;
      }
      h2{
        padding-bottom: 0px;
        color: #3a3a3a;
        text-align:left;
        span{
          font-size: 14px;font-weight:normal;
          margin-left: 10px;
        }
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row td{
        background-color: transparent;
        border: none;
        border-bottom: 1px solid #e3e3e3;
        color: #151515;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-header th{
        background-color: #F90;
        color: #000;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
        background: #f7f7f7!important;
      }
      .rank-tips{
        text-align:right;color:#000;font-size:12px;margin-top:10px;
      }
      .rank-table{
        box-shadow: 0 0 25px #000;
      }
    }
  }
}
.bottom_text{
    color:#000;
    text-align:center;
    margin-top: 100px;
    font-size:30px;
  }
  .tuiguang{
    position:absolute;top: 210px;text-align:center;width: 100%;text-align:center;font-size:26px;color:#F90;font-weight:bold;
  }
  .tuiguang2{
    position:absolute;top: 250px;text-align:center;width: 100%;text-align:center;
  }
  /deep/.tankuang{
    .ivu-drawer-wrap{
      .ivu-drawer{
        width: 600px!important;
      }
    }
  }
  /deep/.tankuang2{
    .ivu-drawer-wrap{
      .ivu-drawer{
        width: 350px!important;
      }
    }
  }
  .tankuang2_text{
    background:#FFF;position:absolute;top: 350px;left:115px;border-radius: 5px;height: 80px;
  }
  .tankuang2_text2{
    position:absolute;top: 445px;text-align:center;width: 100%;text-align:center;
  }
  .tankuang2_text3{
    position:absolute;top: 465px;text-align:center;width: 100%;text-align:center;font-size:16px;
  }
  .tankuang2_content{
    position:relative;
    width: 318px;
  }
  .guize{
    margin-top: 40px;
  }
.ptcard-header{
  display:flex;flex-direction:row;justify-content:flex-start;margin-top:20px;
  .card-img{
    width:260px;height:140px;border-radius:5px;
  }
  .desc{
    height:130px;width:100%;color:#000;padding-left: 20px;
    .title{
      font-size:20px;margin-bottom:5px;
    }
    .amount, .deadline{
      font-size:14px;color: #888;
    }
    .ivu-btn{
      margin-top:25px;
      letter-spacing: 1px;
    }
  }
}
.ptcard-info{
  padding-left: 40px;color:#555;line-height:25px;padding-right:20px;
  .title{
    color: rgb(85, 85, 85);line-height: 25px;padding: 5px 10px;background: #F9F9F9;border: 1px solid #EDEDED;
  }
  .detail{
    margin-top: 10px;
  }
}
@media screen and (max-width:1200px) {
  .shouji_show{
    display: none;
  }
  .bottom_text{
    color:#000;
    text-align:center;
    margin-top: 20px;
    font-size:18px;
    margin-bottom: 20px;
  }
  .guize{
    margin-top: 10px;
  }
  /deep/.tankuang{
    .ivu-drawer-wrap{
      .ivu-drawer{
        width: 256px!important;
      }
    }
  }
  /deep/.tankuang2{
    .ivu-drawer-wrap{
      .ivu-drawer{
        width: 256px!important;
      }
    }
  }
  .tuiguang{
    position:absolute;top: 140px;text-align:center;width: 100%;text-align:center;font-size:26px;color:#F90;font-weight:bold;
  }
  .tuiguang2{
    position:absolute;top: 175px;text-align:center;width: 100%;text-align:center;
  }
  .tankuang2_content{
    position:relative;
    width: 218px;
  }
  .tankuang2_text{
    background:#FFF;position:absolute;top: 220px;left:70px;border-radius: 5px;height: 80px;
  }
  .tankuang2_text2{
    position:absolute;top: 305px;text-align:center;width: 100%;text-align:center;
  }
  .tankuang2_text3{
    position:absolute;top: 325px;text-align:center;width: 100%;text-align:center;font-size:14px;
  }
  .invite {
  display:flex;
  flex-direction: column;
  background: linear-gradient(200deg, #f19100, #ffc84b, #ffc540);
  padding-bottom: 100px;
  min-height: 400px;
  padding: 0px 3%;
  .header{
    position: relative;
    margin-top: 70px;
    padding: 20px 15px;
    width:100%;
    height: auto;
    color: #000;
    background: linear-gradient(200deg, #ff9900, #ffbe2b, #ffa500);
    background-size: 100% 100%;
    box-shadow: 0px 0px 20px #5b5b5b8c;
    margin-bottom: 10px;
    border-radius: 8px;
    .invite-title{
      font-size: 14px;letter-spacing: 2px;
      flex-direction: row;
      margin-right: 170px;
      display: block;
      padding-bottom: 0px;
      border-bottom: 1px dashed #e3a700;
      margin-right: 0px;
      .my-link-text{
        height: 38px;line-height: 38px;
        width:100%;
        text-align: center;
      }
      .my-link-content{
        background:rgba(0, 0, 0, 0);border-radius: 3px;height: 38px;line-height: 20px;width: 100%;text-align: center;
        box-shadow: 0px 0px 15px #00000026 inset;
        text-align: center;
      }
      .copy-button{
        height:32px;line-height:52px;background:linear-gradient(350deg, #e1e1e1, #ffffff);padding:8px 40%;
        border-radius: 5px;color: #000;font-size: 14px;margin-top:3px;box-shadow: 0px 0px 10px #afafaf;
        width:90%;text-align:center;
        cursor: pointer;
        &:hover{
          background:linear-gradient(350deg, #efefef, #e1e1e1);
        }
        &:focus{
          box-shadow: 0px 0px 10px #00000026 inset;
        }
      }
    }
    .invite-detail{
      flex-direction: row;
      justify-content: space-between;
      padding-top: 0px;
      display: block;
      margin-right: 0px;
      .item{
        width: 48%;
        text-align:center;
        float: left;
        .i-t{
          font-size: 12px;
          background: #0000000f;
          padding: 5px 0;
        }
        .i-v{
          font-size: 20px;
          margin-top: 20px;
          font-weight: bold;
        }
        .i-v-2{
          font-size: 20px;
        }
      }
      .item:nth-child(2n + 1){
        margin-right: 4%;
      }
      .item:nth-child(5){
        width: 100%;
      }
    }
    .qr-code{
      position: relative;
      top: 15px;
      right: 0px;
      height: 150px;
      width: 150px;
      margin: 0 auto;
      border-radius: 10px;
      box-shadow: 0px 0px 15px #00000057 inset;
    }
    .mask{
      width: 100%;
      background: #ffffffe0;
      position: absolute;
      top: 0;
      left: 0;
      border-radius: 8px;
      height: 180px;
      text-align: center;
      .login-btn{
        width: 150px;
        height: 35px;
        line-height: 35px;
        border-radius: 3px;
        margin: 0 auto;
        margin-top: 80px;
      }
    }
  }
  p.headertip{
    font-size:12px;text-align:center;color:rgba(0, 0, 0, 0.48);text-shadow: 0 0 25px #ffffff;
  }
  .main {
    min-height: 300px;
    width: 100%;
    margin: 0 auto;
    border-radius: 6px;
    margin-top: 0px;
    .invite-content{
      background: #FFF;
      padding: 30px 30px;
      margin-bottom: 30px;
      color: #6d6d6d;
      border-radius: 5px;
      box-shadow: 0px 0px 20px #909090;
      .content{
        margin-top: 20px;
        min-height: 100px;
        padding-left: 40px;
        line-height: 30px;
        letter-spacing: 1px;
        .percent-table{
          margin-bottom: 10px;
          background:#EDEDED;
          text-align:center;margin-left: 30px;font-size:12px;width:90%;margin-top: 10px;
          border-right: 1px solid #c8c8c8;
          border-bottom: 1px solid #c8c8c8;
          border-collapse:collapse;
          tr{
            background:#FFF;
            td{
              border-left: 1px solid #c8c8c8;
              border-top:1px solid #c8c8c8;
            }
          }
        }
        .rule-update{
          font-size: 12px;text-align:right;color:#AFAFAF;margin-top: 20px;padding-top: 20px;border-top: 1px solid #EDEDED;
        }
      }
      >h2{
        padding-bottom: 10px;
        border-bottom: 1px solid #EDEDED;
      }
    }
    .promotion-tools{
      color: #3a3a3a;
      position:relative;
      h2{
        padding-bottom: 0px;
        text-align:center;
        margin-bottom: 10px;
        margin-top: 20px;
      }
      .tools{
        min-height: 250px;
        background: #FFF;
        border-radius: 5px;
        box-shadow: 0px 0px 20px #909090;
        padding: 20px 20px 50px 20px;
        display: block;
        flex-direction: row;
        .item{
          width: 100%;padding: 5px;
          img{
            width: 100%;border-radius: 5px;height: 160px;
            &:hover{
              opacity:0.9;
            }
          }
          p.title{
            font-size:16px;padding: 0 5px;
          }
          p.desc{
            font-size:12px;padding: 0 5px;color: #999;
          }
          &:hover{
            cursor:pointer;
          }
        }
      }
      .more{
        margin: 0px 0px;
        border-radius: 5px;
        line-height: 153px;
        width: 100%;
        height: 160px;
        padding: 5px;
        margin-bottom: 20px;
        background: #EEE;
        text-align: center;
      }
      .bottom{
        color:#C2C2C2;font-size:12px;text-align:right;margin-top: 10px;position:absolute;bottom: 5px;position:absolute;width:96%;bottom: 10px;left:2%;
        border-top: 1px dashed #C2C2C2;padding-top: 10px;text-align:center;
      }
    }
    .invite-rank{
      margin-top: 60px;
      p.ranktitle{
        text-align:left;font-size:12px;margin-bottom: 20px;color:#383838;
      }
      h2{
        padding-bottom: 0px;
        color: #3a3a3a;
        text-align:left;
        span{
          font-size: 14px;font-weight:normal;
          margin-left: 10px;
        }
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row td{
        background-color: transparent;
        border: none;
        border-bottom: 1px solid #e3e3e3;
        color: #151515;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-header th{
        background-color: #F90;
        color: #000;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
        background: #f7f7f7!important;
      }
      .rank-tips{
        text-align:right;color:#000;font-size:12px;margin-top:10px;
      }
      .rank-table{
        box-shadow: 0 0 25px #000;
      }
    }
  }
  }
  .ptcard-header{
  display:block;flex-direction:row;justify-content:flex-start;margin-top:0px;
  .card-img{
    width:220px;height:120px;border-radius:5px;
  }
  .desc{
    height:130px;width:100%;color:#000;padding-left: 0px;
    text-align: center;
    .title{
      font-size:16px;margin-bottom:5px;
    }
    .amount, .deadline{
      font-size:14px;color: #888;
    }
    .ivu-btn{
      margin-top:10px;
      letter-spacing: 1px;
    }
  }
  }
  .tankuang2{
    width: 100%;
  }
  .ptcard-info{
    padding-left: 0px;color:#555;line-height:25px;padding-right:0px;
    .title{
      color: rgb(85, 85, 85);line-height: 25px;padding: 5px 10px;background: #F9F9F9;border: 1px solid #EDEDED;
    }
    .detail{
      margin-top: 10px;
    }
  }
}
</style>

<style scoped>
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    background-color: transparent;
    border: none;
    border-bottom: 1px solid #e3e3e3;
    color: #151515;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-header th{
    background-color: #F90;
    color: #000;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
    background: #f7f7f7!important;
  }
  .ivu-table-wrapper .ivu-table{
    background-color: #ffffff;
  }
  
  .ivu-table:after{
    width: 0;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    border-bottom: none;
  }
</style>
