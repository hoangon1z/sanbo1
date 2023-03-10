<template>
  <div>
    <Card>
      <p slot="title">实时监控</p>
      <Row class="priceSectionWrapper clearfix">
        <div class="orderStatus">
          <span>交易对：</span>
          <Select v-model="symbol" @on-change="changeSymbol">
            <Option v-for="(item, k) in symbols" :value="item" :key="k">{{
              item
            }}</Option>
          </Select>
        </div>
        <div class="orderStatus">
          <span>实时价格：</span>
          <Button type="dashed" style="width: 120px">{{ closePrice }}</Button>
          <Poptip width="400" height="500" placement="bottom">
            <Button type="ghost">插入价格</Button>
            <div class="api" slot="content">
              <Form :model="form" label-position="left">
                <FormItem label="风控价格">
                  <InputNumber
                    v-model="form.riskPrice"
                    @on-change="focusChangePrice"
                    @on-focus="focusChangePrice"
                  />
                </FormItem>
                <FormItem label="开始时间">
                  <TimePicker
                    type="time"
                    placeholder="选择时间"
                    style="width: 168px"
                    v-model="form.start"
                  ></TimePicker>
                </FormItem>
                <FormItem label="持续时间">
                  <InputNumber v-model="form.last" :min="1" :max="10" />
                </FormItem>
                <FormItem>
                  <Button type="primary" @click="addRiskData">确定</Button>
                </FormItem>
              </Form>
            </div>
          </Poptip>
        </div>
      </Row>
      <div class="baseInfo">
        <CurrentOrder ref="order" :symbol="symbol"/>
      </div>
    </Card>
  </div>
</template>
<script>
import CurrentOrder from "./components/CurrentOrder";
import {
  queryAllContractCoin,
  addRiskData,
} from "@/service/getData";
var Stomp = require("stompjs");
var SockJS = require("sockjs-client");
export default {
  name: "riskPosition",
  components: {
    CurrentOrder
  },
  data() {
    return {
      symbols: [],
      stompClient: null,
      symbol: "BTC/USDT",
      closePrice: 0,
      adjustDraw: false,
      form: {
        start: "",
        last: 6,
        riskPrice: 0,
        priceOn: true,
      },
    };
  },
  watch: {},
  mounted() {
    this.queryAllContractCoin();
    this.changeSymbol();
  },
  beforeDestroy() {
    if (this.stompClient) {
      this.stompClient.ws.close();
    }
  },
  methods: {
    addRiskData() {
      if (!this.symbol) {
        this.$Notice.error({
            title: "请选择币种"
          });
          return false;
      }
      if (!this.form.start || !this.form.riskPrice) {
        this.$Notice.error({
            title: "请填写完整参数"
          });
          return false;
      }
      this.form.symbol = this.symbol;
      addRiskData(this.form).then((res) => {
        if (!res.code) {
          this.$Notice.success({
              title: "插入成功！",
              desc: res.message,
              duration: 10
          });
        } else {
          this.$Notice.error({
            title: "插入失败",
            desc: res.message,
            duration: 10,
          });
        }
      });
    },
    focusChangePrice() {
      this.form.priceOn = false;
    },
    changeSymbol() {
      this.startSocket();
    },
    queryAllContractCoin() {
      queryAllContractCoin().then((res) => {
        let data = res.data;
        let set = new Set();
        data.forEach((e) => {
          set.add(e.symbol + "");
        });
        this.symbols = Array.from(set);
      });
    },
    startSocket() {
      if (this.stompClient) {
        this.stompClient.ws.close();
      }
      var stompClient = null;
      var that = this;
      var socket = new SockJS("swap/swap-ws");
      stompClient = Stomp.over(socket);
      this.stompClient = stompClient;
      stompClient.debug = false;
      stompClient.connect({}, function (frame) {
        // 实例化socket
        stompClient.subscribe("/topic/swap/thumb", function (msg) {
          var resp = JSON.parse(msg.body);
          if (resp.symbol == that.symbol) {
            that.closePrice = resp.close;
            if (that.form.priceOn) {
              that.form.riskPrice = resp.close;
            }
          }
        });
        stompClient.subscribe("/topic/swap/refresh/" + that.symbol, function (msg) {
          that.$refs["order"].refresh();
        });
      });
    }
  },
};
</script>
<style lang="less" scoped>
.btns {
  text-align: center;
}
.switchTab {
  margin: 20px 0;
}
Input,
.ivu-select.ivu-select-single {
  width: 150px;
}
.priceSectionWrapper {
  margin-bottom: 30px;
  .ivu-input-wrapper.ivu-input-type {
    width: 60px;
  }
  .priceSection {
    float: left;
  }
  .orderStatus {
    margin-left: 20px;
    float: left;
  }
  .btns {
    margin-left: 10px;
    float: left;
    height: 30px;
    line-height: 30px;
  }
}
.tips {
  color: red;
}
</style>