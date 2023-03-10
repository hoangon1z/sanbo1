<template>
  <div>
    <Modal
      class="auditModel"
      v-model="closeForce"
      title="强制平仓"
      width="350"
      @on-cancle="closeForce = false"
      @on-ok="doClose"
    >
      <InputNumber
        style="width: 100%"
        v-model="closePrice"
        placeholder="请输入平仓价格"
      />
    </Modal>
    <Card>
      <div slot="title" style="min-height: 30px; width: 100%">
        <Button type="primary" @click="refreshPageManual" style="float: right">
          <Icon type="refresh"></Icon>刷新
        </Button>
      </div>
      <Row class="priceSectionWrapper clearfix">
        <div class="priceSection">
          均价区间：
          <Input v-model="filterSearch.minPrice" />~
          <Input v-model="filterSearch.maxPrice" />
        </div>

        <div class="priceSection" style="margin-left: 10px">
          数量区间：
          <Input v-model="filterSearch.minAmount" />~
          <Input v-model="filterSearch.maxAmount" />
        </div>

        <div class="orderStatus">
          <span>仓位状态：</span>
          <Select v-model="filterSearch.status">
            <Option
              v-for="item in orderStatus"
              :value="item.status"
              :key="item.status"
              >{{ item.text }}</Option
            >
          </Select>
        </div>

        <div class="orderStatus">
          <span>仓位方向：</span>
          <Select v-model="filterSearch.dirt">
            <Option
              v-for="item in orderDirArr"
              :value="item.status"
              :key="item.status"
              >{{ item.text }}</Option
            >
          </Select>
        </div>

        <!-- <div class="orderStatus">
          <span>挂单类型：</span>
          <Select v-model="filterSearch.type" style="width:80px">
            <Option v-for="item in typeArr"
                  :value="item.status"
                  :key="item.status">{{ item.text }}</Option>
          </Select>
        </div>-->
      </Row>
      <Row class="functionWrapper">
        <div class="searchWrapper clearfix" style="width: 100%">
          <Poptip
            trigger="hover"
            content="请输入 币单位 搜索"
            placement="bottom-start"
          >
            <Input
              placeholder="请输入 币单位 搜索"
              v-model="filterSearch.coinSymbol"
            />
          </Poptip>

          <Poptip
            trigger="hover"
            content="请输入 委托单号 搜索"
            placement="bottom-start"
          >
            <Input
              placeholder="请输入 单号 搜索"
              v-model="filterSearch.contractOrderId"
            />
          </Poptip>
          <Poptip
            trigger="hover"
            content="请输入 用户ID 搜索"
            placement="bottom-start"
          >
            <Input
              placeholder="请输入 用户ID 搜索"
              v-model="filterSearch.memberId"
            />
          </Poptip>

          <div class="btns" style="float: right">
            <Button type="info" @click="searchByFilter">搜索</Button>
          </div>
        </div>
      </Row>

      <Row>
        <Table
          :columns="columns_first"
          :data="userpage"
          :loading="ifLoading"
        ></Table>
      </Row>

      <Row class="pageWrapper">
        <Page
          :total="totalNum"
          style="margin-top: 8px"
          :current="current"
          @on-change="changePage"
          show-elevator
        ></Page>
      </Row>
    </Card>
  </div>
</template>

<script>
import dtime from "time-formater";
import { queryContractOrder, closeContractOrder } from "@/service/getData";
import { setStore, getStore, removeStore } from "@/config/storage";

export default {
  data() {
    return {
      closeForce: false,
      closePrice: 0.0,
      closeOrderId: 0,
      btnType: 0,
      filterSearch: {
        coinSymbol: "",
        dirt: "",
        type: "",
        orderId: "",
        memberId: "",
        baseSymbol: "",
        minPrice: "",
        maxPrice: "",
        minAmount: "",
        maxAmount: "",
        status: 1,
        pageNo: 1,
        pageSize: 10,
        completed: 0, //0是委托订单1是历史订单
      },
      typeArr: [
        { status: 2, text: "限价" },
        { status: 3, text: "计划" },
        { status: "", text: "全部" },
      ],
      orderDirArr: [
        { status: 1, text: "做多" },
        { status: 2, text: "做空" },
        { status: "", text: "全部" },
      ],
      priceRange: "",
      orderStatusModel: null,
      // 1:持仓中/2:用户平仓/3:强制平仓/4:委托持仓中/5:止盈止损平仓/6:跟随持仓中/7:跟随平仓
      orderStatus: [
        { status: 1, text: "持仓中" },
        { status: 2, text: "用户平仓" },
        { status: 3, text: "强制平仓" },
        { status: 4, text: "委托持仓中" },
        { status: 5, text: "止盈止损平仓" },
        { status: 6, text: "跟随持仓中" },
        { status: 7, text: "跟随平仓" },
        { status: "", text: "全部" },
      ],
      totalNum: null,
      current: 1,
      priceSecPass: false,
      priceLow: null,
      priceHeight: null,
      coinSymbol: null,
      direction: null,
      orderType: null,
      orderId: null,
      memberId: null,
      baseSymbol: null,
      currentPageIdx: 1,
      ifLoading: true,
      columns_first: [
        {
          title: "仓位号",
          key: "contractOrderId",
          width: 180,
        },
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
          // render: (h, params) => {
          //   const row = params.row;
          //   return h("span", {}, row.marketPrice?'对手价':row.entrustPrice);
          // }
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
        // {
        //   title: "触发价格",
        //   key: "triggerPrice",
        //   render: (h, params) => {
        //     const row = params.row;
        //     return h("span", {}, row.triggerPrice>0?row.triggerPrice:'');
        //   }
        // },

        // {
        //   title: "触发时间",
        //   width: 100,
        //   render: (h, obj) => {
        //     let formatTime = obj.row.triggeringTime?dtime(obj.row.triggeringTime).format('YYYY-MM-DD HH:mm:ss'):''
        //     return h('span',{},formatTime)
        //   }
        // },
        {
          title: "成交时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = dtime(obj.row.createTime).format(
              "YYYY-MM-DD HH:mm:ss"
            );
            return h("span", {}, formatTime);
          },
        },
        {
          title: "计划平仓",
          width: 100,
          render: (h, obj) => {
            let formatTime = "无";
            if (!!obj.row.planCloseTime) {
              formatTime = dtime(obj.row.planCloseTime).format(
                "YYYY-MM-DD HH:mm:ss"
              );
            }
            return h("span", {}, formatTime);
          },
        },
        {
          title: "平仓时间",
          width: 100,
          render: (h, obj) => {
            let formatTime = "-";
            if (!!obj.row.closeTime) {
              formatTime = dtime(obj.row.closeTime).format(
                "YYYY-MM-DD HH:mm:ss"
              );
            }
            return h("span", {}, formatTime);
          },
        },
        // {
        //   title: "成交价格",
        //   key: "strikePrice",
        //   render: (h, params) => {
        //     const row = params.row;
        //     return h("span", {}, row.strikePrice || '');
        //   }
        // },
        {
          title: "预估强平价",
          key: "liquidationPrice",
        },
        {
          title: "状态",
          key: "status",
          render: (h, params) => {
            const row = params.row;
            var direction = "";
            if (row.status == "MARKET_IN_THE_POSITION") {
              direction = "持仓中";
            } else if (row.status == "MARKET_USER_CLOSE_OUT") {
              direction = "用户平仓";
            } else if (row.status == "MARKET_FORCED_CLOSE_OUT") {
              direction = "强制平仓";
            } else if (row.status == "MARKET_ENTRUST_IN_THE_POSITION") {
              direction = "委托持仓中";
            } else if (row.status == "MARKET_STOP_PROFIT_LOSS_CLOSE_OUT") {
              direction = "止盈止损平仓";
            } else if (row.status == "MARKET_FOLLOW_IN_THE_POSITION") {
              direction = "跟随持仓中";
            } else if (row.status == "MARKET_FOLLOW_CLOSE_OUT") {
              direction = "跟随平仓";
            }
            return h("span", {}, direction);
          },
        },
        {
          title: "操作",
          key: "age",
          width: 150,
          render: (h, obj) => {
            var actions = [];
            // actions.push();
            if (obj.row.status == "MARKET_IN_THE_POSITION") {
              actions.push(
                h(
                  "Button",
                  {
                    props: {
                      type: "error",
                      size: "small",
                    },
                    on: {
                      click: () => {
                        this.closeContractOrder(obj.row.id);
                      },
                    },
                  },
                  "强制平仓"
                )
              );
            }
            return h("div", actions);
          },
        },
      ],
      userpage: [],
    };
  },
  methods: {
    // switchEnsure() {

    // },
    localEnsure() {
      this.filterSearch.pageNo = 1;
      this.current = 1;
      this.btnType = 0;
      this.filterSearch.completed = 0;
      this.refreshPage(this.filterSearch);
    },
    hisEnsure() {
      this.filterSearch.pageNo = 1;
      this.current = 1;
      this.btnType = 1;
      this.filterSearch.completed = 1;
      this.refreshPage(this.filterSearch);
    },
    searchByFilter() {
      let reg = /[^0-9]/;
      // alert(this.memberId)
      this.current = 1;
      let bol = reg.test(this.memberId);
      if (bol && !!this.memberId) {
        this.$Message.warning("请输入正确的id！");
        return;
      }

      if (isNaN(this.priceLow * 1) || isNaN(this.priceHeight * 1)) {
        this.$Message.warning("请输入正确的价格！");
      } else if (this.priceLow * 1 < 0 || this.priceHeight * 1 < 0) {
        this.$Message.warning("价格应该大于等于零！");
      } else {
        if (this.priceLow * 1 > this.priceHeight * 1) {
          this.$Message.warning("最低价格不能高于最高价格");
        } else {
          this.$store.commit("switchLoading", true);
          this.refreshPage(this.filterSearch);
        }
      }
    },
    refreshPageManual() {
      for (let key in this.filterSearch) {
        this.filterSearch[key] = "";
      }
      this.currentPageIdx = 1;
      this.current = 1;
      this.btnType = 0;
      this.filterSearch.pageNo = 1;
      this.filterSearch.pageSize = 10;
      this.filterSearch.completed = 0;
      this.refreshPage(this.filterSearch);
    },
    changePage(pageIndex) {
      this.current = pageIndex;
      this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
      this.ifLoading = true;
      console.log(obj)
      queryContractOrder(obj).then((res) => {
        this.userpage = res.data.content;
        this.totalNum = res.data.totalElements;
        this.ifLoading = false;
        this.$store.commit("switchLoading", false);
      });
    },
    closeContractOrder(orderId) {
      this.closeForce = true;
      this.closeOrderId = orderId;
    },
    doClose() {
      console.log(this.closePrice, this.closeOrderId);
      if (!this.closePrice || !this.closeOrderId || this.closePrice <= 0) {
        return;
      }
      closeContractOrder({ orderId: this.closeOrderId, price: this.closePrice })
        .then((res) => {
          if (res.code == 0) {
            this.$Message.success("平仓成功");
            this.refreshPage(this.filterSearch);
          } else {
            this.$Message.error("平仓失败");
          }
        })
        .catch((err) => console.log(err));
    },
  },
  created() {
    this.refreshPage(this.filterSearch);
  },
};
</script>

<style lang="less" scoped>
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
