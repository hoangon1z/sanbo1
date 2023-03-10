<template>
  <div>
    <Tabs :value="type" @on-click="handleType">
      <TabPane label="当前持仓" name="order">
        <Table :columns="columns_orders" :data="orders"></Table>
      </TabPane>
      <TabPane label="当前委托" name="entrust">
        <Table :columns="columns_entrusts" :data="entrusts"></Table>
      </TabPane>
    </Tabs>
  </div>
</template>
<script>
import dtime from "time-formater";
import { queryContractOrder, queryContractEntrust } from "@/service/getData";
export default {
  props: {
    symbol: String,
  },
  data() {
    return {
      type: "order",
      columns_orders: [
        {
          title: "用户ID",
          key: "memberId",
        },
        {
          title: "合约",
          key: "contractCoinName",
        },
        {
          title: "开仓均价",
          key: "openPrice",
        },
        {
          title: "持仓量(张)",
          key: "position",
        },
        {
          title: "杠杆",
          key: "multiple",
        },
        {
          title: "保证金",
          key: "principalAmount",
        },
        {
          title: "仓位方向",
          key: "direction",
          render: (h, params) => {
            const row = params.row;
            const direction = row.direction == "BUY" ? "做多" : "做空";
            return h("span", {}, direction);
          },
        },
        {
          title: "成交时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.createTime).format("YYYY-MM-DD HH:mm:ss");
            return h("span", {}, formatTime);
          },
        },
        
        {
          title: "计划平仓",
          width: 100,
          render: (h, obj) => {
            let formatTime = "无";
            if (!!obj.row.planCloseTime) {
              formatTime = dtime(obj.row.planCloseTime).format("YYYY-MM-DD HH:mm:ss");
            }
            return h("span", {}, formatTime);
          },
        },
        // {
        //   title: "平仓时间",
        //   width: 100,
        //   render: (h, obj) => {
        //     let formatTime = "无";
        //     if (!!obj.row.closeTime) {
        //       formatTime = dtime(obj.row.closeTime).format("YYYY-MM-DD HH:mm:ss");
        //     }
        //     return h("span", {}, formatTime);
        //   },
        // },
        {
          title: "预估强平价",
          key: "liquidationPrice",
        },
      ],
      columns_entrusts: [
        {
          title: "用户ID",
          key: "memberId",
        },
        {
          title: "委托量(张)",
          key: "share",
        },
        {
          title: "杠杆",
          key: "leverage",
        },
        {
          title: "保证金",
          key: "principalAmount",
        },
        {
          title: "订单方向",
          key: "direction",
          render: (h, params) => {
            const row = params.row;
            const direction =
              row.direction == "BUY"
                ? row.entrustType == "OPEN"
                  ? "买入开多"
                  : "买入平空"
                : row.entrustType == "OPEN"
                ? "卖出开空"
                : "卖出平多";
            return h("span", {}, direction);
          },
        },
        {
          title: "委托价格",
          key: "entrustPrice",
          render: (h, params) => {
            const row = params.row;
            return h("span", {}, row.marketPrice ? "对手价" : row.entrustPrice);
          },
        },
        {
          title: "挂单时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.createTime).format("HH:mm:ss");
            return h("span", {}, formatTime);
          },
        },
      ],
      orders: [],
      entrusts: [],
    };
  },
  watch: {
    symbol: function(val) {
      this.refresh();
    },
  },
  methods: {
    refresh() {
      if (this.type == "order") {
        this.queryContractOrder();
      } else {
        this.queryContractEntrust();
      }
    },
    handleType(name) {
      this.type = name;
      this.refresh();
    },
    queryContractOrder() {
      queryContractOrder({ status: 1, pageNo: 1, pageSize: 50, symbol: this.symbol }).then(
        (res) => {
          this.orders = res.data.content;
        }
      );
    },
    queryContractEntrust() {
      queryContractEntrust({
        status: 1,
        pageNo: 1,
        pageSize: 50,
        symbol: this.symbol,
      }).then((res) => {
        this.entrusts = res.data.content;
      });
    },
  },
  created() {
    this.queryContractOrder();
  },
};
</script>

<style lang="less" scoped>
</style>
