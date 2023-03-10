<template>
  <div>
    <Card>
      <p slot="title">风控视图</p>
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
        </div>
      </Row>
      <div class="baseInfo">
        <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
        <Card>
            <p slot="title">待平仓时间区间</p>
            <div id="periods" style="width: 100%; height: 300px"></div>
        </Card>
        <br>
        <Card>
            <p slot="title">三日风控区间</p>
            <div id="history" style="width: 100%; height: 300px"></div>
        </Card>
        <br>
        <Card>
            <p slot="title">未触发风控区间</p>
            <RiskGarbage ref="garbage"/>
        </Card>
      </div>
    </Card>
  </div>
</template>
<script>
import echarts from "echarts";
import {
  getRiskPeriods,
  getRiskHistoryPeriods,
  queryAllContractCoin,
  addRiskData,
} from "@/service/getData";
import RiskGarbage from "./components/RiskGarbage";
var Stomp = require("stompjs");
var SockJS = require("sockjs-client");
export default {
  name: "riskView",
  components: {
    RiskGarbage
  },
  data() {
    return {
      symbols: [],
      stompClient: null,
      symbol: "BTC/USDT",
      closePrice: 0,
      adjustDraw: false,
      period: {
        myChart: null,
        x: [],
        long: [],
        short: [],
        price: [],
        minPrice: 0,
      },
      history: {
        myChart: null,
        x: [],
        long: [],
        short: [],
        price: [],
        riskPrice: [],
        minPrice: 0,
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
          title: "请选择币种",
        });
        return false;
      }
      if (!this.form.start || !this.form.riskPrice) {
        this.$Notice.error({
          title: "请填写完整参数",
        });
        return false;
      }
      this.form.symbol = this.symbol;
      addRiskData(this.form).then((res) => {
        if (!res.code) {
          this.$Notice.success({
            title: "插入成功！",
            desc: res.message,
            duration: 10,
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
    changeSymbol() {
      this.getRiskPeriods();
      this.getRiskHistoryPeriods();
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
      var socket = new SockJS("swap/swap-ws"); //todo socket-url
      stompClient = Stomp.over(socket);
      this.stompClient = stompClient;
      stompClient.debug = false;
      stompClient.connect({}, function (frame) {
        stompClient.subscribe("/topic/swap/thumb", function (msg) {
          var resp = JSON.parse(msg.body);
          if (resp.symbol == that.symbol) {
            that.closePrice = resp.close;
          }
        });

        stompClient.subscribe("/topic/swap/risk/add/" + that.symbol, function (
          msg
        ) {
          var resp = JSON.parse(msg.body);
          that.addCurrentData(resp);
          that.initCurrentChart();
        });

        stompClient.subscribe("/topic/swap/risk/del/" + that.symbol, function (
          msg
        ) {
          var resp = JSON.parse(msg.body);
          that.delCurrentData(resp);
          that.initCurrentChart();
          that.$refs["garbage"].refresh();
        });

        stompClient.subscribe(
          "/topic/swap/risk/history/" + that.symbol,
          function (msg) {
            var resp = JSON.parse(msg.body);
            that.addHistoryData(resp);
            that.initHistoryChart();
          }
        );
      });
    },
    getRiskPeriods() {
      getRiskPeriods({ symbol: this.symbol }).then((res) => {
        if (!res.code) {
          this.initCurrentData(res.data);
          this.period.myChart && this.period.myChart.clear();
          this.period.myChart = echarts.init(
            document.getElementById("periods")
          );
          this.initCurrentChart();
        } else {
          this.$Notice.error({
            title: "读取失败",
            desc: res.message,
            duration: 10,
          });
        }
      });
    },
    getRiskHistoryPeriods() {
      getRiskHistoryPeriods({ symbol: this.symbol }).then((res) => {
        if (!res.code) {
          this.initHistoryData(res.data);
          this.history.myChart && this.history.myChart.clear();
          this.history.myChart = echarts.init(
            document.getElementById("history")
          );
          this.initHistoryChart();
        } else {
          this.$Notice.error({
            title: "读取失败",
            desc: res.message,
            duration: 10,
          });
        }
      });
    },
    addCurrentData(e) {
      this.period.x.push(
        echarts.format.formatTime("hh:mm:ss", e.start * 1000) +
          "至" +
          echarts.format.formatTime("hh:mm:ss", e.end * 1000)
      );
      this.period.long.push(e.longShare);
      this.period.short.push(e.shortShare);
      this.period.price.push(e.openPrice);
      if (this.period.minPrice == 0 || this.period.minPrice > e.openPrice) {
        this.period.minPrice = e.openPrice;
      }
    },
    delCurrentData(e) {
      let x =
        echarts.format.formatTime("hh:mm:ss", e.start * 1000) +
        "至" +
        echarts.format.formatTime("hh:mm:ss", e.end * 1000);
      for (let i = 0; i < this.period.x.length; i++) {
        if (this.period.x[i] == x) {
          this.period.x.splice(i, 1);
          this.period.long.splice(i, 1);
          this.period.short.splice(i, 1);
          this.period.price.splice(i, 1);
        }
      }
    },
    initCurrentData(data) {
      this.period.x = [];
      this.period.long = [];
      this.period.short = [];
      this.period.price = [];
      this.period.minPrice = 0;
      data.forEach((e) => {
        this.addCurrentData(e);
      });
    },
    initCurrentChart() {
      let option = {
        color: ["#41b371", "#d74e5a", "#004564"],
        grid: {
          left: 70,
          right: 100,
          bottom: "35%",
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        legend: {
          data: ["多仓", "空仓", "开盘均价"],
        },
        xAxis: [
          {
            type: "category",
            data: this.period.x,
            axisPointer: {
              type: "shadow",
              interval: 0,
              rotate: 40,
            },
            axisLabel: {
              interval: 0,
              rotate: 40,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "张数",
            axisLabel: {
              formatter: "{value} 张",
            },
            splitLine: {
              show: false,
            },
            min: function (value) {
              return value.min;
            },
            max: function (value) {
              return value.max;
            },
          },
          {
            type: "value",
            name: "价格",
            axisLabel: {
              formatter: "{value} USDT",
            },
            splitLine: {
              show: false,
            },
            min: function (value) {
              return value.min;
            },
            max: function (value) {
              return value.max;
            },
          },
        ],
        series: [
          {
            name: "多仓",
            type: "bar",
            stack: "one",
            data: this.period.long,
          },
          {
            name: "空仓",
            type: "bar",
            stack: "one",
            data: this.period.short,
          },
          {
            name: "开盘均价",
            type: "line",
            yAxisIndex: 1,
            data: this.period.price,
          },
        ],
      };
      // 启动chart
      this.period.myChart.setOption(option);
    },
    addHistoryData(e) {
      this.history.x.push(
        echarts.format.formatTime("dd日 hh:mm:ss", e.start * 1000) +
          "至" +
          echarts.format.formatTime("mm:ss", e.end * 1000)
      );
      this.history.long.push(e.longShare);
      this.history.short.push(e.shortShare);
      this.history.price.push(e.openPrice);
      this.history.riskPrice.push(e.riskPrice);
      if (this.history.minPrice == 0 || this.history.minPrice > e.openPrice) {
        this.history.minPrice = e.openPrice;
      }
      if (this.history.minPrice == 0 || this.history.minPrice > e.riskPrice) {
        this.history.minPrice = e.riskPrice;
      }
    },
    initHistoryData(data) {
      this.history.x = [];
      this.history.long = [];
      this.history.short = [];
      this.history.price = [];
      this.history.riskPrice = [];
      this.history.minPrice = 0;
      data.forEach((e) => {
        this.addHistoryData(e);
      });
    },
    initHistoryChart() {
      let option = {
        color: ["#41b371", "#d74e5a", "#004564", "#f0ac19"],
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            crossStyle: {
              color: "#999",
            },
          },
        },
        grid: {
          left: 70,
          right: 100,
          bottom: "35%",
        },
        legend: {
          data: ["多仓", "空仓", "开盘均价", "风控价格"],
        },
        xAxis: [
          {
            type: "category",
            data: this.history.x,
            axisPointer: {
              type: "shadow",
            },
            axisLabel: {
              interval: 0,
              rotate: 40,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "张数",
            min: function (value) {
              return value.min;
            },
            max: function (value) {
              return value.max;
            },
            splitLine: {
              show: false,
            },
            axisLabel: {
              formatter: "{value} 张",
            },
          },
          {
            type: "value",
            name: "价格",
            min: function (value) {
              return value.min;
            },
            max: function (value) {
              return value.max;
            },
            splitLine: {
              show: false,
            },
            // min: this.history.minPrice - 50,
            axisLabel: {
              formatter: "{value} USDT",
            },
          },
        ],
        series: [
          {
            name: "多仓",
            type: "bar",
            stack: "one",
            data: this.history.long,
          },
          {
            name: "空仓",
            type: "bar",
            stack: "one",
            data: this.history.short,
          },
          {
            name: "开盘均价",
            type: "line",
            yAxisIndex: 1,
            data: this.history.price,
          },
          {
            name: "风控价格",
            type: "line",
            yAxisIndex: 1,
            data: this.history.riskPrice,
          },
        ],
      };
      // 启动chart
      this.history.myChart.setOption(option);
    },
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