<template>
    <div>
        <Row v-if="searchAble" class="functionWrapper">
          <div class="searchWrapper">
            <div class="poptip">
              <Input placeholder="请输入手机号" v-model="filterSearch.mobilePhone" />
            </div>
            <div class="btns">
              <Button type="info" @click="search">搜索</Button>
            </div>
          </div>
        </Row>
        <Row  class="table">
          <Table :row-class-name="rowClassName" :columns="columns" :data="list" border :loading="ifLoading" @on-row-click="onRowClick">
          </Table>
        </Row>
        <div class="pageWrapper">
          <Page
            :total="pageNum"
            :current="currentPageIdx"
            :page-size="filterSearch.pageSize"
            @on-change="changePage"
            show-elevator
          >
          </Page>
        </div>
    </div>    
</template>

<script>
import { getRiskMembers } from "@/service/getData";

export default {
  name: "MemberList",
  props: {
    sid: Number,
    toSid: Number,
    selectAble: {
      type: Boolean,
      default: false
    },
    selected: {
      type: Array,
      default: () => []
    },
    searchAble: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      filterSearch: {
        mobilePhone: "",
        pageNo: 1,
        pageSize: 10,
        riskStrategyId: ""
      },
      currentPageIdx: 1,
      ifLoading: true,
      pageNum: null,
      list: [],
      selectedMember: [],
      columns: [
        {
          title: "用户ID",
          key: "memberId",
        },
        {
          title: "手机号",
          key: "mobilePhone",
        },
        {
          title: "真实姓名",
          key: "realName",
        },
        {
          title: "永续合约收益",
          key: "alwaysProfit",
        },
        {
          title: "秒合约收益",
          key: "secondProfit",
        },
        { title: "策略组", key: "riskStrategyName" },
      ],
    };
  },
  methods: {
    onRowClick(row, index) {
      if (!this.selectAble) {
        return;
      }
      if (!!row.riskStrategyId) {
        if (!this.toSid) {
            this.$Message.error("该用户已存在策略组");
            return;
        } else if (row.riskStrategyId!=this.toSid) {
            this.$Message.error("该用户已存在其它策略组");
            return;
        }
        
      }
      let i = this.selectedMember.findIndex(e=>e.memberId == row.memberId);
        if (i!=-1) {
            this.selectedMember.splice(i, 1);
        } else {
          this.selectedMember.push({memberId: row.memberId, memberName: row.realName ? (row.realName + '-' + row.mobilePhone) : row.mobilePhone})
        }
    },
    rowClassName (row, index) {
        let i =this.selectedMember.findIndex(e=>e.memberId == row.memberId);
        if (i!=-1) {
            return 'table-tr-selected';
        }
        return '';
    },
    getSelected() {
      return this.selectedMember;
    },
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
    search() {
      this.filterSearch.pageNo = 1;
      this.filterSearch.pageSize = 10;
      this.refreshPage();
    },
    refreshPage() {
      this.ifLoading = true;
      getRiskMembers(this.filterSearch).then((res) => {
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
    this.selectedMember = this.selected;
    this.filterSearch.riskStrategyId = this.sid;
    this.refreshPage();
  },
};
</script>

<style scoped>
.ivu-upload {
  display: inline-block;
}
</style>
<style>
.ivu-table .table-tr-selected td{
    background-color: #187;
    color: #fff;
}
</style>
