<template>
    <Table :columns="columns_orders" :data="orders"></Table>
</template>
<script>
import dtime from "time-formater";
import { getRiskGarbage } from "@/service/getData";
export default {
  props: {
    symbol: String,
  },
  data() {
    return {
      type: "order",
      columns_orders: [
        {
          title: "开始时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.start*1000).format("YYYY-MM-DD HH:mm:ss");
            return h("span", {}, formatTime);
          },
        },
        {
          title: "结束时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.end*1000).format("YYYY-MM-DD HH:mm:ss");
            return h("span", {}, formatTime);
          },
        },
        {
          title: "策略",
          key: "strategyName",
        },
        {
          title: "开仓均价",
          key: "openPrice",
        },
        {
          title: "持仓量(张)",
          key: "share",
        },
        {
          width: 700,
          title: "未触发原因",
          key: "reason",
        }
      ],
      orders: [],
    };
  },
  watch: {
  },
  methods: {
    refresh() {
      this.getRiskGarbage()
    },
    getRiskGarbage() {
      getRiskGarbage({ status: 1, pageNo: 1, pageSize: 50 }).then(
        (res) => {
          this.orders = res.data.content;
        }
      );
    }
  },
  created() {
    this.getRiskGarbage();
  },
};
</script>

<style lang="less" scoped>
</style>
