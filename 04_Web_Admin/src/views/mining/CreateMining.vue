<template>
<div>
  <Card>
    <p slot="title">
      添加/编辑矿机
    </p>
    <div class="formWrapper">
      <Form :model="activityForm" :label-width="250" class='form'>
          <FormItem label="矿机名称:">
              <Input v-model="activityForm.miningName"></Input>
          </FormItem>
          <FormItem label="矿机简介:">
            <Input v-model="activityForm.detail"></Input>
          </FormItem>
          <FormItem label="算力KB/S:">
            <Input v-model="activityForm.power"></Input>
          </FormItem>
          <FormItem label="要求一级好友不少于:">
                  <Input v-model="activityForm.leveloneCount"></Input>
          </FormItem>
          <FormItem label="挖矿周期:" >
                  <RadioGroup v-model="activityForm.miningPeriod">
                    <Radio :label="0"><em>天</em></Radio>
                    <Radio :label="1"><em>周</em></Radio>
                    <Radio :label="2"><em>月</em></Radio>
                    <Radio :label="3"><em>年</em></Radio>
                  </RadioGroup>
          </FormItem>
           <FormItem label="多少周期挖一次:" >
              <Input v-model="activityForm.miningDays"></Input>
          </FormItem>

             <FormItem label="每次产出:" >
              <Input v-model="activityForm.miningDaysprofit"></Input>
          </FormItem>
            <FormItem  label="发行价格:">
              <Input v-model="activityForm.price"></Input>
          </FormItem>
          <FormItem  label="价格精度:">
              <Input v-model="activityForm.priceScale"></Input>
          </FormItem>
            <FormItem  label="手续费:">
              <Input v-model="activityForm.fee"></Input>
          </FormItem>
          <FormItem label="挖矿次数:">
              <Input v-model="activityForm.miningTimes"></Input>
          </FormItem>
          <!-- <FormItem label="产出币种:">
              <Input v-model="activityForm.miningUnit"></Input>
          </FormItem> -->
          <FormItem label="接受币种:" >
              <Input v-model="activityForm.acceptUnit"></Input>
          </FormItem>
          <FormItem label="数量精度:" >
              <Input v-model="activityForm.amountScale" ></Input>
          </FormItem>
          <FormItem label="单人限购总量下限:" >
              <Input v-model="activityForm.minLimitAmout" ></Input>
          </FormItem>
          <FormItem label="单人限购总量上限:" >
              <Input v-model="activityForm.maxLimitAmout" ></Input>
          </FormItem>
            <FormItem label="单人购买次数" >
              <Input v-model="activityForm.limitTimes" ></Input>
          </FormItem>
          <FormItem label="邀请(购买)产能增加(0.1 = 10%):" >
              <Input v-model="activityForm.miningInvite" placeholder="如：0.01(即基础产能增加1%)，0则不增加"></Input>
          </FormItem>
          <FormItem label="产能增加上限(0.1 = 10%):" >
              <Input v-model="activityForm.miningInvitelimit" placeholder="如：0.1(即基础产能增加上限为10%)，0则无上限"></Input>
          </FormItem>
          <FormItem label="列表矿机图:">
            <Upload :action="basicUrl+'admin/common/upload/oss/image'"
                    :on-success = 'uploadSuccessedSmall'
                    :on-error = "uploadFailed"
                    :on-progress = "imageUploading"
                    :show-upload-list = "false">
              <Button type="ghost" icon="ios-cloud-upload-outline">添加图片</Button>
            </Upload>
          </FormItem>

          <FormItem label="矿机图:">
              <Input @on-blur="listenValUrl" v-model="activityForm.smallImageUrl"  style="width: 100%;"></Input>
          </FormItem>

          <FormItem label="Banner图片:">
            <Upload :action="basicUrl+'admin/common/upload/oss/image'"
                    :on-success = 'uploadSuccessedBanner'
                    :on-error = "uploadFailed"
                    :on-progress = "imageUploading"
                    :show-upload-list = "false">
              <Button type="ghost" icon="ios-cloud-upload-outline">添加图片</Button>
            </Upload>
          </FormItem>

          <FormItem label="Banner图地址:">
              <Input @on-blur="listenValUrl" v-model="activityForm.bannerImageUrl"  style="width: 100%;"></Input>
          </FormItem>
           <FormItem label="矿机详情:">
            <smeditor
              :config="config"
              ref="smeditor"
              @isUploading="ifUploading"
              style="width:100%;"
            ></smeditor>
          </FormItem>
  


          
      </Form>
      <div class="circleWrapper" v-show="ifShowPercentCircle">
        <i-circle :percent="percentage" :size="60" :stroke-width="10">
          <span class="demo-Circle-inner" style="font-size:24px">{{ percentage.toFixed(1) }}%</span>
        </i-circle>
      </div>
    </div>
    <div class="btnWrapper">
      <Button type="success" :disabled="false" long size='large' @click="submit">
        提交
      </Button>
    </div>
  </Card>

    <Modal
          class="auditModel"
          v-model="loginPassModal"
          title="请输入登录密码"
          width="350"
          @on-cancle="loginPW = ''"
          @on-ok="confirmLoginPass">
          <Input v-model="loginPW" type="password" placeholder="请输入登录密码"></Input>
   </Modal>
</div>

</template>

<script>
import dtime from 'time-formater'
import smeditor from '@/SMEditor/SMEditor.vue'

import { BASICURL,  addMining, miningModify as modify, miningDetail } from '@/service/getData';
import { getStore, removeStore, setStore } from '@/config/storage';


  export default {

    data () {
      return {
        activityForm:{
            detail:"",
            miningName:"",
            title: "",
            detail: "",
            status: "1",
            startTime: "",
            endTime: "",
            totalSupply: "0",
            price: "",
            priceScale: "2",
            unit: "",
            acceptUnit: "",
            amountScale: "2",
            maxLimitAmout: 0,
            minLimitAmout: 0,
            limitTimes: 0,
            activityLink: "",
            noticeLink: "",
            settings: "",
            smallImageUrl: "",
            bannerImageUrl: "",
            leveloneCount: 0,
            holdUnit: "",
            holdLimit: 0,
            miningDays: 1,
            miningDaysprofit: "",
            power: 0,
            miningUnit: "KICK",
            miningTimes: 10,
            miningInvite: 0,
            miningInvitelimit: 0,
            miningPeriod: 0,
            content: ""
        },
        loginPassModal: false,
        loginPW: "",
        ifShowPercentCircle: false,
        percentage: 0,
        picUrl: "",
        picUrlIcon: false,
        basicUrl: BASICURL,

        uploading: false,
        ifAdd: true,
        queryDetailId: null,
        lang: "CN",
        basicUrl: BASICURL,
        config :{
          uploadUrl: `${BASICURL}admin/common/upload/oss/image`,
          uploadName: 'file',
          parentName: 'mining',
          uploadParams: {

          },
          // 上传成功回调
          uploadCallback: (data) => {
            this.uploading = false;
            if(!data.code){
              this.$Message.success('上传成功!');
              return data.data;
            }else{
              this.$Message.error('上传失败!');
            }
          },
          // 上传失败回调, 可选
          uploadFailed: (err) => {
            this.uploading = false;
            console.log(err)
            this.$Message.error('上传失败!');
          }
        }
      }
    },
    methods: {
      confirmLoginPass(){
        console.log("passwd");
        if(!this.ifAdd){
          this.activityForm.id = this.queryDetailId;
          this.activityForm.password = this.loginPW;
          this.activityForm.startTime = dtime(this.activityForm.startTime).format('YYYY-MM-DD HH:mm:ss');
          this.activityForm.endTime = dtime(this.activityForm.endTime).format('YYYY-MM-DD HH:mm:ss');
          modify(this.activityForm)
          .then( res => {
            if (!res.code) {
              this.$Message.success('操作成功!');
              this.$router.push('/mining/Mining');
            } else this.$Message.error('异常错误!');
          });
        }else{
          this.activityForm.startTime = dtime(this.activityForm.startTime).format('YYYY-MM-DD HH:mm:ss');
          this.activityForm.endTime = dtime(this.activityForm.endTime).format('YYYY-MM-DD HH:mm:ss');
          addMining(this.activityForm)
          .then( res => {
            if (!res.code) {
              this.$Message.success('操作成功!');
              this.$router.push('/mining/Mining');
            } else this.$Message.error('异常错误!');
          });
        }
      },
      submit(){
        this.$refs.smeditor.$emit("saveInner");
        this.activityForm.content = getStore('smeditor');
        this.loginPassModal = true;
      },
      ifUploading(val) {
        this.uploading = val;
      },
      changeAdStatus(val) {
        this.status = val*1;
      },
      listenValUrl() {
          this.picUrlIcon = true;
      },
      imageUploading(event, file, fileList) {
          this.ifShowPercentCircle = true;
          this.percentage = file.percentage;
      },
      uploadSuccessedSmall(response, file, fileList) {
          this.activityForm.smallImageUrl = response.data;
          this.ifShowPercentCircle = false;
          this.$Message.success('上传成功');
      },
      uploadSuccessedBanner(response, file, fileList) {
          this.activityForm.bannerImageUrl = response.data;
          this.ifShowPercentCircle = false;
          this.$Message.success('上传成功');
      },
      uploadFailed(error, file, fileList) {
          this.ifShowPercentCircle = false;
          this.$Message.error('上传失败');
      }
    },
    created() {

      removeStore('smeditor');


      this.queryDetailId = getStore('manageID');

      if (!!this.queryDetailId) {
                         // alert( this.queryDetailId)

        this.ifAdd = false;
        this.activityForm.id = this.queryDetailId;

        miningDetail(this.queryDetailId)
       .then(res => {
            this.activityForm.id = this.queryDetailId;
            this.activityForm.miningName = res.data.miningName;
            this.activityForm.detail = res.data.detail;
            this.activityForm.status = res.data.status;
            this.activityForm.price = res.data.price;
            this.activityForm.priceScale = res.data.priceScale;
            this.activityForm.acceptUnit = res.data.acceptUnit;
            this.activityForm.amountScale = res.data.amountScale;
            this.activityForm.miningUnit = res.data.miningUnit;
            this.activityForm.maxLimitAmout = res.data.maxLimitAmout;
            this.activityForm.minLimitAmout = res.data.minLimitAmout;
            this.activityForm.limitTimes = res.data.limitTimes;
            this.activityForm.smallImageUrl = res.data.smallImageUrl;
            this.activityForm.bannerImageUrl = res.data.bannerImageUrl;
            this.activityForm.noticeLink = res.data.noticeLink;
            this.activityForm.activityLink = res.data.activityLink;
            this.activityForm.leveloneCount = res.data.leveloneCount;
            this.activityForm.holdUnit = res.data.holdUnit;
            this.activityForm.holdLimit = res.data.holdLimit;
            this.activityForm.miningDays = res.data.miningDays;
            this.activityForm.miningDaysprofit = res.data.miningDaysprofit;
            this.activityForm.power = res.data.power;
            this.activityForm.miningUnit = res.data.miningUnit;
            this.activityForm.miningTimes = res.data.miningTimes;
            this.activityForm.miningInvite = res.data.miningInvite;
            this.activityForm.miningInvitelimit = res.data.miningInvitelimit;
            this.activityForm.miningPeriod = res.data.miningPeriod;
             this.activityForm.fee = res.data.fee;
            // console.log(this.activityForm)
            setStore('smeditor', res.data.content);
       });
      }else  this.ifAdd = true;
    },

    components: {
      smeditor
    }
  }
</script>

<style lang="less" scoped>
  .wrapper{
    margin: auto;
    width: 70%;
  }
  .formWrapper{
    margin: auto;
    width: 70%;
  }
  .btnWrapper{
    margin: 40px auto;
    width: 30%;
  }
  .formWrapper{
    p, div{
      margin: 0px 0 5px 0;
      Input{
        display: inline-block;
        width: 90px;
      }
    }
  }
  .title{
    display: inline-block;
    width: 200px;
  }
  .circleWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .3);
    p{
      font-size: 30px;
      font-family: '黑体';
      color: #fff;
      text-align: center;
      line-height: 550px;
    }
  }

  em{
    font-style: normal;
  }
</style>

