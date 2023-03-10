<template>
  <div>
    <Card>
      <p slot="title">控盘机器人</p>
      <Row class="priceSectionWrapper clearfix">
        <div class="orderStatus">
          <span>交易对：</span>
          <Select v-model="symbol">
            <Option
              v-for="item in symbols"
              :value="item.symbol"
              :key="item.symbol"
            >{{ item.symbol }}</Option>
          </Select>
        </div>
        <div class="orderStatus">
          <span>日期：</span>
          <DatePicker type="date" placement="bottom-end" v-model="date" placeholder="选择时间区间"></DatePicker>
        </div>
        <div class="orderStatus">
          <span>最高限价：</span>
          <Input placeholder="最高限价" v-model="height" />
          <Button type="info" @click="changeHeight">确定</Button>
        </div>
        <div class="orderStatus">
          <span>统一价格：</span>
          <Input placeholder="" v-model="price" />
          <Button type="info" @click="setAllPrice">设置</Button>
        </div>
        <!-- <div class="orderStatus">
            <span>控制粒度：</span>
            <Radio-group v-model="granularity" type="button" @on-change="changeHeight">
                <Radio :label="15">15分钟</Radio>
                <Radio :label="30">30分钟</Radio>
                <Radio :label="60">1小时</Radio>
                <Radio :label="120">2小时</Radio>
                <Radio :label="240">4小时</Radio>
            </Radio-group>
        </div>-->
        <div class="btns" style="float:right;">
          <Button type="info" @click="initKline">绘制预览k线图</Button>
        </div>
      </Row>
      <div class="baseInfo">
        <!-- 为ECharts准备一个具备大小（宽高）的Dom -->
        <div id="robot_mline" style="width: 100%;height:300px;"></div>
        <div id="robot_kline" style="width: 100%;height:300px;"></div>
      </div>
      <div class="btns">
        <Button type="info" @click="save">保存</Button>
      </div>
    </Card>
  </div>
</template>
<script>
import echarts from "echarts";
import { saveRobotQuotas, getRobotQuotas } from "@/service/getData";
export default {
  name: "exchangeRobot",
  data() {
    return {
      symbol: "",
      symbols: [
        {
          symbol: "KICK/USDT",
        },
      ],
      date: "",
      // chart
      myChart: null,
      myKchart: null,
      symbolSize: 10,
      times: [],
      data: [],
      kData: [],
      option: null,
      kOption: null,
      height: 10,
      granularity: 60,
      init: true,
      price: null
    };
  },
  watch: {
    symbol (val) {
      let height = localStorage.getItem(val+"-height");
      if (height) {
        this.height = height;
        this.changeHeight();
      }
      this.getData();
    },
    date (val) {
      this.getData();
    },
  },
  mounted() {
    this.initData();
    this.$nextTick(() => {
      this.refreshChart();
      this.initKline();
    });
  },
  methods: {
    setAllPrice() {
      this.price = parseFloat(this.price);
      if (!this.price) {
        return;
      }
      this.data.forEach(e=>{
        e[1] = this.price;
      })
      this.refreshChart();
    },
    getData() {
      if (!this.symbol || this.symbol=="") {
        return false;
      }
      let date = this.dateToString(new Date(this.date));
      if (!date || date =="" || date=="NaN-NaN-NaN") {
        return false;
      }
      
      getRobotQuotas({symbol: this.symbol, date: date}).then(res=>{
        if (!res.code) {
          if (res.data.length > 0) {
            this.times = [];
            this.data = [];
            res.data.forEach(e=>{
              var time = new Date(2020, 0, 1).getTime() + e.time*60*1000;
              var x = echarts.format.formatTime("hh:mm", time);
              this.times.unshift(x);
              this.data.unshift([x, e.price]);
            })
            this.init = false;
          } else {
            this.initData();
          }
          this.refreshChart();
        } else {
          this.$Notice.error({
            title: "读取失败",
            desc: res.message,
            duration: 10,
          });
          this.initData();
        }
      })
    },
    dragEnable() {
      // 在图上添加拖拽图层
      var that = this;
      var graphic = echarts.util.map(this.data, function (item, dataIndex) {
        // 此处需要把两条曲线的数组拼成一个数组进行遍历，多条曲线类似
        return {
          type: "circle",
          position: that.myChart.convertToPixel("grid", item),
          shape: {
            cx: 0,
            cy: 0,
            r: that.symbolSize / 2,
          },
          invisible: true,
          draggable: true,
          ondrag: echarts.util.curry(function (dataIndex) {
            // 拖动的处理函数，因为把所有的曲线数组拼成了一个数组，所以添加的图层是一个整体，此处需要拆分图层，还原为两条曲线
            let x = that.data[dataIndex][0];
            let y = that.myChart.convertFromPixel("grid", this.position)[1];
            that.data[dataIndex] = [x, y]; // 将坐标值(x, y)还原为数组的项[a,b]
            // 更新图表
            that.myChart.setOption({
              series: [
                {
                  id: "a",
                  data: that.data,
                },
              ],
            });
          }, dataIndex), // 添加拖动的回调
          onmousemove: echarts.util.curry(that.showTooltip, dataIndex),
          ondragend: echarts.util.curry(that.onPointDraggEnd, dataIndex), //拖动完成事件
          z: 100,
        };
      });
      // console.log(graphic);
      this.myChart.setOption({
        graphic: graphic,
      });
    },
    showTooltip(dataIndex) {
      this.myChart.dispatchAction({
          type: 'showTip',
          seriesIndex: 0,
          dataIndex: dataIndex
      });
    },
    onPointDraggEnd(dataIndex) {
      // 拖拽结束
      // 避免拖动失效
      setTimeout(this.dragEnable, 0);
    },
    initKline() {
      // 初始化k线数据
      this.kData = [];
      let before = [0, 50];
      this.data.forEach((now, k) => {
        this.kData.push([before[1], now[1], before[1] - 10, now[1] + 10]);
        before = now;
      });
      this.kOption = {
        grid: {
          x: 50,
          x2: 20,
          y: 12,
        },
        xAxis: {
          data: this.times,
        },
        yAxis: {
          min: 0,
          max: this.height,
          axisLine: {onZero: false}
        },
        series: [
          {
            type: "k",
            data: this.kData,
            itemStyle: {
              color: "#14b143",
              color0: "#ef232a",
              borderColor: "#14b143",
              borderColor0: "#ef232a",
            },
          },
        ],
      };
      this.refreshKchart();
    },
    initData() {
      // 初始化虚拟折线数据
      this.init = true;
      this.data = [];
      this.times = [];
      var size = (24 * 60) / this.granularity;
      var time = +new Date(2020, 0, 1);
      for (var i = 0; i <= size; i++) {
        var x = echarts.format.formatTime("hh:mm", time);
        this.times.push(x);
        this.data.push([x, this.height / 2]);
        time += this.granularity * 60 * 1000;
        if (i + 1 == size) {
          time -= 60 * 1000;
        }
      }
    },
    //修改高度
    changeHeight() {
      if (!!this.symbol) {
        localStorage.setItem(this.symbol + "-height", this.height)
      }
      if (this.init) {
        this.initData();
      }
      this.refreshChart();
    },
    refreshChart() {
      this.option = {
        // 基本配置，画曲线
        title: {
          text: "",
        },
        tooltip: {
            show: true,
            triggerOn: 'none',
            formatter: function (params) {
                return params.data[1].toFixed(6);
            }
        },
        grid: {
          x: 50,
          x2: 20,
          y: 12,
        },
        xAxis: {
          data: this.times,
          // type: 'time',
          // splitLine: {
          //     show: false
          // }
        },
        yAxis: {
          min: 0,
          max: this.height,
          type: "value",
        },
        series: [
          {
            id: "a",
            type: "line",
            smooth: true,
            symbolSize: this.symbolSize,
            data: this.data,
          },
        ],
      };
      // 启动chart
      this.myChart && this.myChart.clear();
      this.myChart = echarts.init(document.getElementById("robot_mline"));
      this.myChart.setOption(this.option);
      setTimeout(this.dragEnable, 0);
    },
    refreshKchart() {
      // 启动kchart
      this.myKchart && this.myKchart.clear();
      this.myKchart = echarts.init(document.getElementById("robot_kline"));
      this.myKchart.setOption(this.kOption);
    },
    makeDurationToSeconds(time) {
      var str = time;
      var arr = str.split(":");
      var ms = parseInt(arr[0] * 60);
      var ss = parseInt(arr[1]);
      var seconds = ms + ss;
      return seconds;
    },
    dateToString: function (date) {
      var year = date.getFullYear();
      var month = (date.getMonth() + 1).toString();
      var day = date.getDate().toString();
      if (month.length == 1) {
        month = "0" + month;
      }
      if (day.length == 1) {
        day = "0" + day;
      }
      var dateTime = year + "-" + month + "-" + day;
      return dateTime;
    },
    save() {
      let params = this.data.map((e) => {
        return {
          symbol: this.symbol,
          date: this.dateToString(new Date(this.date)),
          time: this.makeDurationToSeconds(e[0]),
          price: e[1],
        };
      });
      saveRobotQuotas(params).then((res) => {
        if (!res.code) {
          this.$Notice.success({
            title: "保存成功！",
            desc: res.message,
            duration: 10,
          });
        } else {
          this.$Notice.error({
            title: "保存失败",
            desc: res.message,
            duration: 10,
          });
        }
      });
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
  margin-bottom: 10px;
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