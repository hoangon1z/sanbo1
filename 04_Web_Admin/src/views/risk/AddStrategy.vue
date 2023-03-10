<template>
  <div>
    <Card>
      <p slot="title">
        添加/编辑策略组
        <Button size="small" @click="back">
          返回
        </Button>
      </p>
      <div class="formWrapper">
        <Form :model="activityForm" :label-width="250" class="form">
          <FormItem label="策略名称:">
            <Input v-model="activityForm.name" />
          </FormItem>
          <FormItem label="收益权重:">
            <InputNumber v-model="activityForm.profitWeight" :min="0"/>
            <span
              style="font-size:10px;color:#FF0000;"
            >&nbsp;&nbsp;&nbsp;&nbsp;单位(USDT)，风控强度为1时 风控触发概率=1-权重/预计收益</span>
          </FormItem>
          <FormItem label="距离平仓时间(秒):">
            <InputNumber
              :max="10"
              :min="1"
              v-model="activityForm.diffCloseTime"
            />
          </FormItem>
          <FormItem label="距离开仓点位(0.01为一个点位):">
            <InputNumber
              :step="0.000001"
              :max="0.01"
              :min="0"
              v-model="activityForm.diffOpenPointMin"
            />
            至
            <InputNumber
              :step="0.000001"
              :max="0.01"
              :min="0"
              v-model="activityForm.diffOpenPointMax"
            />
          </FormItem>
          <FormItem label="智能平仓点位:">
            <i-switch v-model="activityForm.autoClosePoint" size="default"></i-switch>
            <!-- <RadioGroup v-model="activityForm.autoClosePoint">
              <Radio :label="false">
                <em>否</em>
              </Radio>
              <Radio :label="true">
                <em>是</em>
              </Radio>
            </RadioGroup> -->
          </FormItem>
          <FormItem v-if="!activityForm.autoClosePoint" label="距离平仓点位(0.01为一个点位):">
            <InputNumber
              :step="0.001"
              :max="0.1"
              :min="0"
              v-model="activityForm.diffClosePoint"
            />
          </FormItem>
          <FormItem label="最小平仓张数:">
            <InputNumber :min="1" v-model="activityForm.minShare" />
          </FormItem>
          <FormItem label="恢复报价时间(秒):">
            <InputNumber :max="10" :min="1" v-model="activityForm.resumeTime" />
          </FormItem>
          <FormItem label="盈利自动关闭:">
            <i-switch v-model="activityForm.profitAutoClose" size="default"></i-switch>
            <!-- <RadioGroup v-model="activityForm.profitAutoClose">
              <Radio :label="false">
                <em>否</em>
              </Radio>
              <Radio :label="true">
                <em>是</em>
              </Radio>
            </RadioGroup> -->
          </FormItem>
          <FormItem label="用户组:" v-if="!this.activityForm.isDefault">
            <Button type="info" @click="addMember">添加</Button>
            <div>
              <Tag
                v-for="(member, index) in activityForm.members"
                :key="member.id"
                @on-close="removeMember(index)"
                type="dot"
                closable
                color="green"
                style="margin: 5px 5px 0 0"
                >{{ member.memberName }}</Tag
              >
            </div>
          </FormItem>
        </Form>
      </div>
      <div class="btnWrapper">
        <Button
          type="success"
          :disabled="false"
          long
          size="large"
          @click="submit"
        >
          提交
        </Button>
      </div>
      <hr>
      <p style="font-size:11px;">注意1：合理设置距离平仓点位，距离平仓点位不宜过大，会出现明显的k线长(阳/阴)线情况</p>
      <p style="font-size:11px;">注意2：如果客户盈利在距离开仓点位的区间，风控不起作用</p>
      <p style="font-size:11px;">注意3：如果客户张数小于最小张数，风控不起作用</p>
      <p style="font-size:11px;">注意4：启动盈利自动关闭，则再客户已经未盈利的情况下，风控不起作用</p>
      <p style="font-size:11px;">注意5：启动只能平仓点位，系统在满足风控条件下，智能判断当前浮动点位设置价格，避免出现明显长(阳/阴)线情况</p>
    </Card>
    <Modal
      v-model="addMemberModal"
      width="1050px"
      title="添加用户组"
      @on-ok="confirmSelectedMember"
    >
      <MemberList
        ref="memberSelect"
        :selected="activityForm.members"
        v-if="addMemberModal"
        :to-sid="queryDetailId"
        :select-able="true"
        :search-able="true"
      />
    </Modal>
    <Modal
      class="auditModel"
      v-model="loginPassModal"
      title="请输入登录密码"
      width="350"
      @on-cancle="loginPW = ''"
      @on-ok="confirmLoginPass"
    >
      <Input v-model="loginPW" type="password" placeholder="请输入登录密码" />
    </Modal>
  </div>
</template>

<script>
import dtime from "time-formater";

import {
  addRiskStrategy,
  getOneRiskStrategy,
} from "@/service/getData";
import { getStore, removeStore, setStore } from "@/config/storage";
import MemberList from "./components/MemberList";

export default {
  components: {
    MemberList,
  },
  data() {
    return {
      activityForm: {
        name: "",
        profitWeight: 0,
        diffCloseTime: 3,
        diffOpenPointMin: 0.01,
        diffOpenPointMax: 0.03,
        diffClosePoint: 0.01,
        minShare: 1,
        resumeTime: 3,
        members: [],
        status: 0,
        isDefault: false,
        profitAutoClose: true,
        autoClosePoint: false
      },
      addMemberModal: false,
      loginPassModal: false,
      loginPW: "",
      ifAdd: true,
      queryDetailId: null,
      lang: "CN",
      memberSearch: {
        pageNo: 1,
        pageSize: 40,
        memberName: "",
      },
    };
  },
  methods: {
    confirmSelectedMember() {
      let adds = this.$refs["memberSelect"].getSelected();
      if (adds.size == 0) {
        return;
      }
      this.activityForm.members = Array.from(
        new Set(this.activityForm.members.concat(adds))
      );
    },
    addMember() {
      this.addMemberModal = true;
    },
    confirmLoginPass() {
      // if(!this.ifAdd){
      //   this.activityForm.id = this.queryDetailId;
      //   this.activityForm.password = this.loginPW;
      //   modify(this.activityForm)
      //   .then( res => {
      //     if (!res.code) {
      //       this.$Message.success('操作成功!');
      //       this.$router.push('/risk/strategy');
      //     } else this.$Message.error('异常错误!');
      //   });
      // }else{
      this.activityForm.id = this.queryDetailId;
      addRiskStrategy(this.activityForm, this.loginPW).then((res) => {
        if (!res.code) {
          this.$Message.success("操作成功!");
          this.$router.push("/risk/strategy");
        } else this.$Message.error("异常错误!");
      });
      // }
    },
    back() {
      this.$router.push("/risk/strategy");
    },
    submit() {
      if (!this.activityForm.name || this.activityForm.name == "") {
        this.$Message.error("请填写策略名称!");
        return;
      }
      if (!this.activityForm.isDefault && this.activityForm.members.length == 0) {
        this.$Message.error("请至少选择一个用户!");
        return;
      }
      this.loginPassModal = true;
    },
    removeMember(index) {
      this.activityForm.members.splice(index, 1);
    },
  },
  created() {
    this.queryDetailId = getStore("strategyId");
    if (!!this.queryDetailId) {
      // alert( this.queryDetailId)
      this.queryDetailId = parseInt(this.queryDetailId);
      this.ifAdd = false;
      this.activityForm.id = this.queryDetailId;

      getOneRiskStrategy(this.queryDetailId).then((res) => {
        this.activityForm.id = this.queryDetailId;
        this.activityForm.name = res.data.name;
        this.activityForm.profitWeight = res.data.profitWeight;
        this.activityForm.diffCloseTime = res.data.diffCloseTime;
        this.activityForm.diffOpenPointMin = res.data.diffOpenPointMin;
        this.activityForm.diffOpenPointMax = res.data.diffOpenPointMax;
        this.activityForm.diffClosePoint = res.data.diffClosePoint;
        this.activityForm.minShare = res.data.minShare;
        this.activityForm.resumeTime = res.data.resumeTime;
        this.activityForm.isDefault = res.data.isDefault;
        this.activityForm.status = res.data.status;
        this.activityForm.autoClosePoint = res.data.autoClosePoint;
        this.activityForm.profitAutoClose = res.data.profitAutoClose;
        if (!this.activityForm.isDefault) {
          this.activityForm.members = res.data.members;
        }
      });
    } else this.ifAdd = true;
  },
};
</script>

<style lang="less" scoped>
.wrapper {
  margin: auto;
  width: 70%;
}
.formWrapper {
  margin: auto;
  width: 70%;
}
.btnWrapper {
  margin: 40px auto;
  width: 30%;
}
.formWrapper {
  p,
  div {
    margin: 0px 0 5px 0;
    Input {
      display: inline-block;
      width: 90px;
    }
  }
}
.title {
  display: inline-block;
  width: 200px;
}
.circleWrapper {
  position: absolute;
  z-index: 10;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
  p {
    font-size: 30px;
    font-family: "黑体";
    color: #fff;
    text-align: center;
    line-height: 550px;
  }
}

em {
  font-style: normal;
}
</style>

