<template>
  <div>
    <Card>
      <p slot="title">
        风控策略
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>
      <Row class="manageRow clearfix">
        <div class="manageWrapper">
          <Button type="info" @click="add">添加组</Button>
        </div>
      </Row>
      <Row class="tableWrapper">
        <Table :columns="columns" :data="list" border :loading="ifLoading">
        </Table>
      </Row>
      <Row class="pageWrapper">
        <Page
          :total="total"
          :current="currentPageIdx"
          @on-change="changePage"
          show-elevator
        ></Page>
      </Row>
      <p style="font-size:11px;">注意1：同一用户只能绑定一个策略</p>
      <p style="font-size:11px;">注意2：默认策略影响未绑定独立策略的所有用户</p>
      <p style="font-size:11px;">注意3：同一时间策略发生冲突，系统会自动计算选取最优策略</p>
    </Card>
  </div>
</template>

<script>
import {
  getRiskStrategys,
  delRiskStrategy,
  activeRiskStrategy,
} from "@/service/getData";
import { getStore, removeStore, setStore } from "@/config/storage";
import expandRow from "./components/MemberList";
export default {
  components: {
    expandRow,
  },
  data() {
    return {
      list: [],
      total: 0,
      realName: "",
      pageNo: 1,
      mobilePhone: "",
      email: "",
      currentPageIdx: 1,
      ifLoading: true,
      columns: [
        {
          type: "expand",
          width: 50,
          render: (h, params) => {
            return h(expandRow, {
              props: {
                sid: params.row.id,
              },
            });
          },
        },
        // {
        //   title: "策略编号",
        //   key: "id",
        //   width: 50,
        // },
        {
          title: "策略名称",
          key: "name",
        },
        {
          title: "控制人数",
          render: (h, obj) => {
            return h("span", {}, obj.row.isDefault?"全部":obj.row.members.length);
          },
        },
        {
          title: "收益权重",
          render: (h, obj) => {
            return h("span", {}, obj.row.profitWeight);
          },
        },
        {
          title: "最小手数",
          key: "minShare",
        },
        {
          title: "距离开仓点位",
          render: (h, obj) => {
            return h(
              "span",
              {},
              obj.row.diffOpenPointMin + "至" + obj.row.diffOpenPointMax
            );
          },
        },
        {
          title: "距离平仓点位",
          render: (h, obj) => {
            return h("span", {}, obj.row.autoClosePoint?"智能":obj.row.diffClosePoint);
          },
        },
        {
          title: "距离平仓时间",
          key: "diffCloseTime",
        },
        {
          title: "恢复报价时间",
          key: "resumeTime",
        },
        {
          title: "状态",
          render: (h, obj) => {
            return h("span", {}, obj.row.status == 1 ? "启用" : "停用");
          },
        },
        // {
        //   title: "编辑时间",
        //   key: "updateTime",
        // },
        {
          title: "操作",
          key: "xx",
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: { type: "success", size: "small" },
                  style: { marginRight: "5px" },
                  on: {
                    click: () => {
                      this.active(obj.row.id);
                    },
                  },
                },
                "切换"
              ),
              h(
                "Button",
                {
                  props: { type: "warning", size: "small" },
                  style: { marginRight: "5px" },
                  on: {
                    click: () => {
                      this.edit(obj.row.id);
                    },
                  },
                },
                "编辑"
              ),
              h(
                "Button",
                {
                  props: { type: "error", size: "small" },
                  style: { marginRight: "5px" },
                  on: {
                    click: () => {
                      this.del(obj.row.id);
                    },
                  },
                },
                "删除"
              ),
            ]);
          },
        },
      ],
    };
  },
  methods: {
    init() {
      getRiskStrategys({ pageNo: this.pageNo, pageSize: 10 }).then((res) => {
        if (!res.code) {
          this.ifLoading = false;
          this.list = res.data.content;
          this.total = res.data.totalElements;
        } else {
          this.$Message.error(res.message);
        }
      });
    },
    edit(id) {
      setStore("strategyId", id);
      this.$router.push("/risk/strategy/add");
    },
    del(id) {
      this.$Modal.confirm({
        title: "提示",
        content: "<p>是否确定删除？</p>",
        onOk: () => {
          delRiskStrategy(id).then((res) => {
            if (!res.code) {
              this.$Message.success("操作成功!");
              this.refreshPageManual();
            } else this.$Message.error("异常错误!");
          });
        },
        onCancel: () => {},
      });
    },
    active(id) {
      activeRiskStrategy(id).then((res) => {
        if (!res.code) {
          this.$Message.success("切换状态成功!");
          this.refreshPageManual();
        } else this.$Message.error("异常错误!");
      });
    },
    refreshPageManual() {
      this.pageNo = 1;
      this.init();
    },
    search() {
      this.pageNo = 1;
      this.init();
    },
    changePage(index) {
      this.pageNo = index;
      this.init();
    },
    add() {
      removeStore("strategyId");
      this.$router.push("/risk/strategy/add");
    },
  },
  created() {
    this.init();
  },
};
</script>

<style lang="less" scoped>
.manageRow {
  height: 45px;
}
.manageWrapper {
  float: right;
}
.manageWrapper Button {
  margin-right: 10px;
}
</style>
