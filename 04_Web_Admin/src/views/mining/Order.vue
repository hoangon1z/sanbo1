<template>
  <div>
    <Row>
      <Card>
        <p slot="title">
          矿机订单
          <Button type="primary" size="small" @click="refreshPageManual">
            <Icon type="refresh"></Icon>刷新
          </Button>
        </p>
        <Row>
          <Table :columns="columns" :data="list" border :loading="ifLoading">
          </Table>
        </Row>
        <div class="pageWrapper">
          <Page
            :total="pageNum"
            :current="currentPageIdx"
            @on-change="changePage"
            show-elevator
          >
          </Page>
        </div>
      </Card>
    </Row>
  </div>
</template>

<script>
import { getMiningOrder, BASICURL } from "@/service/getData";
import { getStore, removeStore, setStore } from "@/config/storage";

export default {
  data() {
    return {
      picUrl: "",
      picUrlIcon: false,
      basicUrl: BASICURL,
      filterSearch: {
        pageNo: 1,
        pageSize: 10,
      },
      currentPageIdx: 1,
      ifLoading: true,
      pageNum: null,
      orderList: [],

      list: [],
      columns: [
        {
          title: "矿机名称",
          key: "title",
        },
        {
          title: "用户ID",
          key: "memberId",
        },
        {
          title: "小图",
          width: 130,
          render: (h, obj) => {
            let smallImageUrl = obj.row.image;
            return h(
              "img",
              {
                attrs: { src: smallImageUrl },
                style: { height: "45px", padding: "5px 0px" },
              },
              ""
            );
          },
        },
        {
          title: "基础产能",
          render: (h, obj) => {
            let item = obj.row;
            let txt = item.miningDaysprofit + item.miningUnit + "/" + item.miningDays;
            if (item.period==1) {
              txt += '周'
            } else if (item.period==2) {
              txt += '月'
            } else if (item.period==3) {
              txt += '年'
            } else {
              txt += '日'
            }
            return h(
              "span",
              txt
            );
          },
        },
        {
          title: "实际产能",
          render: (h, obj) => {
            let item = obj.row;
            let txt = item.currentDaysprofit + item.miningUnit + "/" + item.miningDays;
            if (item.period==1) {
              txt += '周'
            } else if (item.period==2) {
              txt += '月'
            } else if (item.period==3) {
              txt += '年'
            } else {
              txt += '日'
            }
            return h(
              "span",
              txt
            );
          },
        },
        {
          title: "状态",
          render: (h, obj) => {
            let status = obj.row.status;
            let txt = "";
            if (status == 0) {
              txt = "待部署";
            } else if (status == 1) {
              txt = "已部署";
            } else {
              txt = "已结束";
            }
            let color = status == 1 ? "#19be6b" : "#FF0000";
            return h(
              "span",
              {
                style: {
                  color: color,
                },
              },
              txt
            );
          },
        },

        {
          title: "产出币种",
          key: "miningUnit",
        },
        {
          title: "产出收益",
          key: "totalProfit",
        },
        {
          title: "挖矿周期",
          key: "miningTimes",
        },
        {
          title: "已挖周期",
          key: "miningedTimes",
        },
        { title: "手续费", key: "fee" },
      ],
    };
  },
  methods: {
    refreshPageManual() {
      this.currentPageIdx = 1;
      for (let key in this.filterSearch) {
        this.filterSearch[key] = "";
      }
      this.filterSearch.pageNo = 1;
      this.filterSearch.pageSize = 10;
      this.refreshPage(this.filterSearch);
    },
    changePage(pageIndex) {
      this.currentPageIdx = pageIndex;
      this.filterSearch.pageNo = pageIndex;
      this.refreshPage(this.filterSearch);
    },
    refreshPage(obj) {
      this.ifLoading = true;
      getMiningOrder(this.filterSearch).then((res) => {
        if (!res.code) {
          this.ifLoading = false;
          this.pageNum = (res.data && res.data.totalElements) || 1;
          this.list = (res.data && res.data.content) || [];
        } else {
          this.$Message.error(res.message);
        }
      });
    },
  },
  created() {
    this.refreshPage();
  },
};
</script>

<style scoped>
.ivu-upload {
  display: inline-block;
}
</style>
