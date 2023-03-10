<template>
  <div>
    <Card>
      <p slot="title">
        整体配置
      </p>
      <div class="formWrapper">
        <Form :model="activityForm" :label-width="250" class="form">
          <FormItem label="平台盈亏比:">
            <Input v-model="profitRate" readonly/>
          </FormItem>
          <FormItem label="平台盈利金额:">
            <InputNumber v-model="activityForm.profit" />
          </FormItem>
          <FormItem label="平台亏损金额:">
            <InputNumber v-model="activityForm.loss" />
          </FormItem>
          <FormItem label="整体盈亏比例:">
            <InputNumber :step="0.01"
              :max="1"
              :min="0" v-model="activityForm.spotRate" />
          </FormItem>
          <FormItem label="最大亏损比例:">
            <InputNumber :step="0.01"
              :max="1"
              :min="0" v-model="activityForm.maxLossRate" />
          </FormItem>
          <FormItem label="最大盈利比例:">
            <InputNumber :step="0.01"
              :max="1"
              :min="0" v-model="activityForm.maxProfitRate" />
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
      <p style="font-size:11px;">注意1：初始盈亏金额，用于插件初始化，每次客户交易的盈亏会记录上，用于判断系统的整体盈亏比例</p>
      <p style="font-size:11px;">注意2：整体盈亏比例：希望系统的盈亏比例</p>
      <p style="font-size:11px;">注意3：如系统盈亏比例0.5，最大盈利比例0.3，最大亏损比例0.2，当平台盈利率达到50%，风控自动减弱，当平台盈利率达到80%(0.5+0.3)，风控自动停止，当平台盈利率到30%(0.5-0.2)，风控自动加强</p>
      <p style="font-size:11px;">注意4：风控触发概率=(1-策略最小手数/当前手数)*风控强度</p>
    </Card>
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
  getRiskSetting,
  setRiskSetting,
} from "@/service/getData";
export default {
  data() {
    return {
      activityForm: {
        profit: 0,
        loss: 0,
        spotRate: 0.5,
        maxLossRate: 0.2,
        maxProfitRate: 0.3
      },
      loginPassModal: false,
      loginPW: ""
    };
  },
  computed: {
    profitRate: function () {
      let total = this.activityForm.profit + this.activityForm.loss;
      if (total<=0) {
        return 0;
      }
      let rate = parseFloat(this.activityForm.profit/total*100).toFixed(2);
      return rate + "%";
    }
  },
  methods: {
    confirmLoginPass() {
      // setRiskSetting(this.activityForm, this.loginPW).then((res) => {
      //   if (!res.code) {
      //     this.$Message.success("操作成功!");
      //   } else this.$Message.error("异常错误!");
      // });
    },
    submit() {
      // this.loginPassModal = true;
      setRiskSetting(this.activityForm).then((res) => {
        if (!res.code) {
          this.$Message.success("操作成功!");
        } else this.$Message.error("异常错误!");
      });
    },
  },
  created() {
      getRiskSetting().then((res) => {
        this.activityForm.profit = res.data.profit;
        this.activityForm.loss = res.data.loss;
        this.activityForm.spotRate = res.data.spotRate;
        this.activityForm.maxLossRate = res.data.maxLossRate;
        this.activityForm.maxProfitRate = res.data.maxProfitRate;
      });
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

